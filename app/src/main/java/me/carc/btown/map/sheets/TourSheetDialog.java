package me.carc.btown.map.sheets;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import me.carc.btown.App;
import me.carc.btown.R;
import me.carc.btown.Utils.AndroidUtils;
import me.carc.btown.Utils.IntentUtils;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.Utils.WikiUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.CacheDir;
import me.carc.btown.common.Commons;
import me.carc.btown.extras.WikiWebViewActivity;
import me.carc.btown.tours.adapters.PoiInfoListAdapter;
import me.carc.btown.tours.attractionPager.InfoCard;
import me.carc.btown.db.tours.model.Attraction;
import me.carc.btown.db.tours.model.StopInfo;

/**
 * Bottom Sheet Dialog for points of interest
 * Created by bamptonm on 31/08/2017.
 */
public class TourSheetDialog extends BottomSheetDialogFragment {

    private static final String TAG = TourSheetDialog.class.getName();
    public static final String ID_TAG = "TourSheetDialog";


    public static final String EXTRA_ATTRACTION_DATA = "EXTRA_ATTRACTION_DATA";
    public static final String JSON_EMBEDDED_IMAGE_TAG = "<img src=";


    private int minHeight, infoHeight;



    private BottomSheetBehavior behavior;
    private Attraction attractionData;
    private ArrayList<CardView> cards;
    private ArrayList<me.carc.btown.tours.attractionPager.InfoCard> items;
    private Unbinder unbinder;

    @BindView(R.id.main_content)
    CoordinatorLayout root;

    @Nullable
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

/*
    @Nullable
    @BindView(R.id.titleText)
    TextView titleText;
*/

    @Nullable
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Nullable
    @BindView(R.id.backFab)
    FloatingActionButton backFab;

    @BindView(R.id.backdrop)
    KenBurnsView imageBackDrop;

    @BindView(R.id.attraction_layout)
    LinearLayout ll;



    public static boolean showInstance(final Context appContext, Attraction attraction) {

        AppCompatActivity activity = ((App) appContext).getCurrentActivity();
        try {
            Bundle bundle = new Bundle();
            bundle.putParcelable(EXTRA_ATTRACTION_DATA, attraction);

            TourSheetDialog fragment = new TourSheetDialog();
            fragment.setArguments(bundle);
            fragment.show(activity.getSupportFragmentManager(), ID_TAG);

        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetCB = new BottomSheetBehavior.BottomSheetCallback() {

        private boolean sliding;

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            behavior.setHideable(false);
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                sliding = false;
                adjustLayout(true);
            } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                if (sliding)
                    sliding = false;
                adjustLayout(false);
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            if (Float.isNaN(slideOffset) && !sliding) {
                sliding = true;
            }
        }
    };

    private void adjustLayout(boolean remove) {
        for (CardView card : cards) {
            if (card.getTag().equals("SPACER")) {
                if(remove)
                    card.setVisibility(View.GONE);
                else
                    card.setVisibility(View.INVISIBLE);

                break;
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.PoiItemDialog);
    }

    @Override
    public void setupDialog(final Dialog dialog, int style) {
        View rootView = View.inflate(getContext(), R.layout.sheet_attraction_dialog, null);
        unbinder = ButterKnife.bind(this, rootView);
        dialog.setContentView(rootView);

        Bundle args = getArguments();
        if (args == null)
            dismiss();

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) rootView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            this.behavior = (BottomSheetBehavior) behavior;
            this.behavior.setBottomSheetCallback(mBottomSheetCB);
        }

        try {
            attractionData = args.getParcelable(EXTRA_ATTRACTION_DATA);

            collapsingToolbar.setTitleEnabled(true);
            collapsingToolbar.setTitle(attractionData.getStopName());
            toolbar.setTitle(attractionData.getStopName());

            loadImage();

            buildViews(rootView, C.USER_LANGUAGE.equals("de"));

        } catch (NullPointerException e) {
            e.printStackTrace();

            // something went wrong, close the dialog
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 200);
        }
    }

    public void show() {
        getDialog().show();
    }

    public void hide() {
        getDialog().hide();
    }

    @OnClick(R.id.backFab)
    public void close() {
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private void loadImage() {
        if(Commons.isNotNull(attractionData.getImage())) {
            Glide.with(this)
                    .load(CacheDir.getInstance().getCachePath() + attractionData.getImage())
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);

                            // NO LOCAL VERSION FOUND - Load the image from Firebase
                            StorageReference mCoverImageStorageRef = FirebaseStorage.getInstance().getReference().child("coverImages/");
                            Glide.with(getActivity())
                                    .using(new FirebaseImageLoader())
                                    .load(mCoverImageStorageRef.child(attractionData.getImage()))
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .into(imageBackDrop);
                        }

                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            imageBackDrop.setImageDrawable(resource);
                        }
                    });
        }
    }

    private void buildViews(View rootView, boolean germanLanguage) {
        int text = ContextCompat.getColor(getActivity(), R.color.almostBlack);
        ViewUtils.changeFabColour(getActivity(), backFab, R.color.toursBackButtonBackgroundColor);

        ((TextView) rootView.findViewById(R.id.infoTitle)).setTextColor(text);

        Typeface fontTitle = Typeface.create("serif", Typeface.NORMAL);/*BaseActivity.getDefaultFont(getActivity());*/
        Typeface fontText = fontTitle; //Typeface.createFromAsset(getAssets(), "fonts/Roboto-Thin.ttf");

        cards = new ArrayList<>();
        cards.add(createInformationExpander(rootView, fontTitle, germanLanguage));

        StopInfo info = attractionData.getAttractionStopInfo(germanLanguage);

        LayoutInflater inf = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        String title;
        if (Commons.isNotNull(info.getTeaser())) {
            title = Commons.isNull(info.getTeaserTitle()) ? getString(R.string.teaser_title) : info.getTeaserTitle();
            cards.add(inflateCardView(inf, ll, title, info.getTeaser(), text, fontTitle, fontText));
        }
        if (Commons.isNotNull(info.getLoookout())) {
            title = Commons.isNull(info.getLookoutTitle()) ? getString(R.string.lookout_title) : info.getLookoutTitle();
            cards.add(inflateCardView(inf, ll, title, info.getLoookout(), text, fontTitle, fontText));
        }
        if (Commons.isNotNull(info.getHistory())) {
            title = Commons.isNull(info.getHistoryTitle()) ? getString(R.string.history_title) : info.getHistoryTitle();
            cards.add(inflateCardView(inf, ll, title, info.getHistory(), text, fontTitle, fontText));
        }
        if (Commons.isNotNull(info.getQi())) {
            title = Commons.isNull(info.getQiTitle()) ? getString(R.string.qi_title) : info.getQiTitle();
            cards.add(inflateCardView(inf, ll, title, info.getQi(), text, fontTitle, fontText));
        }
        if (Commons.isNotNull(info.getExtra())) {
            title = Commons.isNull(info.getExtraTitle()) ? getString(R.string.extra_title) : info.getExtraTitle();
            cards.add(inflateCardView(inf, ll, title, info.getExtra(), text, fontTitle, fontText));
        }
        if (Commons.isNotNull(info.getNextStop())) {
            title = Commons.isNull(info.getNextStopTitle()) ? getString(R.string.nextstop_title) : info.getNextStopTitle();
            cards.add(inflateCardView(inf, ll, title, info.getNextStop(), text, fontTitle, fontText));
        }
//        cards.add(inflateInteractCard(inf, ll, "Interact", text, fontTitle));
        cards.add(spacer(ll));
    }

    /**
     * Dynamically add additional cardviews
     *
     * @param title     card title
     * @param text      card text array
     * @param textColor color of the text
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CardView inflateCardView(LayoutInflater inflater, LinearLayout baseLayout, String title, String[] text, int textColor, Typeface fontTitle, Typeface font) {

        View card = inflater.inflate(R.layout.attraction_card_layout, root, false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        Resources r = getResources();
        int paddingDp = AndroidUtils.getPixelsFromDPs(r, 8);
        params.setMargins(paddingDp, 0, paddingDp, paddingDp);
        card.setLayoutParams(params);

        final CardView cardView = card.findViewById(R.id.my_card_layout);
        cardView.setMaxCardElevation(AndroidUtils.getPixelsFromDPs(r, 10));

        TextView descTitle = card.findViewById(R.id.mycardtitle);
        descTitle.setTypeface(fontTitle, Typeface.BOLD);
        descTitle.setText(title);
        descTitle.setTextColor(textColor);

/*      NOT USED
        final ImageView toggleIcon = (ImageView) card.findViewById(R.id.myexpandericon);
        toggleIcon.setTag("ic_expand_less");
        toggleIcon.setVisibility(View.GONE);
*/

        LinearLayout linear = card.findViewById(R.id.descLayout);

        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        imageParams.setMargins(AndroidUtils.getPixelsFromDPs(r, 16), 0, AndroidUtils.getPixelsFromDPs(r, 16), 0);

//        int overrideBaseCalc = (C.SCREEN_WIDTH / 3) *2;
        int imgOverrideSizeX = C.SCREEN_WIDTH - (imageParams.getMarginStart() * 2);
        int imgOverrideSizeY = (imgOverrideSizeX / 3) * 2;

        for (String line : text) {

            if (C.DEBUG_ENABLED)
                Log.d(TAG, "JSON_EMBEDDED_IMAGE_TAG: " + line);

            if (line.contains(JSON_EMBEDDED_IMAGE_TAG)) {  // image line

                final ImageView imageView = new ImageView(getActivity());
                imageView.setLayoutParams(imageParams);

                int start = line.indexOf("http");
                int end = line.indexOf(">");

                Uri uri = Uri.parse(line.substring(start, end));

                Glide.with(this)
                        .load(uri)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .override(imgOverrideSizeX, imgOverrideSizeY)
                        .into(imageView);

                String imageTitle = line.substring(0, line.indexOf("<"));

//                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

                imageView.setTag(R.string.key_image_title, imageTitle);
                imageView.setTag(R.string.key_image_url, uri);
                imageView.setTransitionName("imageCover");
                imageView.setDrawingCacheEnabled(true);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String image = v.getTag(R.string.key_image_url).toString();
                        String title = v.getTag(R.string.key_image_title).toString();
                        ImageDialog.showInstance(getActivity().getApplicationContext(), image, null, title, getString(R.string.wikipedia));
                    }
                });

                linear.addView(imageView);

            } else {

                final TextView textview = new TextView(getActivity());

                paddingDp = AndroidUtils.getPixelsFromDPs(r, 8);
                textview.setPadding(paddingDp, 0, paddingDp, paddingDp);
                textview.setLayoutParams(params);
                textview.setLineSpacing(0.0f, 1.2f);
                textview.setGravity(Gravity.CENTER_VERTICAL);
                if (font != null)
                    textview.setTypeface(font);
                textview.setTextColor(textColor);

                String descStr = line.replace("-nl-", "\n");    // add a new line
//                descStr = descStr.replace("-b-", "  ● ");         // add a primary bullet point
//                descStr = descStr.replace("-bnl-", "\n  ● ");   // primary bullet point
                descStr = descStr.replace("-b2-", "\n    • ");  // new line and secondary bullet point
//                descStr = descStr.replace("-nl2-", "\n\n");     // 2 new lines (separator)

                textview.setText(descStr);
                linear.addView(textview);
            }
        }

        cardView.setTag(title);
        baseLayout.addView(card);

        return cardView;
    }


    /**
     * Add the information card - shows address, telephone, etc
     *
     * @return the created cardview
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private CardView createInformationExpander(final View rootView, Typeface font, boolean germanLanguage) {
        // Get ListView object from xml
        final ListView listView = rootView.findViewById(R.id.informationListView);

        items = new ArrayList<>();

        if (!Commons.isEmpty(attractionData.getAddress()))
            items.add(new InfoCard(attractionData.getAddress(), R.drawable.ic_place));

        if (!Commons.isEmpty(attractionData.getHours()))
            items.add(new InfoCard(attractionData.getHours(), R.drawable.ic_clock));

        if (!Commons.isEmpty(attractionData.getCost()))
            items.add(new InfoCard(attractionData.getCost(), R.drawable.ic_dollar));

        if (!Commons.isEmpty(attractionData.getTel()))
            items.add(new InfoCard(attractionData.getTel(), R.drawable.ic_call));

        if (!Commons.isEmpty(attractionData.getEmail()))
            items.add(new InfoCard(attractionData.getEmail(), R.drawable.ic_email));

        if (!Commons.isEmpty(attractionData.getWebLink()))
            items.add(new InfoCard(attractionData.getWebLink(), R.drawable.ic_web));

        if (!Commons.isEmpty(attractionData.getWiki(germanLanguage)))
            items.add(new InfoCard(attractionData.getWiki(germanLanguage), R.drawable.ic_wiki));

        if (!Commons.isEmpty(attractionData.getFacebookPageIdString()))
            items.add(new InfoCard(attractionData.getFacebookPageIdString(), R.drawable.ic_facebook_box));


//        PoiInfoListAdapter poiInfoListAdapter = new PoiInfoListAdapter(getActivity(), R.layout.layout_information_item, items, font);
        PoiInfoListAdapter poiAdapter = new PoiInfoListAdapter(getActivity(), R.layout.attraction_information_item_layout, items, font);
        listView.setAdapter(poiAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InfoCard item = (InfoCard) listView.getItemAtPosition(position);
                Intent intent;

                switch (item.getDataRes()) {

                    case R.drawable.ic_place:
                        break;

                    case R.drawable.ic_clock:
                        break;

                    case R.drawable.ic_dollar:
                        break;

                    case R.drawable.ic_call:
                        Toast.makeText(getActivity(), "Phone Call::" + item.getData(), Toast.LENGTH_SHORT).show();
                        if (C.HAS_M && getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            getActivity().requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, C.PERMISSION_CALL_PHONE);
                            return;
                        }
                        startActivity(IntentUtils.callPhone(item.getData()));
                        break;

                    case R.drawable.ic_email:
                        intent = IntentUtils.sendEmail(item.getData(), "", "");
                        if (Commons.isNotNull(intent)) startActivity(intent);
                        break;

                    case R.drawable.ic_web:
                        intent = new Intent(getActivity(), WikiWebViewActivity.class);
                        intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_TITLE, attractionData.getStopName());
                        intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_URL, item.getData());
                        startActivity(intent);
                        break;

                    case R.drawable.ic_wiki:
                        intent = new Intent(getActivity(), WikiWebViewActivity.class);
                        intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_TITLE, getActivity().getString(R.string.wikipedia));
                        intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_URL, WikiUtils.createWikiLink(item.getData()));
                        startActivity(intent);
                        break;

                    case R.drawable.ic_facebook_box:
                        intent = new Intent(getActivity(), WikiWebViewActivity.class);
                        intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_TITLE, getActivity().getString(R.string.facebook));
                        intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_SUBHEADING, attractionData.getStopName());
                        intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_URL, "https://www.facebook.com/".concat(item.getData()));
                        startActivity(intent);
                        break;
                    default:
                }
            }
        });

        final CardView informationCard = rootView.findViewById(R.id.information_card);
        informationCard.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                informationCard.getViewTreeObserver().removeOnPreDrawListener(this);
                LinearLayout headerInfo = rootView.findViewById(R.id.headerInfo);
                minHeight = headerInfo.getHeight();

                ViewGroup.LayoutParams layoutParams = informationCard.getLayoutParams();
                layoutParams.height = minHeight;
                informationCard.setLayoutParams(layoutParams);

                RelativeLayout infoElement = rootView.findViewById(R.id.layout_information);
                if (infoElement == null)
                    return false;
                infoHeight = infoElement.getHeight();
                infoHeight = infoHeight * items.size();
                infoHeight = infoHeight + (AndroidUtils.getPixelsFromDPs(getResources(), 8) * items.size());  // inc padding (2dp top, 2dp bottom)
                infoHeight = infoHeight + minHeight;
                return true;
            }
        });


        final TextView txt = rootView.findViewById(R.id.infoTitle);
        txt.setTypeface(font, Typeface.BOLD);

        final ImageView icon = rootView.findViewById(R.id.infoExpanderIcon);
//        icon.getDrawable().setTint(ContextCompat.getColor(this, R.color.almostBlack));

        if (C.HAS_L) {
            icon.getDrawable().setTint(ContextCompat.getColor(getActivity(), R.color.almostBlack));
        } else {
            icon.getDrawable().setColorFilter(ContextCompat.getColor(getActivity(), R.color.almostBlack), PorterDuff.Mode.MULTIPLY);
        }

        icon.setTag("ic_expand_more");
        informationCard.setTag("Information");
        informationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(informationCard, icon);
            }
        });
        return informationCard;
    }

    /**
     * Show / Hide the card details text
     *
     * @param v which card to manipulate
     */
    private void toggleCardViewnHeight(final CardView v, ImageView icon) {

        Resources r = getResources();
        setElevation(r, v.getTag().toString());
        if (v.getHeight() == minHeight) {
            v.setCardElevation(AndroidUtils.getPixelsFromDPs(r, 8));
            expandView(v, icon, infoHeight);
        } else {
            v.setCardElevation(AndroidUtils.getPixelsFromDPs(r, 2));
            collapseView(v, icon, minHeight);
        }
    }

    public void collapseView(final View v, ImageView i, int h) {

//        if(v.getTag().equals("Information"))
        rotateExpanderIcon(i, 180.0f, 0.0f);
//        else
//            rotateExpanderIcon(i, 0.0f, 180.0f);
        ValueAnimator anim = ValueAnimator.ofInt(v.getMeasuredHeightAndState(), h);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = val;
                v.setLayoutParams(layoutParams);

            }
        });
        anim.start();
    }

    public void expandView(final View v, ImageView i, int height) {

//        if(v.getTag().equals("Information"))
        rotateExpanderIcon(i, 0.0f, 180.0f);
//        else
//            rotateExpanderIcon(i, 180.0f, 0.0f);

        ValueAnimator anim = ValueAnimator.ofInt(v.getMeasuredHeightAndState(), height);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = val;
                v.setLayoutParams(layoutParams);
            }
        });
        anim.start();
    }

    public void rotateExpanderIcon(ImageView icon, float fromAngle, float toAngle) {
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new DecelerateInterpolator());
        animSet.setFillAfter(true);
        animSet.setFillEnabled(true);

        final RotateAnimation animRotate = new RotateAnimation(fromAngle, toAngle,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotate.setDuration(300);
        animRotate.setFillAfter(true);
        animSet.addAnimation(animRotate);

        icon.startAnimation(animSet);
    }

    private CardView spacer(LinearLayout baseLayout) {

        CardView cardView = new CardView(getActivity());
        cardView.setMinimumHeight(AndroidUtils.getPixelsFromDPs(getResources(), 200));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                imageBackDrop.getLayoutParams().height + AndroidUtils.DpToDx(getResources(), 40));
        cardView.setLayoutParams(params);

        cardView.setVisibility(View.INVISIBLE);
        baseLayout.addView(cardView);
        cardView.setTag("SPACER");

        return cardView;
    }

    /**
     * Show the credits card
     * @param title the title of the card
     * @param textColor color of text
     * @return the card to be inserted to layout
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private CardView inflateInteractCard(LayoutInflater inflater, LinearLayout baseLayout, String title,
                                        int textColor, Typeface font) {

        View card = inflater.inflate(R.layout.attraction_card_interact_layout, root, false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        int paddingPixel = 16;
        float density = getResources().getDisplayMetrics().density;
        int paddingDp = (int) (paddingPixel * density);
        params.setMargins(paddingPixel, 0, paddingPixel, paddingDp);
        card.setLayoutParams(params);

        CardView cardView = card.findViewById(R.id.interact_card);

        TextView titleTxt = card.findViewById(R.id.interact_title);
        titleTxt.setTypeface(font, Typeface.BOLD);
        titleTxt.setTextColor(textColor);
        titleTxt.setText(title);

        Button btnPostcard = card.findViewById(R.id.btnPostcard);
        Button btnComment = card.findViewById(R.id.btnComment);
        Button btnCamera = card.findViewById(R.id.btnCamera);
        Button btnDonate = card.findViewById(R.id.btnDonate);
        Button btnMap = card.findViewById(R.id.btnMap);


        btnPostcard.setCompoundDrawablesWithIntrinsicBounds(new IconicsDrawable(getActivity(), FontAwesome.Icon.faw_share_alt_square).color(Color.WHITE).sizeDp(20), null, null, null);
        btnComment.setCompoundDrawablesWithIntrinsicBounds(new IconicsDrawable(getActivity(), FontAwesome.Icon.faw_comment).color(Color.WHITE).sizeDp(20), null, null, null);
        btnCamera.setCompoundDrawablesWithIntrinsicBounds(new IconicsDrawable(getActivity(), FontAwesome.Icon.faw_camera_retro).color(Color.WHITE).sizeDp(20), null, null, null);
        btnDonate.setCompoundDrawablesWithIntrinsicBounds(new IconicsDrawable(getActivity(), FontAwesome.Icon.faw_gift).color(Color.WHITE).sizeDp(20), null, null, null);
        btnMap.setCompoundDrawablesWithIntrinsicBounds(new IconicsDrawable(getActivity(), FontAwesome.Icon.faw_map).color(Color.WHITE).sizeDp(20), null, null, null);

        int bgColor = ContextCompat.getColor(getActivity(), R.color.interactiveButtonsBgColor);

        btnPostcard.getBackground().setColorFilter(bgColor, PorterDuff.Mode.MULTIPLY);
        btnComment.getBackground().setColorFilter(bgColor, PorterDuff.Mode.MULTIPLY);
        btnCamera.getBackground().setColorFilter(bgColor, PorterDuff.Mode.MULTIPLY);
        btnDonate.getBackground().setColorFilter(bgColor, PorterDuff.Mode.MULTIPLY);
        btnMap.getBackground().setColorFilter(bgColor, PorterDuff.Mode.MULTIPLY);


//        TextView imageSrc = (TextView) card.findViewById(R.id.image_source_link);
//        imageSrc.setText(" get image src from JSON");

        //ratingBar = (RatingBar)card.findViewById(R.id.rating_bar);

        baseLayout.addView(card);
        cardView.setTag(title);

        return cardView;
    }

    private void setElevation(Resources r, String id) {
        for (CardView card : cards) {
            if (card.getTag().equals(id))
                card.setCardElevation(AndroidUtils.getPixelsFromDPs(r, 8));
            else
                card.setCardElevation(AndroidUtils.getPixelsFromDPs(r, 2));
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

}

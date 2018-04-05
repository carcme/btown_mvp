package me.carc.btown.map.sheets.wiki;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import me.carc.btown.App;
import me.carc.btown.R;
import me.carc.btown.Utils.FragmentUtil;
import me.carc.btown.Utils.MapUtils;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.WikiBookmarkClickListener;
import me.carc.btown.db.AppDatabase;
import me.carc.btown.db.bookmark.BookmarkEntry;
import me.carc.btown.map.sheets.ImageDialog;
import me.carc.btown.map.sheets.share.ShareDialog;
import me.carc.btown.map.sheets.wiki.listactions.ReadingListItemActionsDialog;
import me.carc.btown.ui.custom.DividerItemDecoration;

/**
 * Show the favorites list
 * Created by bamptonm on 31/08/2017.
 */
@SuppressFBWarnings("NM_METHOD_NAMING_CONVENTION")
public class WikiReadingListDialogFragment extends DialogFragment implements WikiBookmarkClickListener {
    private static final String TAG = "DEAD";

    public interface BookmarksCallback {
        void showWikiOnMap(BookmarkEntry entry);

        void todo();
    }

    public static final String ID_TAG = "WikiReadingListDialogFragment";
    private static final String MY_LAT = "MY_LAT";
    private static final String MY_LNG = "MY_LNG";
    private static final String BOOKMARK_LIST = "BOOKMARK_LIST";
    private WikiBookmarksListAdapter adapter;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.bookmarksToolbar)
    Toolbar toolbar;

    @BindView(R.id.backdrop)
    ImageView imageBackDrop;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.emptyListView)
    TextView emptyListView;

    private Unbinder unbinder;


    public static boolean showInstance(final Context appContext, final GeoPoint currLocation, ArrayList<BookmarkEntry> bookmarks) {

        AppCompatActivity activity = ((App) appContext).getCurrentActivity();

        try {
            Bundle bundle = new Bundle();

            if (currLocation != null) {
                bundle.putDouble(MY_LAT, currLocation.getLatitude());
                bundle.putDouble(MY_LNG, currLocation.getLongitude());
            }

            if (Commons.isNotNull(bookmarks)) {
                bundle.putParcelableArrayList(BOOKMARK_LIST, bookmarks);
            }

            WikiReadingListDialogFragment fragment = new WikiReadingListDialogFragment();
            fragment.setArguments(bundle);
            fragment.show(activity.getSupportFragmentManager(), ID_TAG);

            return true;

        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, R.style.Dialog_Fullscreen);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimationSlide;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.marker_list_recyclerview_layout, container, false);

        unbinder = ButterKnife.bind(this, view);

        view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.almostWhite));

        Bundle args = getArguments();
        if (args != null) {

            double lat = args.getDouble(MY_LAT, Double.NaN);
            double lng = args.getDouble(MY_LNG, Double.NaN);
            GeoPoint myLocation = null;
            if (!Double.isNaN(lat) && !Double.isNaN(lng))
                myLocation = new GeoPoint(lat, lng);

            ArrayList<BookmarkEntry> bookmarks = args.getParcelableArrayList(BOOKMARK_LIST);

            RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
            recyclerView.addItemDecoration(itemDecoration);

            adapter = new WikiBookmarksListAdapter(myLocation, buildAdapterList(myLocation, bookmarks), this);

            if (recyclerView != null) {
                recyclerView.setHasFixedSize(true);

                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(layoutManager);

                recyclerView.setAdapter(adapter);
            }

            assert collapsingToolbar != null;

            Drawable drawable = ViewUtils.changeIconColor(getContext(), R.drawable.ic_arrow_back, R.color.white);
            toolbar.setNavigationIcon(drawable);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            toolbar.setTitle(getActivity().getString(R.string.shared_string_bookmark));

            collapsingToolbar.setTitleEnabled(true);
            collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getActivity(), R.color.white));
            collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getActivity(), R.color.white));


            new PopulateHeader(bookmarks).run();
        }
        return view;
    }

    private class PopulateHeader implements Runnable {

        ArrayList<BookmarkEntry> bookmarks;

        PopulateHeader(ArrayList<BookmarkEntry> bookmarks) {
            this.bookmarks = bookmarks;
        }

        @Override
        public void run() {
            buildIt();
        }

        private void buildIt() {

            ArrayList<String> randomImageList = new ArrayList<>();

            for(BookmarkEntry entry : bookmarks) {
                if(!Commons.isEmpty(entry.getThumbnail()))
                    randomImageList.add(entry.getThumbnail());
            }

            if(randomImageList.size() > 0) {
                String randomImageUrl = randomImageList.get(new Random().nextInt(randomImageList.size()));

                if (Commons.isNotNull(randomImageUrl)) {
                    Glide.with(getActivity())
                            .load(randomImageUrl)
                            .placeholder(R.drawable.checkered_background)
                            .into(imageBackDrop);
                } else {
                    imageBackDrop.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageBackDrop.setImageResource(R.drawable.checkered_background);
                }
            }
        }
    }

    public void hide() {
        getDialog().hide();
    }

    public void show() {
        getDialog().show();
    }

    public void closeFavorites() {
        dismiss();
    }


    /**
     * Load favorites from shared preferences
     *
     * @return the list of favorites
     */
    private ArrayList<WikiBookmarksListAdapter.WikiItemAdpater> buildAdapterList(GeoPoint location, ArrayList<BookmarkEntry> readingList) {

        ArrayList<WikiBookmarksListAdapter.WikiItemAdpater> array = new ArrayList<>();
//        IconManager im = new IconManager(getActivity());

        for (BookmarkEntry entry : readingList) {
            WikiBookmarksListAdapter.WikiItemAdpater item = new WikiBookmarksListAdapter.WikiItemAdpater();

            item.pageId = entry.getPageId();
            item.title = entry.getTitle();
            item.desciption = Commons.isEmpty(entry.getUserComment()) ? entry.getExtract() : entry.getUserComment();
            item.lat = entry.getLat();
            item.lon = entry.getLon();
            item.iconUrl = entry.getThumbnail();
            item.fullUrl = entry.getLinkUrl();

            array.add(item);
        }

        if (array.size() == 0) emptyListView.setVisibility(View.VISIBLE);

        return array;
    }


    /* Adapter click events */

    @Override
    public void OnClick(WikiBookmarksListAdapter.WikiItemAdpater item) {
        Intent intent = new Intent(getActivity(), WikiWebViewActivity.class);
        intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_TITLE, item.title);
        intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_URL, item.fullUrl);
        startActivity(intent);
    }

    @Override
    public void OnLongClick(WikiBookmarksListAdapter.WikiItemAdpater item) {
        Toast.makeText(getActivity(), "Long Press " + item.title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnImageClick(WikiBookmarksListAdapter.WikiItemAdpater item) {
        ImageDialog.showInstance(getActivity().getApplicationContext(), item.iconUrl, item.fullUrl, item.title, item.desciption);
    }

    @Override
    public void OnMoreClick(int position) {

        ArrayList<BookmarkEntry> bookmarks = getArguments().getParcelableArrayList(BOOKMARK_LIST);
        BookmarkEntry entry = bookmarks.get(position);

        if (Commons.isNotNull(entry)) {
            ReadingListItemActionsDialog dlg = ReadingListItemActionsDialog.newInstance(getActivity().getApplicationContext(), entry);
            dlg.setListener(new ReadingListItemActionsDialog.Callback() {

                @Override
                public void onShareItem(BookmarkEntry entry) {
                    if (Commons.isNotNull(entry)) {

                        // build the share message - Title, web address and location (web link format)
                        final String httpUrl = MapUtils.buildOsmMapLink(entry.getLat(), entry.getLon());

                        StringBuilder sb = new StringBuilder();
                        if (!Commons.isEmpty(entry.getTitle())) {
                            sb.append(entry.getTitle()).append("\n");
                        }
                        if (!Commons.isEmpty(entry.getLinkUrl())) {
                            sb.append(entry.getLinkUrl()).append("\n");
                        }
                        sb.append(getActivity().getString(R.string.shared_string_location)).append(": ");
                        sb.append(httpUrl);

                        ShareDialog.sendMessage(getActivity(), sb.toString());
                    }
                }

                @Override
                public void onShowOnMap(BookmarkEntry entry) {
                    callback().showWikiOnMap(entry);
                    dismiss();
                }

                @Override
                public void onDeleteItem(final BookmarkEntry entry) {
                    if (Commons.isNotNull(entry)) {
                        Executors.newSingleThreadExecutor().execute(new Runnable() {
                            @Override
                            public void run() {
                                getDatabase().bookmarkDao().delete(entry);

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.removeItem(entry.getPageId());
                                        if (adapter.getItemCount() == 0)
                                            emptyListView.setVisibility(View.VISIBLE);
                                        Toast.makeText(getActivity(), getActivity().getText(R.string.bookmark_removed), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    @NonNull
    private AppDatabase getDatabase() {
        return ((App) getActivity().getApplicationContext()).getDB();
    }


    @Nullable
    private BookmarksCallback callback() {
        return FragmentUtil.getCallback(this, BookmarksCallback.class);
    }
}

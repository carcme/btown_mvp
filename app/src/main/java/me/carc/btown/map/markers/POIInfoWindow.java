package me.carc.btown.map.markers;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import me.carc.btown.MapActivity;
import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.data.model.OverpassQueryResult;
import me.carc.btown.data.wiki.WikiQueryPage;
import me.carc.btown.ui.custom.CapitalisedTextView;

/**
 * A customized InfoWindow handling POIs. 
 * We inherit from MarkerInfoWindow as it already provides most of what we want. 
 * And we just add support for a "more info" button. 
 *
 * @author M.Kergall
 */
public class POIInfoWindow extends MarkerInfoWindow {

    private static final String TAG = C.DEBUG + Commons.getTag();

    private MapActivity mapActivity;
    private OverpassQueryResult.Element mElement;
    private WikiQueryPage mWikiPOI;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public POIInfoWindow(Context context, MapView mapView) {
        super(R.layout.btown_bubble, mapView);

        mapActivity = (MapActivity)context;

        ImageView saveBtn = (ImageView) mView.findViewById(R.id.bubble_more);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d(TAG, "Save Button onClick: ");
                mapActivity.showPoiDlg(mElement != null ? mElement : mWikiPOI);
            }
        });

        ImageView dirBtn = (ImageView)(mView.findViewById(R.id.bubble_directions));
        dirBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Toast.makeText(mapActivity, "Todo:: Need to change parameter to RouteInfo", Toast.LENGTH_SHORT).show();
/*
                if(mElement != null) {
                    if (mElement.getGeoPoint() != null) {
                        mapActivity.routeToPoi(mElement.getGeoPoint());
                    }
//                } else if(mWikiPOI != null) {
//                    mapActivity.routeToPoi(mWikiPOI.getLocation());
                }
*/
            }
        });

        ImageView imageView = (ImageView)(mView.findViewById(R.id.bubble_image));
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(mapActivity, "Show from Image", Toast.LENGTH_SHORT).show();
/*
                if(mWikiPOI != null) {
                    MapActivity.getMapActivity().getExtraWikiInfo(mWikiPOI);

                } else {

                    String imageUrl = mWikiPOI != null ? mWikiPOI.getImageUrl() : mElement.getImage();
                    Intent detailIntent = new Intent(MapActivity.getMapActivity(), ActivityImageDetail.class);
                    detailIntent.putExtra(C.EXTRA_HOLDER_IMAGE, true);
                    detailIntent.putExtra(C.EXTRA_IMAGE_TITLE, mWikiPOI != null ? mWikiPOI.getTitle() : mElement.getName());
                    detailIntent.putExtra(C.EXTRA_IMAGE_URL, WikiUtils.buildWikiCommonsLink(imageUrl, 0));

                    Bundle options = null;
//                if(C.HAS_L) {
//                    detailIntent.putExtra(C.EXTRA_HAS_TRANSITION, true);
//                    options = ActivityOptions.makeSceneTransitionAnimation(TransportrMapActivity.getMapActivity(), v, "imageCover").toBundle();
//                }
                    mapActivity.startActivity(detailIntent, options);
                }
*/
            }
        });
    }

    @Override public void onOpen(Object item){
        Marker marker = (Marker)item;
        Object relatedObj = marker.getRelatedObject();

        super.onOpen(item);

        ImageView dirBtn = (ImageView) mView.findViewById(R.id.bubble_directions);
        ImageView moreBtn = (ImageView) mView.findViewById(R.id.bubble_more);

/*
        if(C.DEBUG_ENABLED)
            moreBtn.setVisibility(View.VISIBLE);
*/

        if (relatedObj instanceof OverpassQueryResult.Element){
            mElement = (OverpassQueryResult.Element)relatedObj;

            //Fetch the thumbnail in background
            if (!Commons.isEmpty(mElement.tags.image))
                Glide.with(mapActivity)
                        .load(mElement.tags.image)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(cloudLoad);


            dirBtn.setVisibility(View.VISIBLE);
            dirBtn.setTag("osmNode");

        } else if(relatedObj instanceof WikiQueryPage) {
            mWikiPOI = (WikiQueryPage)relatedObj;

            CapitalisedTextView desc = (CapitalisedTextView)mView.findViewById(R.id.bubble_description);
            desc.setText(mWikiPOI.description());
            TextView subDesc = (TextView)mView.findViewById(R.id.bubble_subdescription);
            subDesc.setText("TODO"/*String.valueOf(mWikiPOI.Coordinates.Distance())*/);


            if (Commons.isNotNull(mWikiPOI.thumbUrl()))
                Glide.with(mapActivity)
                        .load(mWikiPOI.thumbUrl())
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(cloudLoad);

            dirBtn.setVisibility(View.VISIBLE);
            dirBtn.setTag("wiki");
        }
/*

        if(relatedObj == null)
            Log.d("DEAD", "onOpen: ");
        MapActivity.getMapActivity().scrollToListItem(relatedObj);
*/

    }

    private SimpleTarget<Bitmap> cloudLoad = new SimpleTarget<Bitmap>(C.SCREEN_WIDTH, C.SCREEN_HEIGHT) {

        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            ImageView imageView = (ImageView)mView.findViewById(R.id.bubble_image);
            imageView.setVisibility(View.VISIBLE);

            imageView.setImageBitmap(bitmap);
//            Holder.setBitmap(bitmap);
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            super.onLoadFailed(e, errorDrawable);
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            super.onLoadStarted(placeholder);
        }
    };
}

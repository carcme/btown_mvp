package me.carc.btown.tours;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.ToursScrollListener;
import me.carc.btown.tours.adapters.AttractionGalleryViewerAdapter;
import me.carc.btown.tours.model.Attraction;
import me.carc.btown.ui.custom.MyCustomLayoutManager;
import me.carc.btown.ui.custom.MyRecyclerItemClickListener;


public class AttractionTabsGalleryFragment extends Fragment {
//    private static final String TAG = C.DEBUG + Commons.getTag();

    ToursScrollListener scrollListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);

        try {
            scrollListener = (ToursScrollListener) ctx;
        } catch (ClassCastException e) {
            throw new ClassCastException(ctx.toString() + " must implement TourListener callbacks");
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView rv = (RecyclerView) inflater.inflate(R.layout.tour_list_recycler, container, false);
        setupRecyclerView(rv);
        rv.setNestedScrollingEnabled(true);
        if(C.HAS_M)
            rv.addOnScrollListener(onScrollListener);
        else
            rv.setOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if(dy > 0)
                        scrollListener.onScrollView(true);
                    else
                        scrollListener.onScrollView(false);

                }
            });
        return rv;
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if(dy > 0)
                scrollListener.onScrollView(true);
            else
                scrollListener.onScrollView(false);

            Log.d("DEAD", "onScrolled: ");
        }
    };

    private void setupRecyclerView(RecyclerView recyclerView) {
        MyCustomLayoutManager layoutManager = new MyCustomLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        Bundle args = getArguments();

        if(Commons.isNotNull(args)) {
            ArrayList<Attraction> attractions = args.getParcelableArrayList("ATTRACTIONS_LIST");

            if(Commons.isNull(attractions))
                return;

            AttractionGalleryViewerAdapter adapter = new AttractionGalleryViewerAdapter();
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);

            recyclerView.setVerticalScrollBarEnabled(false);

            recyclerView.addOnItemTouchListener(new MyRecyclerItemClickListener(getActivity(),
                    recyclerView, new MyRecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {

                    Intent showcaseIntent = new Intent(getActivity(), AttractionShowcaseImageActivity.class);
                    showcaseIntent.putExtra("INDEX", pos);

                    ImageView galleryImage = v.findViewById(R.id.item_image_img);
                    if (galleryImage == null)
                        galleryImage = ((View) v.getParent()).findViewById(R.id.item_image_img);

                    if (galleryImage != null && galleryImage.getDrawable() != null) {
                        GalleryItem gallery = AttractionTabsActivity.galleryItems.get(pos);

                        if (gallery != null) {
                            Bitmap bitmap = gallery.getBitmap();
                            if (bitmap != null && !bitmap.isRecycled()) {
                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), galleryImage, "GALLERY_IMAGE");
                                startActivity(showcaseIntent, options.toBundle());
                            }
                        }
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) { /* DO NOTHING */ }

            }));
        }
    }
}

package me.carc.btown.tours;

import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import me.carc.btown.R;
import me.carc.btown.common.CacheDir;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.RecyclerClickListener;
import me.carc.btown.common.interfaces.RecyclerTouchListener;
import me.carc.btown.common.interfaces.ToursScrollListener;
import me.carc.btown.db.tours.TourViewModel;
import me.carc.btown.db.tours.model.Attraction;
import me.carc.btown.db.tours.model.TourCatalogueItem;
import me.carc.btown.tours.adapters.AttractionGalleryViewerAdapter;
import me.carc.btown.ui.custom.MyCustomLayoutManager;


public class AttractionTabsGalleryFragment extends Fragment {
    private static final String TAG = AttractionTabsGalleryFragment.class.getName();

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

    private void getImageURLs(List<Attraction> attractions) {
        if (AttractionTabsActivity.galleryItems == null) {
            int index = 0;
            AttractionTabsActivity.galleryItems = new SparseArray<>(1);

            for (Attraction attraction : attractions) {
                GalleryItem gallery = new GalleryItem();
                gallery.setFilename(attraction.getImage());
                gallery.setCachedFilePath(CacheDir.getInstance().getCachePath() + attraction.getImage());
                gallery.setTitle(attraction.getStopName());
                gallery.setDesc(attraction.getAttractionStopInfo(false).getTeaser()[0]);

                AttractionTabsActivity.galleryItems.put(index++, gallery);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final RecyclerView rv = (RecyclerView) inflater.inflate(R.layout.tour_list_recycler, container, false);

        TourViewModel mTourViewModel = ViewModelProviders.of(this).get(TourViewModel.class);
        mTourViewModel.getTour(getArguments().getInt(CatalogueActivity.CATALOGUE_INDEX)).observe(this, new Observer<TourCatalogueItem>() {
            @Override
            public void onChanged(@Nullable final TourCatalogueItem tour) {
                if (tour != null) {
                    getImageURLs(tour.getAttractions());
                    setupRecyclerView(rv);
                }
            }
        });

        rv.setNestedScrollingEnabled(true);

        rv.addOnScrollListener(onScrollListener);
        return rv;
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if(dy > 0)
                scrollListener.onScrollView(true);
            else
                scrollListener.onScrollView(false);
        }
    };

    private void setupRecyclerView(RecyclerView recyclerView) {
        MyCustomLayoutManager layoutManager = new MyCustomLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        Bundle args = getArguments();

        if(Commons.isNotNull(args)) {
            AttractionGalleryViewerAdapter adapter = new AttractionGalleryViewerAdapter();
            recyclerView.setAdapter(adapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.setVerticalScrollBarEnabled(false);
            recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerClickListener() {
                @Override
                public void onClick(View v, int pos) {

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
                public void onLongClick(View view, int position) { /* DO NOTHING */ }

            }));
        }
    }
}

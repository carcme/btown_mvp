package me.carc.btown.tours.top_pick_lists.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.common.interfaces.OnItemSelectedListener;
import me.carc.btown.common.interfaces.ToursScrollListener;
import me.carc.btown.data.all4squ.FourSquResult;
import me.carc.btown.data.all4squ.FourSquareApi;
import me.carc.btown.data.all4squ.FourSquareServiceProvider;
import me.carc.btown.data.all4squ.entities.GroupsPhoto;
import me.carc.btown.data.all4squ.entities.Photo;
import me.carc.btown.data.all4squ.entities.Photos;
import me.carc.btown.extras.messaging.viewholders.PhotoViewHolder;
import me.carc.btown.map.sheets.ImageDialog;
import me.carc.btown.tours.top_pick_lists.VenueTabsActivity;
import me.carc.btown.tours.top_pick_lists.adapters.VenuePhotoAdapter;
import me.carc.btown.ui.GridMarginDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VenuePhotosFragment extends Fragment {
    private static final String TAG = C.DEBUG + Commons.getTag();

    public static final int PHOTO_LIMIT = 30;

    private int PHOTO_OFFSET = 0;

    private VenuePhotoAdapter mAdapter;


    @BindView(R.id.venuePhotosNestedScrollView)
    NestedScrollView venuePhotosNestedScrollView;

    @BindView(R.id.photoLoadMore)
    Button photoLoadMoreBtn;

    @BindView(R.id.venuePhotoGrid)
    RecyclerView venuePhotoGrid;

    @BindView(R.id.emptyPhotos)
    TextView emptyPhotos;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    ToursScrollListener scrollListener;

    private NestedScrollView.OnScrollChangeListener onScrollListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (scrollY - oldScrollY > 0)
                scrollListener.onScrollView(true);
            else if (scrollY - oldScrollY < 0)
                scrollListener.onScrollView(false);
        }
    };

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);

        try {
            scrollListener = (ToursScrollListener) ctx;
        } catch (ClassCastException e) {
            throw new ClassCastException(ctx.toString() + " must implement TourListener callbacks");
        }
    }

    @Override
    public void onDetach() {
        scrollListener = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View v = inflater.inflate(R.layout.venue_fragment_photos, container, false);
        ButterKnife.bind(this, v);

        setup();

        venuePhotosNestedScrollView.setOnScrollChangeListener(onScrollListener);

        return v;
    }

    private void setup() {
        Bundle args = getArguments();

        if (Commons.isNotNull(args)) {
            setupRecyclerView();
            new SetupMore(args).run();
//            populateGrid(args);
        }
    }

    private void setupRecyclerView() {
        Context ctx = getActivity();

        GridLayoutManager layoutManager = new GridLayoutManager(ctx, ctx.getResources().getInteger(R.integer.venue_photo_grid_columns));
        venuePhotoGrid.setLayoutManager(layoutManager);
        venuePhotoGrid.addItemDecoration(new GridMarginDecoration(getResources().getDimensionPixelSize(R.dimen.grid_item_spacing_none)));
        venuePhotoGrid.setHasFixedSize(false);
        venuePhotoGrid.setNestedScrollingEnabled(false);
    }


    private class SetupMore implements Runnable {

        Bundle args;

        SetupMore(Bundle args) {
            this.args = args;
        }

        @Override
        public void run() {
            populateMore();
        }

        private void populateMore() {
            populateGrid(args);
        }
    }

    private void populateGrid(Bundle args) {

        ArrayList<GroupsPhoto> groups = args.getParcelableArrayList(VenueTabsActivity.EXTRA_PHOTOS);
        ArrayList<Photo> photos = new ArrayList<>();

        if (groups.size() > 1)
            Log.d(TAG, "populateGrid: Check this - have more than one group");


        for (GroupsPhoto grp : groups) {
            if (grp.getType().equals("venue")) {
                photos = grp.getItemsPhotos();
                if(grp.getCount() == photos.size())
                    photoLoadMoreBtn.setVisibility(View.GONE);
                else
                    photoLoadMoreBtn.setText(String.format(getString(R.string.load_more_photos), grp.getCount()));
                photoLoadMoreBtn.setTag(grp.getCount());
            }
        }

        PHOTO_OFFSET = photos.size();

        if(PHOTO_OFFSET == 0) emptyPhotos.setVisibility(View.VISIBLE);

        mAdapter = new VenuePhotoAdapter(photos);
        venuePhotoGrid.setAdapter(mAdapter);

        venuePhotoGrid.addOnItemTouchListener(new OnItemSelectedListener(getActivity()) {

            public void onItemSelected(RecyclerView.ViewHolder holder, int position) {

                if (!(holder instanceof PhotoViewHolder)) {
                    return;
                }

                Photo photo = mAdapter.getPhoto(position);
                String image = photo.getPrefix() + "original" + photo.getSuffix();

                ImageDialog.showInstance(getActivity().getApplicationContext(),
                        image,
                        photo.getSource().getSrcUrl(),
                        photo.getUser().getFullName(),
                        Commons.readableDate(photo.getCreatedAt() * 1000L));
            }
        });
        progressBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.photoLoadMore)
    void loadMorePhotos() {
        Bundle args = getArguments();

        if (Commons.isNotNull(args)) {
            progressBar.setVisibility(View.VISIBLE);
            String venueId = args.getString(VenueTabsActivity.EXTRA_VENUE_ID);

            FourSquareApi service = FourSquareServiceProvider.get();
            Call<FourSquResult> listsCall = service.getVenueMorePhotos(venueId, PHOTO_LIMIT, PHOTO_OFFSET);

            listsCall.enqueue(new Callback<FourSquResult>() {
                @SuppressWarnings({"ConstantConditions"})
                @Override
                public void onResponse(@NonNull Call<FourSquResult> call, @NonNull Response<FourSquResult> response) {

                    FourSquResult body = response.body();

                    try {
                        Photos photos = body.getResponse().getPhotos();

                        mAdapter.updatePhotoList(photos.getPhotos());
                        PHOTO_OFFSET = mAdapter.getItemCount();

                        if(PHOTO_OFFSET >= (int) photoLoadMoreBtn.getTag())
                            photoLoadMoreBtn.setVisibility(View.GONE);
                        else {
                            int remainingImageCount = (int) photoLoadMoreBtn.getTag() - PHOTO_OFFSET;
                            photoLoadMoreBtn.setText(String.format(getString(R.string.load_more_photos), remainingImageCount));
                        }
                    } catch (Exception e) {
                        Commons.Toast(getActivity(), R.string.operation_error, Color.RED, Toast.LENGTH_SHORT);
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Commons.Toast(getActivity(), R.string.operation_error, Color.RED, Toast.LENGTH_SHORT);
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }
}
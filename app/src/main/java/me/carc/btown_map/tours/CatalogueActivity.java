package me.carc.btown_map.tours;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown_map.BaseActivity;
import me.carc.btown_map.BuildConfig;
import me.carc.btown_map.R;
import me.carc.btown_map.Utils.Holder;
import me.carc.btown_map.common.C;
import me.carc.btown_map.common.Commons;
import me.carc.btown_map.common.TinyDB;
import me.carc.btown_map.common.interfaces.DrawableClickListener;
import me.carc.btown_map.tours.adapters.ToursAdapter;
import me.carc.btown_map.tours.data.FirebaseService;
import me.carc.btown_map.tours.model.TourCatalogue;
import me.carc.btown_map.tours.model.TourHolderResult;
import me.carc.btown_map.ui.custom.MyCustomLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CatalogueActivity extends BaseActivity {

    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final String SERVER_FILE = "SERVER_FILE";
    public static final String JSON_VERSION= "JSON_VERSION";



    private ToursAdapter mAdapter;

    @BindView(R.id.catalogue_recycler)
    RecyclerView recyclerView;


    @BindView(R.id.toursToolbar)
    Toolbar toolbar;

    @BindView(R.id.inventoryProgressLayout)
    RelativeLayout progressLayout;

    @BindView(R.id.appBarProgressBar)
    ProgressBar appBarProgressBar;



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tours_catalogue_activity);
        ButterKnife.bind(this);

        // set up UI and collections
        setupUI(savedInstanceState);

        // Display the collections
        getJsonCollections();
    }

    private void setupUI(Bundle savedInstanceState) {

        toolbar.setTitle(R.string.tours_tour_catalogue);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // Set the padding to match the Status Bar height
        if (C.HAS_L)
            toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
    }

    private void getJsonCollections() {

        setProgressItems(View.VISIBLE);

        final Gson gson = new Gson();
        String json = TinyDB.getTinyDB().getString(SERVER_FILE);
        TourHolderResult serverFile = gson.fromJson(json, TourHolderResult.class);

        if(Commons.isNotNull(serverFile)){
            ArrayList<TourCatalogue> tours = serverFile.tours;
            setupRecycler(tours);
        } else {

            FirebaseService service = new Retrofit.Builder()
                    .baseUrl(BuildConfig.FIREBASE_ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(FirebaseService.class);

            Call<TourHolderResult> call = service.getTours(BuildConfig.FIREBASE_ALT, BuildConfig.FIREBASE_TOKEN);

            call.enqueue(new Callback<TourHolderResult>() {
                @Override
                public void onResponse(Call<TourHolderResult> call, Response<TourHolderResult> response) {


                    ArrayList<TourCatalogue> tours = response.body().tours;
//                    TinyDB.getTinyDB().putInt(JSON_VERSION, response.body().version);
//
//                    String json = gson.toJson(response.body());
//                    TinyDB.getTinyDB().putString(SERVER_FILE, json);

                    setupRecycler(tours);
                }

                @Override
                public void onFailure(Call<TourHolderResult> call, Throwable t) {
                    Log.d(TAG, "onResponse: ");
                    setProgressItems(View.GONE);
                }
            });
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupRecycler(ArrayList<TourCatalogue> tours) {

        mAdapter = new ToursAdapter(tours, new DrawableClickListener() {

            @Override
            public void OnClick(View v, Drawable drawable, int pos) {
                TourCatalogue catalogue = mAdapter.getItem(pos);
                Holder.set(drawable);
                Intent intent = new Intent(CatalogueActivity.this, CataloguePreviewActivity.class);
                intent.putExtra("SELECTED_CATALOGUE", catalogue);

                Bundle options = null;
                if (C.HAS_L)
                    options = ActivityOptions.makeSceneTransitionAnimation(
                            CatalogueActivity.this,
                            v,
                            getString(R.string.image_pop_transition)
                    ).toBundle();
                startActivity(intent, options);
            }

            @Override
            public void OnLongClick(View v, int pos) {
            }
        });

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
                recyclerView.setLayoutManager(gridLayoutManager);
            } else {
                MyCustomLayoutManager layoutManager = new MyCustomLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
            }
            recyclerView.setAdapter(mAdapter);
        }

        setProgressItems(View.GONE);
    }

    private void setProgressItems(int vis) {
        appBarProgressBar.setVisibility(vis);
        progressLayout.setVisibility(vis);
    }

    /**
     * Handle Intent returns
     *
     * @param requestCode the request code
     * @param resultCode  result code
     * @param data        the intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
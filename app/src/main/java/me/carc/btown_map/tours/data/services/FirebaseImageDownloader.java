package me.carc.btown_map.tours.data.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

import me.carc.btown_map.BuildConfig;
import me.carc.btown_map.Utils.FileUtils;
import me.carc.btown_map.common.C;
import me.carc.btown_map.common.CacheDir;
import me.carc.btown_map.common.Commons;
import me.carc.btown_map.common.TinyDB;
import me.carc.btown_map.tours.CatalogueActivity;
import me.carc.btown_map.tours.data.FirebaseService;
import me.carc.btown_map.tours.model.Attraction;
import me.carc.btown_map.tours.model.TourCatalogue;
import me.carc.btown_map.tours.model.TourHolderResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Download the images in the background
 * Created by bamptonm on 22/10/2017.
 */

public class FirebaseImageDownloader extends IntentService {

    private static final String TAG = C.DEBUG + Commons.getTag();

    private static boolean updateInProgress;
    private String dir;
    private StorageReference mStorageRef;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FirebaseImageDownloader() {
        super("FirebaseImageDownloader");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if(!updateInProgress) {
            updateInProgress = true;
            Boolean forceUpdate = intent.getBooleanExtra("FORCE_UPDATE", false);
            initValues();
            getLatestJsonTours(forceUpdate);
            updateInProgress = false;
        }
    }

    private void initValues() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        dir = CacheDir.getCacheDir().cacheDirAsStr();
    }

    private void getLatestJsonTours(final boolean force) {

/*
        if(C.DEBUG_ENABLED)
            return;
*/

        FirebaseService service = new Retrofit.Builder()
                .baseUrl(BuildConfig.FIREBASE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FirebaseService.class);

        Call<TourHolderResult> call = service.getTours(BuildConfig.FIREBASE_ALT, BuildConfig.FIREBASE_TOKEN);

        call.enqueue(new Callback<TourHolderResult>() {
            @Override
            public void onResponse(@NonNull Call<TourHolderResult> call, @NonNull final Response<TourHolderResult> response) {

                int version = TinyDB.getTinyDB().getInt(CatalogueActivity.JSON_VERSION, 0);

                if (version < response.body().version || force) {

                    final Gson gson = new Gson();
                    TinyDB.getTinyDB().putInt(CatalogueActivity.JSON_VERSION, response.body().version);

                    String json = gson.toJson(response.body());
                    TinyDB.getTinyDB().putString(CatalogueActivity.SERVER_FILE, json);
                }
                // check we have the images - download any that are missing
                getImages(response.body());
            }

            @Override
            public void onFailure(Call<TourHolderResult> call, Throwable t) {
                Log.d(TAG, "onResponse: ");
            }
        });
    }


    private void getImages(TourHolderResult holder) {

        ArrayList<TourCatalogue> catalogues = holder.tours;

        initValues();

        for (TourCatalogue catalogue : catalogues) {
            getImage("coverImages", catalogue.getCatalogueImage());
            for (Attraction attraction : catalogue.getAttractions()) {
                getImage("coverImages", attraction.getImage());
            }
        }
    }

    /**
     * Get the image
     */
    private boolean getImage(String directory, String filename) {
        try {
            // Is the bitmap in our cache?
            if (!FileUtils.checkValidFilePath(dir + File.separator + filename)) {
                Log.d(TAG, "getImage: FIREBASE:: " + filename);
                getFirebaseImage(directory, filename);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /**
     * Moved storage to Firebase - get the images from there
     */
    private void getFirebaseImage(final String fromWhere, final String image) {

        StorageReference imageRef = mStorageRef.child(fromWhere + "/" + image);

        File localFile = new File(CacheDir.getCacheDirAsFile(), image);

        imageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure: " + image + "::" + exception.getMessage());
            }
        });
    }
}

package me.carc.btown.tours.data;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import me.carc.btown.BuildConfig;
import me.carc.btown.Utils.FileUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.CacheDir;
import me.carc.btown.common.Commons;
import me.carc.btown.common.TinyDB;
import me.carc.btown.data.ToursDataClass;
import me.carc.btown.tours.CatalogueActivity;
import me.carc.btown.tours.model.Attraction;
import me.carc.btown.tours.model.TourCatalogue;
import me.carc.btown.tours.model.TourHolderResult;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Carc.me on 03.05.16.
 * <p/>
 * Download images to cache. Resize them depending on screen size
 */


public class GetFirebaseData {

    private static final String TAG = C.DEBUG + Commons.getTag();

    private String dir;
    private StorageReference mStorageRef;


    public GetFirebaseData() {
        getLatestJsonTours();
    }


    private void initValues() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        dir = CacheDir.getInstance().cacheDirAsStr();
    }


    private void getLatestJsonTours() {

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
            @SuppressWarnings({"ConstantConditions"})
            public void onResponse(@NonNull Call<TourHolderResult> call, @NonNull final Response<TourHolderResult> response) {

                int version = TinyDB.getTinyDB().getInt(CatalogueActivity.JSON_VERSION, 0);

                if (version < response.body().version) {

                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            final Gson gson = new Gson();
                            TinyDB.getTinyDB().putInt(CatalogueActivity.JSON_VERSION, response.body().version);

                            String json = gson.toJson(response.body());
                            if (Commons.isNotNull(json)) {
                                TinyDB.getTinyDB().putString(CatalogueActivity.SERVER_FILE, json);
                                ToursDataClass.getInstance().setTourResult(gson.fromJson(json, TourHolderResult.class));
                                new AsyncGetImagesEvent().execute(response.body());
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<TourHolderResult> call, @NonNull Throwable t) {
                Log.d(TAG, "onResponse: ");
            }
        });
    }

    private class AsyncGetImagesEvent extends AsyncTask<TourHolderResult, Integer, Boolean> {


        @Override
        protected Boolean doInBackground(TourHolderResult... params) {

            TourHolderResult holder = params[0];
            ArrayList<TourCatalogue> catalogues = holder.tours;

            initValues();

            for (TourCatalogue catalogue : catalogues) {
                getImage("coverImages", catalogue.getCatalogueImage());
                for (Attraction attraction : catalogue.getAttractions()) {
                    getImage("coverImages", attraction.getImage());
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean res) {
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

        File localFile = new File(CacheDir.getInstance().getCacheDirAsFile(), image);

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

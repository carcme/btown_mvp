package me.carc.btown.tours.data.services;

import android.app.IntentService;
import android.app.Notification;
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
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;

import me.carc.btown.App;
import me.carc.btown.BuildConfig;
import me.carc.btown.Utils.FileUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.CacheDir;
import me.carc.btown.common.Commons;
import me.carc.btown.common.TinyDB;
import me.carc.btown.data.ToursDataClass;
import me.carc.btown.db.tours.AttractionDao;
import me.carc.btown.db.tours.TourCatalogueDao;
import me.carc.btown.db.tours.model.Attraction;
import me.carc.btown.db.tours.model.TourCatalogueItem;
import me.carc.btown.db.tours.model.ToursResponse;
import me.carc.btown.tours.CatalogueActivity;
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

    private String dir;
    private StorageReference mStorageRef;
    private TinyDB db;

    private boolean updateInProgress() {
        return ((App) getApplicationContext()).isUpdatingFirebase();
    }

    private void setUpdateInProgress(boolean update) {
        ((App) getApplicationContext()).setUpdatingFirebase(update);
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public FirebaseImageDownloader() {
        super("FirebaseImageDownloader");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(1, new Notification());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (!updateInProgress()) {
            setUpdateInProgress(true);
            Boolean forceUpdate = false;
            if(intent != null)
                forceUpdate = intent.getBooleanExtra("FORCE_UPDATE", false);
            initValues();
            getLatestJsonTours(forceUpdate);
        }
    }

    private void initValues() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        dir = CacheDir.getInstance().cacheDirAsStr();
        db = TinyDB.getTinyDB();    // ensure TinyDB is init'd
    }



    private void getLatestJsonTours(final boolean force) {
        FirebaseService service = new Retrofit.Builder()
                .baseUrl(BuildConfig.FIREBASE_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(FirebaseService.class);

        Call<ToursResponse> call = service.getTours(BuildConfig.FIREBASE_ALT, BuildConfig.FIREBASE_TOKEN);

        call.enqueue(new Callback<ToursResponse>() {
            @Override
            public void onResponse(@NonNull Call<ToursResponse> call, @NonNull final Response<ToursResponse> response) {

                int version = db.getInt(CatalogueActivity.JSON_VERSION, 0);

                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {

                        if ((response.body() != null) && (response.body().version > 0)) {
                            TourCatalogueDao tourDao = ((App) getApplicationContext()).getDB().catalogueDao();
                            AttractionDao attrDao = ((App) getApplicationContext()).getDB().attractionDao();
                            for (TourCatalogueItem item : response.body().tours) {
                                tourDao.insert(item);

                                for (Attraction attr : item.getAttractions()) {
                                    attrDao.insert(attr);
                                }
                            }
                        }
                    }
                });

                if (version < response.body().version || force) {

                    final Gson gson = new Gson();
                    db.putInt(CatalogueActivity.JSON_VERSION, response.body().version);

                    String json = gson.toJson(response.body());
                    db.putString(CatalogueActivity.SERVER_FILE, json);
                    ToursDataClass.getInstance().setTourResult(gson.fromJson(json, ToursResponse.class));
                }
                // check we have the images - download any that are missing
                getImages(response.body());

                setUpdateInProgress(false);
            }

            @Override
            public void onFailure(@Nonnull Call<ToursResponse> call, @Nonnull Throwable t) {
                Log.d(TAG, "onResponse: ");
                setUpdateInProgress(false);
            }
        });
    }


    private void getImages(ToursResponse holder) {
        ArrayList<TourCatalogueItem> catalogues = holder.tours;
        initValues();
        for (TourCatalogueItem catalogue : catalogues) {
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
        File localFile = new File(CacheDir.getInstance().getCacheDirAsFile(), image);
        imageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: " + taskSnapshot.toString());

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "onFailure: " + image + "::" + exception.getMessage());
            }
        });
    }
}

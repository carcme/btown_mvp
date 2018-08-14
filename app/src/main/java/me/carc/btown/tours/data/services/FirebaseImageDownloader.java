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

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import javax.annotation.Nonnull;

import me.carc.btown.App;
import me.carc.btown.BuildConfig;
import me.carc.btown.Utils.FileUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.CacheDir;
import me.carc.btown.common.TinyDB;
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

    private static final String TAG = FirebaseImageDownloader.class.getName();
    public static final String FORCE_UPDATE = "FORCE_UPDATE";

    private String dir;
    private StorageReference mStorageRef;
    private TinyDB db;

    private boolean hasNetworkConnection() {
        return ((App) getApplicationContext()).isNetworkAvailable();
    }

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
            Boolean forceUpdate = false;
            if (intent != null)
                forceUpdate = intent.getBooleanExtra(FORCE_UPDATE, false);
            initValues();
            getLatestJsonTours(checkForUpdates(forceUpdate));
        }
    }

    private void initValues() {
        mStorageRef = FirebaseStorage.getInstance().getReference();
        dir = CacheDir.getInstance().cacheDirAsStr();
        db = TinyDB.getTinyDB();    // ensure TinyDB is init'd
    }

    private boolean checkForUpdates(boolean forceUpdate) {
        boolean update = false;
        if (hasNetworkConnection()) {
            if(forceUpdate)
                update = true;

            // Check if upgrading to database - tours still stored in shared prefs
//             db.putString(CatalogueActivity.SERVER_FILE, "SIMULATE SHARED PREFS - REMOVE THIS LINE");
            boolean upgradeToDB = forceUpdate || !db.getString(CatalogueActivity.SERVER_FILE, "").equals("");
            if (upgradeToDB) {
                db.remove(CatalogueActivity.LAST_JSON_UPDATE);
                db.remove(CatalogueActivity.JSON_VERSION);
                update = true;
            }

            long updateTime = db.getLong(CatalogueActivity.LAST_JSON_UPDATE, 0);
            if ((System.currentTimeMillis() - updateTime) > C.TIME_ONE_WEEK)  // todo: make one week configurable from Firebase
                update = true;

            setUpdateInProgress(update);
            return update;
        }
        return update;
    }

    private void getLatestJsonTours(final boolean shouldConnect) {
        if(shouldConnect) {
            FirebaseService service = new Retrofit.Builder()
                    .baseUrl(BuildConfig.FIREBASE_ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(FirebaseService.class);

            Call<ToursResponse> call = service.getTours(BuildConfig.FIREBASE_ALT, BuildConfig.FIREBASE_TOKEN);
            call.enqueue(new Callback<ToursResponse>() {
                @Override
                public void onResponse(@NonNull Call<ToursResponse> call, @NonNull final Response<ToursResponse> response) {

                    Executors.newSingleThreadExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            int version = db.getInt(CatalogueActivity.JSON_VERSION, 0);

                            if ((response.body() != null) && (response.body().version > version)) {
                                TourCatalogueDao tourDao = ((App) getApplicationContext()).getDB().catalogueDao();
                                AttractionDao attrDao = ((App) getApplicationContext()).getDB().attractionDao();
                                for (TourCatalogueItem item : response.body().tours) {
                                    tourDao.insert(item);

                                    for (Attraction attr : item.getAttractions()) {
                                        attrDao.insert(attr);
                                    }
                                }

                                db.putLong(CatalogueActivity.LAST_JSON_UPDATE, System.currentTimeMillis());
                                db.putInt(CatalogueActivity.JSON_VERSION, response.body().version);
                                db.remove(CatalogueActivity.SERVER_FILE);
                            }

                            // check we have the images - download any that are missing
                            getImages(response.body());

                            setUpdateInProgress(false);
                        }
                    });
                }

                @Override
                public void onFailure(@Nonnull Call<ToursResponse> call, @Nonnull Throwable t) {
                    Log.d(TAG, "onResponse: ");
                    setUpdateInProgress(false);
                }
            });
        }
    }

    private void getImages(ToursResponse holder) {
        ArrayList<TourCatalogueItem> catalogues = holder.tours;
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
    private void getImage(String directory, String filename) {
        try {
            // Is the bitmap in our cache?
            if (!FileUtils.checkValidFilePath(dir + File.separator + filename)) {
                Log.d(TAG, "getImage: FIREBASE:: " + filename);
                getFirebaseImage(directory, filename);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

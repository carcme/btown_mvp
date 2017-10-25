package me.carc.btown.extras;

import android.content.DialogInterface;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.BuildConfig;
import com.crashlytics.android.answers.RatingEvent;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.FileUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.CacheDir;
import me.carc.btown.common.Commons;

public class PublicTransportPlan extends BaseActivity implements SubsamplingScaleImageView.OnImageEventListener {

    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final String ANSWERS_PLAN_OPEN= "U_S_BAHN_PLAN_OPENED";

    public static final String FIREBASE_DIR = "resource";
    public static final String FIREBASE_FILE = "berlin_transport_map.png";


    @BindView(R.id.transportMapView)
    SubsamplingScaleImageView imageView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_transport_plan_activity);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.menu_train_route_map));
        }

        showProgressDialog();

        String planPath = CacheDir.getCacheDir().cacheDirAsStr() + "/" + FIREBASE_FILE;
        if (FileUtils.checkValidFilePath(planPath)) {
            imageView.setImage(ImageSource.uri(planPath));

        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.alert_title_pt_route_map)
                    .setIcon(R.drawable.ic_download)
                    .setCancelable(false)
                    .setMessage(R.string.alert_msg_pt_route_map)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

            // get the tranport plan
            downloadFirebaseImage(FIREBASE_DIR, FIREBASE_FILE);
        }
        imageView.setOnImageEventListener(this);


        //// TODO: 24/10/2017 add  click or touch listener to catch clicks on map... show info on each stop
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(BuildConfig.DEBUG)
                    Log.d(TAG, "onClick: ");
            }
        });
    }

    /**
     * Moved storage to Firebase - get the images from there
     */
    private void downloadFirebaseImage(final String fromWhere, final String image) {

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = mStorageRef.child(fromWhere + "/" + image);

        final File localFile = new File(CacheDir.getCacheDirAsFile(), image);
        imageRef.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        imageView.setImage(ImageSource.uri(localFile.toString()));

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                showError("download from Firebase", exception);
                Log.d(TAG, "onFailure: " + image + "::" + exception.getMessage());
            }
        });
    }

    @Override
    public void onReady() {
    }

    @Override
    public void onImageLoaded() {

        imageView.setDebug(C.DEBUG_ENABLED);
        imageView.setScaleAndCenter(1f, new PointF(imageView.getSWidth() / 2, imageView.getSHeight() / 2));
        imageView.setMinScale(0.6f);
        imageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);

        hideProgressDialog();

        Answers.getInstance().logRating(new RatingEvent().putCustomAttribute(ANSWERS_PLAN_OPEN, ANSWERS_PLAN_OPEN));
    }

    private void showError(String source, Exception e) {
        new AlertDialog.Builder(this)
                .setTitle("Error in " + source)
                .setMessage(e != null ? e.getLocalizedMessage() :getString(R.string.network_not_available_error))
                .show();
    }

    @Override
    public void onPreviewLoadError(Exception e) {
    }

    @Override
    public void onImageLoadError(Exception e) {
    }

    @Override
    public void onTileLoadError(Exception e) {
    }

    @Override
    public void onPreviewReleased() {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}

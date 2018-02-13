package me.carc.btown.extras;

import android.content.DialogInterface;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.crashlytics.android.answers.Answers;
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
import butterknife.OnClick;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.Utils.FileUtils;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.C;
import me.carc.btown.common.CacheDir;
import me.carc.btown.common.Commons;
import me.carc.btown.settings.SendFeedback;

public class PublicTransportPlan extends BaseActivity implements SubsamplingScaleImageView.OnImageEventListener {

    private static final String TAG = C.DEBUG + Commons.getTag();
    public static final String ANSWERS_PLAN_OPEN = "U_S_BAHN_PLAN_OPENED";

    public static final String FIREBASE_DIR = "resource";


    @BindView(R.id.transportMapView)
    SubsamplingScaleImageView imageView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fabPlansExit)
    FloatingActionButton fabPlansExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.public_transport_plan_activity);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        showProgressDialog();

        String firebaseFile = "";
        String title = "";

        if (getIntent().hasExtra("MAP_ID")) {
            firebaseFile = getIntent().getStringExtra("MAP_ID");
            title = getString(getIntent().getIntExtra("MAP_DESC", R.string.menu_train_route_map));
            getSupportActionBar().setTitle(title);
        }

        // is file already downloaded and saved?
        String planPath = CacheDir.getCacheDir().cacheDirAsStr() + "/" + firebaseFile;
        if (FileUtils.checkValidFilePath(planPath)) {
            imageView.setImage(ImageSource.uri(planPath));
            imageView.setOnImageEventListener(this);
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.alert_title_pt_route_map)
                    .setIcon(R.drawable.ic_download)
                    .setCancelable(false)
                    .setMessage(String.format(getString(R.string.alert_msg_pt_route_map), title))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

            // get the tranport plan
            downloadFirebaseImage(FIREBASE_DIR, firebaseFile);
        }

        final Animation a = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        a.setDuration(getResources().getInteger(R.integer.gallery_alpha_duration) * 2);
        fabPlansExit.setAnimation(a);

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
                        imageView.setOnImageEventListener(PublicTransportPlan.this);

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

        imageView.setScaleAndCenter(1f, new PointF(imageView.getSWidth() / 2, imageView.getSHeight() / 2));
        imageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        imageView.setMinScale(0.6f);
        imageView.setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);

        hideProgressDialog();

        if (me.carc.btown.BuildConfig.USE_CRASHLYTICS)
            Answers.getInstance().logRating(new RatingEvent().putCustomAttribute(ANSWERS_PLAN_OPEN, ANSWERS_PLAN_OPEN));
    }

    private void showError(String source, Exception e) {
        new AlertDialog.Builder(this)
                .setTitle("Error in " + source)
                .setMessage(e != null ? e.getLocalizedMessage() : getString(R.string.network_not_available_error))
                .show();
    }

    @Override
    public void onPreviewLoadError(Exception e) {
        Log.d(TAG, "onPreviewLoadError: " + e.getLocalizedMessage());
    }

    @Override
    public void onImageLoadError(Exception e) {
        Log.d(TAG, "onImageLoadError: " + e.getLocalizedMessage());
    }

    @Override
    public void onTileLoadError(Exception e) {
        Log.d(TAG, "onTileLoadError: " + e.getLocalizedMessage());
    }

    @Override
    public void onPreviewReleased() {
        Log.d(TAG, "onPreviewReleased: ");
    }


    @OnClick(R.id.fabPlansExit)
    void fabBack() {
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_plans, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.menu_feedback:
                new SendFeedback(this, SendFeedback.TYPE_FEEDBACK);
                break;

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        float temp = getResources().getDimension(R.dimen.fab_margin);
        int duration = getResources().getInteger(R.integer.gallery_alpha_duration);

        ViewUtils.hideView(fabPlansExit, duration, (int) temp);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, duration * 2);
    }
}

package me.carc.btownmvp.camera;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import me.carc.btownmvp.R;
import me.carc.btownmvp.common.C;
import me.carc.btownmvp.common.Commons;

/**
 * CameraActivity - simple android activity, no presenter required (uses Android intents)
 * Created by bamptonm on 17/08/2017.
 */

public class CameraActivity extends AppCompatActivity  {

    private static final String TAG = C.DEBUG + Commons.getTag();

    private final int RESULT_CAMERA_PREVIEW = 102;
    public static final int RESULT_LAUNCH_CAMERA = 6000;
//    public static final String EXTRA_CAMERA_LOCATION = "EXTRA_CAMERA_LOCATION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showAlert();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, C.PERMISSION_CAMERA);
            }

        } else {

            launchCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case C.PERMISSION_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    launchCamera();

                } else {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                        showAlert();
                    }
                }
            }
        }
    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.shared_string_share);
        alertDialog.setMessage(getString(R.string.rationale_camera));

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(CameraActivity.this,
                                new String[]{Manifest.permission.CAMERA},
                                C.PERMISSION_CAMERA);
                    }
                });
        alertDialog.show();
    }

    /**
     * Launch the android camera
     */
    private void launchCamera() {

        String storageState = Environment.getExternalStorageState();

        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            String filename = Commons.simpleDate() + "_" + ".jpg";

            ContentValues values = new ContentValues();

            values.put(MediaStore.Images.Media.TITLE, filename);
            Uri mImageCaptureUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values);

            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);


            try {
                intent.putExtra("LOCATION", "CAN_I_SEE_THIS");
                startActivityForResult(intent, RESULT_LAUNCH_CAMERA);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            new android.app.AlertDialog.Builder(CameraActivity.this)
                    .setMessage(
                            "External Storeage (SD Card) is required.\n\nCurrent state: "
                                    + storageState)
                    .setCancelable(true).create().show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {

            case RESULT_LAUNCH_CAMERA:
                finish();
/*
                if (resultCode == RESULT_OK) {
                    Log.d(TAG, "onActivityResult: ");

                    TinyDB db = TinyDB.getTinyDB();
                    GeoPoint location = new GeoPoint(
                            db.getDouble(C.MAP_CENTER_LAT, MapPresenter.BERLIN_LAT),
                            db.getDouble(C.MAP_CENTER_LNG, MapPresenter.BERLIN_LNG));

                    Intent i = new Intent(CameraActivity.this, CameraPreviewActivity.class);
                    i.putExtra(EXTRA_CAMERA_LOCATION, (Parcelable) location);
                    startActivityForResult(i, RESULT_CAMERA_PREVIEW);

                } else
                    finish();
*/
                break;

            case RESULT_CAMERA_PREVIEW:
                finish();
                break;
        }
    }
}
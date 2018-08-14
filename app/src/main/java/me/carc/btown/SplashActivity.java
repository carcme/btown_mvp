package me.carc.btown;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

import me.carc.btown.common.C;
import me.carc.btown.ui.front_page.FrontPageActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by Carc.me on 03.08.16.
 * <p/>
 * Display a splash screen at start up
 */
public class SplashActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = SplashActivity.class.getName();

    private boolean hasStorage = false;
    private boolean hasLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(C.HAS_L) {
            checkStoragePermission();
        } else {
            hasStorage = true;
            hasLocation = true;
        }
    }

    private void launchNextActivity() {
        if(hasStorage && hasLocation /*&& hasUser && attemptedLogin*/) {
            // have permissions, get the tours and images
            ((App)getApplicationContext()).getFirebaseTours(false);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    //Create an intent that will start the main activity.
//                    Intent intent = new Intent(SplashActivity.this, MapActivity.class);
                    Intent intent = new Intent(SplashActivity.this, FrontPageActivity.class);

                    SplashActivity.this.startActivity(intent);

                    //Finish splash activity so user cant go back to it.
                    SplashActivity.this.finish();

                    //Apply splash exit (fade out) and main entry (fade in) animation transitions.
                    overridePendingTransition(R.anim.main_fade_in, R.anim.splash_fade_out);
                }
            }, 100);
        } else if(!hasStorage){
            checkStoragePermission();
        } else {
            checkLocationPermssion();
        }
    }

    @AfterPermissionGranted(C.PERMISSION_STORAGE)
    public void checkStoragePermission() {
        if(!hasStorage) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                hasStorage = true;
                launchNextActivity();
            } else {
                // Ask for permission
                EasyPermissions.requestPermissions(SplashActivity.this, getString(R.string.rationale_storage),
                        C.PERMISSION_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @AfterPermissionGranted(C.PERMISSION_LOCATION)
    public void checkLocationPermssion() {
        if(!hasLocation) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                hasLocation = true;
                launchNextActivity();
            } else {
                // Ask for permission
                EasyPermissions.requestPermissions(SplashActivity.this, getString(R.string.rationale_storage),
                        C.PERMISSION_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(requestCode == C.PERMISSION_STORAGE)
            hasStorage = true;
        else if(requestCode == C.PERMISSION_LOCATION)
            hasLocation = true;

        launchNextActivity();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        new AlertDialog.Builder(SplashActivity.this)
                .setMessage(R.string.permission_rejected_msg)
                .setPositiveButton(R.string.text_close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}

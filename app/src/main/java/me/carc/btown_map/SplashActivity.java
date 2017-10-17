package me.carc.btown_map;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import java.util.List;

import me.carc.btown_map.common.C;
import me.carc.btown_map.common.Commons;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * Created by Carc.me on 03.08.16.
 * <p/>
 * Display a splash screen at start upTODO: Add a class header comment!
 */
public class SplashActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    private static final String TAG = C.DEBUG + Commons.getTag();

//    private final int SPLASH_DISPLAY_LENGTH = 500;

/*    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
*/

    private boolean bugFixFlag = true;
    private boolean hasStorage = false;
    private boolean hasLocation = false;
    private boolean hasUser = false;
    private boolean attemptedLogin = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Facebook
//        facebookSDKInitialize();

/*        auth = FirebaseAuth.get();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                Profile facebookUser = Profile.getCurrentProfile();

                // bug fix - gets called twice for some reason
                if(bugFixFlag) {
                    if (firebaseUser != null || facebookUser != null) {
                        hasUser = true;
                    }
                    attemptedLogin = true;
                    launchNextActivity();
                    bugFixFlag = false;
                }
            }
        };*/

        if(C.HAS_L) {
            storage();
            location();
        } else {
            hasStorage = true;
            hasLocation = true;
        }


//         /* Start activity and close this Splash-Screen after x milliSeconds */
//        new Handler().postDelayed(new Runnable(){
//            @Override
//            public void run() {
//                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
//                intent.putExtra(C.EXTRA_STARTUP_LOGIN, true);
//                startActivity(intent);
//                finish();
//            }
//        }, SPLASH_DISPLAY_LENGTH);
    }

    private void launchNextActivity() {
        if(hasStorage && hasLocation /*&& hasUser && attemptedLogin*/) {
            Intent intent = new Intent(SplashActivity.this, MapActivity.class);
//            intent.putExtra(C.EXTRA_STARTUP_LOGIN, true);
            finish();
            startActivity(intent);
/*
        } else if(hasStorage && hasLocation && !hasUser && attemptedLogin) {
            Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
            intent.putExtra(C.EXTRA_STARTUP_LOGIN, true);
            finish();
            startActivity(intent);
*/
        }
    }

    @AfterPermissionGranted(C.PERMISSION_STORAGE)
    public void storage() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            hasStorage = true;
            launchNextActivity();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(SplashActivity.this, getString(R.string.rationale_storage),
                    C.PERMISSION_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @AfterPermissionGranted(C.PERMISSION_LOCATION)
    public void location() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            hasLocation = true;
            launchNextActivity();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(SplashActivity.this, getString(R.string.rationale_storage),
                    C.PERMISSION_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
//        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
/*
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
*/
    }
}

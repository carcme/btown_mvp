package me.carc.btown.login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;

import java.util.Arrays;

import me.carc.btown.App;
import me.carc.btown.BuildConfig;
import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;


/** Make sure user is logged in to access teh tours
 * Created by bamptonm on 31/08/2017.
 */
class LoginPresenter implements IfLogin.Presenter, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = C.DEBUG + Commons.getTag();

    private IfLogin.View view;
    private final Context mContext;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private GoogleApiClient mGoogleApiClient;


    LoginPresenter(Context context, IfLogin.View view) {
        this.view = view;
        this.mContext = context;
        view.setPresenter(this);
    }

    private AppCompatActivity getActivity() {
        if(((App) mContext.getApplicationContext()).getCurrentActivity() != null)
            return ((App) mContext.getApplicationContext()).getCurrentActivity();
        else
            return (LoginActivity)mContext;
    }

    @Override
    public void start() {
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // [START auth_state_listener]
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                updateUI(firebaseAuth.getCurrentUser());
            }
        };

        if(Commons.isNotNull(auth)) {
            updateUI(auth.getCurrentUser());
        }

        getGoogleApiClient();
    }

    @Override
    public void stop() {
        if (Commons.isNotNull(mGoogleApiClient)) {
            mGoogleApiClient.disconnect();
        }
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }

    }

    private GoogleApiClient getGoogleApiClient() {

        if (Commons.isNull(mGoogleApiClient)) {
            String id = mContext.getString(R.string.default_web_client_id);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(BuildConfig.GOOGLE_SERVER_KEY)
                    .requestServerAuthCode(id)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                    .enableAutoManage((LoginActivity)mContext, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            mGoogleApiClient.connect();
        }
        return mGoogleApiClient;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            if (user.getProviders() != null && user.getProviders().size() > 0) {
                for (String provider : user.getProviders()) {
                    if (provider.equalsIgnoreCase(GoogleAuthProvider.PROVIDER_ID)) {
                        view.setButtonText(LoginActivity.GOOGLE_BUTTON, R.string.sign_out);
                    } else if (provider.equalsIgnoreCase(FacebookAuthProvider.PROVIDER_ID)) {
                        view.setButtonText(LoginActivity.FACEBOOK_BUTTON, R.string.sign_out);
                    } else if (provider.equalsIgnoreCase(TwitterAuthProvider.PROVIDER_ID)) {
                        // add twitter at some point maybe?
                        Log.d(TAG, "updateUI: TODO: Add Twitter login ");
                    }
                }
            }

            if (Profile.getCurrentProfile() != null) {
                view.setButtonText(LoginActivity.FACEBOOK_BUTTON, R.string.sign_out);
            }
        }
    }

    private boolean checkNetworkAvailable() {
        if (!Commons.isNetworkAvailable(mContext)) {
            view.showNetworkNotAvailableError();
            return false;
        }
        return true;
    }

    public void onGoogleLogin() {
        if (Commons.isNotNull(auth.getCurrentUser())) {
            auth.signOut();
            view.setButtonText(LoginActivity.GOOGLE_BUTTON, R.string.sign_in_with);
        }
        else if (checkNetworkAvailable()) {
            if (Commons.isNotNull(mGoogleApiClient)) {
                view.showLoginProgress();
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                view.signInGoogle(signInIntent);
            } else
                Log.d(TAG, "onGoogleLogin: ");
        }
    }

    public void onFacebookGoogleLogin() {
        if (Commons.isNotNull(Profile.getCurrentProfile())) {
            LoginManager.getInstance().logOut();
            view.setButtonText(LoginActivity.FACEBOOK_BUTTON, R.string.sign_in_with);
        } else  if (checkNetworkAvailable()) {
            LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile", "user_friends", "email"));
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        view.showNetworkNotAvailableError();
    }

    @Override
    public void handleFacebookAccessToken(AccessToken token) {

        if (token != null) {
            view.showLoginProgress();

            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            auth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> res) {

                    if (!res.isSuccessful()) {
                        view.showLoginError(res.getException().getMessage());
                        Log.w(TAG, "signInWithCredential", res.getException());
                    } else {
                        view.setButtonText(LoginActivity.FACEBOOK_BUTTON, R.string.sign_out);
                        view.onFinishActivity();
                    }
                }
            });
        } else {
            //Logged out of Facebook so do a logout from the Firebase app
            auth.signOut();
        }
    }


    @Override
    public void processGoogleSignin(Intent data) {
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            int code = result.getStatus().getStatusCode();
            if (CommonStatusCodes.NETWORK_ERROR == code) {
                new AlertDialog.Builder(mContext)
                        .setMessage(R.string.network_not_available_error)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            } else if (CommonStatusCodes.DEVELOPER_ERROR == code) {
                new AlertDialog.Builder(mContext)
                        .setMessage("Developer Error: Keystore has changed! Please contact developer")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
            updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        view.showLoginProgress();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful())
                    view.showLoginError(mContext.getString(R.string.shared_string_error));
                else
                    view.onFinishActivity();
            }
        });
    }
}

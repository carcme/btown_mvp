package me.carc.btown.login;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.shaishavgandhi.loginbuttons.FacebookButton;
import com.shaishavgandhi.loginbuttons.GooglePlusButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.BaseActivity;
import me.carc.btown.R;
import me.carc.btown.common.C;
import me.carc.btown.common.Commons;
import me.carc.btown.map.sheets.wiki.WikiWebViewActivity;


public class LoginActivity extends BaseActivity implements IfLogin.View {

    private static final String TAG = C.DEBUG + Commons.getTag();
    private static final int RC_SIGN_IN = 9001;

    private static final String SHOWN_RATIONAL = "SHOWN_RATIONAL";  // show reason for sign in

    public static final int GOOGLE_BUTTON = 1;
    public static final int FACEBOOK_BUTTON = 2;

    private IfLogin.Presenter presenter;
    private CallbackManager callbackManager;

    @BindView(R.id.googleBtn)
    GooglePlusButton googleBtn;

    @BindView(R.id.facebookBtn)
    FacebookButton facebookBtn;

    @BindView(R.id.exitLogin)
    ImageView exitLogin;


    @Override
    public void setPresenter(IfLogin.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.signin_activity);
        ButterKnife.bind(this);

        presenter = new LoginPresenter(this, this);

        googleBtn.setText(String.format(getString(R.string.sign_in_with), getString(R.string.google)));

        Profile fbProfile = Profile.getCurrentProfile();
        facebookBtn.setText(fbProfile == null ? String.format(getString(R.string.sign_in_with), getString(R.string.facebook)) : getString(R.string.sign_out));
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                presenter.handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.start();

        if(!db.getBoolean(SHOWN_RATIONAL)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.signin_rational_title)
                    .setIcon(R.drawable.ic_person_outline)
                    .setCancelable(false)
                    .setMessage(R.string.signin_rational_msg)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.putBoolean(SHOWN_RATIONAL, true);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.privacy_policy, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showPrivacyPolicy();
                        }
                    })
                    .show();
        }

    }

    @OnClick(R.id.privacyPolicy)
    public void showPrivacyPolicy() {
        Intent intent = new Intent(LoginActivity.this, WikiWebViewActivity.class);
        intent.putExtra(WikiWebViewActivity.WIKI_EXTRA_PAGE_URL, getString(R.string.privacy_policy_link));
        startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.stop();
    }

    @Override
    public void showLoginProgress() {
        showProgressDialog();
    }

    @Override
    public void onFinishActivity() {
        finish();
    }

    @Override
    public void showLoginSuccess() {
        hideProgressDialog();
    }

    @Override
    public void showLoginError(String message) {
        showProgressDialog("Error", "Something went wrong!");
    }

    @Override
    public void showNetworkNotAvailableError() {
        Toast.makeText(this, getString(R.string.network_not_available_error), Toast.LENGTH_SHORT).show();
     }

    @Override
    public void setButtonText(int id, @StringRes int string) {
        switch (id) {
            case GOOGLE_BUTTON:
                if(string == R.string.sign_in_with)
                    googleBtn.setText(String.format(getString(R.string.sign_in_with), getString(R.string.google)));
                else
                    googleBtn.setText(getString(string));
                break;

            case FACEBOOK_BUTTON:
                if(string == R.string.sign_in_with)
                    facebookBtn.setText(String.format(getString(R.string.sign_in_with), getString(R.string.facebook)));
                else
                    facebookBtn.setText(getString(string));
                break;

            default:
                throw new RuntimeException("Unhandled Button");
        }
    }

    @OnClick(R.id.googleBtn)
    void googleLogin(){
        presenter.onGoogleLogin();
    }

    @OnClick(R.id.facebookBtn)
    void facebookLogin(){
        presenter.onFacebookGoogleLogin();
    }

    @OnClick(R.id.exitLogin)
    void exitLogin(){ finish(); }

    @Override
    public void signInGoogle(Intent signInIntent) {
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            presenter.processGoogleSignin(data);
        }
    }


}

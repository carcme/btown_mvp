package me.carc.btown_map.login;

import android.content.Intent;
import android.support.annotation.StringRes;

import com.facebook.AccessToken;

import me.carc.btown_map.BasePresenter;
import me.carc.btown_map.BaseView;


/**
 * Created by nawin on 6/13/17.
 */

interface IfLogin {

    interface View extends BaseView<Presenter> {
        void showLoginProgress();
        void showLoginSuccess();
        void showLoginError(String message);

        void signInGoogle(Intent signInIntent);

        void showNetworkNotAvailableError();
        void setButtonText(int id, @StringRes int string);

        void onFinishActivity();
    }

    interface Presenter extends BasePresenter {
        void onGoogleLogin();
        void onFacebookGoogleLogin();

        void processGoogleSignin(Intent data);
        void handleFacebookAccessToken(AccessToken token);

    }
}

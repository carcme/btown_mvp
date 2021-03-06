package me.carc.btown.login;

import android.content.Intent;
import android.support.annotation.StringRes;


import me.carc.btown.BasePresenter;
import me.carc.btown.BaseView;


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
        void processGoogleSignin(Intent data);
    }
}

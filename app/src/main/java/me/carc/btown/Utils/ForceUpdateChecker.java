package me.carc.btown.Utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import me.carc.btown.BuildConfig;
import me.carc.btown.common.C;

/**
 * Created by bamptonm on 19/07/2018.
 */

public class ForceUpdateChecker {

    private static final String TAG = ForceUpdateChecker.class.getSimpleName();

    public static final String KEY_UPDATE_REQUIRED       = "force_update_req";
    public static final String KEY_CURRENT_VERSION       = "force_update_current_version";
    public static final String KEY_VERSION_CODE          = "force_update_version_code";
    public static final String KEY_UPDATE_DESCRIPTION_EN = "force_update_desc_en";
    public static final String KEY_UPDATE_DESCRIPTION_DE = "force_update_desc_de";

    private OnUpdateNeededListener onUpdateNeededListener;
    private Context context;

    public interface OnUpdateNeededListener {
        void onUpdateNeeded(String updateDesc);
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    public ForceUpdateChecker(@NonNull Context context, OnUpdateNeededListener onUpdateListener) {
        this.context = context;
        this.onUpdateNeededListener = onUpdateListener;
    }

    public void check() {
        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        remoteConfig.setConfigSettings(configSettings);

        remoteConfig.fetch().addOnCompleteListener(new OnCompleteListener<Void>() {  // for DEBUGGING use fetch(timeout) ** timeout in seconds **
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    remoteConfig.activateFetched();
                    if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {

                        long valRemoteVersion = remoteConfig.getLong(KEY_VERSION_CODE);

                        String updateDescEN = remoteConfig.getString(KEY_UPDATE_DESCRIPTION_EN);
                        String updateDescDE = remoteConfig.getString(KEY_UPDATE_DESCRIPTION_DE);

                        if (valRemoteVersion > BuildConfig.VERSION_CODE && onUpdateNeededListener != null) {
                            onUpdateNeededListener.onUpdateNeeded(C.USER_LANGUAGE.equalsIgnoreCase("en") ? updateDescEN : updateDescDE);
                        }
                    }
                }
            }
        });
    }

    public static class Builder {
        private Context context;
        private OnUpdateNeededListener onUpdateNeededListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateNeeded(OnUpdateNeededListener onUpdateNeededListener) {
            this.onUpdateNeededListener = onUpdateNeededListener;
            return this;
        }

        public ForceUpdateChecker build() {
            return new ForceUpdateChecker(context, onUpdateNeededListener);
        }

        public ForceUpdateChecker check() {
            ForceUpdateChecker forceUpdateChecker = build();
            forceUpdateChecker.check();
            return forceUpdateChecker;
        }
    }
}

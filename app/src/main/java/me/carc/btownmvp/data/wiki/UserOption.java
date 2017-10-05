package me.carc.btownmvp.data.wiki;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by bamptonm on 25/09/2017.
 */
public class UserOption {
    @NonNull
    private final String key;
    @Nullable
    private final String val;

    public UserOption(@NonNull String key) {
        this(key, null);
    }

    public UserOption(@NonNull UserOption option) {
        this(option.key(), option.val());
    }

    public UserOption(@NonNull String key, @Nullable String val) {
        this.key = key;
        this.val = val;
    }

    @NonNull public String key() {
        return key;
    }

    @Nullable public String val() {
        return val;
    }
}

package me.carc.btown_map.Utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

/**
 * Created by bamptonm on 26/09/2017.
 */
public final class FragmentUtil {
    @Nullable
    public static <T> T getCallback(@NonNull Fragment fragment, @NonNull Class<T> callback) {
        if (callback.isInstance(fragment.getTargetFragment())) {
            //noinspection unchecked
            return (T) fragment.getTargetFragment();
        }
        if (callback.isInstance(fragment.getParentFragment())) {
            //noinspection unchecked
            return (T) fragment.getParentFragment();
        }
        if (callback.isInstance(fragment.getActivity())) {
            //noinspection unchecked
            return (T) fragment.getActivity();
        }
        return null;
    }

    private FragmentUtil() { }

}

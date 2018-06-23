package me.carc.btown.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import me.carc.btown.R;

public class Preferences {

    private final static String PREFS = "btownPrefs";
    public final static String GPS = "pref_key_use_gps";
    public final static String THEME = "pref_key_theme";
    public final static String TOURS = "pref_key_tours";
    public final static String LANGUAGE = "pref_key_language";
    private final static String EXIT_ON_BACK = "pref_key_exit_app_on_back_press";


    public static boolean darkThemeEnabled(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(THEME, context.getString(R.string.pref_theme_value_light)).equals(context.getString(R.string.pref_theme_value_dark));
    }

    public static Boolean alwaysUseGps(Context context) {
        SharedPreferences gps = PreferenceManager.getDefaultSharedPreferences(context);
        return gps.getBoolean(GPS, true);
    }

    public static String getLanguage(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(LANGUAGE, context.getString(R.string.pref_language_value_default));
    }

    public static boolean showTours(Context context) {
/*        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(TOURS, true);
*/
        return false;
    }

    public static boolean exitOnBack(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getBoolean(EXIT_ON_BACK, false);
    }


    public static boolean getPrefBoolean(Context context, String pref, boolean defValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return settings.getBoolean(pref, defValue);
    }

    public static void setPrefBoolean(Context context, String pref, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(pref, value);
        editor.apply();
    }

}

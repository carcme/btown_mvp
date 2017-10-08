package me.carc.btownmvp.common;

import android.os.Build;

import me.carc.btownmvp.BuildConfig;

/**
 * Holder for various constants
 * Created by bamptonm on 19/09/2017.
 */

public class C {
    public static final boolean DEBUG_ENABLED = BuildConfig.DEBUG;
    public static final String DEBUG = "DEAD:";

    public static final boolean HAS_K = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    public static final boolean HAS_L = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    public static final boolean HAS_M = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    public static final boolean HAS_N = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;

    // PERMISSION REQUESTS
    public static final int PERMISSION_LOCATION = 200;
    public static final int PERMISSION_CALL_PHONE = 201;
    public static final int PERMISSION_CAMERA     = 202;
    public static final int PERMISSION_STORAGE = 203;

    // NETWORK CHANGE STATES
    public static final String CONNECT_TO_WIFI = "WIFI";
    public static final String CONNECT_TO_MOBILE = "MOBILE";
    public static final String NOT_CONNECT = "NOT_CONNECT";
    final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

    public static final int TYPE_NC     = 0;
    public static final int TYPE_WIFI   = 1;
    public static final int TYPE_MOBILE = 2;


    public static String USER_LANGUAGE;

    public static final String DATA_DIR = "data/BTown";

    public static final String INTENT_MARKET = "market://details?id=";

    public static final String SAVED_FAVORITES_LIST = "SAVED_FAVORITES_LIST";
    public static final String SAVED_FAVORITES_ID_LIST = "SAVED_FAVORITES_ID_LIST";


    public final static String PREF_ROUTING_VEHICLE = "PREF_ROUTING_VEHICLE";


    public final static String MAP_ZOOM_LEVEL = "MAP_ZOOM_LEVEL";
    public final static String MAP_CENTER_LAT = "MAP_CENTER_LAT";
    public final static String MAP_CENTER_LNG = "MAP_CENTER_LNG";

    public final static String LAST_ZOOM_LEVEL = "LAST_ZOOM_LEVEL";
    public final static String LAST_CENTER_LAT = "LAST_CENTER_LAT";
    public final static String LAST_CENTER_LNG = "LAST_CENTER_LNG";



    public static Integer IMAGE_WIDTH;
    public static Integer IMAGE_HEIGHT;
    public static Integer SCREEN_WIDTH;
    public static Integer SCREEN_HEIGHT;


    public static final int TIME_ONE_SECOND = 1000;
    public static final int TIME_ONE_MINUTE = 60 * TIME_ONE_SECOND;
    public static final int TIME_ONE_HOUR = TIME_ONE_MINUTE * 60;
    public static final int TIME_ONE_DAY = TIME_ONE_HOUR * 24;
    public static final int TIME_ONE_WEEK = TIME_ONE_DAY * 7;

}

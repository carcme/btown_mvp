package me.carc.btownmvp.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.appcompat.BuildConfig;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Common functions used throughout the app
 * Created by nawin on 6/13/17.
 */

public class Commons {

    public static String getTag() {
        String tag = "";
        if (BuildConfig.DEBUG) {
            final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
            for (int i = 0; i < ste.length; i++) {
                if (ste[i].getMethodName().equals("getTag")) {
                    tag = "(" + ste[i + 1].getFileName() + ":" + ste[i + 1].getLineNumber() + ")";
                }
            }
        }
        return tag;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && (networkInfo.isConnected());
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return "";
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static String simpleDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        return sdf.format(new Date());
    }

    public static String readableDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy", Locale.getDefault());
        if(timestamp != 0) {
            return  sdf.format(new Date(timestamp));
        }
        return sdf.format(new Date());
    }


    public static boolean isEmpty(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isEmpty(Collection c) {
        return c == null || c.size() == 0;
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.size() == 0;
    }

    public static boolean isEmpty(String[] s) {
        return s == null || s[0].isEmpty();
    }

    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    public static Field extractFieldByString(Class<?> classToInspect, String findThis) {
        Field[] allFields = classToInspect.getDeclaredFields();

        for (Field field : allFields) {
            if (field.getType().isAssignableFrom(String.class)) {
                if (field.getName().equals(findThis))
                    return field;
            }
        }
        return null;
    }

}

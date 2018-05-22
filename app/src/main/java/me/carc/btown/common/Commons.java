package me.carc.btown.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import me.carc.btown.R;

/**
 * Common functions used throughout the app
 * Created by nawin on 6/13/17.
 */

public class Commons {

/*
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
*/

    @SuppressFBWarnings("NM_METHOD_NAMING_CONVENTION")
    public static void Toast(Context context, @StringRes int resId, @ColorInt int bgColor, int duration) {

        final View v = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.custom_toast, null);

        TextView tv = v.findViewById(R.id.toastMsg);
        tv.setText(resId);
        tv.setTextColor(Color.WHITE);

        Drawable background = context.getDrawable(R.drawable.toast_background);
        background.setTint(bgColor);
        tv.setBackground(background);

        final Toast toast = new Toast(context);
        toast.setDuration(duration);
        toast.setView(v);
        toast.show();
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

    public static String buildStringFromArray(String[] arr) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i != arr.length - 1)
                sb.append("\n\n");
        }
        return sb.toString();
    }

    public static String buildStringFromArray(List<String> arr) {
        int size = arr.size();
        int loop = 1;
        StringBuilder sb = new StringBuilder();
        for(String str : arr) {
            sb.append(str);
            if(loop != size)
                sb.append("\n\n");
            loop++;
        }
        return sb.toString();
    }

    public static String getString(Context ctx, int strID) {
        return ctx.getApplicationContext().getString(strID);
    }

    /**
     * Replace text, igmore case
     * @param text the string
     * @param pattern the text to replace
     * @return
     */
    public static String replace(String text, String pattern) {
        return replace(text, pattern, null);
    }

    /**
     * Replace text, igmore case
     * @param text the string
     * @param pattern the text to replace
     * @param newWord what to replace with
     * @return
     */
    public static String replace(String text, String pattern, String newWord) {
        if(text == null || pattern == null)
            return text;
        return Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(text).replaceAll(isNull(newWord) ? "" : newWord);
    }

    public static boolean contains(String s1, String s2) {
        if(s1 == null || s2 == null)
            return false;
        return Pattern.compile(Pattern.quote(s2), Pattern.CASE_INSENSITIVE).matcher(s1).find();
    }

    public static boolean equals(String s1, String s2) {
        return !(s1 == null || s2 == null) && Pattern.compile(s1).matcher(s2).matches();
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

    public static boolean startsWith(String s, String ch) {
        return !isEmpty(s) && s.startsWith(ch);
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

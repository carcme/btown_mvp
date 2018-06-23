package me.carc.btown.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by bamptonm on 13/11/2017.
 */

public class SocialMediaUtils {

    /**
     *  Check if Facebook app is installed
     */
    public static Intent getFacebookPageIntent(Context ctx, String pageId) {
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);

        try {
            int versionCode = ctx.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
//            boolean activated =  packageManager.getApplicationInfo("com.facebook.katana", 0).enabled;
            if (versionCode >= 3002850) //newer versions of fb app
                facebookIntent.setData(Uri.parse("fb://facewebmodal/f?href=" + "https://www.facebook.com/" + pageId));
            else  //older versions of fb app
                facebookIntent.setData(Uri.parse("fb://page/" + pageId));

            return facebookIntent;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     *  Check if Twitter app is installed
     */
    public static Intent getTwitterIntent(Context ctx, String userID) {
        Intent twitterIntent = null;
        try {
            // is Twitter app installed?
            ctx.getPackageManager().getPackageInfo("com.twitter.android", 0);

            if (userID.startsWith("http"))
                userID = userID.substring(userID.lastIndexOf("/") + 1);
            else if (userID.contains("@"))
                userID = userID.replace("@", "");

            twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + userID));
            twitterIntent.setPackage("com.twitter.android");
            twitterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            /* EMPTY - App not installed */
        }
        return twitterIntent;
    }

    /**
     *  Check if Instagram app is installed
     */
    public static Intent getInstagramIntent(Context ctx, String url) {
        Intent instagramIntent = new Intent(Intent.ACTION_VIEW);

        try {
            ctx.getPackageManager().getPackageInfo("com.instagram.android", 0);

            if (url.endsWith("/"))
                url = url.substring(0, url.length() - 1);
            if (url.endsWith("/"))
                url = url.substring(0, url.length() - 1);

            final String username = url.substring(url.lastIndexOf("/") + 1);
            // http://stackoverflow.com/questions/21505941/intent-to-open-instagram-user-profile-on-android
            instagramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/" + username));
            instagramIntent.setPackage("com.instagram.android");

            return instagramIntent;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}

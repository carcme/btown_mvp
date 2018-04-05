package me.carc.btown.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by bamptonm on 13/11/2017.
 */

public class SocialMediaUtils {


    //method to get the right URL to use in the intent
    public static Intent getFacebookPageIntent(Context context, String pageId) {
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        PackageManager packageManager = context.getPackageManager();

        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
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

    //method to get the right URL to use in the intent
    public static Intent getInstagramIntent(Context context, String pageId) {
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        PackageManager packageManager = context.getPackageManager();

        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) //newer versions of fb app
                facebookIntent.setData(Uri.parse("fb://facewebmodal/f?href=" + "https://www.facebook.com/" + pageId));
            else  //older versions of fb app
                facebookIntent.setData(Uri.parse("fb://page/" + pageId));

            return facebookIntent;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}

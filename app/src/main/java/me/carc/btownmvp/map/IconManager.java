package me.carc.btownmvp.map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import me.carc.btownmvp.R;
import me.carc.btownmvp.Utils.AndroidUtils;
import me.carc.btownmvp.Utils.ImageUtils;
import me.carc.btownmvp.common.Commons;

/**
 * Created by bamptonm on 21/09/2017.
 */

public class IconManager {

    private final int DEFAULT_ICON = R.drawable.ic_place;
    private Resources res;
    private String packageName;

    public IconManager(Context context) {
        this.res = context.getResources();
        this.packageName = context.getPackageName();
    }


    public Drawable getRoundedIcon(String identifier) {
        try {
            Drawable icon;
            int iconRes = getIdentifier(identifier);
            if (iconRes == 0)
                icon = ResourcesCompat.getDrawable(res, DEFAULT_ICON, null);
            else
                icon = ResourcesCompat.getDrawable(res, iconRes, null);

            RoundedBitmapDrawable roundDrawable = null;
            if (Commons.isNotNull(icon)) {
                Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
                int size = AndroidUtils.getPixelsFromDPs(res, 22);
                Bitmap thumbnail = Bitmap.createScaledBitmap(bitmap, size, size, true);
                roundDrawable = RoundedBitmapDrawableFactory.create(res, thumbnail);
                roundDrawable.setCornerRadius(Math.max(thumbnail.getWidth(), thumbnail.getHeight()) / 2.0f);
            }
            return roundDrawable;
        } catch (Exception e) {
            return null;
        }
    }


/*
    public Drawable getRoundedIcon(String identifier) {
        return getRoundedIcon(getIdentifier(identifier));
    }
*/

    public Drawable getRoundedIcon(int iconRes){
        Drawable icon;
        try {
            if (iconRes == 0)
                icon = ResourcesCompat.getDrawable(res, DEFAULT_ICON, null);
            else
                icon = ResourcesCompat.getDrawable(res, iconRes, null);

            RoundedBitmapDrawable roundDrawable = null;
            if (Commons.isNotNull(icon)) {
                Bitmap bitmap = ((BitmapDrawable) icon).getBitmap();
                int size = AndroidUtils.getPixelsFromDPs(res, 22);
                Bitmap thumbnail = Bitmap.createScaledBitmap(bitmap, size, size, true);
                roundDrawable = RoundedBitmapDrawableFactory.create(res, thumbnail);
                roundDrawable.setCornerRadius(Math.max(thumbnail.getWidth(), thumbnail.getHeight()) / 2.0f);
            }
            return roundDrawable;

        } catch (Exception e){
            return null;
        }
    }


    public int getIdentifier(String id) {

        return res.getIdentifier(id, "raw", packageName);
    }

    private Drawable getDrawable(int id) {
        return ResourcesCompat.getDrawable(res, id, null);
    }

    public Drawable resizeDrawable(@NonNull int id) {
        try {
            Drawable drawable = ResourcesCompat.getDrawable(res, id, null);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return new BitmapDrawable(res, ImageUtils.resizeBitmap(res, bitmap, 50, 50));
        } catch (NullPointerException e) {
            return ResourcesCompat.getDrawable(res, R.drawable.ic_place, null);
        }
    }

    public Drawable resizeDrawable(@NonNull Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        return new BitmapDrawable(res, ImageUtils.resizeBitmap(res, bitmap, 50, 50));
    }
}

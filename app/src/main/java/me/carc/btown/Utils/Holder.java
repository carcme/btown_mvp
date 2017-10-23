package me.carc.btown.Utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class Holder {
    private static Drawable sDrawable;
    private static Bitmap sBitmap;

    private Holder() {
        throw new AssertionError();
    }

    public static synchronized void set(Drawable drawable) {
        sDrawable = drawable;
    }

    public static synchronized Drawable get() {
        return sDrawable;
    }

    public static synchronized void setBitmap(Bitmap bitmap) {
        sBitmap = bitmap;
    }

    public static synchronized Bitmap getBitmap() {
        return sBitmap;
    }
}

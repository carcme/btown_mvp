package me.carc.btown_map.tours.model;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.util.SparseArray;

/**
 * Created by bamptonm on 19/10/2017.
 */

public class ImageCache {

    private static ImageCache imageCache;
    public static SparseArray<Cache> CACHE;

    public class Cache {
        private Bitmap bitmap;
        private Palette.Swatch swatch;

        public Cache(Bitmap bitmap, Palette.Swatch swatch) {
            this.bitmap = bitmap;
            this.swatch = swatch;
        }

        public Bitmap getBitmap() { return bitmap; }
        public Palette.Swatch getSwatch() { return swatch;}
    }

    public ImageCache() {
        CACHE = new SparseArray<>(1);
        imageCache = this;
    }


    public static ImageCache getImageCache() {
        if(imageCache == null)
            new ImageCache();
        return imageCache;
    }

    public Cache getByIndex(int index) {
        return CACHE.get(index);
    }

    public void setCacheEntry(int index,Bitmap bitmap, Palette.Swatch swatch) {
        Cache cache = new Cache(bitmap, swatch);
        CACHE.put(index, cache);
    }


}

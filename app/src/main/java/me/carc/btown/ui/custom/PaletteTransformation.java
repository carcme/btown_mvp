package me.carc.btown.ui.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.graphics.Palette;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PaletteTransformation extends BitmapTransformation {
    private static PaletteTransformation INSTANCE;

    private static final Map<Bitmap, Palette> CACHE = new LinkedHashMap<>();

    public PaletteTransformation(Context context) {
        super(context);
        INSTANCE = this;
    }

    public static PaletteTransformation getInstance(Context context) {
        if(INSTANCE == null)
            new PaletteTransformation(context);
        return INSTANCE;
    }

    public static Palette getPalette(Bitmap bitmap) { return CACHE.get(bitmap); }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int width, int height) {

        Bitmap result = pool.get(width, height, Bitmap.Config.ARGB_8888);
        // If no matching Bitmap is in the pool, get will return null, so we should allocate.
        if (result == null) {
            // Use ARGB_8888 since we're going to add alpha to the image.
            result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        // Create a Canvas backed by the result Bitmap.
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setAlpha(128);
        // Draw the original Bitmap onto the result Bitmap with a transformation.
        canvas.drawBitmap(toTransform, 0, 0, paint);
        Palette palette = Palette.from(toTransform).generate();
        CACHE.put(toTransform, palette);

        // Since we've replaced our original Bitmap, we return our new Bitmap here. Glide will
        // will take care of returning our original Bitmap to the BitmapPool for us.

        return toTransform;
    }

    @Override
    public String getId() {
        return "me.carc.btowntoursmvp.ui.PaletteTransformation";
    }

}
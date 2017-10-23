package me.carc.btown.common;

import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * Created by Carc.me on 04.05.16.
 * <p/>
 *
 * Set the application cache directory
 */
public class CacheDir {

    public static CacheDir cacheDir;
    private static File cacheDirectory;

    public CacheDir() {

        // Find the dir to save cached images
        if(TinyDB.isExternalStorageReadable()) {
            File sdDir = android.os.Environment.getExternalStorageDirectory();
            cacheDirectory = new File(sdDir, C.DATA_DIR);
        }
        if (!cacheDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cacheDirectory.mkdirs();
        }
        cacheDir = this;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public CacheDir(Context ctx) {

        // Find the dir to save cached images
        if(TinyDB.isExternalStorageReadable()) {
            File sdDir = android.os.Environment.getExternalStorageDirectory();
            cacheDirectory = new File(sdDir, C.DATA_DIR);
        } else {
            // use cache or files directory??
            cacheDirectory = ctx.getFilesDir();
        }

        if (!cacheDirectory.exists()) {
            //noinspection ResultOfMethodCallIgnored
            cacheDirectory.mkdirs();
        }

        File file = new File(cacheDirectory, ".nomedia");
        try {if (!file.exists())  file.createNewFile(); }
        catch (IOException e) { e.printStackTrace(); }

        cacheDir = this;
    }

    public static CacheDir getCacheDir() {
        return cacheDir;
    }

    public static String getCachePath() {
        return cacheDir.cacheDirectory.getAbsolutePath() + File.separator;
    }

    public static File getCacheDirAsFile() { return cacheDirectory; }

    public String cacheDirAsStr() {
        return cacheDirectory.getAbsolutePath();
    }

    public File cacheDirAsFile() {
        return cacheDirectory;
    }
}

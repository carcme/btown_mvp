package me.carc.btownmvp.common;

import android.content.Context;

import java.io.File;

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
        cacheDir = this;
    }

    public static CacheDir getCacheDir() {
        return cacheDir;
    }

    public static File getCacheDirAsFile() { return cacheDirectory; }

    public String cacheDirAsStr() {
        return cacheDirectory.getAbsolutePath();
    }

    public File cacheDirAsFile() {
        return cacheDirectory;
    }
}

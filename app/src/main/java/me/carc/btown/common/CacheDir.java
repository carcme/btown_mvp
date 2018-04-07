package me.carc.btown.common;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import me.carc.btown.App;

/**
 * Created by Carc.me on 04.05.16.
 * <p/>
 *
 * Set the application cache directory
 */

public class CacheDir {

    private static final CacheDir INSTANCE = new CacheDir();
    private File cacheDirectory;

    public static CacheDir getInstance() {
        return INSTANCE;
    }

    private CacheDir() {
        init(App.getAC());
    }

    public void init(Context ctx) {

        // Find the dir to save cached images
        if(TinyDB.isExternalStorageWritable()) {
            File sdDir = android.os.Environment.getExternalStorageDirectory();
            cacheDirectory = new File(sdDir, C.DATA_DIR);
        } else {
            // use cache or files directory??
            cacheDirectory = ctx.getFilesDir();
        }

        if (!cacheDirectory.exists()) {
            if(!cacheDirectory.mkdirs())
                cacheDirectory = ctx.getCacheDir();
        }

        File file = new File(cacheDirectory, ".nomedia");
        try {
            if (!file.exists())
                file.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCachePath() {
        return getInstance().cacheDirectory.getAbsolutePath() + File.separator;
    }

    public File getCacheDirAsFile() { return cacheDirectory; }

    public String cacheDirAsStr() {
        return cacheDirectory.getAbsolutePath();
    }

    public File cacheDirAsFile() {
        return cacheDirectory;
    }
}

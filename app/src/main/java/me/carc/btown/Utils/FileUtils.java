package me.carc.btown.Utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Scanner;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Created by bamptonm on 19/10/2017.
 */

public class FileUtils {
    private static final String TAG = FileUtils.class.getName();

    /**
     * Is this a valid filename and path - does it exist?
     *
     * @param filename - full path
     * @return true is filename contains path
     */
    public static boolean checkValidFilePath(String filename) {
        if (filename != null) {
            File f = new File(filename);
            return f.exists() && !f.isDirectory();
        }
        return false;
    }

    /**
     * extract the filename from a path
     *
     * @param url
     * @return
     */
    public static String fileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1, url.length());
    }

    /**
     * Get the filename without the ext
     *
     * @param url
     * @return
     */
    public static String fileNameWithoutExtnFromUrl(String url) {
        return url.substring(0, url.lastIndexOf('.'));
    }

    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    @SuppressFBWarnings("REC_CATCH_EXCEPTION")  // using catch Exception which is a catch all (FindBugs doesn't like this)
    public static String getFileAsString(File file) {
        try {
            FileInputStream fin = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin, "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(line);
            }
            reader.close();
            fin.close();
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * read an input stream
     *
     * @param inputStream
     * @return
     */
    @SuppressFBWarnings("DM_DEFAULT_ENCODING")
    @SuppressWarnings("Unused")
    public static String readInputStream(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "File write failed: " + e.toString());
        }
        return outputStream.toString();
    }

    /**
     * http://stackoverflow.com/questions/10854211/android-store-inputstream-in-file
     *
     * @param in
     * @param file
     */

    public static void copyInputStreamToFile(InputStream in, File file) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
            } catch (NullPointerException | IOException e) { e.printStackTrace(); }
        }
    }

    /**
     * http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String readFile(File file)
            throws IOException {
        return new Scanner(file, "UTF-8").useDelimiter("\\A").next();
    }

    /**
     * http://developer.android.com/training/basics/data-storage/files.html
     *
     * @param albumName
     * @return
     */
    public static File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.i("wall-splash", "Directory not created");
        }
        return file;
    }

    /**
     * http://developer.android.com/training/basics/data-storage/files.html
     *
     * @return
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}

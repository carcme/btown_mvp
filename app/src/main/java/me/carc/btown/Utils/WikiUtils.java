package me.carc.btown.Utils;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bamptonm on 6/1/17.
 */

public class WikiUtils {
    private static final String URL_ENCODING = "UTF-8";

    /**
     * @param wikiLink  eg http://commons.wikimedia.org/wiki/File:Prinz-Albrecht.jpg
     * @param thumbSize size of thumbnail
     * @return eg https://upload.wikimedia.org/wikipedia/commons/thumb/4/4c/Prinz-Albrecht.jpg/181px-Prinz-Albrecht.jpg
     */
    public static String buildWikiCommonsLink(String wikiLink, int thumbSize) {

        if(wikiLink == null)
            return null;
        try {
            int fileIndex = wikiLink.indexOf("File:");
            int imageTagLen = "File:".length();

            // catch DE language
            if (fileIndex == -1) {
                fileIndex = wikiLink.indexOf("Datei:");
                imageTagLen = "Datei:".length();
            }

            if (fileIndex != -1) {
                String filename = wikiLink.substring(fileIndex + imageTagLen, wikiLink.length());
                if (filename.contains("?")) {
                    filename = filename.substring(0, filename.indexOf("?"));
                }

                filename = decodeFilename(filename.replace(" ", "_"));

                StringBuilder commonsUrl = new StringBuilder("https://upload.wikimedia.org/wikipedia/commons/");

                if (thumbSize > 0)
                    commonsUrl.append("thumb/");

                byte[] temp = new byte[1];
                md5(filename, temp);  // not interested in the return value

                String md2 = Integer.toHexString((temp[0] & 0xFF));
                char md1 = md2.charAt(0);

                commonsUrl.append(md1).append("/");
                commonsUrl.append(md2).append("/");
                commonsUrl.append(URLEncoder.encode(filename, URL_ENCODING));

                if (thumbSize > 0)
                    commonsUrl.append("/").append(thumbSize).append("px-").append(filename);

                return commonsUrl.toString();
            } else
                Log.d("TEST", "WIKI LINK BUILDER TEST: " + wikiLink);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return wikiLink;
    }


    public static String decodeFilename(String filename) {
        String decoded = "";

        try {
            decoded = URLDecoder.decode(filename, URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            decoded = e.getMessage();
        }

        return decoded;
    }


    /**
     * Format the osm returned wiki string
     */
    public static String createWikiLink(String osmWikiTag) {

        String ret = osmWikiTag;
        int index = osmWikiTag.indexOf(":") + 1;

        if (osmWikiTag.startsWith("de:")) {
            //https://de.wikipedia.org/wiki/Br%C3%B6han-Museum
            ret = "https://de.wikipedia.org/wiki/" + osmWikiTag.substring(index, osmWikiTag.length());

        } else if (osmWikiTag.startsWith("en:")) {
            //https://en.wikipedia.org/wiki/Br%C3%B6han_Museum
            ret = "https://en.wikipedia.org/wiki/" + osmWikiTag.substring(index, osmWikiTag.length());
        }
        return ret;
    }

    public static String removeWikiLinkCountry(String osmWikiTag) {

        String ret = osmWikiTag;
        int index = osmWikiTag.indexOf(":") + 1;
        String[] arr = osmWikiTag.split(":");

        return arr[1];
    }

    /**
     * http://stackoverflow.com/questions/3934331/android-how-to-encrypt-a-string
     *
     * @param str String to md5
     * @return
     */
    public static String md5(String str, byte[] temp) {
        try {
            byte[] msgBytes = str.getBytes("UTF-8");

            // Create MD5 Hash
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte messageDigest[] = md.digest(msgBytes);

            System.arraycopy(messageDigest, 0, temp, 0, temp.length);

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (byte aMessageDigest : messageDigest)
                hexString.append(Integer.toHexString(0xFF & aMessageDigest));

            return hexString.toString();

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}

package me.carc.btown_map.Utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bamptonm on 6/1/17.
 */

public class WikiUtils {

    /**
     * @param wikiLink  eg http://commons.wikimedia.org/wiki/File:Prinz-Albrecht.jpg
     * @param thumbSize size of thumbnail
     * @return eg https://upload.wikimedia.org/wikipedia/commons/thumb/4/4c/Prinz-Albrecht.jpg/181px-Prinz-Albrecht.jpg
     */
    public static String buildWikiCommonsLink(String wikiLink, int thumbSize) {
        int fileIndex = wikiLink.indexOf("File:");
        if(fileIndex != -1) {
            String filename = wikiLink.substring(fileIndex + "File:".length(), wikiLink.length());

            StringBuilder commonsUrl = new StringBuilder("https://upload.wikimedia.org/wikipedia/commons/");

            if (thumbSize > 0)
                commonsUrl.append("thumb/");

            byte[] temp = new byte[1];
            md5(filename, temp);  // not interested in the return value

            String md2 = Integer.toHexString((temp[0] & 0xFF));
            char md1 = md2.charAt(0);

            commonsUrl.append(md1).append("/");
            commonsUrl.append(md2).append("/");
            commonsUrl.append(filename);
            if (thumbSize > 0)
                commonsUrl.append("/").append(thumbSize).append("px-").append(filename);

            return commonsUrl.toString();
        }
        return wikiLink;
    }

    /**
     *  Format the osm returned wiki string
     */
    public static String createWikiLink(String osmWikiTag) {

        String ret = osmWikiTag;
        int index = osmWikiTag.indexOf(":") + 1;

        if(osmWikiTag.startsWith("de:")) {
            //https://de.wikipedia.org/wiki/Br%C3%B6han-Museum
            ret = "https://de.wikipedia.org/wiki/" + osmWikiTag.substring(index, osmWikiTag.length());

        } else if(osmWikiTag.startsWith("en:")) {
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

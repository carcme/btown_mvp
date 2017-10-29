package me.carc.btown;

import org.junit.Test;

import java.util.ArrayList;

import me.carc.btown.Utils.WikiUtils;

import static org.junit.Assert.assertEquals;

/**
 * Created by bamptonm on 27/10/2017.
 */

public class WikiLinkBuilderTest {
    private static final String URL_ENCODING = "UTF-8";

    @Test
    public void filenameDecode() throws Exception {
        String filename = "Stolperstein_Marschnerstr_38_%28Lichf%29_Kurt_Aron.jpg";
        String result = "Stolperstein_Marschnerstr_38_(Lichf)_Kurt_Aron.jpg";

        assertEquals(WikiUtils.decodeFilename(filename), result);
    }

    @Test
    public void md5Check1() throws Exception {

        StringBuilder testRes = new StringBuilder();
        String filename = "Stolperstein_Marschnerstr_38_%28Lichf%29_Kurt_Aron.jpg";

        String decoded = WikiUtils.decodeFilename(filename);

        byte[] temp = new byte[1];
        WikiUtils.md5(decoded, temp);  // not interested in the return value

        String md2 = Integer.toHexString((temp[0] & 0xFF));
        char md1 = md2.charAt(0);

        testRes.append(md1).append("/");
        testRes.append(md2).append("/");

        assertEquals(testRes.toString(), "2/2e/");
    }

    @Test
    public void wikiImageLinkBuilder() throws Exception {

        ArrayList<String> links = new ArrayList<>();
        ArrayList<String> results = new ArrayList<>();

        links.add("http://commons.wikimedia.org/wiki/File:Stolperstein_Marschnerstr_38_%28Lichf%29_Kurt_Aron.jpg");
        results.add("https://upload.wikimedia.org/wikipedia/commons/2/2e/Stolperstein_Marschnerstr_38_%28Lichf%29_Kurt_Aron.jpg");

        links.add("http://commons.wikimedia.org/wiki/File:Stolperstein_Hartmannstr_35_%28Lichtf%29_Edith_Braun.jpg");
        results.add("https://upload.wikimedia.org/wikipedia/commons/7/77/Stolperstein_Hartmannstr_35_%28Lichtf%29_Edith_Braun.jpg");

        links.add("http://commons.wikimedia.org/wiki/File:Stolperstein Hartmannstr 35 (Lichtf) Edith Braun.jpg");
        results.add("https://upload.wikimedia.org/wikipedia/commons/7/77/Stolperstein_Hartmannstr_35_%28Lichtf%29_Edith_Braun.jpg");

        links.add("https://de.wikipedia.org/wiki/Datei:Stolperstein_Holsteinische_Str_34_(Fried)_Regina_Lewitt.jpg?uselang=de");
        results.add("https://upload.wikimedia.org/wikipedia/commons/e/ee/Stolperstein_Holsteinische_Str_34_%28Fried%29_Regina_Lewitt.jpg");

        links.add("https://commons.wikimedia.org/wiki/File:Berlin,_Mitte,_Unter_den_Linden,_Reiterstandbild_Friedrich_II.jpg");
        results.add("https://upload.wikimedia.org/wikipedia/commons/6/65/Berlin%2C_Mitte%2C_Unter_den_Linden%2C_Reiterstandbild_Friedrich_II.jpg");

        links.add("https://commons.wikimedia.org/wiki/File:Fetchter_Memorial_040409.JPG");
        results.add("https://upload.wikimedia.org/wikipedia/commons/a/ab/Fetchter_Memorial_040409.JPG");


        for (int i = 0; i < links.size(); i++) {
            String ret = WikiUtils.buildWikiCommonsLink(links.get(i), 0);
            assertEquals(ret, results.get(i));
        }
    }
}

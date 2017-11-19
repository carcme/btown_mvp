package me.carc.btown.tours;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.carc.btown.R;
import me.carc.btown.common.Commons;

/**
 * Created by Carc.me on 22.09.16.
 * <p/>
 * TODO: Add a class header comment!
 */
public class GalleryItem implements Serializable {

    private String filename;
    private String url;
    private String cacheFile;
    private String title;
    private String desc;
    private Bitmap bm;
    private int cardColor;

    transient private ArrayList<Palette.Swatch> swatches = new ArrayList<>();
    transient private Palette.Swatch swatch;

    public Bitmap getBitmap() {
        return bm;
    }

    public void setBitmap(Bitmap bm) {
        this.bm = bm;
    }

    public boolean hasCachedFile() {
        return !Commons.isEmpty(cacheFile);
    }

    public String getUrl() {
        return url;
    }

    public File getCachedFile() {
        if (cacheFile != null)
            return new File(cacheFile);
        return null;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCachedFilePath(String path) {
        this.cacheFile = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public Palette.Swatch getSwatch() {
        return swatch;
    }

    public void setSwatch(Palette.Swatch s) {
  /*      if (this.colors == null)
            this.colors = new PaletteColours();
*/
        this.swatch = s;

/*        this.colors.setDominantColor(ImageUtils.getDominantColor(bm));
        this.colors.setRgb(s.getRgb());
        this.colors.setTitleTextColor(s.getTitleTextColor());
        this.colors.setBodyTextColor(s.getBodyTextColor());*/

    }

    public void setValidSwatchs(List<Palette.Swatch> swatchList) {
        for (int i = 0; i < swatchList.size(); i++) {
            Palette.Swatch temp = swatchList.get(i);
            if (temp != null)
                swatches.add(temp);
        }
    }

    public ArrayList<Palette.Swatch> getSwatches() {
        return swatches;
    }

    public int getCardColor() {
        return cardColor;
    }

    public void setCardColor(int c) {
        this.cardColor = c;
    }

    public int getButtonColor() {
        if (swatches.size() > 0) {
            Palette.Swatch s = swatches.get(0);
            if (s != null) {
                return s.getRgb();
            }
        }
        return R.color.colorAccent;
    }

    public int getButtonPressedColor() {
        if (swatches.size() > 0) {
            Palette.Swatch s = swatches.get(1);
            if (s != null) {
                return s.getRgb();
            }
        }
        return R.color.black;
    }


    public void setFilename(String filename) { this.filename = filename; }
    public String getFilename() { return filename; }

}

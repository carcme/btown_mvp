package me.carc.btown.tours;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.io.File;
import java.util.List;

import me.carc.btown.db.tours.model.Attraction;

/**
 * Created by bamptonm on 31/10/2017.
 */

public class RetainedFragment extends Fragment {

    public static final String ID_TAG = "RetainedFragment";

    private GalleryItem galleryItem;
    private int textColor;
    private int rgb;

    private Attraction attractionData;
    private File attractionImage;

    private List<Attraction> attractionList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void setGalleryItem(GalleryItem data) { this.galleryItem = data; }
    public GalleryItem getGalleryItem() { return galleryItem; }

    public int getTextColor() { return textColor; }
    public void setTextColor(int textColor) { this.textColor = textColor; }

    public int getRgb() { return rgb; }
    public void setRgb(int rgb) { this.rgb = rgb; }

    public Attraction getAttractionData() {
        return attractionData;
    }
    public void setAttractionData(Attraction attractionData) {
        this.attractionData = attractionData;
    }

    public File getAttractionImage() {
        return attractionImage;
    }
    public void setAttractionImage(File attractionImage) {
        this.attractionImage = attractionImage;
    }

    public List<Attraction> getAttractionList() { return attractionList; }
    public void setAttractionList(List<Attraction> list) { attractionList = list; }
}
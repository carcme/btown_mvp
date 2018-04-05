package me.carc.btown.extras.bahns;

import com.mikepenz.community_material_typeface_library.CommunityMaterial;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import me.carc.btown.BuildConfig;
import me.carc.btown.R;
import me.carc.btown.Utils.FileUtils;
import me.carc.btown.common.CacheDir;

/**
 * Created by bamptonm on 02/12/2017.
 */

public class Bahns {

    public static final String FIREBASE_UBAHN    = "berlin_transport_map.png";
    public static final String FIREBASE_TRAM     = "berlin-tram-map.jpg";
    public static final String FIREBASE_REGIONAL = "berlin_regional.jpg";
    public static final String FIREBASE_ZOO      = "berlIn_zoo_map.jpg";


    public enum BahnItem {
        US_BAHN(CommunityMaterial.Icon.cmd_package_down, R.string.usbahn_title, R.string.usbahn_desc, FIREBASE_UBAHN),
        TRAM(CommunityMaterial.Icon.cmd_package_down, R.string.tram_title, R.string.tram_desc, FIREBASE_TRAM),
        REGIONAL(CommunityMaterial.Icon.cmd_package_down, R.string.regional_title, R.string.regional_desc, FIREBASE_REGIONAL),
        ZOO(CommunityMaterial.Icon.cmd_package_down, R.string.zoo_title, R.string.zoo_desc, FIREBASE_ZOO);

        private CommunityMaterial.Icon iconResId;
        private int titleResId;
        private int descResId;
        private String imageUrl;
        private String filename;
        private boolean isDownloaded;

        BahnItem(CommunityMaterial.Icon iconResId, int titleResId, int descriptionResId, String filename) {
            this.iconResId = iconResId;
            this.titleResId = titleResId;
            this.descResId = descriptionResId;
            this.filename = filename;
            this.isDownloaded = hasLocalFile(filename);
        }

        public CommunityMaterial.Icon getIconResourceId() {
            return iconResId;
        }
        public int getTitleResourceId() {
            return titleResId;
        }
        public int getDescriptionResourceId() {
            return descResId;
        }
        public String getImageUrl() {
            return imageUrl;
        }
        public String getFileName() { return filename; }
        public boolean hasFile() { return isDownloaded; }


        public boolean hasLocalFile(String filename) {
            String planPath = CacheDir.getInstance().cacheDirAsStr() + "/" + filename;

            boolean haveFile = FileUtils.checkValidFilePath(planPath);
            imageUrl = haveFile ? planPath :  filename;

            return haveFile;
        }
    }

    public void removeCachedFiles(){
        if(BuildConfig.DEBUG) {
            for (BahnItem item :  getItems()) {
                if(item.hasLocalFile(item.filename)) {
                    File file = new File(item.getImageUrl());
                    boolean deleted = file.delete();
                    if(file.exists()){
                        try {
                            file.getCanonicalFile().delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public List<BahnItem> getItems() {
        List<BahnItem> list = new LinkedList<>();
        list.add(BahnItem.US_BAHN);
        list.add(BahnItem.TRAM);
        list.add(BahnItem.REGIONAL);
        list.add(BahnItem.ZOO);
        return list;
    }

}

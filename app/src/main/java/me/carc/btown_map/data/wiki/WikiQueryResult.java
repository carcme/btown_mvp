package me.carc.btown_map.data.wiki;

import android.support.annotation.Nullable;

import java.util.List;

public class WikiQueryResult {

    @SuppressWarnings("unused")
    @Nullable
    private List<WikiQueryPage> pages;

    @Nullable
    public List<WikiQueryPage> pages() {
        return pages;
    }
}

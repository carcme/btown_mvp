package me.carc.btownmvp.data.wiki;

import android.support.annotation.NonNull;

import java.util.List;

public class NearbyResult {

    @NonNull
    private final List<NearbyPage> list;

    NearbyResult(@NonNull List<NearbyPage> list) {
        this.list = list;
    }

    @NonNull
    public List<NearbyPage> getList() {
        return list;
    }
}

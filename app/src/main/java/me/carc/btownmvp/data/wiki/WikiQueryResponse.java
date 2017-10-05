package me.carc.btownmvp.data.wiki;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class WikiQueryResponse {

    @SuppressWarnings("unused") @SerializedName("batchcomplete") private boolean batchComplete;

    @SuppressWarnings("unused") @SerializedName("continue") @Nullable
    private Map<String, String> continuation;

    @Nullable
    private WikiQueryResult query;

    public boolean batchComplete() {
        return batchComplete;
    }

    @Nullable
    public Map<String, String> continuation() {
        return continuation;
    }

    @Nullable
    public WikiQueryResult query() {
        return query;
    }

    public boolean success() {
        return success() && query != null;
    }

    @VisibleForTesting
    protected void setQuery(@Nullable WikiQueryResult query) {
        this.query = query;
    }


}

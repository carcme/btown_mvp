package me.carc.btownmvp.data.wiki;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WikiQueryResult {

    @SuppressWarnings("unused")
    @Nullable
    private List<WikiQueryPage> pages;

    @SuppressWarnings("unused") @Nullable
    private List<Redirect> redirects;
    @SuppressWarnings("unused") @SerializedName("userinfo") private UserInfo userInfo;
    @SuppressWarnings("unused") @Nullable
    private List<ListUsersResponse> users;
    @SuppressWarnings("unused") @Nullable
    private Tokens tokens;

    @SuppressWarnings("unused") @Nullable
    private MarkReadResponse echomarkread;

    @Nullable
    public List<WikiQueryPage> pages() {
        return pages;
    }

    @Nullable
    public List<Redirect> redirects() {
        return redirects;
    }

    @Nullable
    public UserInfo userInfo() {
        return userInfo;
    }

    @Nullable
    public String csrfToken() {
        return tokens != null ? tokens.csrf() : null;
    }

    @Nullable
    public String createAccountToken() {
        return tokens != null ? tokens.createAccount() : null;
    }

    @Nullable
    public String loginToken() {
        return tokens != null ? tokens.login() : null;
    }

    @NonNull
    public Set<String> getGroupsFor(@NonNull String userName) {
        if (users != null) {
            for (ListUsersResponse user : users) {
                final Set<String> groups = user.getGroupsFor(userName);
                if (groups != null) {
                    return groups;
                }
            }
        }
        return Collections.emptySet();
    }

    @NonNull
    public Map<String, ImageInfo> images() {
        Map<String, ImageInfo> result = new HashMap<>();
        if (pages != null) {
            for (WikiQueryPage page : pages) {
                if (page.imageInfo() != null) {
                    result.put(page.title(), page.imageInfo());
                }
            }
        }
        return result;
    }

    @NonNull
    public Map<String, VideoInfo> videos() {
        Map<String, VideoInfo> result = new HashMap<>();
        if (pages != null) {
            for (WikiQueryPage page : pages) {
                if (page.videoInfo() != null) {
                    result.put(page.title(), page.videoInfo());
                }
            }
        }
        return result;
    }

    @Nullable
    public String wikitext() {
        if (pages != null) {
            for (WikiQueryPage page : pages) {
                if (page.revisions() != null && page.revisions().get(0) != null) {
                    return page.revisions().get(0).content();
                }
            }
        }
        return null;
    }

    @NonNull public List<NearbyPage> nearbyPages() {
        List<NearbyPage> result = new ArrayList<>();
        if (pages != null) {
            for (WikiQueryPage page : pages) {
                NearbyPage nearbyPage = new NearbyPage(page);
                if (nearbyPage.getLocation() != null) {
                    result.add(nearbyPage);
                }
            }
        }
        return result;
    }

    public static class Redirect {
        @SuppressWarnings("unused") private int index;
        @SuppressWarnings("unused") @Nullable
        private String from;
        @SuppressWarnings("unused") @Nullable
        private String to;
        @SuppressWarnings("unused") @SerializedName("tofragment") @Nullable
        private String toFragment;

        @Nullable
        public String to() {
            return to;
        }

        @Nullable
        public String from() {
            return from;
        }

        @Nullable
        public String toFragment() {
            return toFragment;
        }
    }

    private static class ListUsersResponse {
        @SuppressWarnings("unused") @SerializedName("name") @Nullable
        private String name;
        @SuppressWarnings("unused") @Nullable
        private List<String> groups;

        @Nullable
        Set<String> getGroupsFor(@NonNull String userName) {
            if (userName.equals(name) && groups != null) {
                Set<String> groupNames = new ArraySet<>();
                groupNames.addAll(groups);
                return Collections.unmodifiableSet(groupNames);
            }
            return null;
        }
    }

    private static class Tokens {
        @SuppressWarnings("unused,NullableProblems") @SerializedName("csrftoken")
        @Nullable
        private String csrf;
        @SuppressWarnings("unused,NullableProblems") @SerializedName("createaccounttoken")
        @Nullable
        private String createAccount;
        @SuppressWarnings("unused,NullableProblems") @SerializedName("logintoken")
        @Nullable
        private String login;

        @Nullable
        private String csrf() {
            return csrf;
        }

        @Nullable
        private String createAccount() {
            return createAccount;
        }

        @Nullable
        private String login() {
            return login;
        }
    }

    private static class MarkReadResponse {
        @SuppressWarnings("unused") @Nullable
        private String result;

        @Nullable
        public String result() {
            return result;
        }
    }
}

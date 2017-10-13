package me.carc.btownmvp.map.interfaces;

import me.carc.btownmvp.map.sheets.wiki.WikiBookmarksListAdapter;

/**
 * Created by bamptonm on 11/10/2017.
 */

public interface WikiBookmarkClickListener {
    void OnClick(WikiBookmarksListAdapter.WikiItemAdpater item);
    void OnLongClick(WikiBookmarksListAdapter.WikiItemAdpater item);

    void OnImageClick(WikiBookmarksListAdapter.WikiItemAdpater item);

    void OnMoreClick(int posistion);

}

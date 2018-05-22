package me.carc.btown.common.interfaces;

import me.carc.btown.db.bookmark.BookmarkEntry;

/**
 * Created by bamptonm on 11/10/2017.
 */

public interface WikiBookmarkClickListener {
    void OnClick(BookmarkEntry item);
    void OnLongClick(BookmarkEntry item);
    void OnImageClick(BookmarkEntry item);
    void OnMoreClick(int posistion);
}

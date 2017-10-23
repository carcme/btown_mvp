package me.carc.btown.common.interfaces;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by bamptonm on 05/09/2017.
 */

public interface DrawableClickListener {
    void OnClick(View v, Drawable drawable, int position);
    void OnLongClick(View v, int position);
}

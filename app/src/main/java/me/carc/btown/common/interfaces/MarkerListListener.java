package me.carc.btown.common.interfaces;

import android.view.View;

public interface MarkerListListener {

    void onClick(View v, int position);
    void onClickImage(View v, int position);
    void onClickOverflow(View v, int position);
    void onClickCompass(View v, int position);
}

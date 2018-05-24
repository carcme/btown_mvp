package me.carc.btown.common.interfaces;

import android.view.View;

/**
 * Created by bamptonm on 05/09/2017.
 */

public interface RecyclerClickListener{
    public void onClick(View view, int position);
    public void onLongClick(View view,int position);
}

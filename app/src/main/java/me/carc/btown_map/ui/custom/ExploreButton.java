package me.carc.btown_map.ui.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.shaishavgandhi.loginbuttons.BaseButton;

import me.carc.btown_map.R;

/**
 * Created by Carc.me on 05.10.16.
 * <p/>
 * TODO: Add a class header comment!
 */
public class ExploreButton extends BaseButton {

    public ExploreButton(Context context, AttributeSet attrs) {
        super(context,attrs, R.color.tour_explore_button_color, R.drawable.ic_map);
    }

    public ExploreButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs,defStyle, R.color.tour_explore_button_color, R.drawable.ic_map);
    }

    public ExploreButton(Context context) {
        super(context);
    }

}
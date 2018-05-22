package me.carc.btown.ui.custom;

import android.content.Context;
import android.util.AttributeSet;

import com.shaishavgandhi.loginbuttons.BaseButton;

import me.carc.btown.R;

/**
 * Created by Carc.me on 05.10.16.
 * <p/>
 * TODO: Add a class header comment!
 */
public class DownloadButton extends BaseButton {

    public DownloadButton(Context context, AttributeSet attrs) {
        super(context,attrs, R.color.md_blue_grey_600, R.drawable.ic_download_white);
    }

    public DownloadButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs,defStyle, R.color.md_blue_grey_600, R.drawable.ic_download_white);
    }

    public DownloadButton(Context context) {
        super(context);
    }

}
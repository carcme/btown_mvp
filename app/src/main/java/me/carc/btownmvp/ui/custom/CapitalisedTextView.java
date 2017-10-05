package me.carc.btownmvp.ui.custom;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Capialise the first character in the textview
 * Created by bamptonm on 5/16/17.
 */
public class CapitalisedTextView extends android.support.v7.widget.AppCompatTextView {

    public CapitalisedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null && text.length() > 0) {
            text = text.toString().replace('_', ' ');
            text = String.valueOf(text.charAt(0)).toUpperCase() + text.subSequence(1, text.length());
        }
        super.setText(text, type);
    }
}
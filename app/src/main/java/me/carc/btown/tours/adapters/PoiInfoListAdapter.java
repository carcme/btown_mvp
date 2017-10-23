package me.carc.btown.tours.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import me.carc.btown.R;
import me.carc.btown.common.Commons;
import me.carc.btown.tours.attractionPager.InfoCard;

/**
 * Created by Carc.me on 27.09.16.
 * <p/>
 * TODO: Add a class header comment!
 */
public class PoiInfoListAdapter extends ArrayAdapter<InfoCard> {

    private ArrayList<InfoCard> items = new ArrayList<>();
    private Typeface font;
    private int layoutID;
    private LayoutInflater inflater;

    public PoiInfoListAdapter(Activity context, int textViewResourceId, @NonNull ArrayList itemnames, Typeface font) {
        super(context, textViewResourceId, itemnames);
        items = itemnames;
        this.font = font;
        this.layoutID = textViewResourceId;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    @NonNull
    public View getView(int pos, View convertView, @NonNull ViewGroup viewGroup) {

        View v = convertView;

        if (v == null) {
            v = inflater.inflate(layoutID, viewGroup, false);
        }

        ImageView icon = (ImageView) v.findViewById(R.id.infoIcon);
        TextView text = (TextView) v.findViewById(R.id.infoText);

        switch (items.get(pos).getDataRes()) {

            case R.drawable.ic_place:
                text.setText(items.get(pos).getData());
                text.setAutoLinkMask(Linkify.MAP_ADDRESSES);
                break;
/*
            case R.drawable.ic_clock:
                text.setText(items.get(pos).getData());
                break;

            case R.drawable.ic_food_drink:
                text.setText(items.get(pos).getData());
                break;

            case R.drawable.ic_dollar:
                text.setText(items.get(pos).getData());
                break;
*/

            case R.drawable.ic_call:
                text.setText(items.get(pos).getData());
                text.setAutoLinkMask(Linkify.PHONE_NUMBERS);
                break;

            case R.drawable.ic_email:
                text.setText(items.get(pos).getData());
                text.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
                break;

            case R.drawable.ic_web:
                setText(text, items.get(pos).getData(), Commons.getString(getContext(), R.string.view_homepage));
                break;

            case R.drawable.ic_wiki:
                setText(text, items.get(pos).getData(), Commons.getString(getContext(), R.string.wikipedia));
                break;

            case R.drawable.ic_facebook_box:
                setText(text, items.get(pos).getData(), Commons.getString(getContext(), R.string.facebook_page));
                break;

            default:
                text.setText(items.get(pos).getData());
        }

        if (font != null) text.setTypeface(font);
        icon.setImageResource(items.get(pos).getDataRes());

        return v;
    }

    private void setText(TextView view, String link, String disp) {

        String baseLink = String.format("<a href=\"%s\">%s</a>", link, disp);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            view.setText(Html.fromHtml(baseLink, Html.FROM_HTML_MODE_LEGACY));
        else
            view.setText(Html.fromHtml(baseLink));

//        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setAutoLinkMask(Linkify.WEB_URLS);

        view.setLinksClickable(true);
    }
}
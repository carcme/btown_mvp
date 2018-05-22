package me.carc.btown.ui.front_page.getting_about;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.carc.btown.R;
import me.carc.btown.Utils.ViewUtils;
import me.carc.btown.common.interfaces.ToursScrollListener;

/**
 * Getting around pager activity
 *
 * Created by bamptonm on 24/10/17.
 */

public class TransportActivity extends Activity implements ToursScrollListener {

//    FloatingActionButton fabTransport;
    @Nullable
    @BindView(R.id.fabTransport) FloatingActionButton fabTransport;

    @OnClick(R.id.fabTransport)
    void done() {
        float temp = getResources().getDimension(R.dimen.fab_margin);
        int duration = getResources().getInteger(R.integer.gallery_alpha_duration);
        ViewUtils.hideView(fabTransport, duration, (int) temp);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, duration * 2);
    }

    @Override
    public void onScrollView(boolean hide) {
        if(fabTransport != null) {
            if (hide) {
                fabTransport.hide();
            } else {
                fabTransport.show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_content);
        ButterKnife.bind(this);

        if(getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back);
            upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            getActionBar().setHomeAsUpIndicator(upArrow);

            // TODO: 22/12/2017 dont like this - find way to change actionbar text color that is correct/nice
            getActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.front_menu_getting_around) + "</font>"));
            getActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.color_getting_around)));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_getting_around_dark));
        }

        if (savedInstanceState != null)
            return;

        TransportPagerFragment fragment = new TransportPagerFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment, TransportPagerFragment.TAG_ID)
                .commit();

//        getFragmentManager().beginTransaction().replace(android.R.id.content, new TransportPagerFragment()).commit();

    }

    public FloatingActionButton getFab(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ButterKnife.bind(this);
        return (FloatingActionButton) inflater.inflate(R.layout.fab_layout, parent, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

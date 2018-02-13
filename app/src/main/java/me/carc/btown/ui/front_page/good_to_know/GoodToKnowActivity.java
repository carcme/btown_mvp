package me.carc.btown.ui.front_page.good_to_know;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.MenuItem;

import me.carc.btown.R;

/**
 * Good to know information pager activity
 *
 * Created by bamptonm on 24/10/17.
 */

public class GoodToKnowActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back);
            upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            getActionBar().setHomeAsUpIndicator(upArrow);

            // TODO: 22/12/2017 dont like this - find way to change actionbar text color that is correct/nice
            getActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.front_menu_good_to_know) + "</font>"));
            getActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.color_good_to_know)));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_good_to_know_dark));
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content, new GoodToKnowPagerFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

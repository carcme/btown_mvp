package me.carc.btown.settings;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.MenuItem;

import me.carc.btown.BaseActivity;
import me.carc.btown.R;

/**
 * Setting activity
 *
 * Created by bamptonm on 24/10/17.
 */

public class SettingsActivity extends BaseActivity implements AboutFragment.BtnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back);
            upArrow.setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);

            // TODO: 22/12/2017 dont like this - find way to change actionbar text color that is correct/nice
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.shared_string_settings) + "</font>"));
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.color_settings)));
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.color_settings_dark));
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsPagerFragment()).commit();
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
        if(getIntent().hasExtra("lang")) {
            setResult(RESULT_OK, getIntent());
        }
        super.onBackPressed();
    }

    @Override
    public void onDonateClick() {
        showDonateDialog();
    }
}

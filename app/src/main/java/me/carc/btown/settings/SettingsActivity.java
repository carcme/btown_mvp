package me.carc.btown.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import me.carc.btown.R;

/**
 * Setting activity
 *
 * Created by bamptonm on 24/10/17.
 */

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setTitle(getString(R.string.shared_string_settings));
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsPagerFragment()).commit();
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
        if(getIntent().hasExtra("lang")) {
            setResult(RESULT_OK, getIntent());
        }
        super.onBackPressed();
    }
}

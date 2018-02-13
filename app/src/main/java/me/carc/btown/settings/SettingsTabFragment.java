package me.carc.btown.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import me.carc.btown.R;

/**
 * System settings
 * Created by bamptonm on 7/11/17.
 */

public class SettingsTabFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(Preferences.LANGUAGE)) {
            ListPreference langPref = (ListPreference)findPreference(key);
            langPref.setSummary(langPref.getEntry());
            Intent intent = getActivity().getIntent();
            intent.putExtra("lang", true);
        } else if(key.equals(Preferences.TOURS)) {
            CheckBoxPreference toursPref = (CheckBoxPreference)findPreference(key);
            if(toursPref.isChecked())
                toursPref.setSummary(R.string.pref_tours_summary_show);
            else
                toursPref.setSummary(R.string.pref_tours_summary_hide);
        } else if (key.equals(Preferences.GPS)) {
            CheckBoxPreference gpsPref = (CheckBoxPreference)findPreference(key);
            if(gpsPref.isChecked())
                gpsPref.setSummary(R.string.pref_location_use_gps_best_accuracy);
            else
                gpsPref.setSummary(R.string.pref_location_use_gps_best_battery);
        }
    }

/*
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }
*/
}

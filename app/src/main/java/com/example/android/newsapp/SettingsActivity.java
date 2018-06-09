package com.example.android.newsapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    final static String LOG_TAG = SettingsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        Log.i(LOG_TAG, "SettingsActivity/onCreate");
    }

    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private static final String OR_SEPARATOR = ", ";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the Preferences from the XML file
            addPreferencesFromResource(R.xml.settings_main);

            Preference orderByDate = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderByDate);

            Preference pageSize = findPreference(getString(R.string.settings_page_size_key));
            bindPreferenceSummaryToValue(pageSize);

            Preference section = findPreference(getString(R.string.settings_section_key));
            bindPreferenceSummaryToValue(section);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            if (preference instanceof MultiSelectListPreference) {
                Set<String> values = (Set<String>) value;
                MultiSelectListPreference mslPreference = (MultiSelectListPreference) preference;
                CharSequence[] entries = mslPreference.getEntries();

                StringBuilder valuesString = new StringBuilder();
                boolean isFirst = true;
                for (String sections : values) {
                    int index = mslPreference.findIndexOfValue(sections);
                    if (index >= 0) {
                        if (!isFirst) {
                            valuesString.append(OR_SEPARATOR);
                        } else {
                            isFirst = false;
                        }
                        valuesString.append(entries[index]);
                    }
                }
                preference.setSummary(valuesString.toString());
            } else {
                String valueString = (String) value;
                preference.setSummary(valueString);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            if (preference instanceof MultiSelectListPreference) {
                Set<String> sectionsSet = preferences.getStringSet(preference.getKey()
                        , null);
                onPreferenceChange(preference, sectionsSet);
            } else {
                String preferenceString = preferences.getString(preference.getKey(), "");
                onPreferenceChange(preference, preferenceString);
            }
        }


    }
}

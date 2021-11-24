package com.learntodroid.simplealarmclock.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.preference.Preference;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.learntodroid.simplealarmclock.R;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        final String[] validCategories = {"chin", "eng", "math", "cs"};

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            for (String key : validCategories) {
                findPreference(key).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    public boolean onPreferenceClick(Preference preference) {
                        ArrayList<String> activeCategories = new ArrayList<>();
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                        for (String cat : validCategories) {
                            if (sharedPreferences.getBoolean(cat, false)) {
                                activeCategories.add(cat);
                            }
                        }
                        if (activeCategories.size() <= 0) {
                            Toast toast = Toast.makeText(requireContext(), "Reminder: You will receive questions from a random category if nothing is selected.", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
                });
            }
        }
    }
}
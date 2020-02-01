package com.example.queenelizabethviii.caireen2;

import android.os.Bundle;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
     //  EditTextPreference fullname = (EditTextPreference) findViewById(R.id.namesettings);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

      //  Preference changepass = (Preference) findViewById(R);

    }
}
     

package my.home.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;

import com.example.musictrainning.R;

public class SettingsFragment extends PreferenceFragment {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        EditTextPreference addressEditTextPreference = (EditTextPreference) findPreference("pref_server_address");
        addressEditTextPreference.setSummary(sharedPreferences.getString("pref_server_address", "http://192.168.1.100:8888"));
	}
}

package com.inf1315.vertretungsplan;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.layout.preferences);
	}

	@Override
	public void onPause() {

		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);

	}

	@Override
	public void onResume() {

		super.onResume();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

		updatePrefs();

	}

	public void updatePrefs() {

		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();

		Preference logoutPref = findPreference("pref_logout");

		if (sp.getBoolean("pref_logout", true)) {

			logoutPref.setSummary(R.string.pref_logout_summary);

		}

		else {
			logoutPref.setSummary(R.string.pref_logout_summary_false);
		}

		Preference toastPref = findPreference("pref_toast");

		if (sp.getBoolean("pref_toast", true)) {

			toastPref.setSummary(R.string.pref_toast_summary);

		}

		else {
			toastPref.setSummary(R.string.pref_toast_summary_false);
		}

	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("pref_logout")) {
			Preference logoutPref = findPreference(key);

			if (sharedPreferences.getBoolean(key, true)) {

				logoutPref.setSummary(R.string.pref_logout_summary);

			}

			else {
				logoutPref.setSummary(R.string.pref_logout_summary_false);
			}

		}

		if (key.equals("pref_toast")) {
			Preference toastPref = findPreference(key);

			if (sharedPreferences.getBoolean(key, true)) {

				toastPref.setSummary(R.string.pref_toast_summary);

			}

			else {
				toastPref.setSummary(R.string.pref_toast_summary_false);
			}

		}

	}
}

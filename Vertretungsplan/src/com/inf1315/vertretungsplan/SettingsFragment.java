package com.inf1315.vertretungsplan;

import com.inf1315.vertretungsplan.api.API;
import com.inf1315.vertretungsplan.api.AllObject;
import com.inf1315.vertretungsplan.api.UserInfo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment implements
		OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);

		findPreference("pref_github").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {

						Intent browserIntent = new Intent(Intent.ACTION_VIEW,
								Uri.parse("https://github.com/GymDon/VPlanApp"));
						startActivity(browserIntent);

						return true;
					}
				});

		findPreference("pref_clear_cache").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						String token = API.DATA.getToken();
						String user = API.DATA.userInfo.username;
						API.DATA = new AllObject();
						API.DATA.setToken(token);
						API.DATA.userInfo = new UserInfo(user);
						API.reload = true;
						Toast.makeText(getActivity(),
								getText(R.string.cache_cleared),
								Toast.LENGTH_SHORT).show();
						return true;
					}
				});

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
		} else {
			logoutPref.setSummary(R.string.pref_logout_summary_false);
		}
		Preference toastPref = findPreference("pref_toast");
		if (sp.getBoolean("pref_toast", true)) {
			toastPref.setSummary(R.string.pref_toast_summary);
		} else {
			toastPref.setSummary(R.string.pref_toast_summary_false);
		}
		Preference notificationsPref = findPreference("pref_notifications");
		if (sp.getBoolean("pref_notifitcations", true)) {
			notificationsPref.setSummary(R.string.pref_notifications_summary);
		} else {
			notificationsPref
					.setSummary(R.string.pref_notifications_summary_false);
		}
		Preference vibratePref = findPreference("pref_vibrate");
		if (sp.getBoolean("pref_vibrate", true)) {
			vibratePref.setSummary(R.string.pref_vibrate_summary);
		} else {
			vibratePref.setSummary(R.string.pref_vibrate_summary_false);
		}
		ListPreference intervallPref = (ListPreference) findPreference("pref_intervall");
		intervallPref.setSummary(intervallPref.getEntry());
		Preference clearCachePref = findPreference("pref_clear_cache");
		clearCachePref.setSummary(R.string.pref_clear_cache_summary);
		// TODO: (lemuecke)Find better way to update the app's version in
		// settings
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("pref_logout")) {
			Preference logoutPref = findPreference(key);
			if (sharedPreferences.getBoolean(key, true)) {
				logoutPref.setSummary(R.string.pref_logout_summary);
			} else {
				logoutPref.setSummary(R.string.pref_logout_summary_false);
			}
		}

		if (key.equals("pref_toast")) {
			Preference toastPref = findPreference(key);
			if (sharedPreferences.getBoolean(key, true)) {
				toastPref.setSummary(R.string.pref_toast_summary);
			} else {
				toastPref.setSummary(R.string.pref_toast_summary_false);
			}
		}

		if (key.equals("pref_notifications")) {
			Preference notificationsPref = findPreference(key);
			if (sharedPreferences.getBoolean(key, true)) {
				notificationsPref
						.setSummary(R.string.pref_notifications_summary);
			} else {
				notificationsPref
						.setSummary(R.string.pref_notifications_summary_false);
			}
		}

		if (key.equals("pref_vibrate")) {
			Preference vibratePref = findPreference(key);
			if (sharedPreferences.getBoolean(key, true)) {
				vibratePref.setSummary(R.string.pref_vibrate_summary);
			} else {
				vibratePref.setSummary(R.string.pref_vibrate_summary_false);
			}
		}

		if (key.equals("pref_clear_cache")) {
			Preference clearCachePref = findPreference(key);
			clearCachePref.setSummary(R.string.pref_clear_cache_summary);
		}
		
		ListPreference intervallPref = (ListPreference) findPreference("pref_intervall");
		intervallPref.setSummary(intervallPref.getEntry());

	}
}

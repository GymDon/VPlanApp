package com.inf1315.vertretungsplan;

import com.inf1315.vertretungsplan.api.API;
import com.inf1315.vertretungsplan.api.AllObject;
import com.inf1315.vertretungsplan.api.UserInfo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

@SuppressLint("NewApi")
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
		Preference toastPref = findPreference("pref_toast");
		if (sp.getBoolean("pref_toast", true)) {
			toastPref.setSummary(R.string.pref_toast_summary);
		} else {
			toastPref.setSummary(R.string.pref_toast_summary_false);
		}
		ListPreference intervalPref = (ListPreference) findPreference("pref_interval");
		intervalPref.setSummary(intervalPref.getEntry());
		Preference clearCachePref = findPreference("pref_clear_cache");
		clearCachePref.setSummary(R.string.pref_clear_cache_summary);
		Preference version = findPreference("pref_version");
		try {
			version.setSummary(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			version.setSummary(R.string.error);
		}
	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {

		if (key.equals("pref_toast")) {
			Preference toastPref = findPreference(key);
			if (sharedPreferences.getBoolean(key, true)) {
				toastPref.setSummary(R.string.pref_toast_summary);
			} else {
				toastPref.setSummary(R.string.pref_toast_summary_false);
			}
		}

		if (key.equals("pref_clear_cache")) {
			Preference clearCachePref = findPreference(key);
			clearCachePref.setSummary(R.string.pref_clear_cache_summary);
		}

		ListPreference intervalPref = (ListPreference) findPreference("pref_interval");
		intervalPref.setSummary(intervalPref.getEntry());

	}
}

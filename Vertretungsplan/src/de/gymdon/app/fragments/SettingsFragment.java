package de.gymdon.app.fragments;

import de.gymdon.app.R;
import de.gymdon.app.activities.ChangelogActivity;
import de.gymdon.app.api.API;
import de.gymdon.app.api.AllObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
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
						API.DATA = new AllObject();
						API.reload = true;
						Toast.makeText(getActivity(),
								getText(R.string.cache_cleared),
								Toast.LENGTH_SHORT).show();
						return true;
					}
				});

		findPreference("pref_bug").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference preference) {
						Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
								Uri.fromParts("mailto",
										"bugs@pvpctutorials.de", null));
						emailIntent.putExtra(Intent.EXTRA_SUBJECT,
								"Bug Report: Gym-Don App");
						startActivity(Intent.createChooser(emailIntent,
								"E-Mail senden..."));

						return true;
					}
				});

		findPreference("pref_version").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						if (!API.isNetworkAvailable()) {
							Toast.makeText(getActivity(),
									R.string.no_internet_connection,
									Toast.LENGTH_SHORT).show();
							return true;
						}
						Intent browserIntent = new Intent(getActivity(),
								ChangelogActivity.class);
						startActivity(browserIntent);

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
		}/*
		 * ListPreference intervalPref = (ListPreference)
		 * findPreference("pref_interval");
		 * intervalPref.setSummary(intervalPref.getEntry());
		 */
		Preference clearCachePref = findPreference("pref_clear_cache");
		clearCachePref.setSummary(R.string.pref_clear_cache_summary);
		Preference version = findPreference("pref_version");
		try {
			version.setSummary(getActivity().getPackageManager()
					.getPackageInfo(getActivity().getPackageName(), 0).versionName);
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
		/*
		 * ListPreference intervalPref = (ListPreference)
		 * findPreference("pref_interval");
		 * intervalPref.setSummary(intervalPref.getEntry());
		 */

	}
}

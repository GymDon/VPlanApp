package com.inf1315.vertretungsplan;

import com.google.gson.Gson;
import com.inf1315.vertretungsplan.api.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText usernameEditText;
	private EditText passwordEditText;
	private static boolean isFirstLaunch = true;
	private int appVersion;
	private int prevAppVersion;

	private boolean runOnFirstLaunch() {
		LoginActivity.isFirstLaunch = false;

		SharedPreferences sharedPrefs = getSharedPreferences("data",
				MODE_PRIVATE);

		String json = sharedPrefs.getString("data", "");
		API.DATA = "".equals(json) ? new AllObject() : new Gson().fromJson(
				json, AllObject.class);

		prevAppVersion = sharedPrefs.getInt("appVersionPref", 0);
		try {
			appVersion = getPackageManager()
					.getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			appVersion = -1;
			e.printStackTrace();
		}

		// TODO alcros test something
		if (prevAppVersion != appVersion) {
			if (appVersion == -1)
				return false;
			showChangelog(prevAppVersion, appVersion);
			SharedPreferences.Editor spe = sharedPrefs.edit();
			spe.putInt("appVersionPref", appVersion);
			spe.apply();
		}

		PreferenceManager.setDefaultValues(this, R.layout.preferences, false);


		try {
			API.CONTEXT = getApplicationContext();
			API.APP_VERSION = getPackageName()
					+ " "
					+ getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Log.w("Startup", "Couldn't determine app version");
			e.printStackTrace();
		}

		PreferenceManager.setDefaultValues(this, R.layout.preferences, false);

		if (prevAppVersion != appVersion && API.isNetworkAvailable()) {
			if (appVersion == -1)
				return false;
			showChangelog(prevAppVersion, appVersion);
			SharedPreferences.Editor spe = sharedPrefs.edit();
			spe.putInt("appVersionPref", appVersion);
			spe.apply();
			return false;
		}

		long currentTimestamp = System.currentTimeMillis() / 1000L;
		if (!"".equals(API.DATA.getToken())
				&& API.DATA.timestamp + 86400L > currentTimestamp) {
			Intent intent = new Intent(this, PlanActivity.class);
			startActivity(intent);
			return true;
		}

		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isFirstLaunch)
			if (runOnFirstLaunch())
				return;

		setContentView(R.layout.activity_login);

		// Initalize variables
		usernameEditText = (EditText) findViewById(R.id.username_EditText);
		passwordEditText = (EditText) findViewById(R.id.password_EditText);

		String username = getSharedPreferences("data", MODE_PRIVATE).getString(
				"username", "");
		usernameEditText.setText(username);

		String error = getIntent().getStringExtra("error");
		if (error == null)
			return;

		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(R.string.error);
		adb.setPositiveButton(android.R.string.ok, null);

		if (error.equals("ServerRequestFailed"))
			adb.setMessage(R.string.server_request_failed);
		if (error.equals("NoInternetConnection")) {
			adb.setMessage(R.string.no_internet_connection);
		}
		String password = getIntent().getStringExtra("password");
		if (password != null)
			passwordEditText.setText(password);

		adb.show();
	}

	public void showChangelog(final int from, final int to) {
		new AsyncTask<Object, Object, Commit[]>() {
			@Override
			protected Commit[] doInBackground(Object... params) {
				try {
					return ((ApiResultArray) API.STANDARD_API.request(
							ApiAction.CHANGELOG,
							"from="
									+ (from > 0 ? Commit.versionTags.get(from)
											: "start"),
							"to="
									+ (to > 0 ? Commit.versionTags.get(to)
											: "end")).getResult())
							.getArray(new Commit[0]);
				} catch (Exception e) {
					e.printStackTrace();
					return new Commit[0];
				}
			}

			@Override
			protected void onPostExecute(Commit[] result) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						LoginActivity.this);

				StringBuilder message = new StringBuilder("<h2>"
						+ LoginActivity.this.getText(R.string.whatsnewNews)
						+ "</h2>");
				boolean hasUl = false;
				for (Commit commit : result) {
					if (commit.tag != null) {
						message.append(hasUl ? "</ul>" : "")
								.append("<h3>Version ").append(commit.tag.name)
								.append(":</h3><ul>");
						hasUl = true;
					}
					if (!hasUl) {
						message.append("<ul>");
						hasUl = true;
					}
					message.append("<li>")
							.append(commit.comment.replace("\n", "<br />"))
							.append("</li>");
				}
				message.append("</ul>");
				// Log.d("Html", message.toString());
				WebView webView = new WebView(LoginActivity.this);
				webView.loadDataWithBaseURL(null, message.toString(),
						"text/html", "UTF-8", null);
				builder.setView(webView);
				builder.setTitle(R.string.whatsnew);
				builder.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								long currentTimestamp = System
										.currentTimeMillis() / 1000L;
								if (!"".equals(API.DATA.getToken())
										&& API.DATA.timestamp + 86400L > currentTimestamp) {
									Intent intent = new Intent(
											LoginActivity.this,
											PlanActivity.class);
									startActivity(intent);
								}
							}
						});

				AlertDialog dialog = builder.create();
				dialog.show();
			}
		}.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent startSettings = new Intent(this, SettingsActivity.class);
			startActivity(startSettings);
			return true;
		case R.id.action_changelog:
			showChangelog(appVersion - 1, BuildConfig.DEBUG ? -1 : appVersion);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void loginButtonOnClick(View v) {

		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		if (username.equals("")) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Bitte Benutzername eingeben!", Toast.LENGTH_SHORT);
			toast.show();
		} else if (password.equals("")) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Bitte Passwort eingeben!", Toast.LENGTH_SHORT);
			toast.show();
		} else {

			SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
			SharedPreferences.Editor spe = sp.edit();
			spe.putString("username", username);
			spe.apply();

			Intent intent = new Intent(this, PlanActivity.class);
			intent.putExtra("username", username);
			intent.putExtra("password", password);
			startActivity(intent);
		}
	}

}

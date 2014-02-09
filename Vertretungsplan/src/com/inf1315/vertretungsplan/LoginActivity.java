package com.inf1315.vertretungsplan;

import com.google.gson.Gson;
import com.inf1315.vertretungsplan.api.*;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

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

		try {
			API.CONTEXT = getApplicationContext();
			API.APP_VERSION = getPackageName()
					+ " "
					+ getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Log.w("Startup", "Couldn't determine app version");
			e.printStackTrace();
		}

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		if (prevAppVersion != appVersion && API.isNetworkAvailable()) {
			if (appVersion == -1)
				return false;
			showChangelog(prevAppVersion, appVersion);
			SharedPreferences.Editor spe = sharedPrefs.edit();
			spe.putInt("appVersionPref", appVersion);
			spe.commit();
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
		final Dialog loadingDialog = ProgressDialog.show(this, "",
				getText(R.string.loading_changelog), true);
		loadingDialog.show();
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

			@SuppressLint("NewApi") @Override
			protected void onPostExecute(Commit[] result) {
				loadingDialog.dismiss();
				StringBuilder message = new StringBuilder("<h2>"
						+ LoginActivity.this.getText(R.string.whatsnewNews)
						+ "</h2>");
				boolean hasUl = false;
				for (Commit commit : result) {
					if (commit.tag != null) {
						message.append(hasUl ? "</ul>" : "")
								.append("<h3>")
								.append(LoginActivity.this.getText(R.string.pref_version))
								.append(" ")
								.append(commit.tag.name)
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
				
				AlertDialog.Builder builder = Build.VERSION.SDK_INT >= 11 ? 
						new AlertDialog.Builder(LoginActivity.this, R.style.DialogTheme) : 
						new AlertDialog.Builder(new ContextThemeWrapper(LoginActivity.this, R.style.DialogTheme));
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
				dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_full_dark_blue);
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
		int itemId = item.getItemId();
		if (itemId == R.id.action_settings) {
			Intent startSettings;
			if (Build.VERSION.SDK_INT >= 11)
				startSettings = new Intent(this, Settings30.class);
			else
				startSettings = new Intent(this, Settings21.class);
			startActivity(startSettings);
			return true;
		} else if (itemId == R.id.action_changelog) {
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
			spe.commit();

			Intent intent = new Intent(this, PlanActivity.class);
			intent.putExtra("username", username);
			intent.putExtra("password", password);
			startActivity(intent);
		}
	}

}

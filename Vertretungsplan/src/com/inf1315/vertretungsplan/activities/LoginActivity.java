package com.inf1315.vertretungsplan.activities;

import com.google.gson.Gson;
import com.inf1315.vertretungsplan.R;
import com.inf1315.vertretungsplan.api.*;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

	private EditText usernameEditText;
	private EditText passwordEditText;
	private static boolean isFirstLaunch = true;

	private boolean runOnFirstLaunch() {
		LoginActivity.isFirstLaunch = false;

		SharedPreferences sharedPrefs = getSharedPreferences("data",
				MODE_PRIVATE);

		String json = sharedPrefs.getString("data", "");
		API.DATA = "".equals(json) ? new AllObject() : new Gson().fromJson(
				json, AllObject.class);

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

		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isFirstLaunch)
			runOnFirstLaunch();

		setContentView(R.layout.activity_login);

		// Initalize variables
		usernameEditText = (EditText) findViewById(R.id.username_EditText);
		passwordEditText = (EditText) findViewById(R.id.password_EditText);

		String username = getSharedPreferences("data", MODE_PRIVATE).getString(
				"username", "");
		usernameEditText.setText(username);

		if (getIntent().getBooleanExtra("closeapp", false)) {
			onBackPressed();
			return;
		}

		String error = getIntent().getStringExtra("error");
		if (error == null) {
			long currentTimestamp = System.currentTimeMillis() / 1000L;
			if (!"".equals(API.DATA.getToken())
					&& API.DATA.timestamp + 86400L > currentTimestamp) {
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
			}
		} else {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(R.string.error);
			adb.setPositiveButton(android.R.string.ok, null);

			if (error.equals("ServerRequestFailed"))
				adb.setMessage(R.string.server_request_failed);
			else if (error.equals("NoInternetConnection"))
				adb.setMessage(R.string.no_internet_connection);
			else
				adb.setMessage(error);
			String password = getIntent().getStringExtra("password");
			if (password != null)
				passwordEditText.setText(password);

			adb.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_settings) {
			if (Build.VERSION.SDK_INT >= 11)
				startActivity(new Intent(this, Settings11.class));
			else
				startActivity(new Intent(this, Settings7.class));
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

			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("username", username);
			intent.putExtra("password", password);
			startActivity(intent);
		}
	}

}

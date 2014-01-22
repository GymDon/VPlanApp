package com.inf1315.vertretungsplan;

import com.inf1315.vertretungsplan.api.API;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity
{

	private EditText usernameEditText;
	private EditText passwordEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Initalize variables
		usernameEditText = (EditText) findViewById(R.id.username_EditText);
		passwordEditText = (EditText) findViewById(R.id.password_EditText);

		PreferenceManager.setDefaultValues(this, R.layout.preferences, false);

		try
		{
			API.CONTEXT = getApplicationContext();
			API.APP_VERSION = getPackageName() + " " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e)
		{
			Log.w("Startup", "Couldn't determine app version");
			e.printStackTrace();
		}

		String username = getSharedPreferences("data", MODE_PRIVATE).getString("username", "");
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
			String password = getIntent().getStringExtra("password");
			if (password != null) passwordEditText.setText(password);
		}
		

		adb.show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.action_settings:
				Intent startSettings = new Intent(this, SettingsActivity.class);
				startActivity(startSettings);
				return true;
			case R.id.action_info:
				Intent startInfo = new Intent(this, InfoActivity.class);
				startActivity(startInfo);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void loginButtonOnClick(View v)
	{
		
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		
		if (username.equals(""))
		{
			Toast toast = Toast.makeText(getApplicationContext(), "Bitte Benutzername eingeben!", Toast.LENGTH_SHORT);
			toast.show();
		} else if (password.equals(""))
		{
			Toast toast = Toast.makeText(getApplicationContext(), "Bitte Passwort eingeben!", Toast.LENGTH_SHORT);
			toast.show();
		} else
		{

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

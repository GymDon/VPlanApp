package com.inf1315.vertretungsplan;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText usernameEditText;
	private EditText passwordEditText;

	static Dialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Initalize variables
		usernameEditText = (EditText) findViewById(R.id.username_EditText);
		passwordEditText = (EditText) findViewById(R.id.password_EditText);
		

		PreferenceManager.setDefaultValues(this, R.layout.preferences, false);

		String username = getSharedPreferences("data", MODE_PRIVATE).getString("username", "");
		usernameEditText.setText(username);
		
		String error = getIntent().getStringExtra("error");
		if (error == null) return;
		
		AlertDialog.Builder adb = new AlertDialog.Builder(getApplicationContext());
		adb.setTitle(R.string.error);
		adb.setPositiveButton(android.R.string.ok, null);
		
		if (error.equals("ServerRequestFailed")) adb.setMessage(R.string.server_request_failed);
		
		adb.show();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
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
		case R.id.action_info:
		    Intent startInfo = new Intent(this, InfoActivity.class);
		    startActivity(startInfo);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void loginButtonOnClick(View v) {
		// TODO: Do login
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();
		
		
		//TODO: Implement better way of this
		if(username.equals("")) {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Bitte Benutzername eingeben!", Toast.LENGTH_SHORT);
			toast.show();
		}

		else {
		loadingDialog = ProgressDialog.show(this, "", "Loading", true);
		loadingDialog.show();
		
		//TODO: Remove debug
		Log.d("Login", "Username : "+username+" Password : "+ password);
		
		SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
		SharedPreferences.Editor spe = sp.edit();
		spe.putString("username", username);
		spe.apply();
		
		Intent intent = new Intent(this,PlanActivity.class);
		startActivity(intent); }
	}

}

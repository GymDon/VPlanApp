package com.inf1315.vertretungsplan;

import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText usernameEditText;
	private EditText passwordEditText;
	public static Dialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		
		//Initalize variables
		usernameEditText = (EditText) findViewById(R.id.username_EditText);
		passwordEditText = (EditText) findViewById(R.id.password_EditText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void loginButtonOnClick (View v) {
		//TODO: Do login
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();

		loadingDialog = ProgressDialog.show(this, "", "Loading", true);
		loadingDialog.show();
		
		//TODO: Remove debug
		Log.d("Login", "Username : "+username+" Password : "+ password);
		Intent intent = new Intent(this,PlanActivity.class);
		startActivity(intent);
	}

}

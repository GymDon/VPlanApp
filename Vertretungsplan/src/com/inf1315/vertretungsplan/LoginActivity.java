package com.inf1315.vertretungsplan;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText usernameEditText;
	private EditText passwordEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_login);
		
		//Initalisiere Variablen
		username_EditText = (EditText) findViewById(R.id.username_EditText);
		password_EditText = (EditText) findViewById(R.id.password_EditText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void loginButtonOnClick (View v) {
		// TODO Login ausführen
		String username = usernameEditText.getText().toString();
		String password = passwordEditText.getText().toString();
				
		//Only for Debug
		String toastString = "Username : "+username+" Password : "+ password;
		Toast.makeText(this, toastString, Toast.LENGTH_LONG).show();
		
		Intent intent = new Intent(this,PlanActivity.class);
		startActivity(intent);
	}

}

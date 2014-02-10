package com.inf1315.vertretungsplan.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.inf1315.vertretungsplan.AllAsyncTask;
import com.inf1315.vertretungsplan.FinishedLoading;
import com.inf1315.vertretungsplan.R;
import com.inf1315.vertretungsplan.api.API;
import com.inf1315.vertretungsplan.fragments.*;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends ActionBarActivity implements FinishedLoading {

	private int fragmentPosition;
	private ListView drawerList;
	private String[] drawerTitles;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private String username;
	private String password;
	private Dialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupNavigationDrawer();

		username = getIntent().getStringExtra("username");
		password = getIntent().getStringExtra("password");
		Log.i("PlanActivity", "User logged in: " + username);
		loadData();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		if (fragmentPosition != 1 || drawerLayout.isDrawerOpen(drawerList))
			menu.findItem(R.id.action_show_ticker).setVisible(false);
		else
			menu.findItem(R.id.action_show_ticker).setVisible(true);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item))
			return true;
		int itemid = item.getItemId();
		if (itemid == R.id.action_reload_plan) {
			loadData();
			return true;
		} else if (itemid == R.id.action_show_ticker) {
			VPlanFragment.showTicker(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("closeapp", true);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onResume() {

		super.onResume();
		if (API.reload) {
			dataChanged();
			loadData();
		}
	}

	private void selectItem(int position) {
		drawerLayout.closeDrawer(drawerList);

		switch (position) {
		case 0:
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new HomeFragment()).commit();
			break;
		case 1:
			if (fragmentPosition != 1)
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.content_frame, new VPlanFragment())
						.commit();
			break;
		case 2:
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new MensaFragment()).commit();
			break;
		case 3:
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new AppointmentFragment())
					.commit();
			break;
		case 4:
			if (Build.VERSION.SDK_INT >= 11)
				startActivity(new Intent(this, Settings11.class));
			else
				startActivity(new Intent(this, Settings7.class));
			return;
		case 5:
			logout();
			return;
		}

		if (position != 1 && fragmentPosition == 1)
			getSupportActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
		this.fragmentPosition = position;
		supportInvalidateOptionsMenu();
		drawerList.setItemChecked(position, true);
		getSupportActionBar().setSubtitle(drawerTitles[position]);
	}

	private void logout() {
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				"pref_logout", true)) {
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setTitle(R.string.logout);
			adb.setMessage(R.string.really_logout);
			adb.setNegativeButton(android.R.string.no, null);
			adb.setPositiveButton(android.R.string.yes, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					NavUtils.navigateUpFromSameTask(MainActivity.this);
					API.DATA.deleteToken();
				}
			});
			adb.show();
		} else {
			NavUtils.navigateUpFromSameTask(this);
			API.DATA.deleteToken();
		}
	}

	public void loadData() {
		API.reload = false;
		if (!API.isNetworkAvailable() && "".equals(API.DATA.hash)) {
			Log.w("LoadingData", "No network and no chache");
			finishedLoading("NoInternetConnection");
			return;
		} else if (!API.isNetworkAvailable()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.no_internet_connection_title);
			builder.setMessage(getText(R.string.no_internet_connection) + "\n"
					+ getText(R.string.usernanme) + API.DATA.userInfo.fullname
					+ "\n" + getText(R.string.old_data_message) + "\n"
					+ API.DATA.timeString);
			builder.setPositiveButton(R.string.ok, null);
			builder.show();
			Log.i("LoadingData", "No network: loading chache");

			dataChanged();

			return;
		}

		loadingDialog = ProgressDialog.show(this, "",
				getText(R.string.loading_plan), true);
		loadingDialog.show();
		Log.i("LoadingData", "Loading new data from remote");

		if (username == null || password == null)
			new AllAsyncTask(this).execute();
		else
			new AllAsyncTask(this, username, password).execute();
	}

	public void finishedLoading(String error) {
		if (loadingDialog != null)
			loadingDialog.dismiss();
		if (error != null) {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra("error", error);
			intent.putExtra("password", password);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return;
		}
		dataChanged();

		SharedPreferences.Editor spe = getSharedPreferences("data",
				MODE_PRIVATE).edit();
		String json = new Gson().toJson(API.DATA);
		spe.putString("data", json);
		spe.commit();
	}

	private void dataChanged() {
		// TODO implement dataChanged()
	}

	private void setupNavigationDrawer() {
		drawerList = (ListView) findViewById(R.id.left_drawer);
		drawerTitles = getResources().getStringArray(R.array.navigation_list);
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
				R.drawable.ic_drawer, R.string.open_navigation_drawer,
				R.string.close_navigation_drawer) {

			@Override
			public void onDrawerClosed(View drawerView) {
				supportInvalidateOptionsMenu();
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				supportInvalidateOptionsMenu();
				super.onDrawerOpened(drawerView);
			}

		};
		drawerLayout.setDrawerListener(drawerToggle);

		int[] images = { R.drawable.ic_launcher, R.drawable.ic_launcher,
				R.drawable.ic_launcher, R.drawable.ic_action_refresh,
				R.drawable.ic_action_refresh, R.drawable.ic_action_refresh };

		drawerList.setAdapter(getCustomSimpleAdapter(images, drawerTitles));
		drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectItem(position);
			}
		});

		selectItem(0);
	}

	private SimpleAdapter getCustomSimpleAdapter(int[] images, String[] texts) {
		if (images.length != texts.length)
			throw new UnsupportedOperationException(
					"length of images must equals with length of texts");
		String[] from = { "image", "text" };
		int[] to = { R.id.imageView, R.id.text_drawerLayout };
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; i < images.length; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("image", Integer.toString(images[i]));
			map.put("text", texts[i]);
			list.add(map);
		}
		return new SimpleAdapter(this, list, R.layout.drawer_layout, from, to);
	}
}

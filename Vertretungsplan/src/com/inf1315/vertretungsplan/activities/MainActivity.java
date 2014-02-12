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
import com.inf1315.vertretungsplan.api.UserInfo;
import com.inf1315.vertretungsplan.fragments.*;

import android.annotation.SuppressLint;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements FinishedLoading {

	private int fragmentPosition;
	private LinearLayout drawer;
	private ListView drawerList;
	private String[] drawerTitles;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private String username;
	private String password;
	private Dialog loadingDialog;

	public static final int ITEM_HOME = 0;
	public static final int ITEM_PLAN = 1;
	public static final int ITEM_MENSA = 2;
	public static final int ITEM_EVENTS = 3;
	public static final int ITEM_COUNT = 4;

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
		if (fragmentPosition != ITEM_PLAN || drawerLayout.isDrawerOpen(drawer))
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
		} else if (itemid == R.id.action_logout)
			return logout(false);
		else if (itemid == R.id.action_settings)
			return showSettings();
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
		drawerLayout.closeDrawer(drawer);

		switch (position) {
		case ITEM_HOME:
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new HomeFragment()).commit();
			break;
		case ITEM_PLAN:
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new VPlanFragment()).commit();
			break;
		case ITEM_MENSA:
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new MensaFragment()).commit();
			break;
		case ITEM_EVENTS:
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new EventFragment()).commit();
			break;
		}

		if ((position != ITEM_PLAN && fragmentPosition == ITEM_PLAN)
				|| (position != ITEM_EVENTS && fragmentPosition == ITEM_EVENTS))
			getSupportActionBar().setNavigationMode(
					ActionBar.NAVIGATION_MODE_STANDARD);
		this.fragmentPosition = position;
		supportInvalidateOptionsMenu();
		drawerList.setItemChecked(position, true);
		getSupportActionBar().setSubtitle(drawerTitles[position]);
	}

	@SuppressLint("NewApi")
	private boolean showUserInfo() {
		drawerLayout.closeDrawer(drawer);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = LayoutInflater
				.from(Build.VERSION.SDK_INT >= 11 ? builder.getContext() : this);
		View view = inflater.inflate(R.layout.user_info_dialog, null);
		UserInfo userInfo = API.DATA.userInfo;

		TextView fullname = (TextView) view
				.findViewById(R.id.user_info_fullname);
		fullname.setText(userInfo.fullname);

		TextView username = (TextView) view
				.findViewById(R.id.user_info_username);
		username.setText(userInfo.username);

		TextView mainGroup = (TextView) view
				.findViewById(R.id.user_info_main_group);
		mainGroup.setText(getText(R.string.main_group) + userInfo.mainGroup);

		ListView groupList = (ListView) view
				.findViewById(R.id.user_info_group_list);
		groupList.setAdapter(new ArrayAdapter<String>(groupList.getContext(),
				R.layout.user_info_group_list_text, userInfo.groups));

		builder.setView(view);
		builder.setPositiveButton(R.string.ok, null);
		builder.setNegativeButton(R.string.logout, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				logout(false);
			}
		});
		builder.show();
		return true;
	}

	private boolean logout(boolean alwaysAsk) {
		if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(
				"pref_logout", true)
				|| alwaysAsk) {
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
		return true;
	}

	private boolean showSettings() {
		if (Build.VERSION.SDK_INT >= 11)
			startActivity(new Intent(this, Settings11.class));
		else
			startActivity(new Intent(this, Settings7.class));
		return true;
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
		selectItem(fragmentPosition);
		TextView usernameView = (TextView) findViewById(R.id.drawer_username);
		usernameView.setText(API.DATA.userInfo.fullname);
	}

	private void setupNavigationDrawer() {
		drawer = (LinearLayout) findViewById(R.id.left_drawer);
		drawerList = (ListView) findViewById(R.id.drawer_list);
		drawerTitles = getResources().getStringArray(R.array.navigation_list);

		TextView username_view = (TextView) findViewById(R.id.drawer_username);
		username_view.setText(API.DATA.userInfo.fullname);
		username_view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showUserInfo();
			}
		});
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

		int[] images = new int[ITEM_COUNT];
		images[ITEM_HOME] = R.drawable.ic_launcher;
		images[ITEM_PLAN] = R.drawable.ic_action_view_as_list;
		images[ITEM_MENSA] = R.drawable.ic_action_view_as_list;
		images[ITEM_EVENTS] = R.drawable.ic_action_view_as_list;

		drawerList.setAdapter(getCustomSimpleAdapter(images, drawerTitles));
		drawerList
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						selectItem(position);
					}
				});

		selectItem(ITEM_HOME);
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

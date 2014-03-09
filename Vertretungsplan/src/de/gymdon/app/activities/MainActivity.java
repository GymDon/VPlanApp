package de.gymdon.app.activities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import de.gymdon.app.AllAsyncTask;
import de.gymdon.app.FinishedLoading;
import de.gymdon.app.R;
import de.gymdon.app.api.API;
import de.gymdon.app.api.AllObject;
import de.gymdon.app.api.ApiResponse;
import de.gymdon.app.api.UserInfo;
import de.gymdon.app.fragments.*;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity implements FinishedLoading {

	private int fragmentPosition;
	private LinearLayout drawer;
	private ListView drawerList;
	private String[] drawerTitles;
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle drawerToggle;
	private Dialog loadingDialog;

	public static final int ITEM_HOME = 0;
	public static final int ITEM_PLAN = 1;
	public static final int ITEM_MENSA = 2;
	public static final int ITEM_EVENTS = 3;
	public static final int ITEM_COUNT = 4;
	
	private static boolean isFirstLaunch = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isFirstLaunch)
			runOnFirstLaunch();
		
		setContentView(R.layout.activity_main);
		setupNavigationDrawer();
		loadData();
	}
	
	private boolean runOnFirstLaunch() {
		MainActivity.isFirstLaunch = false;

		SharedPreferences sharedPrefs = getSharedPreferences("data",
				MODE_PRIVATE);

		String json = sharedPrefs.getString("data", "");
		API.DATA = "".equals(json) ? new AllObject() : new Gson().fromJson(
				json, AllObject.class);

		int prevAppVersion = sharedPrefs.getInt("prevAppVersion", 0);
		int appVersion = 0;
		try {
			appVersion = getPackageManager()
					.getPackageInfo(getPackageName(), 0).versionCode;
			API.CONTEXT = getApplicationContext();
			API.APP_VERSION = getPackageName()
					+ " "
					+ getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			Log.w("Startup", "Couldn't determine app version");
			e.printStackTrace();
		}

		if (API.isNetworkAvailable() && appVersion > prevAppVersion) {
			sharedPrefs.edit().putInt("prevAppVersion", appVersion).commit();
			Intent intent = new Intent(this, ChangelogActivity.class);
			intent.putExtra("prevAppVersion", prevAppVersion);
			intent.putExtra("appVersion", appVersion);
			startActivity(intent);
			return true;
		}

		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		return false;
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
		if (API.STANDARD_API.isLoggedIn())
			menu.findItem(R.id.action_login).setTitle(R.string.logout);
		else
			menu.findItem(R.id.action_login).setTitle(R.string.login);
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
		} else if (itemid == R.id.action_login)
			return API.STANDARD_API.isLoggedIn() ? logout(false) : showUserInfoOrLogin(0);
		else if (itemid == R.id.action_settings)
			return showSettings();
		return super.onOptionsItemSelected(item);
	}

	/*@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("closeapp", true);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}*/

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

		boolean noSwitch = false;
		switch (position) {
		case ITEM_HOME:
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new HomeFragment()).commit();
			break;
		case ITEM_PLAN:
			if(API.STANDARD_API.isLoggedIn())
				if(API.DATA.hasReplacements() || API.DATA.hasOthers() || API.DATA.hasPages() || API.DATA.hasTicker())
				getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new VPlanFragment()).commit();
				else {
					Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show();
					noSwitch = true;
				}
			else {
				showUserInfoOrLogin(R.string.login_needed);
				noSwitch = true;
			}
			break;
		case ITEM_MENSA:
			if (API.DATA.hasMensa())
				getSupportFragmentManager().beginTransaction()
						.replace(R.id.content_frame, new MensaFragment()).commit();
			else {
				Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show();
				noSwitch = true;
			}
			break;
		case ITEM_EVENTS:
			if (API.DATA.hasEvents())
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, new EventFragment()).commit();
			else {
				Toast.makeText(this, R.string.no_data, Toast.LENGTH_SHORT).show();
				noSwitch = true;
			}
			break;
		}

		if(noSwitch) {
			position = ITEM_HOME;
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.content_frame, new HomeFragment()).commit();
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

	AlertDialog loginDialog;
	@SuppressLint("NewApi")
	private boolean showUserInfoOrLogin(int message) {
		boolean showLogin = !API.STANDARD_API.isLoggedIn();
		if(showLogin) {
			drawerLayout.closeDrawer(drawer);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater = LayoutInflater
					.from(Build.VERSION.SDK_INT >= 11 ? builder.getContext() : this);
			final View view = inflater.inflate(R.layout.dialog_login, null);
			builder.setView(view);
			builder.setPositiveButton(R.string.login, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					login(
							((EditText)view.findViewById(R.id.login_username_field)).getText().toString(),
							((EditText)view.findViewById(R.id.login_password_field)).getText().toString());
				}
			});
			((EditText)view.findViewById(R.id.login_password_field)).setOnEditorActionListener(new OnEditorActionListener() {
				
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent e) {
					if(loginDialog != null && loginDialog.isShowing())
						loginDialog.dismiss();
					if(actionId == EditorInfo.IME_ACTION_DONE) {
						login(
								((EditText)view.findViewById(R.id.login_username_field)).getText().toString(),
								((EditText)view.findViewById(R.id.login_password_field)).getText().toString());
						return true;
					}
					return false;
				}
			});
			if(message != 0) {
				TextView mView = (TextView)view.findViewById(R.id.login_message);
				mView.setText(message);
				mView.setVisibility(View.VISIBLE);
			}
			builder.setNegativeButton(R.string.cancel, null);
			loginDialog = builder.show();
		}else {
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
		}
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
					logout();
				}
			});
			adb.show();
		} else {
			logout();
		}
		return true;
	}
	
	private void logout() {
		API.DATA.deleteToken();
		API.STANDARD_API.setUsername(null);
		API.STANDARD_API.setPassword(null);
		configureUsernameView();
	}
	
	private void login(String username, String password) {
		if(username == null || username.length() == 0) {
			showUserInfoOrLogin(R.string.no_username);
			return;
		}
		if(password == null || password.length() == 0) {
			showUserInfoOrLogin(R.string.no_password);
			return;
		}
		API.STANDARD_API.setUsername(username);
		API.STANDARD_API.setPassword(password);
		loadData();
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
			Log.i("LoadingData", "No network: loading cache");

			dataChanged();

			return;
		}

		loadingDialog = ProgressDialog.show(this, "",
				getText(R.string.loading_plan), true);
		loadingDialog.show();
		Log.i("LoadingData", "Loading new data from remote");

		new AllAsyncTask(this).execute();
	}

	public void finishedLoading(String error) {
		if (loadingDialog != null)
			loadingDialog.dismiss();

		
		if(error != null) {
			Toast.makeText(this, getText(R.string.error) + ": " + error, Toast.LENGTH_LONG).show();
			return;
		}
		
		
		dataChanged();

		SharedPreferences.Editor spe = getSharedPreferences("data",
				MODE_PRIVATE).edit();
		String json = new Gson().toJson(API.DATA);
		spe.putString("username", API.STANDARD_API.getUsername());
		spe.putString("data", json);
		spe.commit();

		AllObject data = API.DATA;
		final ApiResponse response = data.getParent();
		if(response == null) {
			Log.w("MainActivity", "ApiResponse == null");
			return;
		}

		if (!response.isCurrentVersion()) {
			Log.i("MainActivity", "Update available! " + response.getCurrentVersion() + ", has: " + (API.APP_VERSION.split(" ")[1]));

			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle(R.string.update_available_title);
			alertDialog.setPositiveButton(R.string.download,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent downloadIntent = new Intent(
									Intent.ACTION_VIEW, response.getApkDownloadUrl());
							startActivity(downloadIntent);
						}
					});
			alertDialog.setNegativeButton(R.string.dismiss, null);
			alertDialog.setMessage(R.string.update_available_text);
			alertDialog.show();
		} else
			Log.d("MainActivity", "App is current version: " + response.getCurrentVersion() + ", has: " + (API.APP_VERSION.split(" ")[1]));
		
		if(response.hasAdditionalMessage()) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle(R.string.additional_notes);
			alertDialog.setPositiveButton(R.string.ok, null);
			alertDialog.setMessage(response.getAdditionalMessage());
			alertDialog.show();
			
		}
	}

	private void dataChanged() {
		if(		fragmentPosition == ITEM_PLAN && 
					!(API.DATA.hasReplacements() || API.DATA.hasOthers() || API.DATA.hasPages() || API.DATA.hasTicker())
				|| fragmentPosition == ITEM_MENSA && !API.DATA.hasMensa()
				|| fragmentPosition == ITEM_EVENTS && !API.DATA.hasEvents()) {
			fragmentPosition = ITEM_HOME;
			Toast.makeText(this, R.string.no_data, Toast.LENGTH_LONG).show();
		}
		selectItem(fragmentPosition);
		configureUsernameView();
	}
	
	private View configureUsernameView() {
		TextView usernameView = (TextView) findViewById(R.id.drawer_username);
		View usernameViewBg = (View) findViewById(R.id.drawer_username_bg);
		AllObject data = API.DATA;
		if(API.STANDARD_API.isLoggedIn() && data != null && data.userInfo != null && data.userInfo.fullname != null && data.userInfo.fullname.length() > 0) {
			usernameView.setText(data.userInfo.fullname);
			Log.d("MainActivity", "UsernameView: " + data.userInfo.fullname);
		} else {
			usernameView.setText(R.string.login);
			Log.d("MainActivity", "UsernameView: " + getText(R.string.login).toString());
		}
		return usernameViewBg;
	}

	private void setupNavigationDrawer() {
		Log.d("MainActivity", "Setting up navigation drawer");
		drawer = (LinearLayout) findViewById(R.id.left_drawer);
		drawerList = (ListView) findViewById(R.id.drawer_list);
		drawerTitles = getResources().getStringArray(R.array.navigation_list);

		configureUsernameView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showUserInfoOrLogin(0);
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
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		int[] images = new int[ITEM_COUNT];
		images[ITEM_HOME] = R.drawable.ic_menu_home;
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
		return new SimpleAdapter(this, list, R.layout.drawer_item, from, to);
	}
}

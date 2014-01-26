package com.inf1315.vertretungsplan;

import com.google.gson.Gson;
import com.inf1315.vertretungsplan.api.*;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.Toast;

public class PlanActivity extends FragmentActivity implements
		ActionBar.TabListener {

	Dialog loadingDialog;
	Boolean tickerToast;
	Boolean logoutConf;
	PlanPagerAdapter planPagerAdapter;
	ViewPager viewPager;
	VertretungsplanAdapter todayReplacements;
	VertretungsplanAdapter tomorrowReplacements;
	private String username;
	private String password;
	private Gson gson;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Show the Up button in the action bar.
		actionBar.setDisplayHomeAsUpEnabled(true);

		todayReplacements = new VertretungsplanAdapter(this, 0,
				API.DATA.todayReplacementsList);
		tomorrowReplacements = new VertretungsplanAdapter(this, 0,
				API.DATA.tomorrowReplacementsList);

		// Create the adapter that will return a fragment for each of
		// the three
		// primary sections of the app.
		planPagerAdapter = new PlanPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		viewPager = (ViewPager) findViewById(R.id.plan_pager);
		viewPager.setAdapter(planPagerAdapter);

		// When swiping between different sections, select the
		// corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we
		// have
		// a reference to the Tab.
		viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		gson = new Gson();
		getPreferences();

		username = getIntent().getStringExtra("username");
		password = getIntent().getStringExtra("password");
		loadData(username, password);
	}

	@Override
	public void onResume() {

		super.onResume();
		getPreferences();
		if(API.reload)
			loadData(username, password);
	}

	private void dataChanged() {
			
		todayReplacements = new VertretungsplanAdapter(this, 0,
				API.DATA.todayReplacementsList);
		tomorrowReplacements = new VertretungsplanAdapter(this, 0,
				API.DATA.tomorrowReplacementsList);

		todayReplacements.notifyDataSetChanged();
		tomorrowReplacements.notifyDataSetChanged();
		planPagerAdapter.notifyDataSetChanged();

		ActionBar actionBar = getActionBar();
		actionBar.removeAllTabs();
		for (int i = 0; i < planPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(planPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		showTicker();
	}

	private void loadData(String username, String password) {
		API.reload = false;
		if (!isNetworkAvailable() && "".equals(API.DATA.hash)) {
			Intent intent = new Intent(this, LoginActivity.class);
			intent.putExtra("error", "NoInternetConnection");
			intent.putExtra("password", password);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return;
		} else if (!isNetworkAvailable()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.no_internet_connection_title);
			builder.setMessage(getText(R.string.no_internet_connection) + "\n"
					+ getText(R.string.usernanme) + API.DATA.userInfo.fullname
					+ "\n" + getText(R.string.old_data_message) + "\n"
					+ API.DATA.timeString);
			builder.setPositiveButton(R.string.ok, null);
			builder.show();

			dataChanged();

			return;
		}

		loadingDialog = ProgressDialog.show(this, "",
				getText(R.string.loading_plan), true);
		loadingDialog.show();

		new AllAsyncTask(this, username, password).execute();
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = conMan.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	void finishedLoading() {
		loadingDialog.dismiss();
		dataChanged();

		SharedPreferences.Editor spe = getSharedPreferences("data",
				MODE_PRIVATE).edit();
		String json = gson.toJson(API.DATA);
		spe.putString("data", json);

		spe.apply();
	}

	public void getPreferences() {

		try {
			SharedPreferences sharedPref = getSharedPreferences(
					"com.inf1315.vertretungsplan_preferences", MODE_PRIVATE);
			tickerToast = sharedPref.getBoolean("pref_toast", true);
			logoutConf = sharedPref.getBoolean("pref_logout", true);
		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	private void logoutFromPlan() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(R.string.logout);
		adb.setMessage(R.string.really_logout);
		adb.setNegativeButton(android.R.string.no, null);
		adb.setPositiveButton(android.R.string.yes, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				NavUtils.navigateUpFromSameTask(PlanActivity.this);
			}
		});
		adb.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.plan_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			if (logoutConf) {
				logoutFromPlan();
			} else {
				NavUtils.navigateUpFromSameTask(PlanActivity.this);
			}
			return true;
		case R.id.action_show_ticker:
			showTicker();
			return true;
		case R.id.action_reload_plan:
			loadData(username, password);
			return true;
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

	@Override
	public void onBackPressed() {
		if (logoutConf)
			logoutFromPlan();
		else {
			NavUtils.navigateUpFromSameTask(PlanActivity.this);
		}
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding
		// page in
		// the ViewPager.
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public void showTicker() {

		if (tickerToast) {

			if (!API.DATA.tickers.isEmpty()) {
				String ticker = "";
				for (TickerObject to : API.DATA.tickers) {
					ticker += to.toString() + "\n\n";
				}
				ticker = ticker.substring(0, ticker.length() - 2);
				Toast.makeText(this, ticker, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getApplicationContext(), R.string.no_ticker,
						Toast.LENGTH_SHORT).show();
			}
		}

		else {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			String message = "";
			if (API.DATA.tickers.isEmpty())
				message = (String) getText(R.string.no_ticker);
			else {
				for (int i = 0; i < API.DATA.tickers.size(); i++) {
					if (i == API.DATA.tickers.size() - 1)
						message = message + API.DATA.tickers.get(i).toString();
					else
						message = message + API.DATA.tickers.get(i).toString()
								+ "\n";
				}
			}
			builder.setMessage(message).setTitle(R.string.ticker_dialog_title);

			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {

						}

					});

			AlertDialog dialog = builder.create();
			dialog.show();

		}

	}

	public class PlanPagerAdapter extends FragmentPagerAdapter {

		public PlanPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			int reps = 0;
			if (todayReplacements.hasReplacements())
				reps++;
			if (tomorrowReplacements.hasReplacements())
				reps++;
			if (position < reps) {
				boolean isTabToday = position == 0
						&& todayReplacements.hasReplacements();
				Fragment fragment = new PlanFragment();
				Bundle args = new Bundle();
				args.putBoolean(PlanFragment.ARG_TODAY, isTabToday);
				fragment.setArguments(args);
				return fragment;
			}
			position -= reps;
			if (API.DATA.pages.size() > position) {
				Fragment fragment = new PageFragment();
				Bundle args = new Bundle();
				args.putInt(PageFragment.SITE_NUMBER, position);
				fragment.setArguments(args);
				return fragment;
			}
			return null;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (todayReplacements.hasReplacements())
				count++;
			if (tomorrowReplacements.hasReplacements())
				count++;
			if (!API.DATA.pages.isEmpty())
				count += API.DATA.pages.size();
			return count;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			int reps = 0;
			if (todayReplacements.hasReplacements())
				reps++;
			if (tomorrowReplacements.hasReplacements())
				reps++;
			if (position < reps) {
				if (position == 0)
					return getResources()
							.getString(
									todayReplacements.hasReplacements() ? R.string.today
											: R.string.tomorrow);
				else
					return getResources().getString(R.string.tomorrow);
			}
			position -= reps;
			if (API.DATA.pages.size() > position)
				return API.DATA.pages.get(position).title;
			return null;
		}
	}

}

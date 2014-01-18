package com.inf1315.vertretungsplan;

import java.util.ArrayList;
import java.util.List;

import com.inf1315.vertretungsplan.api.*;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.widget.Toast;



	

public class PlanActivity extends FragmentActivity implements ActionBar.TabListener
{
	
	
	SharedPreferences sharedPref;
	Boolean tickerToast;
    PlanPagerAdapter planPagerAdapter;
    ViewPager viewPager;
    List<TickerObject> tickers = new ArrayList<TickerObject>();
    List<ReplacementObject> todayReplacements = new ArrayList<ReplacementObject>();
    List<ReplacementObject> tomorrowReplacements = new ArrayList<ReplacementObject>();
    List<PageObject> pages = new ArrayList<PageObject>();
    List<OtherObject> todayOthers = new ArrayList<OtherObject>();
    List<OtherObject> tomorrowOthers = new ArrayList<OtherObject>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan);

		getPreferences();

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Show the Up button in the action bar.
		actionBar.setDisplayHomeAsUpEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		planPagerAdapter = new PlanPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		viewPager = (ViewPager) findViewById(R.id.plan_pager);
		viewPager.setAdapter(planPagerAdapter);


		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < planPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(planPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		loadData();
	}

	

		
	

    private void loadData()
    {
    	String username = getSharedPreferences("data", MODE_PRIVATE).getString("username", "");
    	new AllAsyncTask(this, username).execute();
    }
    
    void finishedLoading()

    {
	LoginActivity.loadingDialog.hide();
	showTicker();
    }
    
    public void getPreferences() {

		try {
			sharedPref = getSharedPreferences(
					"com.inf1315.vertretungsplan_preferences", MODE_PRIVATE);
			tickerToast = sharedPref.getBoolean("pref_toast", true);
		} catch (Exception e) {

			e.printStackTrace();

		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
	// Inflate the menu; this adds items to the action bar if it is present.
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.plan_activity_actions, menu);
	return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
	switch (item.getItemId())
	{
	    case android.R.id.home:
	    	AlertDialog.Builder adb = new AlertDialog.Builder(getApplicationContext());
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
	    	return true;
	    case R.id.action_show_ticker:
		showTicker();
		return true;
	}
	return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
	// When the given tab is selected, switch to the corresponding page in
	// the ViewPager.
	viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {}

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {}

    public void showTicker() {

		if (tickerToast) {

			if (!tickers.isEmpty()) {
				for (TickerObject to : tickers) {
					Toast toast = Toast.makeText(getApplicationContext(),
							to.toString(), Toast.LENGTH_LONG);
					toast.show();
				}
			} else {
				Toast toast = Toast.makeText(getApplicationContext(),
						ticker < 0 ? R.string.ticker_loading_error
								: R.string.no_ticker,
						ticker < 0 ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
				toast.show();
			}
		}

		else {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			String message = "";
			for (int i = 0; i < tickers.size(); i++) {

				if (i == tickers.size() - 1)
					message = message + tickers.get(i).toString();
				else
					message = message + tickers.get(i).toString() + "\n";
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

    public class PlanPagerAdapter extends FragmentPagerAdapter
    {

	public PlanPagerAdapter(FragmentManager fm)
	{
	    super(fm);
	}

	@Override
	public Fragment getItem(int position)
	{
	    Fragment fragment = new PlanFragment();
	    Bundle args = new Bundle();
	    boolean isTabToday = (position == 0) ? true : false;
	    args.putBoolean(PlanFragment.ARG_TODAY, isTabToday);
	    fragment.setArguments(args);
	    return fragment;
	}

	@Override
	public int getCount()
	{
	    // There are 2 Tabs
	    return 2;
	}

	@Override
	public CharSequence getPageTitle(int position)
	{
	    switch (position)
	    {
		case 0:
		    return getResources().getString(R.string.today);
		case 1:
		    return getResources().getString(R.string.tomorrow);
	    }
	    return null;
	}
    }


}

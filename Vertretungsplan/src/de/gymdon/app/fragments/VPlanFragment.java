package de.gymdon.app.fragments;

import de.gymdon.app.*;
import de.gymdon.app.activities.MainActivity;
import de.gymdon.app.api.*;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.*;
import android.widget.Toast;

public class VPlanFragment extends Fragment implements ActionBar.TabListener {

	View rootView;
	MainActivity parentActivity;
	PlanPagerAdapter planPagerAdapter;
	ViewPager viewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		parentActivity = (MainActivity) getActivity();
		showTicker(getActivity());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ActionBar actionBar = parentActivity.getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_plan, container, false);

		planPagerAdapter = new PlanPagerAdapter(getChildFragmentManager());

		// Set up the ViewPager with the sections adapter.
		viewPager = (ViewPager) rootView.findViewById(R.id.plan_pager);
		viewPager.setAdapter(planPagerAdapter);
		viewPager.setOffscreenPageLimit(2);

		viewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						parentActivity.getSupportActionBar()
								.setSelectedNavigationItem(position);
					}
				});

		ActionBar actionBar = parentActivity.getSupportActionBar();
		actionBar.removeAllTabs();
		for (int i = 0; i < planPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(planPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}

		return rootView;
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

	public static void showTicker(Context context) {
		if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
				"pref_toast", true)) {
			if (!API.DATA.tickers.isEmpty()) {
				String ticker = "";
				for (TickerObject to : API.DATA.tickers) {
					ticker += to.toString() + "\n\n";
				}
				ticker = ticker.substring(0, ticker.length() - 2);
				Toast.makeText(context, ticker, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, R.string.no_ticker, Toast.LENGTH_SHORT)
						.show();
			}
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);

			String message = "";
			if (API.DATA.tickers.isEmpty())
				message = (String) context.getText(R.string.no_ticker);
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
			builder.setPositiveButton(R.string.ok, null);
			builder.show();
		}

	}

	public class PlanPagerAdapter extends FragmentPagerAdapter {

		public PlanPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			int reps = 0;
			if (API.hasReplacements(true))
				reps++;
			if (API.hasReplacements(false))
				reps++;
			if (position < reps) {
				boolean isTabToday = position == 0 && API.hasReplacements(true);
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
			if (API.hasReplacements(true))
				count++;
			if (API.hasReplacements(false))
				count++;
			if (!API.DATA.pages.isEmpty())
				count += API.DATA.pages.size();
			return count;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			int reps = 0;
			if (API.hasReplacements(true))
				reps++;
			if (API.hasReplacements(true))
				reps++;
			if (position < reps) {
				if (position == 0)
					return getResources().getString(
							API.hasReplacements(true) ? R.string.today
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

package com.inf1315.vertretungsplan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlanFragment extends Fragment {

	public static final String ARG_TODAY = "ARG_TODAY";

	// true, if today's fragment
	private boolean argToday = true;

	public PlanFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.argToday = getArguments().getBoolean(ARG_TODAY);
		View rootView = inflater.inflate(R.layout.fragment_plan, container, false);
		
		return rootView;
	}
}
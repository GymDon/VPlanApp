package com.inf1315.vertretungsplan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PlanFragment extends Fragment {

	public static final String ARG_TODAY = "ARG_TODAY";

	// true, wenn Fragment für heute
	private boolean arg_today = true;

	public PlanFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.arg_today = getArguments().getBoolean(ARG_TODAY);
		View rootView = inflater.inflate(R.layout.fragment_plan, container, false);
		
		return rootView;
	}
}
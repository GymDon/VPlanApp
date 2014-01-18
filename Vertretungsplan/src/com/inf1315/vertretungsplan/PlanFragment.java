package com.inf1315.vertretungsplan;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PlanFragment extends Fragment {

	public static final String ARG_TODAY = "ARG_TODAY";

	// true, if today's fragment
	private boolean argToday = true;

	public PlanFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.argToday = getArguments().getBoolean(ARG_TODAY);
		View rootView = inflater.inflate(R.layout.fragment_plan, container, false);
		
		ListView lv = (ListView) rootView.findViewById(R.id.plan_ListView);
		PlanActivity pa = (PlanActivity) getActivity();
		VertretungsplanAdapter va = argToday ? pa.todayReplacements : pa.tomorrowReplacements;
		lv.setAdapter(va);
		
		return rootView;
	}
}
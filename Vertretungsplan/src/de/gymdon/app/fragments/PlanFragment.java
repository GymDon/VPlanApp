package de.gymdon.app.fragments;

import de.gymdon.app.*;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class PlanFragment extends Fragment {

	public static final String ARG_TODAY = "ARG_TODAY";

	// true, if today's fragment
	private boolean argToday = true;
	private VertretungsplanAdapter adapter;

	public PlanFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		argToday = getArguments().getBoolean(ARG_TODAY);
		adapter = new VertretungsplanAdapter(getActivity(), argToday);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_plan, container,
				false);

		ExpandableListView lv = (ExpandableListView) rootView
				.findViewById(R.id.plan_ListView);
		lv.setAdapter(adapter);
		if (adapter.getGroupCount() == 1)
			lv.expandGroup(0);

		return rootView;
	}
}
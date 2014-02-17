package de.gymdon.app.fragments;

import de.gymdon.app.EventsAdapter;
import de.gymdon.app.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

public class EventPageFragment extends Fragment {

	public static final String TIMESTAMP_WEEK_BEGIN = "TIMESTAMP_WEEK_BEGIN";
	private long timestampWeekToday;
	private EventsAdapter adapter;

	public EventPageFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		timestampWeekToday = getArguments().getLong(TIMESTAMP_WEEK_BEGIN);
		adapter = new EventsAdapter(getActivity(), timestampWeekToday);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_plan, container,
				false);

		ExpandableListView lv = (ExpandableListView) rootView
				.findViewById(R.id.plan_ListView);
		lv.setAdapter(adapter);
		for (int i = 0; i < adapter.getGroupCount(); i++) {
			lv.expandGroup(i);
		}

		return rootView;
	}
}
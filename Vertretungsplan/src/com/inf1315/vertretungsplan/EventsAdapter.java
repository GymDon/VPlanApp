package com.inf1315.vertretungsplan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inf1315.vertretungsplan.api.*;

public class EventsAdapter extends BaseExpandableListAdapter {

	private Context context;
	private long timestampWeekBegin;
	private List<Event>[] events;

	public EventsAdapter(Context context, long timestampWeekBegin) {
		if (context == null)
			throw new UnsupportedOperationException(
					"null is not supported as valid for context");
		this.context = context;
		this.timestampWeekBegin = timestampWeekBegin;
		notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return events[groupPosition].get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		Event o = (Event) getChild(groupPosition, childPosition);

		RelativeLayout group = new RelativeLayout(context);
		group.setPadding(10, 4, 8, 4);

		TextView line1 = new TextView(context);
		TextView line2 = new TextView(context);

		line1.setId(4944947);
		line2.setId(4944948);

		line1.setText(o.title);
		line2.setText(o.detail);

		line1.setGravity(Gravity.LEFT | Gravity.TOP);
		line2.setGravity(Gravity.LEFT | Gravity.BOTTOM);

		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		params1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		params1.setMargins(15, 0, 0, 0);

		params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params2.addRule(RelativeLayout.ALIGN_LEFT, line1.getId());
		params2.addRule(RelativeLayout.BELOW, line1.getId());

		group.addView(line1, params1);
		group.addView(line2, params2);

		return group;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return events[groupPosition].size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		int arrayPosotion = -1;
		while (groupPosition > -1) {
			arrayPosotion += 1;
			if (!events[arrayPosotion].isEmpty()) {
				groupPosition -= 1;
			}
		}
		return events[arrayPosotion];
	}

	@Override
	public int getGroupCount() {
		int count = 0;
		for (List<Event> list : events) {
			count += list.isEmpty() ? 0 : 1;
		}
		return count;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		RelativeLayout group = new RelativeLayout(context);
		group.setPadding(10, 4, 8, 4);

		ImageView iv = new ImageView(context);
		iv.setImageResource(isExpanded ? R.drawable.ic_action_collapse
				: R.drawable.ic_action_expand);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(85, 0, 0, 0);

		@SuppressWarnings("unchecked")
		long timestamp = ((List<Event>) getGroup(groupPosition)).get(0).timestamp;
		TextView tv = new TextView(context);
		tv.setText(getDateString(timestamp));
		tv.setTextSize(20);

		group.addView(iv);
		group.addView(tv, params);

		return group;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		events = new List[7];
		for (int i = 0; i < events.length; i++) {
			events[i] = new ArrayList<Event>();
		}
		for (Event event : API.DATA.events) {
			if (event.timestamp >= timestampWeekBegin
					&& event.timestamp < timestampWeekBegin + 604800L) {
				int day = (int) ((event.timestamp - timestampWeekBegin) / 86400L);
				events[day].add(event);
			}

		}
	}

	@SuppressLint("NewApi")
	public static String getDateString(long timestamp) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timestamp*1000L);
		Date date = calendar.getTime();
		SimpleDateFormat sdf;
		if (Build.VERSION.SDK_INT < 9)
			sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
		else
			sdf = new SimpleDateFormat("cccc, dd. LLLL yyyy",
					Locale.getDefault());
		return sdf.format(date);
	}
}

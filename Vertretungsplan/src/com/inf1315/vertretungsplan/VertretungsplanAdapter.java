package com.inf1315.vertretungsplan;

import java.util.List;

import com.inf1315.vertretungsplan.api.ReplacementObject;

import android.content.*;
import android.view.*;
import android.widget.*;

public class VertretungsplanAdapter extends ArrayAdapter<ReplacementObject> {
	public VertretungsplanAdapter(Context context, int resource,
			List<ReplacementObject> objects) {
		super(context, resource, objects);
	}

	public boolean hasReplacements() {
		return !isEmpty();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout group = new RelativeLayout(getContext());
		group.setPadding(10, 4, 8, 4);

		TextView lesson = new TextView(getContext());
		TextView line1 = new TextView(getContext());
		TextView line2 = new TextView(getContext());
		ReplacementObject o = getItem(position);

		lesson.setText(Integer.toString(o.lesson));
		
		line1.setText(	(o.room.length() > 0 ? o.room + (o.teacherChange ? ": " : "") : "") + 
						(o.teacherChange ? (o.teacherLong != null ? o.teacherLong + " (" + o.teacher + ")" : o.teacher) +
						(o.replacement.length() > 0 ?  " -> " + o.replacement : "") : ""));
		line2.setText(o.comment);

		lesson.setTextSize(30);
		lesson.setEms(3);

		line1.setGravity(Gravity.LEFT | Gravity.TOP);
		line2.setGravity(Gravity.LEFT | Gravity.BOTTOM);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);

		params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);

		params1.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		params1.addRule(RelativeLayout.RIGHT_OF, lesson.getId());

		params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		params2.addRule(RelativeLayout.ALIGN_LEFT, line1.getId());
		params2.addRule(RelativeLayout.BELOW, line1.getId());

		group.addView(lesson, params);
		group.addView(line1, params1);
		group.addView(line2, params2);

		if (o.addition)
			group.setBackgroundColor(0xFFFF5555);
		return group;
	}
}

package com.inf1315.vertretungsplan;

import java.util.List;

import com.inf1315.vertretungsplan.api.ReplacementObject;

import android.content.*;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

public class VertretungsplanAdapter extends ArrayAdapter<ReplacementObject> {
	public VertretungsplanAdapter(Context context, int resource,
			List<ReplacementObject> objects) {
		super(context, resource, objects);
	}

	public boolean hasReplacements()
	{
		return !isEmpty();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv = new TextView(getContext());
		ReplacementObject o = getItem(position);
		tv.setPadding(10, 4, 8, 4);
		tv.append(o.lesson
				+ ". "
				+ o.teacher
				+ " -> "
				+ o.comment
				+ (o.replacement.trim().length() > 0 ? " ("
						+ o.replacement.trim() + ")" : "")
				+ (o.room.length() > 0 ? " in " + o.room : "") + "\n");
		// tv.append(o.lesson + ". " + Teachers.expand(o.teacher) + " -> " +
		// o.comment + (o.replacement.trim().length() > 0 ? " (" +
		// o.replacement.trim() + ")" : "") + (o.room.length() > 0 ? " in " +
		// o.room : "") + "\n");
		if (o.addition)
			tv.setTextColor(Color.RED);
		return tv;
	}
}

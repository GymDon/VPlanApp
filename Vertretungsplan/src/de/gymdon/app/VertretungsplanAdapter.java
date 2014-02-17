package de.gymdon.app;

import java.util.Collections;
import java.util.List;

import de.gymdon.app.api.*;
import de.gymdon.app.api.OtherObject.OtherType;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class VertretungsplanAdapter extends BaseExpandableListAdapter {

	private Context context;
	private boolean today;

	public VertretungsplanAdapter(Context context, boolean today) {
		if (context == null)
			throw new UnsupportedOperationException(
					"null is not supported as valid for context");
		this.context = context;
		this.today = today;
	}

	private List<ReplacementObject> getReplacements() {
		return today ? API.DATA.todayReplacementsList
				: API.DATA.tomorrowReplacementsList;
	}

	private List<OtherObject> getOthers() {
		return today ? API.DATA.todayOthers : API.DATA.tomorrowOthers;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groupPosition < getReplacements().size() ? null : getOthers()
				.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (groupPosition < getReplacements().size())
			return null;

		OtherObject o;
		int count = 0;
		int type = -1;
		int i = 0;
		while (i < childPosition) {
			o = getOthers().get(count);
			if (o.type.ordinal() != type)
				type = o.type.ordinal();
			else
				count++;
			i++;
		}
		o = getOthers().get(count);
		return type == o.type.ordinal() ? getOtherContentView(getOthers().get(
				count)) : getOtherCategoryView(o.type);
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupPosition < getReplacements().size())
			return 0;

		int[] array = new int[6];
		for (OtherObject o : getOthers()) {
			array[o.type.ordinal()] = 1;
		}
		int count = 0;
		for (int i : array) {
			count += i;
		}
		return count + getOthers().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupPosition < getReplacements().size() ? getReplacements()
				.get(groupPosition) : getOthers();
	}

	@Override
	public int getGroupCount() {
		return getReplacements().size() + (getOthers().isEmpty() ? 0 : 1);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (groupPosition < getReplacements().size()) {
			RelativeLayout group = new RelativeLayout(context);
			group.setPadding(10, 4, 8, 4);

			TextView lesson = new TextView(context);
			TextView line1 = new TextView(context);
			TextView line2 = new TextView(context);
			ReplacementObject o = (ReplacementObject) getGroup(groupPosition);

			lesson.setId(4944941);
			line1.setId(4944942);
			line2.setId(4944943);

			lesson.setText(Integer.toString(o.lesson));

			if (API.DATA.userInfo.isStudent)
				line1.setText((o.room.length() > 0 ? o.room
						+ (o.teacherChange ? ": " : "") : "")
						+ (o.teacherChange ? (o.teacherLong != null ? o.teacherLong
								+ " (" + o.teacher + ")"
								: o.teacher)
								+ (o.replacement.length() > 0 ? " -> "
										+ o.replacement : "")
								: ""));
			else
				line1.setText((o.room.length() > 0 ? o.room + ": " : "")
						+ (o.grade.length() > 0 ? o.grade : ""));

			line2.setText(o.comment);

			lesson.setTextSize(30);
			lesson.setEms(1);

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
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,
					RelativeLayout.TRUE);
			params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
					RelativeLayout.TRUE);

			params1.addRule(RelativeLayout.ALIGN_PARENT_TOP,
					RelativeLayout.TRUE);
			params1.addRule(RelativeLayout.RIGHT_OF, lesson.getId());
			params1.setMargins(15, 0, 0, 0);

			params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,
					RelativeLayout.TRUE);
			params2.addRule(RelativeLayout.ALIGN_LEFT, line1.getId());
			params2.addRule(RelativeLayout.BELOW, line1.getId());

			group.addView(lesson, params);
			group.addView(line1, params1);
			group.addView(line2, params2);

			if (o.addition)
				group.setBackgroundColor(context.getResources().getColor(
						R.color.addition));
			return group;
		} else {
			RelativeLayout group = new RelativeLayout(context);
			group.setPadding(10, 4, 8, 4);

			ImageView iv = new ImageView(context);
			iv.setImageResource(isExpanded ? R.drawable.ic_action_collapse
					: R.drawable.ic_action_expand);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(85, 0, 0, 0);

			TextView tv = new TextView(context);
			tv.setText(R.string.others);
			tv.setTextSize(20);

			group.addView(iv);
			group.addView(tv, params);

			return group;
		}
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
		Collections.sort(getOthers());
		super.notifyDataSetChanged();
	}

	private View getOtherCategoryView(OtherType type) {
		RelativeLayout group = new RelativeLayout(context);
		group.setPadding(10, 4, 8, 4);

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		TextView tv = new TextView(context);
		tv.setText(type.toString());
		tv.setTextSize(20);

		group.addView(tv, params);
		return group;
	}

	private View getOtherContentView(OtherObject o) {
		RelativeLayout group = new RelativeLayout(context);
		group.setPadding(10, 4, 8, 4);

		TextView lesson = new TextView(context);
		TextView line1 = new TextView(context);
		TextView line2 = new TextView(context);

		lesson.setId(4944944);
		line1.setId(4944945);
		line2.setId(4944946);

		lesson.setText(o.lesson);
		line1.setText(o.name);
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
		params1.addRule(RelativeLayout.RIGHT_OF, lesson.getId());
		params1.setMargins(15, 0, 0, 0);

		params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
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

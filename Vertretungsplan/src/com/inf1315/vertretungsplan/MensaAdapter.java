package com.inf1315.vertretungsplan;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import com.inf1315.vertretungsplan.api.API;
import com.inf1315.vertretungsplan.api.MensaClient;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MensaAdapter extends ArrayAdapter<MensaClient> {

	private Context context;

	public MensaAdapter(Context context) {
		super(context, 0, API.DATA.mensa);
		Collections.sort(API.DATA.mensa);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout group = new RelativeLayout(context);
		group.setPadding(10, 4, 8, 4);

		TextView date = new TextView(context);
		TextView line1 = new TextView(context);
		TextView line2 = new TextView(context);
		MensaClient o = API.DATA.mensa.get(position);

		date.setId(4944949);
		line1.setId(4944950);
		line2.setId(4944951);

		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM",
				Locale.getDefault());
		calendar.setTimeInMillis(o.timestamp * 1000L);
		String day = sdf.format(calendar.getTime());
		date.setText(day);

		line1.setText(context.getText(R.string.menu1) + o.menu1);
		line2.setText(context.getText(R.string.menu2) + o.menu2);

		date.setTextSize(30);
		date.setEms(3);

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
		params1.addRule(RelativeLayout.RIGHT_OF, date.getId());

		params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params2.addRule(RelativeLayout.ALIGN_LEFT, line1.getId());
		params2.addRule(RelativeLayout.BELOW, line1.getId());

		group.addView(date, params);
		group.addView(line1, params1);
		group.addView(line2, params2);

		return group;
	}

}

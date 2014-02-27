package de.gymdon.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import de.gymdon.app.api.API;
import de.gymdon.app.api.MensaServer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MensaAdapter extends ArrayAdapter<MensaServer> {

	private Context context;

	public MensaAdapter(Context context) {
		super(context, 0, API.DATA.mensa);
		Collections.sort(API.DATA.mensa);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MensaServer o = API.DATA.mensa.get(position);
		LayoutInflater inflater = LayoutInflater.from(context);
		
		View mensaItem = inflater.inflate(R.layout.mensa_item, null);
		
		TextView date = (TextView) mensaItem.findViewById(R.id.mensa_date);
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM", Locale.getDefault());
		calendar.setTimeInMillis(o.timestamp * 1000L);
		String day = sdf.format(calendar.getTime());
		date.setText(day);

		TextView type = (TextView) mensaItem.findViewById(R.id.mensa_type);
		type.setText(context.getResources().getStringArray(R.array.mensa_types)[o.type]);
		
		TextView value = (TextView) mensaItem.findViewById(R.id.mensa_value);
		value.setText(o.value);
		
		return mensaItem;
	}

}

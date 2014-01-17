package com.inf1315.vertretungsplan.api;

import org.json.*;

import android.text.Html;

public class TickerObject extends ApiResult implements Comparable<TickerObject> {

	public final int id;
	public final boolean automatic;
	public final String value;
	public final long fromTimestamp;
	public final long toTimestamp;
	public final int order;

	public TickerObject(JSONObject obj) throws JSONException {
		id = obj.getInt("id");
		automatic = obj.getInt("automatic") != 0;
		value = strip(obj.getString("value"));
		fromTimestamp = obj.getLong("fromTimestap");
		toTimestamp = obj.getLong("toTimestamp");
		order = obj.getInt("order");
	}

	private static String strip(String in) {
		return Html.fromHtml(in).toString().replace('\u00A0', ' ').trim();
	}
	
	@Override
	public int compareTo(TickerObject other) {
		return fromTimestamp > other.fromTimestamp ? 1
				: fromTimestamp < other.fromTimestamp ? -1
						: toTimestamp > other.toTimestamp ? 1
								: toTimestamp < other.toTimestamp ? -1
										: order > other.order ? 1
												: order < other.order ? -1
														: id - other.id;
	}
}

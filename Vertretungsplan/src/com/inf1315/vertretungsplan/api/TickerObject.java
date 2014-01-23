package com.inf1315.vertretungsplan.api;

import org.json.*;

public class TickerObject extends ApiResult implements Comparable<TickerObject> {

	public final int id;
	public final boolean automatic;
	public final String value;
	public final long fromTimestamp;
	public final long toTimestamp;
	public final int order;

	public TickerObject(JSONObject obj) throws JSONException {
		id = obj.getInt("id");
		automatic = obj.getBoolean("automatic");
		value = obj.getString("value");
		fromTimestamp = obj.getLong("from_stamp");
		toTimestamp = obj.getLong("to_stamp");
		order = obj.getInt("order");
	}

	@Override
	public int compareTo(TickerObject other) {
		return fromTimestamp > other.fromTimestamp ? 1
				: fromTimestamp < other.fromTimestamp ? -1
						: toTimestamp > other.toTimestamp ? 1
								: toTimestamp < other.toTimestamp ? -1
										: order > other.order ? 1
												: order < other.order ? -1 : id
														- other.id;
	}

	@Override
	public String toString() {
		return value;
	}
}

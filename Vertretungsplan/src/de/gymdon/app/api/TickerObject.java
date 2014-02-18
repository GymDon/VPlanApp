package de.gymdon.app.api;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (automatic ? 1231 : 1237);
		result = prime * result
				+ (int) (fromTimestamp ^ (fromTimestamp >>> 32));
		result = prime * result + id;
		result = prime * result + order;
		result = prime * result + (int) (toTimestamp ^ (toTimestamp >>> 32));
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TickerObject other = (TickerObject) obj;
		if (automatic != other.automatic)
			return false;
		if (fromTimestamp != other.fromTimestamp)
			return false;
		if (id != other.id)
			return false;
		if (order != other.order)
			return false;
		if (toTimestamp != other.toTimestamp)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}

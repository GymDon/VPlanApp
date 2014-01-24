package com.inf1315.vertretungsplan.api;

import org.json.*;

public class PageObject extends ApiResult implements Comparable<PageObject> {

	public final int id;
	public final int order;
	public final String title;
	public final String content;
	public final long fromTimestamp;
	public final long toTimestamp;
	public final boolean pupils;
	public final boolean teachers;

	public PageObject(JSONObject obj) throws JSONException {
		id = obj.getInt("id");
		order = obj.getInt("order_num");
		title = obj.getString("title");
		content = obj.getString("content");
		fromTimestamp = obj.getLong("timestamp_from");
		toTimestamp = obj.getLong("timestamp_end");
		pupils = obj.getBoolean("pupils");
		teachers = obj.getBoolean("teachers");
	}

	@Override
	public int compareTo(PageObject other) {
		return fromTimestamp > other.fromTimestamp ? 1
				: fromTimestamp < other.fromTimestamp ? -1
						: toTimestamp > other.toTimestamp ? 1
								: toTimestamp < other.toTimestamp ? -1
										: order > other.order ? 1
												: order < other.order ? -1 : id
														- other.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result
				+ (int) (fromTimestamp ^ (fromTimestamp >>> 32));
		result = prime * result + id;
		result = prime * result + order;
		result = prime * result + (pupils ? 1231 : 1237);
		result = prime * result + (teachers ? 1231 : 1237);
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + (int) (toTimestamp ^ (toTimestamp >>> 32));
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
		PageObject other = (PageObject) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (fromTimestamp != other.fromTimestamp)
			return false;
		if (id != other.id)
			return false;
		if (order != other.order)
			return false;
		if (pupils != other.pupils)
			return false;
		if (teachers != other.teachers)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (toTimestamp != other.toTimestamp)
			return false;
		return true;
	}

}

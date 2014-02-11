package com.inf1315.vertretungsplan.api;

import org.json.*;

public class Event extends ApiResult implements Comparable<Event> {
	public final int id;
	public final long timestamp;
	public final String title;
	public final String detail;

	public Event(JSONObject obj) throws JSONException {
		id = obj.getInt("id");
		timestamp = obj.getLong("timestamp");
		title = obj.getString("title");
		detail = obj.getString("detail");
	}

	@Override
	public int compareTo(Event o) {
		return this.timestamp < o.timestamp ? -1
				: this.timestamp > o.timestamp ? 1 : this.id - o.id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((detail == null) ? 0 : detail.hashCode());
		result = prime * result + id;
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		Event other = (Event) obj;
		if (detail == null) {
			if (other.detail != null)
				return false;
		} else if (!detail.equals(other.detail))
			return false;
		if (id != other.id)
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

}

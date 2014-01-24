package com.inf1315.vertretungsplan.api;

import android.annotation.SuppressLint;
import org.json.*;

public class OtherObject extends ApiResult {
	public final int id;
	public final OtherType type;
	public final String name;
	public final String lesson;
	public final String comment;
	public final long timestamp;
	public final long timestampUpdate;
	public final boolean addition;
	public final boolean isToday;

	@SuppressLint("DefaultLocale")
	public OtherObject(JSONObject obj) throws JSONException {
		id = obj.getInt("id");
		type = OtherType.valueOf(obj.getString("type").toUpperCase());
		name = obj.getString("name");
		lesson = obj.getString("lesson");
		comment = obj.getString("comment");
		timestamp = obj.getLong("timestamp");
		timestampUpdate = obj.getLong("timestamp_update");
		addition = obj.getBoolean("addition");
		isToday = obj.getBoolean("is_today");
	}

	public static enum OtherType {
		T, G, R, S, A, N;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (addition ? 1231 : 1237);
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + id;
		result = prime * result + (isToday ? 1231 : 1237);
		result = prime * result + ((lesson == null) ? 0 : lesson.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result
				+ (int) (timestampUpdate ^ (timestampUpdate >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		OtherObject other = (OtherObject) obj;
		if (addition != other.addition)
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (id != other.id)
			return false;
		if (isToday != other.isToday)
			return false;
		if (lesson == null) {
			if (other.lesson != null)
				return false;
		} else if (!lesson.equals(other.lesson))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (timestampUpdate != other.timestampUpdate)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

}

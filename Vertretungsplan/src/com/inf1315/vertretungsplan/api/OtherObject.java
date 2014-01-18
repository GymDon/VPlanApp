package com.inf1315.vertretungsplan.api;

import android.annotation.SuppressLint;
import org.json.*;

public class OtherObject extends ApiResult
{
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
	public OtherObject(JSONObject obj) throws JSONException
	{
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
	
	public static enum OtherType
	{
		T,G,R,S,A,N;
	}
}

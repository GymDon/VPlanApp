package com.inf1315.vertretungsplan.api;

import org.json.JSONException;
import org.json.JSONObject;

public class ReplacementObject extends ApiResult implements Comparable<ReplacementObject> {
	
	public final int id;
	public final String gradePre;
	public final String grade;
	public final String gradeLast;
	public final int lesson;
	public final String teacher;
	public final String replacement;
	public final String room;
	public final String comment;
	public final boolean isToday;
	public final boolean addition;

	public ReplacementObject(JSONObject json) throws JSONException {
		id = json.getInt("id");
		gradePre = json.getString("grade_pre");
		grade = json.getString("grade");
		gradeLast = json.getString("grade_last");
		lesson = json.getInt("lesson");
		teacher = json.getString("teacher");
		replacement = (json.getString("replacement"));
		room = json.getString("room");
		comment = json.getString("comment");
		isToday = json.getBoolean("is_today");
		addition = json.getBoolean("addition");
	}

	@Override
	public int compareTo(ReplacementObject other) {
		if (isToday != other.isToday) return isToday ? -1 : 1;
		return lesson - other.lesson;
	}
}

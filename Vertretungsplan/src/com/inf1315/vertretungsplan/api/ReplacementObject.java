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
		gradePre = strip(json.getString("grade_pre"));
		grade = strip(json.getString("grade"));
		gradeLast = strip(json.getString("grade_last"));
		lesson = json.getInt("lesson");
		teacher = strip(json.getString("teacher"));
		replacement = strip(json.getString("replacement"));
		room = strip(json.getString("room"));
		comment = strip(json.getString("comment"));
		isToday = json.getBoolean("is_today");
		addition = json.getInt("addition") != 0;
	}

	@Override
	public int compareTo(ReplacementObject other) {
		if (isToday != other.isToday) return isToday ? -1 : 1;
		return lesson - other.lesson;
	}
}

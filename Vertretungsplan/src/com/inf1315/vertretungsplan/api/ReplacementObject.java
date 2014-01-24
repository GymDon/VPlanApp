package com.inf1315.vertretungsplan.api;

import org.json.JSONException;
import org.json.JSONObject;

public class ReplacementObject extends ApiResult implements
		Comparable<ReplacementObject> {

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
		if (isToday != other.isToday)
			return isToday ? -1 : 1;
		return lesson - other.lesson;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (addition ? 1231 : 1237);
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((grade == null) ? 0 : grade.hashCode());
		result = prime * result
				+ ((gradeLast == null) ? 0 : gradeLast.hashCode());
		result = prime * result
				+ ((gradePre == null) ? 0 : gradePre.hashCode());
		result = prime * result + id;
		result = prime * result + (isToday ? 1231 : 1237);
		result = prime * result + lesson;
		result = prime * result
				+ ((replacement == null) ? 0 : replacement.hashCode());
		result = prime * result + ((room == null) ? 0 : room.hashCode());
		result = prime * result + ((teacher == null) ? 0 : teacher.hashCode());
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
		ReplacementObject other = (ReplacementObject) obj;
		if (addition != other.addition)
			return false;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (grade == null) {
			if (other.grade != null)
				return false;
		} else if (!grade.equals(other.grade))
			return false;
		if (gradeLast == null) {
			if (other.gradeLast != null)
				return false;
		} else if (!gradeLast.equals(other.gradeLast))
			return false;
		if (gradePre == null) {
			if (other.gradePre != null)
				return false;
		} else if (!gradePre.equals(other.gradePre))
			return false;
		if (id != other.id)
			return false;
		if (isToday != other.isToday)
			return false;
		if (lesson != other.lesson)
			return false;
		if (replacement == null) {
			if (other.replacement != null)
				return false;
		} else if (!replacement.equals(other.replacement))
			return false;
		if (room == null) {
			if (other.room != null)
				return false;
		} else if (!room.equals(other.room))
			return false;
		if (teacher == null) {
			if (other.teacher != null)
				return false;
		} else if (!teacher.equals(other.teacher))
			return false;
		return true;
	}

}

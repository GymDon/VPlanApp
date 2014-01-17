package com.inf1315.vertretungsplan.api;


import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;

public class ReplacementObject implements Comparable<ReplacementObject>
{
    public final int id;
    public final String grade_pre;
    public final String grade;
    public final String grade_last;
    public final int lesson;
    public final String teacher;
    public final String replacement;
    public final String room;
    public final String comment;
    public final long timestamp;
    public final long timestamp_update;
    public final boolean addition;

    public ReplacementObject(JSONObject json) throws JSONException
    {
	id = json.getInt("id");
	grade_pre = strip(json.getString("grade_pre"));
	grade = strip(json.getString("grade"));
	grade_last = strip(json.getString("grade_last"));
	lesson = json.getInt("lesson");
	teacher = strip(json.getString("teacher"));
	replacement = strip(json.getString("replacement"));
	room = strip(json.getString("room"));
	comment = strip(json.getString("comment"));
	timestamp = json.getLong("timestamp");
	timestamp_update = json.getLong("timestamp_update");
	addition = json.getInt("addition") != 0;
    }
    
    private static String strip(String in)
    {
	return Html.fromHtml(in).toString().replace('\u00A0', ' ').trim();
    }

    @Override
    public int compareTo(ReplacementObject another)
    {
	return timestamp > another.timestamp ? 1 : timestamp < another.timestamp ? -1 : lesson > another.lesson ? 1 : lesson < another.lesson ? -1 : id - another.id;
    }
}

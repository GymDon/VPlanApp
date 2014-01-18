package com.inf1315.vertretungsplan.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AllObject extends ApiResult {

	public final JSONArray ticker;
	public final JSONArray replacements;
	public final JSONArray pages;
	public final JSONArray others;
	public final JSONObject user;

	public AllObject(JSONObject json) throws JSONException {
		ticker = json.getJSONArray("ticker");
		replacements = json.getJSONArray("replacements");
		pages = json.getJSONArray("pages");
		others = json.getJSONArray("others");
		user = json.getJSONObject("user");
	}
}

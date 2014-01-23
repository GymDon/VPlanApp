package com.inf1315.vertretungsplan.api;

import org.json.JSONException;
import org.json.JSONObject;

public class AllObject extends ApiResult {

	public final TickerObject[] ticker;
	public final ReplacementObject[] replacements;
	public final PageObject[] pages;
	public final OtherObject[] others;
	public final UserInfo user;

	public AllObject(JSONObject json) throws JSONException {
		ticker = new ApiResultArray(json.getJSONArray("ticker"), TickerObject.class).getArray(new TickerObject[0]);
		replacements = new ApiResultArray(json.getJSONArray("replacements"), ReplacementObject.class).getArray(new ReplacementObject[0]);
		pages = new ApiResultArray(json.getJSONArray("pages"), PageObject.class).getArray(new PageObject[0]);
		others = new ApiResultArray(json.getJSONArray("others"), OtherObject.class).getArray(new OtherObject[0]);
		user = new UserInfo(json.getJSONObject("user"));
	}
}

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
		ticker = (TickerObject[]) (new ApiResultArray(json.getJSONArray("ticker"), TickerObject.class)).getArray();
		replacements = (ReplacementObject[]) (new ApiResultArray(json.getJSONArray("replacements"), ReplacementObject.class)).getArray();
		pages = (PageObject[]) (new ApiResultArray(json.getJSONArray("pages"), PageObject.class)).getArray();
		others = (OtherObject[]) (new ApiResultArray(json.getJSONArray("others"), OtherObject.class)).getArray();
		user = new UserInfo(json.getJSONObject("user"));
	}
}

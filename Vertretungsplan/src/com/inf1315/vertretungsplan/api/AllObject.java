package com.inf1315.vertretungsplan.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;

public class AllObject extends ApiResult {

	public List<PageObject> pages = new ArrayList<PageObject>();
	public List<OtherObject> todayOthers = new ArrayList<OtherObject>();
	public List<OtherObject> tomorrowOthers = new ArrayList<OtherObject>();
	public List<TickerObject> tickers = new ArrayList<TickerObject>();
	public List<ReplacementObject> todayReplacementsList = new ArrayList<ReplacementObject>();
	public List<ReplacementObject> tomorrowReplacementsList = new ArrayList<ReplacementObject>();
	public UserInfo userInfo;
	public String hash = "";
	public String timeString = "";
	public long timestamp;

	public AllObject() {
	}

	@SuppressLint("SimpleDateFormat")
	public AllObject(JSONObject json) throws JSONException {
		TickerObject[] ticker = new ApiResultArray(json.getJSONArray("ticker"),
				TickerObject.class).getArray(new TickerObject[0]);
		tickers = Arrays.asList(ticker);

		ReplacementObject[] replacementsArray = new ApiResultArray(
				json.getJSONArray("replacements"), ReplacementObject.class)
				.getArray(new ReplacementObject[0]);
		List<ReplacementObject> replacements = Arrays.asList(replacementsArray);		
		todayReplacementsList.clear();
		tomorrowReplacementsList.clear();
		for (ReplacementObject ro : replacements) {
			if (ro.isToday)
				todayReplacementsList.add(ro);
			else
				tomorrowReplacementsList.add(ro);
		}
		Collections.sort(todayReplacementsList);
		Collections.sort(tomorrowReplacementsList);

		PageObject[] pagesArray = new ApiResultArray(
				json.getJSONArray("pages"), PageObject.class)
				.getArray(new PageObject[0]);
		pages = Arrays.asList(pagesArray);

		OtherObject[] othersArray = new ApiResultArray(
				json.getJSONArray("others"), OtherObject.class)
				.getArray(new OtherObject[0]);
		List<OtherObject> others = Arrays.asList(othersArray);
		todayOthers.clear();
		tomorrowOthers.clear();
		for (OtherObject oo : others) {
			if (oo.isToday)
				todayOthers.add(oo);
			else
				tomorrowOthers.add(oo);
		}

		userInfo = new UserInfo(json.getJSONObject("user"));
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		timeString = sdf.format(new Date());
		
		timestamp = System.currentTimeMillis()/1000L;
	}
}

package de.gymdon.app.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AllObject extends ApiResult {

	public List<PageObject> pages = new ArrayList<PageObject>();
	public List<OtherObject> todayOthers = new ArrayList<OtherObject>();
	public List<OtherObject> tomorrowOthers = new ArrayList<OtherObject>();
	public List<TickerObject> tickers = new ArrayList<TickerObject>();
	public List<ReplacementObject> todayReplacementsList = new ArrayList<ReplacementObject>();
	public List<ReplacementObject> tomorrowReplacementsList = new ArrayList<ReplacementObject>();
	public List<Event> events = new ArrayList<Event>();
	public List<MensaServer> mensa = new ArrayList<MensaServer>();
	public UserInfo userInfo;
	public String hash = "";
	public String timeString = "";
	public long timestamp;
	private int has = HAS_NONE;

	private static final int HAS_NONE = 0;
	private static final int HAS_USER = 1;
	private static final int HAS_TICKER = 2;
	private static final int HAS_REPLACEMENTS = 4;
	private static final int HAS_PAGES = 8;
	private static final int HAS_OTHERS = 16;
	private static final int HAS_EVENTS = 32;
	private static final int HAS_MENSA = 64;

	public AllObject() {
	}

	@SuppressLint("SimpleDateFormat")
	public AllObject(JSONObject json) throws JSONException {
		if (json.has("user") && !json.isNull("user")) {
			userInfo = new UserInfo(json.getJSONObject("user"));
			has |= HAS_USER;
			API.STANDARD_API.setUsername(userInfo.username);
		}

		if (json.has("ticker") && !json.isNull("ticker")) {
			tickers = new ApiResultArray(json.getJSONArray("ticker"),
					TickerObject.class).getList(tickers);
			if (!tickers.isEmpty())
				has |= HAS_TICKER;
		}

		if (json.has("replacements") && !json.isNull("replacements")) {
			ReplacementObject[] replacementsArray = new ApiResultArray(
					json.getJSONArray("replacements"), ReplacementObject.class)
					.getArray(new ReplacementObject[0]);
			List<ReplacementObject> replacements = Arrays
					.asList(replacementsArray);
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
			if (!replacements.isEmpty())
				has |= HAS_REPLACEMENTS;
		}

		if (json.has("pages") && !json.isNull("pages")) {
			pages = new ApiResultArray(json.getJSONArray("pages"),
					PageObject.class).getList(pages);
			if (!pages.isEmpty())
				has |= HAS_PAGES;
		}

		if (json.has("others") && !json.isNull("others")) {
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
			if (!others.isEmpty())
				has |= HAS_OTHERS;
		}

		if (json.has("events") && !json.isNull("events")) {
			events = new ApiResultArray(json.getJSONArray("events"),
					Event.class).getList(events);
			if (!events.isEmpty())
				has |= HAS_EVENTS;
		}

		if (json.has("mensa") && !json.isNull("mensa")) {
			mensa = new ApiResultArray(json.getJSONArray("mensa"),
					MensaServer.class).getList(mensa);
			if (!mensa.isEmpty())
				has |= HAS_MENSA;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		timeString = sdf.format(new Date());

		timestamp = System.currentTimeMillis() / 1000L;
	}

	public void saveData() {
		if (API.CONTEXT == null) {
			Log.i("AllObject", "API.CONTEXT == null");
			return;
		}
		SharedPreferences.Editor spe = API.CONTEXT.getSharedPreferences("data",
				Context.MODE_PRIVATE).edit();
		String json = (new Gson()).toJson(this);
		spe.putString("data", json);
		spe.commit();
	}

	public boolean hasUser() {
		return (has & HAS_USER) > 0 && userInfo != null;
	}
	
	public boolean hasTicker() {
		return (has & HAS_TICKER) > 0 && tickers != null;
	}
	
	public boolean hasReplacements() {
		return (has & HAS_REPLACEMENTS) > 0 && todayReplacementsList != null && tomorrowReplacementsList != null;
	}
	
	public boolean hasPages() {
		return (has & HAS_PAGES) > 0 && pages != null;
	}
	
	public boolean hasOthers() {
		return (has & HAS_OTHERS) > 0 && todayOthers != null && tomorrowOthers != null;
	}
	
	public boolean hasEvents() {
		return (has & HAS_EVENTS) > 0 && events != null;
	}
	
	public boolean hasMensa() {
		return (has & HAS_MENSA) > 0 && mensa != null;
	}

	@Override
	public void setParent(ApiResponse parent) {
		super.setParent(parent);
		if (parent != null && parent.getHash() != null)
			this.hash = parent.getHash();
	}

}

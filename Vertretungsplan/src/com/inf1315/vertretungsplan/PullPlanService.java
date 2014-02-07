package com.inf1315.vertretungsplan;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.inf1315.vertretungsplan.api.API;
import com.inf1315.vertretungsplan.api.AllObject;
import com.inf1315.vertretungsplan.api.ApiAction;
import com.inf1315.vertretungsplan.api.ApiResponse;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class PullPlanService extends IntentService {

	private String username;
	private String password;
	private String token;

	public PullPlanService() {
		
		super("com.inf1315.vertretungsplan.PullPlanService");

		Log.e("PULLPLAN", "Constructor started");

		this.username = API.DATA.userInfo.username;
		this.token = API.DATA.getToken();
	}

	@SuppressLint("SimpleDateFormat")
	public AllObject download() {
		// TODO: Do a cleanup
		
		Log.e("PULLPLAN", "Download started!");

		try {
			ApiResponse resp;
			if (token == null)
				resp = API.STANDARD_API.request(ApiAction.ALL, "u=" + username,
						"pass=" + password);
			else
				resp = API.STANDARD_API.request(ApiAction.ALL, "u=" + username,
						"sync=true", "token=" + token);
			if (!resp.getSuccess()) {
				Log.w("All Loader", "Loading unsuccesfull");
				return null;
			}
			if (!resp.getChanged()) {
				API.DATA.timestamp = System.currentTimeMillis() / 1000L;
				SimpleDateFormat sdf = new SimpleDateFormat(
						"dd.MM.yyyy HH:mm:ss");
				API.DATA.timeString = sdf.format(new Date());
				return API.DATA;
			}
			AllObject ao = (AllObject) resp.getResult();
			ao.hash = resp.getHash();
			ao.setToken(resp.getToken());
			return ao;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	protected void onHandleIntent(Intent workIntent) {

		Log.e("PULLPLAN", "onHandleIntent");
		API.DATA = download();

	}

}

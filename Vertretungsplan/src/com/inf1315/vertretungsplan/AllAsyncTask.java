package com.inf1315.vertretungsplan;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.inf1315.vertretungsplan.api.*;

public class AllAsyncTask extends AsyncTask<Object, Object, AllObject> {

	private FinishedLoading plan;
	private String username;
	private String password;
	private String token;

	public AllAsyncTask(FinishedLoading plan, String username, String password) {
		this.plan = plan;
		this.username = username;
		this.password = password;
	}

	public AllAsyncTask(FinishedLoading plan) {
		this.plan = plan;
		this.username = API.DATA.userInfo.username;
		this.token = API.DATA.getToken();
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected AllObject doInBackground(Object... args) {
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
	protected void onPostExecute(AllObject result) {
		if (result == null)
			plan.finishedLoading("ServerRequestFailed");
		else {
			API.DATA = result;
			plan.finishedLoading(null);
		}
	}

}

package de.gymdon.app;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import de.gymdon.app.api.*;

public class AllAsyncTask extends AsyncTask<Object, Object, AllObject> {

	private FinishedLoading plan;
	private String token;

	public AllAsyncTask(FinishedLoading plan) {
		this.plan = plan;
		this.token = API.DATA.getToken();
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	protected AllObject doInBackground(Object... args) {
		try {
			ApiResponse resp;
			if (!"".equals(token))
				resp = API.STANDARD_API.request(ApiAction.ALL, "sync", "true", "token", token);
			else
				resp = API.STANDARD_API.request(ApiAction.ALL);
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
			if (ao != null) {
				ao.hash = resp.getHash();
				ao.setToken(resp.getToken());
			}
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

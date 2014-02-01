package com.inf1315.vertretungsplan;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.inf1315.vertretungsplan.api.*;

public class AllAsyncTask extends AsyncTask<Object, Object, AllObject> {

	private PlanActivity plan;
	private String username;
	private String password;
	private String token;

	AllAsyncTask(PlanActivity plan, String username, String password) {
		this.plan = plan;
		this.username = username;
		this.password = password;
	}

	AllAsyncTask(PlanActivity plan) {
		this.plan = plan;
		this.username = API.DATA.userInfo.username;
		this.token = API.DATA.token;
	}

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
			API.DATA.token = resp.getToken();
			if (!resp.getChanged())
				return API.DATA;

			AllObject ao = (AllObject) resp.getResult();
			ao.hash = resp.getHash();
			ao.token = resp.getToken();
			return ao;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(AllObject result) {
		if (result == null) {
			Intent intent = new Intent(plan, LoginActivity.class);
			intent.putExtra("error", "ServerRequestFailed");
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			plan.loadingDialog.dismiss();
			plan.startActivity(intent);

		}

		else {
			API.DATA = result;
			plan.finishedLoading();
		}
	}

}

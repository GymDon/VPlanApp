package com.inf1315.vertretungsplan;

import java.util.Arrays;
import java.util.Collections;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.inf1315.vertretungsplan.api.*;

public class AllAsyncTask extends AsyncTask<Object, Object, AllObject> {

	private PlanActivity plan;
	private String username;
	private String password;

	AllAsyncTask(PlanActivity plan, String username, String password) {
		this.plan = plan;
		this.username = username;
		this.password = password;
	}

	@Override
	protected AllObject doInBackground(Object... args) {
		try {
			ApiResponse resp = API.STANDARD_API.request(ApiAction.ALL, "u="
					+ username, "pass=" + password);
			if (!resp.getSuccess()) {
				Log.w("All Loader", "Loading unsuccesfull");
				return null;
			}
			return (AllObject) resp.getResult();

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
			API.DATA.tickers = Arrays.asList(result.ticker);

			API.DATA.todayReplacementsList.clear();
			API.DATA.tomorrowReplacementsList.clear();
			for (ReplacementObject ro : result.replacements) {
				if (ro.isToday)
					API.DATA.todayReplacementsList.add(ro);
				else
					API.DATA.tomorrowReplacementsList.add(ro);
			}
			Collections.sort(API.DATA.todayReplacementsList);
			Collections.sort(API.DATA.tomorrowReplacementsList);
			plan.todayReplacements.notifyDataSetChanged();
			plan.tomorrowReplacements.notifyDataSetChanged();

			API.DATA.pages = Arrays.asList(result.pages);

			API.DATA.todayOthers.clear();
			API.DATA.tomorrowOthers.clear();
			for (OtherObject oo : result.others) {
				if (oo.isToday)
					API.DATA.todayOthers.add(oo);
				else
					API.DATA.tomorrowOthers.add(oo);
			}

			plan.finishedLoading();
		}
	}

}

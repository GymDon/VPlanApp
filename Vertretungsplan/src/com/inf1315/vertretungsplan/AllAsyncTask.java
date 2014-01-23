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
			plan.tickers = Arrays.asList(result.ticker);

			plan.todayReplacements.clear();
			plan.tomorrowReplacements.clear();
			for (ReplacementObject ro : result.replacements) {
				if (ro.isToday)
					plan.todayReplacementsList.add(ro);
				else
					plan.tomorrowReplacementsList.add(ro);
			}
			Collections.sort(plan.todayReplacementsList);
			Collections.sort(plan.tomorrowReplacementsList);
			plan.todayReplacements.notifyDataSetChanged();
			plan.tomorrowReplacements.notifyDataSetChanged();

			plan.pages = Arrays.asList(result.pages);

			plan.todayOthers.clear();
			plan.tomorrowOthers.clear();
			for (OtherObject oo : result.others) {
				if (oo.isToday)
					plan.todayOthers.add(oo);
				else
					plan.tomorrowOthers.add(oo);
			}

			plan.finishedLoading();
		}
	}

}

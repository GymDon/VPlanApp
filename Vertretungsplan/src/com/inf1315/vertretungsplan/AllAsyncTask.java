package com.inf1315.vertretungsplan;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.inf1315.vertretungsplan.api.*;

public class AllAsyncTask extends AsyncTask<Object, Object, AllObject> {

	private PlanActivity plan;
	private String username;
	
	AllAsyncTask (PlanActivity plan, String username) {
		this.plan = plan;
		this.username = username;
	}
	
	@Override
	protected AllObject doInBackground(Object... args) {
		try {
			ApiResponse resp = API.STANDARD_API.request(ApiAction.ALL,"u="+username);
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
		if (result==null) {
			Intent intent = new Intent(plan, LoginActivity.class);
			intent.putExtra("error", "ServerRequestFailed");
			plan.startActivity(intent);
		
		}
		
		plan.tickers = Arrays.asList(result.ticker);
		
		plan.todayReplacements = new ArrayList<ReplacementObject>();
		plan.tomorrowReplacements = new ArrayList<ReplacementObject>();
		for (ReplacementObject ro : result.replacements) {
			if (ro.isToday) plan.todayReplacements.add(ro);
			else plan.tomorrowReplacements.add(ro);
		}
		
		plan.pages = Arrays.asList(result.pages);
	}

}

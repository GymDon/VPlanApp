package com.inf1315.vertretungsplan;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.e("BOOTREC","onReceive started");
		
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.e("BOOTREC", "if is working!");
			AlarmManager alarmMgr = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent serviceIntent = new Intent(context, PullPlanService.class);
			PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0,
					serviceIntent, 0);
			context.startService(serviceIntent);
			alarmMgr.setInexactRepeating(
					AlarmManager.ELAPSED_REALTIME_WAKEUP, // TODO: Implement
															// time-change
					AlarmManager.INTERVAL_HOUR, AlarmManager.INTERVAL_HOUR,
					alarmIntent);
		}

	}

}

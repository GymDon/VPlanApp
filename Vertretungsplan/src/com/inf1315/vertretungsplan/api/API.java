package com.inf1315.vertretungsplan.api;

import java.io.*;
import java.net.*;
import java.util.*;

import org.json.*;

import com.google.gson.Gson;
import com.inf1315.vertretungsplan.LoginActivity;
import com.inf1315.vertretungsplan.R;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class API {
	/**
	 * The standard url: {@value #STANDARD_URL}
	 */
	public static final String STANDARD_URL = "https://pvpctutorials.de/VPlanApp/api/";

	public static API STANDARD_API;
	public static API LOCAL_DEBUG_API;
	public static String APP_VERSION;
	public static Context CONTEXT;
	public static AllObject DATA;
	public static boolean reload = false;
	public final static String API_VERSION = "0.2";

	private URL url;

	/**
	 * Creates a new API with the {@link #STANDARD_URL} ( {@value #STANDARD_URL}
	 * )
	 * 
	 * @throws MalformedURLException
	 * @see API#API(String)
	 * @see API#API(URL)
	 */
	public API() throws MalformedURLException {
		this(STANDARD_URL);
	}

	/**
	 * Creates a new API with for the specified URL
	 * 
	 * @param url
	 *            The URL
	 * @throws MalformedURLException
	 * @see API#API()
	 * @see API#API(URL)
	 */
	public API(String url) throws MalformedURLException {
		this(new URL(url));
	}

	/**
	 * Creates a new API with for the specified URL
	 * 
	 * @param url
	 *            The URL
	 * @throws MalformedURLException
	 * @see API#API()
	 * @see API#API(String)
	 */
	public API(URL url) {
		this.url = url;
	}

	/**
	 * Make a new API-request
	 * 
	 * @param action
	 *            The action to be performed
	 * @param params
	 *            The parameters to the action as strings with format
	 *            <i>"key=value"</i>
	 * @return The Response from the server
	 * @throws IOException
	 * @see {@link API#request(ApiAction, Map)}
	 */
	public ApiResponse request(ApiAction action, String... params)
			throws IOException {
		Map<String, String> paramsMap = new HashMap<String, String>();
		for (String param : params) {
			String[] sp = param.split("=");
			if (sp.length != 2)
				throw new IllegalArgumentException(
						"Params need to be key=value");
			paramsMap.put(sp[0], sp[1]);
		}
		return request(action, paramsMap);
	}

	/**
	 * Make a new API-request
	 * 
	 * @param action
	 *            The action to be performed
	 * @param params
	 *            The parameters to the action
	 * @return The Response from the server
	 * @throws IOException
	 * @see {@link API#request(ApiAction, String...)}
	 */
	@SuppressLint({ "NewApi", "DefaultLocale" })
	@SuppressWarnings("deprecation")
	public ApiResponse request(ApiAction action, Map<String, String> params) {
		ApiResponse r;
		JSONObject obj = null;
		try {
			params.put("a", action.toString().toLowerCase());
			params.put("os", "Android " + Build.VERSION.RELEASE + " ("
					+ Build.DISPLAY + ")");
			params.put("app", APP_VERSION);
			boolean wifi = false;
			boolean adb = false;
			boolean data;
			if (CONTEXT != null)
				if (Build.VERSION.SDK_INT < 17) {
					wifi = Settings.Secure.getInt(CONTEXT.getContentResolver(),
							Settings.Secure.WIFI_ON) != 0;
					adb = Settings.Secure.getInt(CONTEXT.getContentResolver(),
							Settings.Secure.ADB_ENABLED) != 0;
				} else {
					wifi = Settings.Global.getInt(CONTEXT.getContentResolver(),
							Settings.Global.WIFI_ON) != 0;
					adb = Settings.Global.getInt(CONTEXT.getContentResolver(),
							Settings.Global.ADB_ENABLED) != 0;
				}

			ConnectivityManager cm = (ConnectivityManager) CONTEXT
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			if (activeNetwork != null
					&& activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				data = true;
			else
				data = false;

			JSONObject o = new JSONObject();
			o.put("android_id", Settings.Secure.getString(
					CONTEXT.getContentResolver(), Settings.Secure.ANDROID_ID));
			o.put("wifi", wifi);
			o.put("adb", adb);
			o.put("data", data);
			params.put("stats", o.toString());
			params.put("hash", DATA.hash);
			params.put("api", API_VERSION);
			params.put("lang", Locale.getDefault().getLanguage());
			obj = getJSONfromURL(url, "POST", params);
			if (!actionToClassMap.containsKey(action))
				throw new RuntimeException("invalid action \"" + action + "\"");
			r = new ApiResponse(obj, actionToClassMap.get(action),
					actionIsArrayMap.get(action));
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("API Params", params.toString());
			if (obj != null)
				try {
					Log.i("API Response", obj.toString(4));
				} catch (JSONException e1) {
				}
			r = new ApiResponse(e);
		}
		if (r.getWarnings() == null || r.getWarnings().isEmpty())
			Log.i("API-Warning", "No Warnings");
		else
			for (ApiWarning w : r.getWarnings()) {
				Log.w("API-Warning", w.getWarning() + ": " + w.getDescription());
			}
		return r;
	}

	/**
	 * Method for getting a JSONObject from an url
	 * 
	 * @param url
	 *            The HTTP-URL
	 * @param requestMethod
	 *            The HTTP request method (GET/POST)
	 * @param params
	 *            Additional params for GET/POST
	 * @return A JSONObject parsed from the specified url
	 */
	@SuppressLint("DefaultLocale")
	public static JSONObject getJSONfromURL(URL url, String requestMethod,
			Map<String, String> params) throws IOException, JSONException {
		requestMethod = requestMethod.toUpperCase();
		StringBuilder get = new StringBuilder();
		if (params != null) {
			for (String key : params.keySet())
				get.append(get.length() == 0 ? "" : "&")
						.append(URLEncoder.encode(key, "UTF-8")).append('=')
						.append(URLEncoder.encode(params.get(key), "UTF-8"));
		}
		if (requestMethod.equals("GET"))
			url = new URL(url.toExternalForm() + "?" + get);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod(requestMethod);
		conn.setReadTimeout(2000);

		conn.connect();
		if (requestMethod.equals("POST"))
			conn.getOutputStream().write(get.toString().getBytes());
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null)
			sb.append(line).append('\n');
		reader.close();
		conn.disconnect();
		try {
			return new JSONObject(sb.toString());
		} catch (JSONException e) {
			Log.i("Response", sb.toString());
			throw e;
		}
	}

	public static void deleteToken() {
		DATA.token = "";
		SharedPreferences.Editor spe = CONTEXT.getSharedPreferences("data",
				Context.MODE_PRIVATE).edit();
		String json = (new Gson()).toJson(DATA);
		spe.putString("data", json);
		spe.apply();
	}

	public static void showNotification(AllObject o1, AllObject o2) {
		boolean[] changed = new boolean[4];
		for (int i = 0; i < changed.length; i++)
			changed[i] = false;

		if (o1.hash.equals(o2.hash)) {
			displayNotification(changed);
			return;
		}
		if (o1.todayReplacementsList.hashCode() != o2.todayReplacementsList
				.hashCode()
				|| o1.tomorrowReplacementsList.hashCode() != o2.tomorrowReplacementsList
						.hashCode())
			changed[0] = true;
		if (o1.tickers.hashCode() != o2.tickers.hashCode())
			changed[1] = true;
		if (o1.todayOthers.hashCode() != o2.todayOthers.hashCode()
				|| o1.tomorrowOthers.hashCode() != o2.tomorrowOthers.hashCode())
			changed[2] = true;
		if (o1.pages.hashCode() != o2.pages.hashCode())
			changed[3] = true;
		displayNotification(changed);
	}

	private static void displayNotification(boolean[] changed) {
		if (changed.length != 4) {
			Log.i("showNotification", "boolean[] must have size 4");
			return;
		}
		int count = 0;
		int where = 0;
		for (int i = 0; i < changed.length; i++) {
			count += changed[i] ? 1 : 0;
			if (changed[i])
				where = i;
		}
		if (count == 0)
			return;

		NotificationCompat.Builder ncb = new NotificationCompat.Builder(CONTEXT);
		ncb.setAutoCancel(true);
		if (count != 1)
			ncb.setNumber(count);
		ncb.setSmallIcon(R.drawable.ic_launcher);
		if (count == 1) {
			CharSequence text = where == 0 ? CONTEXT
					.getText(R.string.replacements_changed)
					: where == 1 ? CONTEXT.getText(R.string.tickers_changed)
							: where == 2 ? CONTEXT
									.getText(R.string.others_changed) : CONTEXT
									.getText(R.string.pages_changed);
			ncb.setContentText(text);
		} else
			ncb.setContentText(CONTEXT.getText(R.string.data_changed));
		ncb.setContentTitle(CONTEXT.getText(R.string.app_name));

		Intent intent = new Intent(API.CONTEXT, LoginActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(API.CONTEXT, 0,
				intent, 0);
		ncb.setContentIntent(pendingIntent);

		NotificationManager nm = (NotificationManager) CONTEXT
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = ncb.build();
		n.defaults |= Notification.DEFAULT_VIBRATE;
		nm.notify((int) Math.random()*10000, n);
	}

	private static Map<ApiAction, Class<? extends ApiResult>> actionToClassMap;
	private static Map<ApiAction, Boolean> actionIsArrayMap;

	static {
		try {
			STANDARD_API = new API();
			LOCAL_DEBUG_API = new API("http://192.168.0.36/VPlanApp/api.php");
		} catch (MalformedURLException e) {
		}

		actionToClassMap = new HashMap<ApiAction, Class<? extends ApiResult>>();
		actionIsArrayMap = new HashMap<ApiAction, Boolean>();

		actionToClassMap.put(ApiAction.USER, UserInfo.class);
		actionIsArrayMap.put(ApiAction.USER, false);

		actionToClassMap.put(ApiAction.TICKER, TickerObject.class);
		actionIsArrayMap.put(ApiAction.TICKER, true);

		actionToClassMap.put(ApiAction.ALL, AllObject.class);
		actionIsArrayMap.put(ApiAction.ALL, false);

		actionToClassMap.put(ApiAction.CHANGELOG, Commit.class);
		actionIsArrayMap.put(ApiAction.CHANGELOG, true);
	}
}

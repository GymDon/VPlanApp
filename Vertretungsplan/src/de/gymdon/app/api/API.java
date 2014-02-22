package de.gymdon.app.api;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.http.*;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.*;

import de.gymdon.app.R;
import de.gymdon.app.activities.MainActivity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

@SuppressWarnings("unchecked")
public class API {
	/**
	 * The standard url: {@value #STANDARD_URL}
	 */
	public static final String STANDARD_URL = "https://pvpctutorials.de/VPlanApp/api/";

	public static API STANDARD_API;
	public static String APP_VERSION;
	public static Context CONTEXT;
	public static AllObject DATA;
	public static boolean reload = false;
	public final static String API_VERSION = "0.3";

	private URL url;
	
	private String username;
	private String password;

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
	 *            The parameters to the action as strings in pairs of key and value
	 * @return The Response from the server
	 * @throws IOException
	 * @see {@link API#request(ApiAction, Map)}
	 */
	public ApiResponse request(ApiAction action, String... params)
			throws IOException {
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		for(int i = 0; i < params.length - 1; i+=2) {
			paramsList.add(new BasicNameValuePair(params[i], params[i+1]));
		}
		return request(action, paramsList);
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
	@SuppressLint("DefaultLocale")
	public ApiResponse request(ApiAction action, List<NameValuePair> params) {
		ApiResponse r;
		JSONObject obj = null;
		String url = this.url + API_VERSION + '/' + action.toString().toLowerCase();
		try {
			if(isLoggedIn())
				Log.i("API", getUsername() + " is logged in");
			else
				Log.w("API", "No user logged in!");
			params.add(new BasicNameValuePair("os", "Android " + Build.VERSION.RELEASE + " ("
					+ Build.DISPLAY + ")"));
			params.add(new BasicNameValuePair("app", APP_VERSION));
			params.add(new BasicNameValuePair("hash", DATA.hash));
			params.add(new BasicNameValuePair("api", API_VERSION));
			params.add(new BasicNameValuePair("lang", Locale.getDefault().getLanguage()));
			Log.d("API", "Request: " + action);
			if (actionClass[action.ordinal()] == null)
				throw new RuntimeException("invalid action \"" + action + "\"");
			long time = System.currentTimeMillis();
			if(actionNeedsLogin[action.ordinal()] || isLoggedIn() && !(action == ApiAction.ALL && DATA.hasToken()))
				obj = getJSONfromURL(url, params, getUsername(), password);
			else
				obj = getJSONfromURL(url, params, getUsername(), null);
			r = new ApiResponse(obj, actionClass[action.ordinal()],
					actionIsArray[action.ordinal()]);
			Log.d("API", "Response: " + (System.currentTimeMillis()-time) + "ms");
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("API", "Params: " + params.toString());
			if (obj != null)
				try {
					Log.i("API", "Response: " + obj.toString(4));
				} catch (JSONException e1) {
				}
			r = new ApiResponse(e);
		}
		if (r.getWarnings() == null || r.getWarnings().isEmpty())
			Log.i("API", "No Warnings");
		else {
			Log.w("API", r.getWarnings().size() + " Warnings:");
			for (ApiWarning w : r.getWarnings()) {
				Log.w("API", w.getWarning() + ": " + w.getDescription() + (w.getExtra().size() > 0 ? "\nExtra: " + w.getExtra() : ""));
			}
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
	public static JSONObject getJSONfromURL(String url,
			List<NameValuePair> params, String username, String password) throws IOException, JSONException {
		DefaultHttpClient client = new DefaultHttpClient();
		if(username != null) {
			params.add(new BasicNameValuePair("u", username));
			if(password != null) {
				params.add(new BasicNameValuePair("pass", password));
				//client.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
			}
		}
		HttpPost post = new HttpPost(url);
		post.setEntity(new UrlEncodedFormEntity(params));
		HttpResponse resp = client.execute(post);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				resp.getEntity().getContent()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null)
			sb.append(line).append('\n');
		reader.close();
		try {
			return new JSONObject(sb.toString());
		} catch (JSONException e) {
			Log.i("Response", sb.toString());
			throw e;
		}
	}
	
	/*private static HttpRequestInterceptor getAuthInterceptor(String username, String password) {
		return new HttpRequestInterceptor() {
		    public void process(final HttpRequest request, final HttpContext context) throws HttpException, IOException {
		        AuthState authState = (AuthState) context.getAttribute(ClientContext.TARGET_AUTH_STATE);
		        CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(
		                ClientContext.CREDS_PROVIDER);
		        HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
		        
		        if (authState.getAuthScheme() == null) {
		            AuthScope authScope = new AuthScope(targetHost.getHostName(), targetHost.getPort());
		            Credentials creds = credsProvider.getCredentials(authScope);
		            if (creds != null) {
		                authState.setAuthScheme(new BasicScheme());
		                authState.setCredentials(creds);
		            }
		        }
		    }  
		};
	}*/
	
	public boolean isLoggedIn() {
		return (username != null && password != null) || DATA.hasToken();
	}

	public static boolean isNetworkAvailable() {
		ConnectivityManager conMan = (ConnectivityManager) CONTEXT
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = conMan.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static void showNotification(AllObject o1, AllObject o2) {
		if (o1.hash.equals(o2.hash))
			return;

		boolean replacementsChanged = o1.todayReplacementsList.hashCode() != o2.todayReplacementsList
				.hashCode()
				|| o1.tomorrowReplacementsList.hashCode() != o2.tomorrowReplacementsList
						.hashCode();
		boolean tickersChanged = o1.tickers.hashCode() != o2.tickers.hashCode();
		boolean othersChanged = o1.todayOthers.hashCode() != o2.todayOthers
				.hashCode()
				|| o1.tomorrowOthers.hashCode() != o2.tomorrowOthers.hashCode();
		boolean pagesChanged = o1.pages.hashCode() != o2.pages.hashCode();
		displayNotification(replacementsChanged, tickersChanged, othersChanged,
				pagesChanged);
	}

	private static void displayNotification(boolean replacementsChanged,
			boolean tickersChanged, boolean othersChanged, boolean pagesChanged) {
		if (!replacementsChanged && !tickersChanged && !othersChanged
				&& !pagesChanged)
			return;
		int count = 0;
		if (replacementsChanged)
			count++;
		if (tickersChanged)
			count++;
		if (othersChanged)
			count++;
		if (pagesChanged)
			count++;

		NotificationCompat.Builder ncb = new NotificationCompat.Builder(CONTEXT);
		ncb.setAutoCancel(true);
		if (count != 1)
			ncb.setNumber(count);
		ncb.setSmallIcon(R.drawable.ic_launcher);
		if (count == 1) {
			CharSequence text = replacementsChanged ? CONTEXT
					.getText(R.string.replacements_changed)
					: tickersChanged ? CONTEXT
							.getText(R.string.tickers_changed)
							: othersChanged ? CONTEXT
									.getText(R.string.others_changed) : CONTEXT
									.getText(R.string.pages_changed);
			ncb.setContentText(text);
		} else
			ncb.setContentText(CONTEXT.getText(R.string.data_changed));

		ncb.setContentTitle(CONTEXT.getText(R.string.app_name));

		Intent intent = new Intent(API.CONTEXT, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(API.CONTEXT, 0,
				intent, 0);
		ncb.setContentIntent(pendingIntent);

		NotificationManager nm = (NotificationManager) CONTEXT
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification n = ncb.build();
		boolean vibrate = CONTEXT
				.getSharedPreferences(
						"com.inf1315.vertretungsplan_preferences",
						Context.MODE_PRIVATE).getBoolean("pref_vibrate", true);

		if (vibrate)
			n.defaults |= Notification.DEFAULT_VIBRATE;
		nm.notify(0, n);
	}

	public static boolean hasReplacements(boolean forToday) {
		return !((forToday ? API.DATA.todayReplacementsList
				: API.DATA.tomorrowReplacementsList).isEmpty() && (forToday ? API.DATA.todayOthers
				: API.DATA.tomorrowOthers).isEmpty());
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getUsername() {
		return username;
	}

	private static Class<? extends ApiResult>[] actionClass;
	private static boolean[] actionIsArray;
	private static boolean[] actionNeedsLogin;

	static {
		try {
			STANDARD_API = new API();
		} catch (MalformedURLException e) {
		}
		actionClass = new Class[ApiAction.values().length];
		actionIsArray = new boolean[ApiAction.values().length];
		actionNeedsLogin = new boolean[ApiAction.values().length];

		actionClass[ApiAction.USER.ordinal()] = UserInfo.class;
		actionIsArray[ApiAction.USER.ordinal()] = false;
		actionIsArray[ApiAction.USER.ordinal()] = true;

		actionClass[ApiAction.TICKER.ordinal()] = TickerObject.class;
		actionIsArray[ApiAction.TICKER.ordinal()] = true;
		actionIsArray[ApiAction.TICKER.ordinal()] = true;

		actionClass[ApiAction.ALL.ordinal()] = AllObject.class;
		actionIsArray[ApiAction.ALL.ordinal()] = false;
		actionIsArray[ApiAction.ALL.ordinal()] = false;

		actionClass[ApiAction.CHANGELOG.ordinal()] = Commit.class;
		actionIsArray[ApiAction.CHANGELOG.ordinal()] = true;
		actionIsArray[ApiAction.CHANGELOG.ordinal()] = false;
	}
}

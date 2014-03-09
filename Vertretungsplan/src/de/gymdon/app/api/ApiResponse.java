package de.gymdon.app.api;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.util.Log;

public class ApiResponse {
	private ApiResult result;
	private boolean success;
	private String action;
	private Map<String, String> params;
	private String hash;
	private boolean changed;
	private boolean authorized;
	private String developer;
	private String language;
	private List<ApiWarning> warnings;
	private String token;
	private String currentVersion;
	private boolean isCurrentVersion;
	private Uri apkDownloadUrl;
	private String additionalMessage;

	public ApiResponse(JSONObject obj, Class<? extends ApiResult> resultType,
			boolean array) throws JSONException {
		if (obj.has("result") && !obj.isNull("result")) {
			if (!array) {
				try {
					result = resultType.getConstructor(JSONObject.class)
							.newInstance(obj.getJSONObject("result"));
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				}
			} else {
				result = new ApiResultArray(obj.getJSONArray("result"),
						resultType);
			}
		} else
			Log.i("ApiResponse", "result == null");
		success = obj.getBoolean("success");
		action = obj.getString("action");
		params = new HashMap<String, String>();
		hash = obj.getString("hash");
		changed = obj.getBoolean("changed");
		authorized = obj.getBoolean("authorized");
		developer = obj.optString("developer");
		if(obj.has("token")) {
			token = obj.getString("token");
			if(result instanceof AllObject)
				((AllObject)result).setToken(token);
		}
		if (developer != null)
			Log.i("ApiResponse", "Developer: " + developer);
		language = obj.optString("language");
		try {
			JSONObject par = obj.getJSONObject("params");
			for (Iterator<?> i = par.keys(); i.hasNext();) {
				String key = (String) i.next();
				params.put(key, par.getString(key));
			}
		} catch (JSONException e) {
			// params is no object but empty array
		}
		try {
			JSONArray warns = obj.getJSONArray("warnings");
			warnings = new ArrayList<ApiWarning>(warns.length());
			for (int i = 0; i < warns.length(); i++) {
				warnings.add(new ApiWarning(warns.getJSONObject(i)));
			}
		} catch (JSONException e) {
			warnings = new ArrayList<ApiWarning>(0);
			warnings.add(new ApiWarning(e));
		}
		if (obj.has("is_current_version"))
			isCurrentVersion = obj.getBoolean("is_current_version");
		else
			isCurrentVersion = true;
		currentVersion = obj.optString("curr_app_version");
		if (obj.has("apk_download"))
			apkDownloadUrl = Uri.parse(obj.getString("apk_download"));
		if (obj.has("additional_message"))
			additionalMessage = obj.getString("additional_message");
		if (result != null)
			result.setParent(this);
	}

	public ApiResponse(Exception e) {
		success = false;
	}

	public ApiResult getResult() {
		return result;
	}

	public List<ApiWarning> getWarnings() {
		return warnings;
	}

	public boolean getSuccess() {
		return success;
	}

	public String getAction() {
		return action;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public String getHash() {
		return hash;
	}

	public boolean getChanged() {
		return changed;
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public boolean isDeveloper() {
		return developer != null;
	}
	
	public boolean isCurrentVersion() {
		return isCurrentVersion;
	}
	
	public String getCurrentVersion() {
		return currentVersion;
	}
	
	public Uri getApkDownloadUrl() {
		return apkDownloadUrl;
	}

	public String getDeveloper() {
		return developer;
	}

	public String getLanguage() {
		return language;
	}

	public Locale getLocaleForLanguage() {
		return new Locale(language);
	}
	
	public String getToken() {
		return token;
	}
	
	public boolean hasToken() {
		return token != null && token.length() > 0;
	}
	
	public String getAdditionalMessage() {
		return additionalMessage;
	}
}

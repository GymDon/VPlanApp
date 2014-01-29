package com.inf1315.vertretungsplan.api;

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

	public ApiResponse(JSONObject obj, Class<? extends ApiResult> resultType,
			boolean array) throws JSONException {
		if (!array) {
			try {
				if (obj.isNull("result"))
					result = null;
				else
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
			if (obj.isNull("result"))
				result = null;
			else
				result = new ApiResultArray(obj.getJSONArray("result"),
						resultType);
		}
		success = obj.getBoolean("success");
		action = obj.getString("action");
		params = new HashMap<String, String>();
		hash = obj.getString("hash");
		changed = obj.getBoolean("changed");
		authorized = obj.getBoolean("authorized");
		developer = obj.optString("developer");
		if(developer != null)
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
		}
	}

	public ApiResponse(Exception e) {
		success = false;
	}

	public ApiResult getResult() {
		return result;
	}
	
	public List<ApiWarning> getWarnings()
	{
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

	public String getDeveloper() {
		return developer;
	}

	public String getLanguage() {
		return language;
	}
	
	public Locale getLocaleForLanguage() {
		return new Locale(language);
	}
}

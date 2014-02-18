package de.gymdon.app.api;

import org.json.*;

public class UserInfo extends ApiResult {
	public final String username;
	public final int uid;
	public final int gid;
	public final String mainGroup;
	public final String[] groups;
	public final String fullname;
	public final boolean isStudent;

	public UserInfo(String user) {
		username = user;
		uid = 0;
		gid = 0;
		mainGroup = null;
		groups = null;
		fullname = "";
		isStudent = true;
	}

	public UserInfo(JSONObject obj) throws JSONException {
		username = obj.getString("user");
		uid = obj.getInt("uid");
		gid = obj.getInt("gid");
		mainGroup = obj.getString("group");
		isStudent = obj.getBoolean("student");

		JSONArray arr = obj.getJSONArray("groups");
		groups = new String[arr.length()];
		for (int i = 0; i < arr.length(); i++)
			groups[i] = arr.getString(i);

		fullname = obj.getString("fullname");
	}
}

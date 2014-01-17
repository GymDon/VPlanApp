package com.inf1315.vertretungsplan.api;

import org.json.*;

public class UserInfo extends ApiResult
{
    public final String username;
    public final int uid;
    public final int gid;
    public final String mainGroup;
    public final String[] groups;
    public final String fullname;
    
    public UserInfo(JSONObject obj) throws JSONException
    {
	username = obj.getString("user");
	uid = obj.getInt("uid");
	gid = obj.getInt("gid");
	mainGroup = obj.getString("group");
	
	JSONArray arr = obj.getJSONArray("groups");
	groups = new String[arr.length()];
	for(int i = 0; i < arr.length(); i++)
	    groups[i] = arr.getString(i);
	
	fullname = obj.getString("fullname");
    }
}

package com.inf1315.vertretungsplan.api;

import java.io.*;
import java.net.*;
import java.util.*;

import org.json.*;

public class API
{
    private URL url;
    
    public API() throws MalformedURLException
    {
	this("http://skirising.no-ip.org/VPlanApp/api/");
    }

    public API(String url) throws MalformedURLException
    {
	this(new URL(url));
    }

    public API(URL url)
    {
	this.url = url;
    }
    
    public ApiResponse request(String action, String...params) throws IOException
    {
	Map<String, String> paramsMap = new HashMap<String, String>();
	for(String param : params)
	{
	    String[] sp = param.split("=");
	    if(sp.length != 2)
		throw new IllegalArgumentException("Params need to be key=value");
	    paramsMap.put(sp[0], sp[1]);
	}
	return request(action, paramsMap);
    }
    
    public ApiResponse request(String action, Map<String, String> params)
    {
	JSONObject obj = getJSONfromURL(url, "GET", params);
	try
	{
	    if(!actionToClassMap.containsKey(action))
		throw new RuntimeException("invalid action \"" + action + "\"");
	    return new ApiResponse(obj, actionToClassMap.get(action));
	} catch (Exception e)
	{
	    return new ApiResponse(e);
	}
    }
    
    public static JSONObject getJSONfromURL(URL url, String requestMethod, Map<String,String> params)
    {
	try
	{
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setRequestMethod(requestMethod);
	    if(params != null)
		for(String key : params.keySet())
		    conn.addRequestProperty(key, params.get(key));
	    conn.connect();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    StringBuilder sb = new StringBuilder();
	    String line;
	    while ((line = reader.readLine()) != null)
		sb.append(line).append('\n');
	    reader.close();
	    conn.disconnect();
	    return new JSONObject(sb.toString());
	} catch (IOException e)
	{
	    e.printStackTrace();
	} catch (JSONException e)
	{
	    e.printStackTrace();
	}
	return new JSONObject();
    }
    
    private static Map<String, Class<? extends ApiResult>> actionToClassMap;
    
    static
    {
	actionToClassMap = new HashMap<String, Class<? extends ApiResult>>();
	actionToClassMap.put("user", UserInfo.class);
    }
}

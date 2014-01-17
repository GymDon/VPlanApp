package com.inf1315.vertretungsplan.api;

import java.io.*;
import java.net.*;
import java.util.*;

import org.json.*;

public class API
{
    /**
     * The standard url: {@value #STANDARD_URL}
     */
    public static final String STANDARD_URL = "http://skirising.no-ip.org/VPlanApp/api/";
    
    private URL url;
    
    /**
     * Creates a new API with the {@link #STANDARD_URL} ({@value #STANDARD_URL})
     * @throws MalformedURLException
     * @see API#API(String)
     * @see API#API(URL)
     */
    public API() throws MalformedURLException
    {
	this(STANDARD_URL);
    }

    /**
     * Creates a new API with for the specified URL
     * @param url The URL
     * @throws MalformedURLException
     * @see API#API()
     * @see API#API(URL)
     */
    public API(String url) throws MalformedURLException
    {
	this(new URL(url));
    }

    /**
     * Creates a new API with for the specified URL
     * @param url The URL
     * @throws MalformedURLException
     * @see API#API()
     * @see API#API(String)
     */
    public API(URL url)
    {
	this.url = url;
    }
    
    /**
     * Make a new API-request
     * @param action The action to be performed
     * @param params The parameters to the action as strings with format <i>"key=value"</i>
     * @return The Response from the server
     * @throws IOException
     * @see {@link API#request(ApiAction, Map)}
     */
    public ApiResponse request(ApiAction action, String...params) throws IOException
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
    

    /**
     * Make a new API-request
     * @param action The action to be performed
     * @param params The parameters to the action
     * @return The Response from the server
     * @throws IOException
     * @see {@link API#request(ApiAction, String...)}
     */
    public ApiResponse request(ApiAction action, Map<String, String> params)
    {
	try
	{
	    JSONObject obj = getJSONfromURL(url, "GET", params);
	    if(!actionToClassMap.containsKey(action))
		throw new RuntimeException("invalid action \"" + action + "\"");
	    return new ApiResponse(obj, actionToClassMap.get(action));
	} catch (Exception e)
	{
	    return new ApiResponse(e);
	}
    }
    
    /**
     * Method for getting a JSONObject from an url
     * @param url The HTTP-URL
     * @param requestMethod The HTTP request method (GET/POST)
     * @param params Additional params for GET/POST
     * @return A JSONObject parsed from the specified url
     */
    public static JSONObject getJSONfromURL(URL url, String requestMethod, Map<String,String> params) throws IOException, JSONException
    {
	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	conn.setRequestMethod(requestMethod);
	if (params != null)
	    for (String key : params.keySet())
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
    }
    
    private static Map<ApiAction, Class<? extends ApiResult>> actionToClassMap;
    
    static
    {
	actionToClassMap = new HashMap<ApiAction, Class<? extends ApiResult>>();
	actionToClassMap.put(ApiAction.USER, UserInfo.class);
    }
}

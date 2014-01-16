package com.inf1315.vertretungsplan.api;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class ApiResponse
{
    private ApiResult result;
    private boolean success;
    private String action;
    private Map<String, String> params;
    
    public ApiResponse(JSONObject obj, Class<? extends ApiResult> resultType) throws JSONException
    {
	try
	{
	    result = resultType.getConstructor(JSONObject.class).newInstance(obj.getJSONObject("result"));
	} catch (InstantiationException e)
	{
	    e.printStackTrace();
	} catch (IllegalAccessException e)
	{
	    e.printStackTrace();
	} catch (IllegalArgumentException e)
	{
	    e.printStackTrace();
	} catch (InvocationTargetException e)
	{
	    e.printStackTrace();
	} catch (NoSuchMethodException e)
	{
	    e.printStackTrace();
	}
	success = obj.getBoolean("success");
	action = obj.getString(action);
	JSONObject par = obj.getJSONObject("params");
	for(Iterator<?> i = par.keys(); i.hasNext();)
	{
	    String key = (String) i.next();
	    params.put(key, par.getString(key));
	}
    }
    
    public ApiResponse(Exception e)
    {
	success = false;
    }
    
    public ApiResult getResult()
    {
	return result;
    }
    
    public boolean getSuccess()
    {
	return success;
    }
    
    public String getAction()
    {
	return action;
    }
    
    public Map<String, String> getParams()
    {
	return params;
    }
}

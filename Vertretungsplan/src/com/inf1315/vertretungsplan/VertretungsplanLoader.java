package com.inf1315.vertretungsplan;


import java.io.*;
import java.net.*;
import java.util.*;

import org.json.*;

import android.app.*;
import android.os.*;
import android.widget.*;

/**
 * The {@link VertretungsplanLoader} loads a JSON array from an {@link URL}
 * and adds it to the lists in the tabs of the {@link Main} activity
 */
public class VertretungsplanLoader extends AsyncTask<URL, Void, JSONArray>
{
    private Activity theActivity;
    private Dialog dialog;

    public VertretungsplanLoader(Activity activity)
    {
	theActivity = activity;
    }
    
    @Override
    protected void onPreExecute()
    {
	dialog = ProgressDialog.show(theActivity, "", "Loading", true);
    }

    @Override
    protected JSONArray doInBackground(URL... arg0)
    {
	JSONArray arr = getJSONfromURL(arg0[0], "GET", null);
	return arr;
    }

    //TODO: implement display activity
    /*
    protected void onPostExecute(JSONArray result)
    {
	ListView listViewToday = (ListView) theActivity.findViewById(R.id.listToday);
	ListView listViewTomorrow = (ListView) theActivity.findViewById(R.id.listTomorrow);
	List<ReplacementObject> listToday = new ArrayList<ReplacementObject>();
	List<ReplacementObject> listTomorrow = new ArrayList<ReplacementObject>();
	Calendar cal = Calendar.getInstance();
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	long time = cal.getTimeInMillis() / 1000;
	for (int i = 0; i < result.length(); i++)
	{
	    try
	    {
		JSONObject o = result.getJSONObject(i);
		long ot = o.getLong("timestamp");
		if (ot > time)
		    listTomorrow.add(new ReplacementObject(o));
		else if (ot == time)
		    listToday.add(new ReplacementObject(o));
	    } catch (JSONException e)
	    {
		e.printStackTrace();
	    }
	}
	Collections.sort(listToday);
	Collections.sort(listTomorrow);
	VertretungsplanAdapter adapterToday = new VertretungsplanAdapter(theActivity, R.layout.activity_main, listToday);
	listViewToday.setAdapter(adapterToday);
	adapterToday.notifyDataSetChanged();
	VertretungsplanAdapter adapterTomorrow = new VertretungsplanAdapter(theActivity, R.layout.activity_main, listTomorrow);
	listViewTomorrow.setAdapter(adapterTomorrow);
	adapterTomorrow.notifyDataSetChanged();
	dialog.hide();
	dialog = null;
    }*/

    /**
     * 
     * @param url The url requested
     * @param requestMethod The request method ("GET" or "POST")
     * @param params The request parameters in a map
     * @return A {@link JSONArray} parsed from the content of a HTTP-Request to <code>url</code>
     */
    public static JSONArray getJSONfromURL(URL url, String requestMethod, Map<String,String> params)
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
	    return new JSONArray(sb.toString());
	} catch (IOException e)
	{
	    e.printStackTrace();
	} catch (JSONException e)
	{
	    e.printStackTrace();
	}
	return new JSONArray();
    }
}

package com.inf1315.vertretungsplan.api;

import org.json.*;

public class PlanObject extends ApiResult
{
    private final ReplacementObject[] replacements;
    private final PageObject[] pages;
    
    
    public PlanObject(JSONObject obj) throws JSONException
    {
	JSONArray reps = obj.optJSONArray("replacements");
	if(reps != null)
	{
	    replacements = new ReplacementObject[reps.length()];
	    for(int i = 0; i < reps.length(); i++)
		replacements[i] = new ReplacementObject(reps.getJSONObject(i));
	}
	else
	    replacements = new ReplacementObject[0];
	JSONArray jpages = obj.optJSONArray("pages");
	if(reps != null)
	{
	    pages = new PageObject[reps.length()];
	    for(int i = 0; i < jpages.length(); i++)
		pages[i] = new PageObject(jpages.getJSONObject(i));
	}
	else
	    pages = new PageObject[0];
    }
    
    public ReplacementObject[] getReplacements()
    {
	return replacements;
    }
    
    public PageObject[] getPages()
    {
	return pages;
    }
}

package com.inf1315.vertretungsplan.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;

public abstract class ApiResult
{
    public ApiResult()
    {}

    public ApiResult(JSONObject object) throws JSONException
    {
	throw new UnsupportedOperationException("Constructor not defined");
    }
}

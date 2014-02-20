package de.gymdon.app.api;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ApiResult {
	private transient ApiResponse parent;
	
	public ApiResult() {
	}

	public ApiResult(JSONObject object) throws JSONException {
		throw new UnsupportedOperationException("Constructor not defined");
	}
	
	public void setParent(ApiResponse parent) {
		this.parent = parent;
	}
	
	public ApiResponse getParent() {
		return parent;
	}
}

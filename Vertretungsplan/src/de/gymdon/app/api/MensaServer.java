package de.gymdon.app.api;

import org.json.JSONException;
import org.json.JSONObject;

public class MensaServer extends ApiResult {
	public final int id;
	public final long timestamp;
	public final int type;
	public final String value;
	public final boolean week;

	public MensaServer(JSONObject obj) throws JSONException {
		id = obj.getInt("id");
		timestamp = obj.getLong("timestamp");
		type = obj.getInt("type");
		value = obj.getString("value");
		week = obj.getBoolean("week");
	}
}

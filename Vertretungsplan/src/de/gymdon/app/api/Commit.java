package de.gymdon.app.api;

import org.json.*;

import android.util.SparseArray;

public class Commit extends ApiResult {

	public String hash;
	public String comment;
	public long timestamp;
	public Tag tag;

	public Commit(JSONObject obj) throws JSONException {
		hash = obj.getString("commit");
		comment = obj.getString("comment");
		if(obj.has("tag"))
			this.tag = new Tag(this, obj.getString("tag"));
	}

	public Commit(String hash) {
		this.hash = hash;
	}

	public Commit(String hash, String comment) {
		this(hash);
		this.comment = comment;
	}

	public void setTag(String name) {
		this.tag = new Tag(this, name);
	}

	public static class Tag {
		public Commit commit;
		public String name;

		public Tag(Commit commit, String name) {
			this.commit = commit;
			this.name = name;
		}
	}

	public static SparseArray<String> versionTags;

	static {
		versionTags = new SparseArray<String>();
		versionTags.put(1, "0.1");
		versionTags.put(2, "0.2");
		versionTags.put(3, "0.3");
	}
}

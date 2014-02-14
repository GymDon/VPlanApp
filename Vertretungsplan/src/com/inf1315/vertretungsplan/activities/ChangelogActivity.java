package com.inf1315.vertretungsplan.activities;

import com.inf1315.vertretungsplan.R;
import com.inf1315.vertretungsplan.api.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

@SuppressLint("NewApi")
public class ChangelogActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changelog);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		load((WebView) findViewById(R.id.changelog_webview), findViewById(R.id.changelog_loading), 0, 0);
	}

	public void load(final WebView webView, final View progress,
			final int from, final int to) {
		new AsyncTask<Object, Object, Commit[]>() {

			@Override
			protected void onPreExecute() {
				webView.setVisibility(View.GONE);
				progress.setVisibility(View.VISIBLE);
			}

			@Override
			protected Commit[] doInBackground(Object... params) {
				try {
					return ((ApiResultArray) API.STANDARD_API.request(
							ApiAction.CHANGELOG,
							"from=" + (from > 0 ? Commit.versionTags.get(from) : "start"),
							"to=" + (to > 0 ? Commit.versionTags.get(to) : "end")).getResult())
							.getArray(new Commit[0]);
				} catch (Exception e) {
					e.printStackTrace();
					return new Commit[0];
				}
			}

			@Override
			protected void onPostExecute(Commit[] result) {
				Log.d("Changelog", "Finished Loading");
				StringBuilder message = new StringBuilder("<h2>"
						+ ChangelogActivity.this.getText(R.string.whatsnewNews)
						+ "</h2>");
				boolean hasUl = false;
				for (Commit commit : result) {
					if (commit.tag != null) {
						message.append(hasUl ? "</ul>" : "")
								.append("<h3>")
								.append(ChangelogActivity.this.getText(R.string.pref_version))
								.append(" ").append(commit.tag.name)
								.append(":</h3><ul>");
						hasUl = true;
					}
					if (!hasUl) {
						message.append("<ul>");
						hasUl = true;
					}
					message.append("<li>")
							.append(commit.comment.replace("\n", "<br />"))
							.append("</li>");
					Log.d("Changelog", "Commit: " + (commit.tag != null ? "(Tag " + commit.tag.name + ")": "") + 
							commit.hash + " " + commit.comment);
				}
				message.append("</ul>");
				webView.loadDataWithBaseURL(null, message.toString(),
						"text/html", "UTF-8", null);
				webView.setVisibility(View.VISIBLE);
				progress.setVisibility(View.GONE);
			}
		}.execute();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		} else
			return super.onOptionsItemSelected(item);
	}

}

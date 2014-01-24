package com.inf1315.vertretungsplan.api;

import java.util.ArrayList;
import java.util.List;

public class DataObject {

	public List<PageObject> pages = new ArrayList<PageObject>();
	public List<OtherObject> todayOthers = new ArrayList<OtherObject>();
	public List<OtherObject> tomorrowOthers = new ArrayList<OtherObject>();
	public List<TickerObject> tickers = new ArrayList<TickerObject>();
	public List<ReplacementObject> todayReplacementsList = new ArrayList<ReplacementObject>();
	public List<ReplacementObject> tomorrowReplacementsList = new ArrayList<ReplacementObject>();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pages == null) ? 0 : pages.hashCode());
		result = prime * result + ((tickers == null) ? 0 : tickers.hashCode());
		result = prime * result
				+ ((todayOthers == null) ? 0 : todayOthers.hashCode());
		result = prime
				* result
				+ ((todayReplacementsList == null) ? 0 : todayReplacementsList
						.hashCode());
		result = prime * result
				+ ((tomorrowOthers == null) ? 0 : tomorrowOthers.hashCode());
		result = prime
				* result
				+ ((tomorrowReplacementsList == null) ? 0
						: tomorrowReplacementsList.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataObject other = (DataObject) obj;
		if (pages == null) {
			if (other.pages != null)
				return false;
		} else if (!pages.equals(other.pages))
			return false;
		if (tickers == null) {
			if (other.tickers != null)
				return false;
		} else if (!tickers.equals(other.tickers))
			return false;
		if (todayOthers == null) {
			if (other.todayOthers != null)
				return false;
		} else if (!todayOthers.equals(other.todayOthers))
			return false;
		if (todayReplacementsList == null) {
			if (other.todayReplacementsList != null)
				return false;
		} else if (!todayReplacementsList.equals(other.todayReplacementsList))
			return false;
		if (tomorrowOthers == null) {
			if (other.tomorrowOthers != null)
				return false;
		} else if (!tomorrowOthers.equals(other.tomorrowOthers))
			return false;
		if (tomorrowReplacementsList == null) {
			if (other.tomorrowReplacementsList != null)
				return false;
		} else if (!tomorrowReplacementsList
				.equals(other.tomorrowReplacementsList))
			return false;
		return true;
	}

}

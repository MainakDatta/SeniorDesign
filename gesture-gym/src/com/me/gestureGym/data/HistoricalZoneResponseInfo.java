package com.me.gestureGym.data;

public class HistoricalZoneResponseInfo extends ZoneResponseInfo {
	private String _date;
	
	HistoricalZoneResponseInfo(int zoneNumber, float successDuration,
			double hitRate, String date) {
		super(zoneNumber, successDuration, hitRate);
		_date = date;
	}

	public String getDate() {
		return _date;
	}
}

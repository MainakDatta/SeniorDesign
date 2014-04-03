package com.me.gestureGym.data;

import java.util.List;

import almonds.*;

public class ParseWrapper {	
	private static final int N_ZONES = 16;
	private static final float DEFAULT_SUCCESS_DUR = 2;
	
	public ParseWrapper() {}
	
	/*
	 * Get the ParseObject corresponding to the input zone number.
	 */
	private ParseObject getZone(int zoneNumber) throws ParseException {
		ParseQuery q = new ParseQuery("ZoneInfo");
		q.whereEqualTo("zoneNumber", Integer.toString(zoneNumber));
		List<ParseObject> list = q.find();
		return list.size() >= 1 ? list.get(0) : null;
	}
	
	/*
	 * Put a ZoneInfo object into the DB.
	 */
	public void putZoneInfo(ZoneResponseInfo info) throws ParseException {
		ParseObject o = getZone(info.getZoneNumber());
		o.delete();
		
		ParseObject toPut = new ParseObject("ZoneInfo");
		toPut.put("zoneNumber", Integer.toString(info.getZoneNumber()));
		toPut.put("successDuration", Float.toString(info.getSuccessDuration()));
		toPut.put("hitRate", Double.toString(info.getHitRate()));
		toPut.save();
	}
	
	/*
	 * Get a single ZoneInfo from the DB.
	 */
	public ZoneResponseInfo getZoneInfo(int zoneNumber) throws ParseException {
		ParseObject o = getZone(zoneNumber);
		float successDuration = Float.parseFloat(o.getString("successDuration"));
		double hitRate = Double.parseDouble(o.getString("hitRate"));
		
		return new ZoneResponseInfo(zoneNumber, successDuration, hitRate);
	}
	
	/*
	 * Get all ZoneInfos from the DB (because there's probably no reason to wait, you
	 * can just get them all at the start of a session so you have them in memory).
	 */
	public ZoneResponseInfo[] getAllZoneInfos() {
		ZoneResponseInfo[] out = new ZoneResponseInfo[N_ZONES];
		
		for (int i = 0; i < N_ZONES; i++) {
			ParseObject o;			
			try {

				o = getZone(i);
				
			} catch (ParseException e) {
				//Get Zone failed
				System.out.println("Get zone failed");
				e.printStackTrace();
				o = new ParseObject("ZoneInfo");
				o.put("zoneNumber", Integer.toString(i));
				o.put("successDuration", Float.toString(DEFAULT_SUCCESS_DUR));
				o.put("hitRate", Double.toString(1.0));
				o.saveInBackground();
			}
			
			if (o == null) {
				//Get Zone returned null
				System.out.println("Get zone returned null");
				o = new ParseObject("ZoneInfo");
				o.put("zoneNumber", Integer.toString(i));
				o.put("successDuration", Float.toString(DEFAULT_SUCCESS_DUR));
				o.put("hitRate", Double.toString(1.0));
				o.saveInBackground();
			}
			
			float successDuration = Float.parseFloat(o.getString("successDuration"));
			double hitRate = Double.parseDouble(o.getString("hitRate"));
			
			out[i] = new ZoneResponseInfo(i, successDuration, hitRate);
		}
		
		return out;
	}
	
	/*
	 * Set all ZoneInfo entries in the database to have the default success duration. 
	 */
	public void clean(float defaultSuccessDuration) throws ParseException {
		for (int i = 0; i < N_ZONES; i++) {
			ParseObject o = getZone(i);
			o.delete();
			
			ParseObject toPut = new ParseObject("ZoneInfo");
			toPut.put("zoneNumber", Integer.toString(i));
			toPut.put("successDuration", Float.toString(defaultSuccessDuration));
			toPut.put("hitRate", Double.toString(1.0));
			toPut.save();
		}
	}
}

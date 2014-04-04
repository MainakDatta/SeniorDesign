package com.me.gestureGym.data;

import java.util.List;

import com.me.gestureGym.controllers.ZoneInfoWrapper;

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
	
	public void getZoneAsync(int zoneNumber){
		//Stupid thing required by nested class
		final int zoneNum  = zoneNumber;
		ParseQuery q = new ParseQuery("ZoneInfo");
		q.whereEqualTo("zoneNumber", Integer.toString(zoneNumber));
		try{
			q.findInBackground(new FindCallback() {
			     public void done(List<ParseObject> objects, ParseException e) {
			         if (e == null) {
			        	 if(objects.size() >= 1){
			        		 ParseObject response = objects.get(0);
			        		 int zoneNum = Integer.parseInt(response.getString("zoneNumber"));
			     			 float successDuration = Float.parseFloat(response.getString("successDuration"));
			    			 double hitRate = Double.parseDouble(response.getString("hitRate"));
			    			 ZoneResponseInfo zres = new ZoneResponseInfo(zoneNum, successDuration, hitRate);
			    			 ZoneInfoWrapper.updateZone(zres);
			        	 }
			         } else {
			        	//Make default
			     		ParseObject o = new ParseObject("ZoneInfo");
			     		o.put("zoneNumber", Integer.toString(zoneNum));
			     		o.put("successDuration", Float.toString(DEFAULT_SUCCESS_DUR));
			     		o.put("hitRate", Double.toString(1.0));
			     		o.saveInBackground();
			     		ZoneResponseInfo zres = new ZoneResponseInfo(zoneNum, DEFAULT_SUCCESS_DUR, 1.0);
			     		ZoneInfoWrapper.updateZone(zres);
			         }
			     }
			 });
		}catch (RuntimeException e) {
			//Make default
			ParseObject o = new ParseObject("ZoneInfo");
			o.put("zoneNumber", Integer.toString(zoneNumber));
			o.put("successDuration", Float.toString(DEFAULT_SUCCESS_DUR));
			o.put("hitRate", Double.toString(1.0));
			o.saveInBackground();
			ZoneResponseInfo zres = new ZoneResponseInfo(zoneNumber, DEFAULT_SUCCESS_DUR, 1.0);
			ZoneInfoWrapper.updateZone(zres);
		}
		
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

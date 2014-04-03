package com.me.gestureGym.controllers;

import almonds.ParseException;

import com.me.gestureGym.data.ParseWrapper;
import com.me.gestureGym.data.ZoneResponseInfo;

/*
 * Wrapper class that will be used by game screens and such to access the
 * ZoneResponse stuff from Parse
 * 
 */
public class ZoneInfoWrapper {
	
	private static ZoneResponseInfo[] zoneInfo; 
	
	//Gives the caller the current status of db
	public static ZoneResponseInfo[] getZoneInfo(){		
		ParseWrapper parse = new ParseWrapper();
		//Should do this every time after first time
		if (zoneInfo != null) {
			//System.out.println("retrieved zone response infos without hitting db");
			return zoneInfo;
		}
		
		ZoneResponseInfo[] info = parse.getAllZoneInfos();
		System.out.println("retrieved zone response infos, had to hit db");
		zoneInfo = info;
		return info;				
	}
	
	public static boolean isZoneReady(){
		return (zoneInfo != null);
	}
	
	//Updates the static zoneInfo array for future calls
	public static void updateZone(ZoneResponseInfo updated_zone){		
		int index = updated_zone.getZoneNumber();
		zoneInfo[index] = updated_zone;
	}
	
	//Pushes current data to DB
	public static boolean push(){	
		try {
			for(int i = 0; i < zoneInfo.length; i++){
				ParseWrapper parse = new ParseWrapper();	
				parse.putZoneInfo(zoneInfo[i]);				
			}
			return true;
		}
		 catch (ParseException e) {
				//Dunno about this...but we have to handle it cleanly
				return false;
		}	
	}
	
}

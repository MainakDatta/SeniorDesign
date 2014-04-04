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
	private static final int N_ZONES = 16;
	
	//Gives the caller the current status of db
	public static ZoneResponseInfo[] getZoneInfo(){		
		ParseWrapper parse = new ParseWrapper();
		//Should do this every time after first time
		if (zoneInfo != null) {
			//System.out.println("retrieved zone response infos without hitting db");
			return zoneInfo;
		}
		//This needs to happen in background
		zoneInfo = new ZoneResponseInfo[N_ZONES];		
		for(int i = 0; i< zoneInfo.length; i++){
			parse.getZoneAsync(i);
			
		}				
		System.out.println("Had to hit db");
		return zoneInfo;				
	}
		
	public static boolean isReady(){
		//Returns true if array is fully loaded
		if(zoneInfo != null){
			for(int i = 0; i< zoneInfo.length; i++){
				if(zoneInfo[i] == null)return false;
			}
			return true;
		}
		return false;
	}
	
	//Updates the static zoneInfo array for future calls
	public static void updateZone(ZoneResponseInfo updated_zone){
		//Creates zoneInfo if needed
		if(zoneInfo == null){
			zoneInfo = new ZoneResponseInfo[N_ZONES];
		}
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

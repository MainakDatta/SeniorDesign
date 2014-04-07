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
	private static ZoneResponseInfo[] mtZoneInfo;
	private static final int N_ZONES = 16;
	
	//Gives the caller the current status of db
	public static ZoneResponseInfo[] getZoneInfo(boolean isMT){		
		ParseWrapper parse = new ParseWrapper();
		if (isMT) {
			if (mtZoneInfo != null) {
				return mtZoneInfo;
			}
			
			mtZoneInfo = new ZoneResponseInfo[N_ZONES];
			for (int i = 0; i < mtZoneInfo.length; i++) {
				parse.getZoneAsync(i, true);
			}
			
			System.out.println("Had to hit db mt");
			return mtZoneInfo;
		} else {
			//Should do this every time after first time
			if (zoneInfo != null) {
				//System.out.println("retrieved zone response infos without hitting db");
				return zoneInfo;
			}
			//This needs to happen in background
			zoneInfo = new ZoneResponseInfo[N_ZONES];		
			for(int i = 0; i< zoneInfo.length; i++){
				parse.getZoneAsync(i, false);
				
			}				
			System.out.println("Had to hit db");
			return zoneInfo;
		}
	}
		
	public static boolean isReady(){
		return singleTouchIsReady() && multiTouchIsReady();
	}
	
	private static boolean singleTouchIsReady() {
		//Returns true if array is fully loaded
		if (zoneInfo != null) {
			for (int i = 0; i< zoneInfo.length; i++){
				if (zoneInfo[i] == null) return false;
			}
			
			System.out.println("st is ready");
			return true;
		}
		return false;
	}
	
	private static boolean multiTouchIsReady() {
		//Returns true if array is fully loaded
		if (mtZoneInfo != null) {
			for (int i = 0; i < mtZoneInfo.length; i++){
				if (mtZoneInfo[i] == null) {
					System.out.println("mt not ready, " + i + " is null");
					return false;
				}
			}
			
			System.out.println("mt is ready");
			return true;
		}
		System.out.println("mt not ready, is null");
		return false;
	}
	
	//Updates the static zoneInfo array for future calls
	public static void updateZone(ZoneResponseInfo updated_zone, boolean isMT){
		if (isMT) {
			//Creates zoneInfo if needed
			if(mtZoneInfo == null){
				mtZoneInfo = new ZoneResponseInfo[N_ZONES];
			}
			int index = updated_zone.getZoneNumber();
			mtZoneInfo[index] = updated_zone;
		} else {
			//Creates zoneInfo if needed
			if(zoneInfo == null){
				zoneInfo = new ZoneResponseInfo[N_ZONES];
			}
			int index = updated_zone.getZoneNumber();
			zoneInfo[index] = updated_zone;
		}
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

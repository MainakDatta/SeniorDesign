package com.me.gestureGym.controllers;

import java.io.IOException;

import almonds.ParseException;
import almonds.ParseObject;

import com.me.gestureGym.data.DataWrapper;
import com.me.gestureGym.data.LocalStorageDoesNotExistException;
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
	private static String currentPatient;
	
	//Gives the caller the current status of db
	public static ZoneResponseInfo[] getZoneInfo(boolean isMT){		
		if (isMT) {
			if (mtZoneInfo != null) {
				return mtZoneInfo;
			}
			
	        try {
	        	//need to remove
	        	DataWrapper.setCurrentPatient("Mainak Datta");
				currentPatient = DataWrapper.getCurrentPatient();
				mtZoneInfo = DataWrapper.getMostRecentMultiTouchData(currentPatient);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Problems getting current patient");
			}
	        
	        
		
			return mtZoneInfo;
		} else {
			//SINGLE TOUCH			
			if (zoneInfo != null) {
				return zoneInfo;
			}
			
			try {
				//need to remove
				DataWrapper.setCurrentPatient("Mainak Datta");
				currentPatient = DataWrapper.getCurrentPatient();
				zoneInfo = DataWrapper.getMostRecentSingleTouchData(currentPatient);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Problems getting current patient");
			}

			return zoneInfo;
		}
	}

	
	public static boolean singleTouchIsReady() {
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
	
	public static boolean multiTouchIsReady() {
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
	
//	//Updates the static zoneInfo array for future calls
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
//	
//	//Pushes current data to DB
//	public static boolean push(){	
//		try {
//			for(int i = 0; i < zoneInfo.length; i++){
//				ParseWrapper parse = new ParseWrapper();	
//				parse.putZoneInfo(zoneInfo[i]);				
//			}
//			return true;
//		}
//		 catch (ParseException e) {
//				//Dunno about this...but we have to handle it cleanly
//				return false;
//		}	
//	}
	
}

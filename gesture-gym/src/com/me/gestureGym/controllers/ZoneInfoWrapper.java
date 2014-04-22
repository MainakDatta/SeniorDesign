package com.me.gestureGym.controllers;

import java.io.IOException;


import com.me.gestureGym.data.DataWrapper;
import com.me.gestureGym.data.LocalStorageDoesNotExistException;

import com.me.gestureGym.data.ZoneResponseInfo;

/*
 * Wrapper class that will be used by game screens and such to access the
 * ZoneResponse stuff from Parse
 * 
 */
public class ZoneInfoWrapper {
	
	private static ZoneResponseInfo[] zoneInfo;
	private static ZoneResponseInfo[] mtZoneInfo;
	private static String currentPatient;
	
	//Gives the caller the current status of db
	public static ZoneResponseInfo[] getZoneInfo(boolean isMT){		
		if (isMT) {
			if (mtZoneInfo != null) {
				return mtZoneInfo;
			}
			
	        try {
	        	//DataWrapper.setCurrentPatient("Mainak Datta");
				currentPatient = DataWrapper.getCurrentPatient();
				
				// commented out cuz mainak said so
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
				//DataWrapper.setCurrentPatient("Mainak Datta");
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
			int index = updated_zone.getZoneNumber();
			mtZoneInfo[index] = updated_zone;
		} else {
			int index = updated_zone.getZoneNumber();
			zoneInfo[index] = updated_zone;
		}
	}
	
	//Pushes current data to DB
	public static boolean push(boolean isMultiTouch){	
		try {
			if(isMultiTouch)
				DataWrapper.putMultiTouchData(currentPatient, mtZoneInfo);
			else
				DataWrapper.putSingleTouchData(currentPatient, zoneInfo);
			return true;
		}
		 catch (Exception e) {
			 e.printStackTrace();
			 return false;
		}	
	}
	
}

package com.me.gestureGym.controllers;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.utils.*;
import com.me.gestureGym.data.*;
import com.me.gestureGym.models.*;

public class SequenceGenerator {
	private static final int N_ZONES = 16;
	private static final int CUES_PER_SEQUENCE = 60;
	
	public static Sequence generateSequence(Zone[] zones, ZoneResponseInfo[] zoneResponses, 
			boolean connected) {
		//4 zones that this sequence will populate
		ZoneResponseInfo[] seqZones = getSequenceZones(zoneResponses, connected);
		//Interesting technique used to ensure each of the 4 zones receives the same number of cues
		int[] zoneCounts = {
			CUES_PER_SEQUENCE / 4,
			CUES_PER_SEQUENCE / 4,
			CUES_PER_SEQUENCE / 4,
			CUES_PER_SEQUENCE / 4
		};
		//OMG ADAPTIVE DIFFICULTY. MUCH WOW.
		float duration = durationFromZones(seqZones);
		//Time between cues appearing. May want to be different from duration
		float timeBetweenCues = duration;
		
		Array<TapCue> cues = new Array<TapCue>();
		float startTime = 0;
		//Populates the 4 zones of sequence with cues
		while (zoneCounts[0] > 0 && zoneCounts[1] > 0 &&
			   zoneCounts[2] > 0 && zoneCounts[3] > 0) {
			//Which of the 4 zones we are fucking with
			int which = (int) (Math.random() * 4);
			int zoneNum = seqZones[which].getZoneNumber();
			Zone theZone = zones[zoneNum];
			if (zoneCounts[which] > 0) {
				float x = getRandomXFromZone(theZone);
				float y = getRandomYFromZone(theZone);
				System.out.println("zone number is " + zoneNum);
				System.out.println("adding cue with coordinate (" + x + ", " + y + ")");
				System.out.println("cue has start time " + startTime + " and end time " + (startTime + duration));
				cues.add(new TapCue(x, y, zoneNum, startTime, startTime + duration));				
				startTime += timeBetweenCues;
				zoneCounts[which]--;
			}
			int numZones = theZone.getZoneNumber() + 1;
			theZone.setNum(numZones);
		}
		
		return new Sequence(cues, duration);
	}
	
	private static float getRandomXFromZone(Zone zone) {
		return zone.getX() + ((float) Math.random()) * zone.getWidth();
	}
	
	private static float getRandomYFromZone(Zone zone) {
		return zone.getY() + ((float) Math.random()) * zone.getHeight();
	}
	
	
	//VERY CRUCIAL FUNCTION
	private static float durationFromZones(ZoneResponseInfo[] seqZones) {
		float minDuration = Float.MAX_VALUE;
		for (int i = 0; i < seqZones.length; i++) {
			if (seqZones[i].getSuccessDuration() < minDuration) {
				minDuration = seqZones[i].getSuccessDuration();
			}
		}
		
		System.out.println("chose min duration of " + minDuration);
		
		return minDuration - deltaDuration(minDuration);
	}
	
	private static float deltaDuration(float duration) {
		// this was a shitty made up regression
		// we can change it
		// like please change it
		return 3.669069119f * (float) Math.pow(10, -2) * duration * duration * duration
			  - 9.696005577f * (float) Math.pow(10, -2) * duration * duration
			  + 0.1484657986f * duration
			  - 0.2683188376f;
	}
	
	//Returns 4 adjacent or separated zones
	private static ZoneResponseInfo[] getSequenceZones(ZoneResponseInfo[] zones, boolean connected) {
		HashSet<Integer> seqZones = new HashSet<Integer>();
		seqZones.add((int) (N_ZONES * Math.random()));
		seqZones.add(getRandomAdjacentZone(seqZones));
		
		if (connected) {
			seqZones.add(getRandomAdjacentZone(seqZones));
			seqZones.add(getRandomAdjacentZone(seqZones));
		} else {
			int far = getRandomNonAdjacentZone(seqZones);
			seqZones.add(far);
			seqZones.add(getRandomAdjacentZone(far));
		}
		
		ZoneResponseInfo[] out = new ZoneResponseInfo[4];
		int count = 0;
		for (int i : seqZones) {
			out[count] = zones[i];
			count++;
		}
		
		return out;
	}
	
	private static int getRandomNonAdjacentZone(HashSet<Integer> zones) {
		HashSet<Integer> choices = new HashSet<Integer>();
		for (int i = 0; i < N_ZONES; i++) {
			choices.add(i);
		}
		
		for (int i : zones) {
			choices.remove(i);
			choices.remove(i + 1);
			choices.remove(i - 1);
			choices.remove(i - (int) Math.sqrt(N_ZONES));
			choices.remove(i + (int) Math.sqrt(N_ZONES));
		}
		
		int[] choicesArr = new int[choices.size()];
		int count = 0;
		for (int i : choices) {
			choicesArr[count] = i;
			count++;
		}
		
		return choicesArr[(int) (Math.random() * choicesArr.length)];
	}
	
	private static int getRandomAdjacentZone(HashSet<Integer> zones) {
		int count = 0;
		while (count < 10) {
			for (int i : zones) {
				int adj = getRandomAdjacentZone(i);
				if (!zones.contains(adj)) {
					return adj;
				}
			}
			count++;
		}
		return -1;
	}
	
	private static int getRandomAdjacentZone(int zone) {
		int rowSize = (int) Math.sqrt(N_ZONES);
		
		boolean inTopRow = zone / rowSize == 0;
		boolean inBottomRow = zone / rowSize == rowSize - 1;
		boolean inLeftColumn = zone % rowSize == 0;
		boolean inRightColumn = zone % rowSize == rowSize - 1;
		
		ArrayList<Integer> opts = new ArrayList<Integer>();
		if (inTopRow) {
			if (inLeftColumn) {
				opts.add(zone + 1);
				opts.add(zone + rowSize);
			} else if (inRightColumn) {
				opts.add(zone - 1);
				opts.add(zone + rowSize);
			} else {
				opts.add(zone - 1);
				opts.add(zone + 1);
				opts.add(zone + rowSize);
			}
		} else if (inBottomRow) {
			if (inLeftColumn) {
				opts.add(zone - rowSize);
				opts.add(zone + 1);
			} else if (inRightColumn) {
				opts.add(zone - rowSize);
				opts.add(zone - 1);
			} else {
				opts.add(zone - rowSize); 
				opts.add(zone - 1); 
				opts.add(zone + 1);
			}
		} else if (inLeftColumn) {
			opts.add(zone - rowSize);
			opts.add(zone + 1);
			opts.add(zone + rowSize);
		} else if (inRightColumn) {
			opts.add(zone - rowSize);
			opts.add(zone - 1);
			opts.add(zone + rowSize);
		} else {
			opts.add(zone - rowSize);
			opts.add(zone - 1);
			opts.add(zone + 1);
			opts.add(zone + rowSize);
		}
		
		return opts.get((int) (Math.random() * opts.size()));
	}
}

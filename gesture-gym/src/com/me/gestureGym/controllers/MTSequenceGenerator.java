package com.me.gestureGym.controllers;

import java.util.HashSet;

import com.badlogic.gdx.utils.Array;
import com.me.gestureGym.data.*;
import com.me.gestureGym.models.*;

public class MTSequenceGenerator {
	private static final int N_ZONES = 16;
	private static final int CUE_PAIRS_PER_SEQUENCE = 28;
	
	public static Sequence generateSequence(Zone[] zones, ZoneResponseInfo[] zoneResponses) {
		ZoneResponseInfo[] seqZoneResponses = getSequenceZones(zoneResponses);
		
		int[] zoneCounts = {
			CUE_PAIRS_PER_SEQUENCE / 2,
			CUE_PAIRS_PER_SEQUENCE / 2,
		};
		
		float duration = durationFromZones(seqZoneResponses);
		float timeBetweenCues = duration;
		
		Array<TapCue> cues = new Array<TapCue>();
		float startTime = 0;
		
		while (zoneCounts[0] > 0 || zoneCounts[1] > 0) {
			int which = (int) (2 * Math.random());
			int firstZoneNum = seqZoneResponses[which * 2].getZoneNumber();
			int secondZoneNum = seqZoneResponses[which * 2 + 1].getZoneNumber();
			Zone firstZone = zones[firstZoneNum];
			Zone secondZone = zones[secondZoneNum];
			
			if (zoneCounts[which] > 0) {
				float x1 = getRandomXFromZone(firstZone);
				float y1 = getRandomYFromZone(firstZone);
				cues.add(new TapCue(x1, y1, firstZoneNum, startTime, startTime + duration));
				
				float x2 = getRandomXFromZone(secondZone);
				float y2 = getRandomYFromZone(secondZone);
				cues.add(new TapCue(x2, y2, secondZoneNum, startTime, startTime + duration));
				
				startTime += timeBetweenCues;
				zoneCounts[which]--;
				firstZone.setNumCues(firstZone.getNumCues() + 1);
				secondZone.setNumCues(secondZone.getNumCues() + 1);
			}
		}
		
		return new Sequence(cues, duration, deltaDuration(duration));
	}
	
	// takes in a zone and returns a random x coordinate in that zone
	private static float getRandomXFromZone(Zone zone) {
		return zone.getX() + ((float) Math.random()) * zone.getWidth();
	}
	
	// takes in a zone and returns a random y coordinate in that zone	
	private static float getRandomYFromZone(Zone zone) {
		return zone.getY() + ((float) Math.random()) * zone.getHeight();
	}
	
	private static ZoneResponseInfo[] getSequenceZones(ZoneResponseInfo[] zones) {
		ZoneResponseInfo[] out = new ZoneResponseInfo[4];
		
		int firstRandomZone = (int) (N_ZONES * Math.random());
		out[0] = zones[firstRandomZone];
		out[1] = zones[getRandomNonAdjacentZone(firstRandomZone)];
		
		int secondRandomZone = (int) (N_ZONES * Math.random());
		out[2] = zones[secondRandomZone];
		out[3] = zones[getRandomNonAdjacentZone(secondRandomZone)];
		
		return out;
	}
	
	private static int getRandomNonAdjacentZone(int zone) {
		HashSet<Integer> choices = new HashSet<Integer>();
		
		// start with all options
		for (int i = 0; i < N_ZONES; i++) {
			choices.add(i);
		}
		
		// remove all adjacent options
		choices.remove(zone);
		choices.remove(zone + 1);
		choices.remove(zone - 1);
		choices.remove(zone - (int) Math.sqrt(N_ZONES));
		choices.remove(zone + (int) Math.sqrt(N_ZONES));
		
		// hashset to array
		int[] choicesArr = new int[choices.size()];
		int count = 0;
		for (int i : choices) {
			choicesArr[count] = i;
			count++;
		}
		
		// pick one randomly
		return choicesArr[(int) (Math.random() * choicesArr.length)];
	}
	
	// takes in a list of zone responses and returns the duration of a cue that
	// should be used for a sequence hitting these zones
	private static float durationFromZones(ZoneResponseInfo[] seqZones) {
		float maxDuration = Float.MIN_VALUE;
		for (int i = 0; i < seqZones.length; i++) {
			if (seqZones[i].getSuccessDuration() > maxDuration) {
				maxDuration = seqZones[i].getSuccessDuration();
			}
		}
		
		System.out.println("chose max duration of " + maxDuration);		
		System.out.println("Delta duration is " + deltaDuration(maxDuration));
		return maxDuration + deltaDuration(maxDuration);
	}

	//MOST IMPORTANT PART OF SENIOR DESIGN. NEED TO MOVE TO ANOTHER FILE
	// takes in a duration and returns the change in duration that should occur
	private static float deltaDuration(float duration) {
		// this was a shitty made up regression
		// we can change it
		// like please change it
		
		/*
		 * 2.0 -0.2
         * 1.8 -0.15
         * 1.6 -0.125
         * 1.4 -0.1
         * 1.2 -0.08
         * 1.0 -0.0625
         * 0.8 -0.04
         * 0.6 -0.03
         * 0.4 -0.02
		 */
		return -0.04701028139f * duration * duration
			  + 0.005533008658f * duration
			  - 0.01613095238f;
	}
}

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
		// choose zones that this sequence will put cues in
		ZoneResponseInfo[] seqZoneResponses = getSequenceZones(zoneResponses, connected);
		for(ZoneResponseInfo z: seqZoneResponses){			
			System.out.println("Picked zone " + z.getZoneNumber() + " with dur: " + z.getSuccessDuration());
		}
		
		// counter list to make sure all zones get the same number of cues (may be a more
		// efficient way to do this)
		int[] zoneCounts = {
			CUES_PER_SEQUENCE / 4,
			CUES_PER_SEQUENCE / 4,
			CUES_PER_SEQUENCE / 4,
			CUES_PER_SEQUENCE / 4
		};
		//get duration based on previous performance
		// duration: time that a single cue lasts
		float duration = durationFromZones(seqZoneResponses);
		System.out.println("Duration for this round: " + duration);
		//timeBetweenCues: time from one cue's appearance to the next
		// could be different from duration (maybe like 4 / 5)
		float timeBetweenCues = duration;
		
		Array<TapCue> cues = new Array<TapCue>();
		float startTime = 0;
		
		// get all the cues
		while (zoneCounts[0] > 0 || zoneCounts[1] > 0 ||
			   zoneCounts[2] > 0 || zoneCounts[3] > 0) {
			// pick which zone the cue gets put in
			int which = (int) (Math.random() * 4);
			int zoneNum = seqZoneResponses[which].getZoneNumber();
			Zone zone = zones[zoneNum];
			
			// put a cue in that zone if that zone isn't full of cues
			if (zoneCounts[which] > 0) {
				float x = getRandomXFromZone(zone);
				float y = getRandomYFromZone(zone);
//				System.out.println("zone number is " + zoneNum);
//				System.out.println("adding cue with coordinate (" + x + ", " + y + ")");
//				System.out.println("cue has start time " + startTime + " and end time " + (startTime + duration));
				cues.add(new TapCue(x, y, zoneNum, startTime, startTime + duration));				
				startTime += timeBetweenCues;
				zoneCounts[which]--;
				zone.setNumCues(zone.getNumCues() + 1);
			}
		}
		
		return new Sequence(cues, duration);
	}
	
	// takes in a zone and returns a random x coordinate in that zone
	private static float getRandomXFromZone(Zone zone) {
		return zone.getX() + ((float) Math.random()) * zone.getWidth();
	}
	
	// takes in a zone and returns a random y coordinate in that zone	
	private static float getRandomYFromZone(Zone zone) {
		return zone.getY() + ((float) Math.random()) * zone.getHeight();
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
		return 3.669069119f * (float) (Math.pow(10, -2) * duration * duration * duration)
			  - 9.696005577f * (float) (Math.pow(10, -2) * duration * duration)
			  + 0.1484657986f * duration
			  - 0.2683188376f;
	}
	
	// takes in a list of zone responses and a parameter indicating whether or not the zones in the sequence
	// should be all connected or not
	//
	// returns a list of zone responses that will be included in the sequence being generated
	private static ZoneResponseInfo[] getSequenceZones(ZoneResponseInfo[] zones, boolean connected) {
		HashSet<Integer> seqZones = new HashSet<Integer>();
		
		// pick a zone at random
		seqZones.add((int) (N_ZONES * Math.random()));
		
		// get a zone adjacent to it
		seqZones.add(getRandomAdjacentZone(seqZones));
		
		if (connected) {
			// get two more adjacent zones
			seqZones.add(getRandomAdjacentZone(seqZones));
			seqZones.add(getRandomAdjacentZone(seqZones));
		} else {
			// get a non-adjacent zone and then a zone adjacent to that zone
			int far = getRandomNonAdjacentZone(seqZones);
			seqZones.add(far);
			seqZones.add(getRandomAdjacentZone(far));
		}
		
		// hashset to array
		ZoneResponseInfo[] out = new ZoneResponseInfo[4];
		int count = 0;
		for (int i : seqZones) {
			out[count] = zones[i];
			count++;
		}
		
		return out;
	}
	
	// takes in a set of zones and returns a random zone not adjacent to any zones in the set
	private static int getRandomNonAdjacentZone(HashSet<Integer> zones) {
		HashSet<Integer> choices = new HashSet<Integer>();
		
		// start with all options
		for (int i = 0; i < N_ZONES; i++) {
			choices.add(i);
		}
		
		// remove all adjacent options
		for (int i : zones) {
			choices.remove(i);
			choices.remove(i + 1);
			choices.remove(i - 1);
			choices.remove(i - (int) Math.sqrt(N_ZONES));
			choices.remove(i + (int) Math.sqrt(N_ZONES));
		}
		
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
	
	// get a random zone adjacent a zone in the set of zones input
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
		if (!inTopRow)      opts.add(zone - rowSize);
		if (!inLeftColumn)  opts.add(zone - 1);
		if (!inRightColumn) opts.add(zone + 1);
		if (!inBottomRow)   opts.add(zone + rowSize);
		
		return opts.get((int) (Math.random() * opts.size()));
	}
}

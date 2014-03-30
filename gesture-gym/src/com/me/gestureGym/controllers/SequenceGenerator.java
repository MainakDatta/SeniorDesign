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

		//timeBetweenCues: time from one cue's appearance to the next
		// could be different from duration (maybe like 4 / 5)
		float timeBetweenCues = duration;
		
		Array<TapCue> cues = new Array<TapCue>();
		float startTime = 0;
		
		// get all the cues
		while (zoneCounts[0] > 0 && zoneCounts[1] > 0 &&
			   zoneCounts[2] > 0 && zoneCounts[3] > 0) {
			// pick which zone the cue gets put in
			int which = (int) (Math.random() * 4);
			int zoneNum = seqZoneResponses[which].getZoneNumber();
			Zone theZone = zones[zoneNum];
			
			// put a cue in that zone if that zone isn't full of cues
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
			
			// update num cues per zone
			theZone.setNumCues(theZone.getNumCues() + 1);
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
		float minDuration = Float.MAX_VALUE;
		for (int i = 0; i < seqZones.length; i++) {
			if (seqZones[i].getSuccessDuration() < minDuration) {
				minDuration = seqZones[i].getSuccessDuration();
			}
		}
		
		System.out.println("chose min duration of " + minDuration);
		
		return minDuration - deltaDuration(minDuration);
	}
	
	// takes in a duration and returns the change in duration that should occur
	private static float deltaDuration(float duration) {
		// this was a shitty made up regression
		// we can change it
		// like please change it
		return 3.669069119f * (float) Math.pow(10, -2) * duration * duration * duration
			  - 9.696005577f * (float) Math.pow(10, -2) * duration * duration
			  + 0.1484657986f * duration
			  - 0.2683188376f;
	}
	
	// takes in a list of zone responses and a parameter indicating whether or not the zones in the sequence
	// should be all connected or not
	//
	// returns a list of zone responses that will be included in the sequence being generated
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

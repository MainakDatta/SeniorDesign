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
		ZoneResponseInfo[] seqZones = getSequenceZones(zoneResponses, connected);
		
		int[] zoneCounts = {
			CUES_PER_SEQUENCE / 4,
			CUES_PER_SEQUENCE / 4,
			CUES_PER_SEQUENCE / 4,
			CUES_PER_SEQUENCE / 4
		};
		
		float duration = durationFromZones(seqZones);
		float timeBetweenCues = duration;
		
		Array<TapCue> cues = new Array<TapCue>();
		float startTime = 0;
		while (zoneCounts[0] > 0 && zoneCounts[1] > 0 &&
			   zoneCounts[2] > 0 && zoneCounts[3] > 0) {
			int which = (int) (Math.random() * 4);
			if (zoneCounts[which] > 0) {
				float x = getRandomXFromZone(zones[seqZones[which].getZoneNumber()]);
				float y = getRandomYFromZone(zones[seqZones[which].getZoneNumber()]);
				cues.add(new TapCue(x, y, startTime, startTime + duration));
				startTime += timeBetweenCues;
			}
		}
		
		return new Sequence(cues);
	}
	
	private static float getRandomXFromZone(Zone zone) {
		return zone.getX() + ((float) Math.random()) * zone.getWidth();
	}
	
	private static float getRandomYFromZone(Zone zone) {
		return zone.getY() + ((float) Math.random()) * zone.getHeight();
	}

	private static float durationFromZones(ZoneResponseInfo[] seqZones) {
		float minDuration = Float.MAX_VALUE;
		for (int i = 0; i < seqZones.length; i++) {
			if (seqZones[i].getSuccessDuration() < minDuration) {
				minDuration = seqZones[i].getSuccessDuration();
			}
		}
		return minDuration - deltaDuration(minDuration);
	}
	
	private static float deltaDuration(float duration) {
		// this was a shitty made up regression
		// we can change it
		// like please change it
		return -0.1142461098f * duration * duration * duration
			  + 0.407304256f  * duration * duration
			  - 0.2611833644f * duration
			  + 0.05440708281f;
	}

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

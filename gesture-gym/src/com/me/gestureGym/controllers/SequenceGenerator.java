package com.me.gestureGym.controllers;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.utils.*;
import com.me.gestureGym.data.*;
import com.me.gestureGym.models.*;

public class SequenceGenerator {
	private static final int N_ZONES = 16;
	
	public static Sequence generateSequence(ZoneResponseInfo[] zones, boolean connected) {
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
		
		Array<TapCue> cues = new Array<TapCue>();
		
		double duration = 0;
		
		double timeBetweenCues = 0;
		
		return new Sequence(cues, duration, timeBetweenCues);
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

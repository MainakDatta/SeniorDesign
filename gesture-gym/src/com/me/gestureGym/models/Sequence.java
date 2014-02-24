package com.me.gestureGym.models;

public class Sequence {
	// TODO: won't be only taps
	private TapCue[] cues;

	// start times of each cue (probably in ms)
	private double[] startTimes;

	// end times of each cue (probably in ms)
	private double[] endTimes;

	// TODO: add more parameters
	public Sequence(TapCue[] cues, double duration, double timeBetweenCues) {
		this.cues = new TapCue[cues.length];
		startTimes = new double[cues.length];
		endTimes = new double[cues.length];

		for (int i = 0; i < cues.length; i++) {
			this.cues[i] = cues[i];

			// set up times
			if (i == 0)	startTimes[i] = 0;
			else        startTimes[i] = startTimes[i - 1] + timeBetweenCues;

			endTimes[i] = startTimes[i] + duration;
		}
	}

	public int length() {
		return cues.length;
	}

	public TapCue getCue(int index) {
		return cues[index];
	}

	// Given a time, returns the index of the last cue that should have
	// been started by this time.
	public int cueToStart(double time) {
		for (int i = 0; i < startTimes.length; i++) {
			if (time < startTimes[i]) return i - 1;
		}

		return startTimes.length - 1;
	}

	// Given a time, returns the index of the last cue that should have
	// been stopped by this time.
	public int cueToStop(double time) {
		for (int i = 0; i < endTimes.length; i++) {
			if (time < endTimes[i]) return i - 1;
		}

		return endTimes.length - 1;
	}

}
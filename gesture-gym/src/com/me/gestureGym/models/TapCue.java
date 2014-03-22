package com.me.gestureGym.models;

import com.badlogic.gdx.scenes.scene2d.ui.Image;

// TODO: need an abstract class or interface to link up TapCue and the 
// unwritten SwipeCue and PinchCue

public class TapCue extends Image{
	// x position of the center of a tap
	private int xPosition;

	// y position of the center of a tap
	private int yPosition;

	// duration of the cue (may not be used from this class, but we
	// may want to just associate it with each cue)
	private double duration;

	// time between cues in the sequence this cue was in (may not be
	// used)
	private double timeBetweenCues;

	public TapCue(int xPosition, int yPosition, double duration, 
			      double timeBetweenCues) {
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.duration = duration;
		this.timeBetweenCues = timeBetweenCues;
	}

	public int getXPosition() {
		return xPosition;
	}

	public int getYPosition() {
		return yPosition;
	}

	public double getDuration() {
		return duration;
	}

	public double getTimeBetweenCues() {
		return timeBetweenCues;
	}
}
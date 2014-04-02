package com.me.gestureGym.models;

public class Zone {
	private int _zoneNumber;
	private int numCues;
	private float _x, _y;
	private float _width, _height;
	
	public Zone(int zoneNumber, float x, float y,
			float width, float height) {
		_zoneNumber = zoneNumber;
		_x = x;
		_y = y;
		_width = width;
		_height = height;
		numCues = 0;
	}
	
	public int getZoneNumber() {
		return _zoneNumber;
	}
	
	public float getX() {
		return _x;
	}
	
	public float getY() {
		return _y;
	}
	
	public float getWidth() {
		return _width;
	}
	
	public float getHeight() {
		return _height;
	}
	
	public int getNumCues() {
		return numCues;
	}
	
	public void setNumCues(int number) {
		numCues = number;
	}
}

package com.me.gestureGym.data;

public class ZoneResponseInfo {
	private int _zoneNumber;
	private float _successDuration;
	private double _hitRate;
	
	public ZoneResponseInfo(int zoneNumber, float successDuration, double hitRate) {
		_zoneNumber = zoneNumber;
		_successDuration = successDuration;
		_hitRate = hitRate;
	}
	
	public int getZoneNumber() {
		return _zoneNumber;
	}
	
	public float getSuccessDuration() {
		return _successDuration;
	}
	
	public double getHitRate() {
		return _hitRate;
	}
}

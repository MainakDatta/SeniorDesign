package com.me.gestureGym.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class SoundWrapper {
	private static Sound _backgroundMusic;
	private static Sound _hitNoise;
	
	public static void init() {
		if (_backgroundMusic == null) {
			_backgroundMusic = Gdx.audio.newSound(Gdx.files.internal("data/broken_reality.mp3"));
		}
		
		if (_hitNoise == null) {
			_hitNoise = Gdx.audio.newSound(Gdx.files.internal("data/hit.wav"));
		}
	}
	
	public static Sound getBackgroundMusic() {
		return _backgroundMusic;
	}
	
	public static Sound getHitNoise() {
		return _hitNoise;
	}
}

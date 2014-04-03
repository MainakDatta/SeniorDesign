package com.me.gestureGym.controllers;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable, AssetErrorListener {
	public static final String TAG = Assets.class.getName();
	public static final Assets instance = new Assets();
	private static AssetManager assets;

	// singleton: prevent instantiation from other classes
	private Assets() {
	}

	public static void load() {
		assets = new AssetManager();
		// set asset manager error handler
		//assets.setErrorListener(this);
		// load texture atlas
		
    	// Tell the manager to load assets for the loading screen
		assets.load("data/loading.pack", TextureAtlas.class);
        
        // Load all sound assets
		assets.load("data/audio/broken_reality.mp3", Music.class);
		assets.load("data/audio/invaders_must_die.mp3", Music.class);
		assets.load("data/audio/hit.wav", Sound.class);
        
        // Load all button assets
		assets.load("data/buttons/pause.png", Texture.class);
		assets.load("data/buttons/play.png", Texture.class);
        assets.load("data/buttons/tapCue.png", Texture.class);
        assets.load("data/buttons/tapCueBoom.png", Texture.class);
        
        
        // Wait until they are finished loading
        assets.finishLoading();

		
		//Gdx.app.debug(TAG,"# of assets loaded: " + assetManager.getAssetNames().size);
		for (String a : assets.getAssetNames())
			System.out.println("asset: " + a);
	}

	public static AssetManager getManager(){
		return assets;
	}
	
	@Override
	public void dispose() {
		assets.dispose();
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) {
		// TODO Auto-generated method stub
		
	}

}

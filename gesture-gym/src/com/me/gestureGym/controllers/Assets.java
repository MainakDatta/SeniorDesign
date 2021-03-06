package com.me.gestureGym.controllers;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
		Texture.setEnforcePotImages(false);
		// set asset manager error handler
		//assets.setErrorListener(this);
		// load texture atlas
		
    	// Tell the manager to load assets for the loading screen
		assets.load("data/loading.pack", TextureAtlas.class);
		
		assets.load("data/background.png", Texture.class);
		
        // Load all sound assets
		assets.load("data/audio/broken_reality.mp3", Music.class);
		assets.load("data/audio/invaders_must_die.mp3", Music.class);
		assets.load("data/audio/hit.wav", Sound.class);
        // Load all button assets (THESE SHOULD BE PACKED)
		assets.load("data/ui_elements/back.png", Texture.class);
		assets.load("data/ui_elements/SINGLE_TOUCH.png", Texture.class);
		assets.load("data/ui_elements/MULTI_TOUCH.png", Texture.class);
		assets.load("data/ui_elements/PATIENT_VIEW.png", Texture.class);
		assets.load("data/ui_elements/DOCTOR_VIEW.png", Texture.class);		
		assets.load("data/ui_elements/GIVE_TABLET.png", Texture.class);
		assets.load("data/ui_elements/CHECK_PATIENTS.png", Texture.class);		
		assets.load("data/ui_elements/SUBMIT.png", Texture.class);
		assets.load("data/ui_elements/PLAY_AGAIN.png", Texture.class);
		assets.load("data/ui_elements/GO_HOME.png", Texture.class);
		assets.load("data/ui_elements/pause.png", Texture.class);
		assets.load("data/ui_elements/play.png", Texture.class);
		//Other assets
		assets.load("data/ui_elements/MTTapCue.png", Texture.class);
        assets.load("data/ui_elements/tapCue.png", Texture.class);
        assets.load("data/ui_elements/tapCueBoom.png", Texture.class);
        assets.load("data/ui_elements/ui_score.png", Texture.class);
        assets.load("data/ui_elements/ui_time.png", Texture.class);

        assets.load("data/uiskin.json", Skin.class);
        assets.load("data/gabriola.fnt", BitmapFont.class);
        
        // Wait until they are finished loading
        assets.finishLoading();

	}

	public static AssetManager getManager(){
		return assets;
	}
	
	@Override
	public void dispose() {
		assets.dispose();
	}

	@Override
	public void error(@SuppressWarnings("rawtypes") AssetDescriptor asset, Throwable throwable) {
		// TODO Auto-generated method stub
		
	}

}

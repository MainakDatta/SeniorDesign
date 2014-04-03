package com.me.gestureGym;

import com.me.gestureGym.screens.GameStartScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GestureGym extends Game {
	
    public SpriteBatch batch;
    public BitmapFont font;
	public AssetManager assets;
    
	@Override
	public void create() {	
		batch = new SpriteBatch();
		assets = new AssetManager();
        //Use LibGDX's default Arial font.
        font = new BitmapFont();
        
		this.setScreen(new GameStartScreen(this));
	}
	
	public void render(){
		super.render();
	}
	
	public void dispose(){
		batch.dispose();
		font.dispose();
		assets.dispose();
	}

}

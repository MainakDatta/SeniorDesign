package com.me.gestureGym;

import almonds.Parse;

import com.me.gestureGym.screens.SplashScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GestureGym extends Game {
	
    public SpriteBatch batch;
    public BitmapFont font;
	
	@Override
	public void create() {		
		Parse.initialize("a9fgXH8y5WZxzucfA8ZrPOdQ6dEEsSLHfhykvyzY", "fvjFSvTnypy9zgLEiXbv3kgC3YhlY2zt4MvssEP3");
		
		batch = new SpriteBatch();
        //Use LibGDX's default Arial font.
        font = new BitmapFont();
        
		this.setScreen(new SplashScreen(this));
	}
	
	public void render(){
		super.render();
	}
	
	public void dispose(){
		batch.dispose();
		font.dispose();
		
	}

}

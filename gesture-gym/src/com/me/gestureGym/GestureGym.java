package com.me.gestureGym;

import almonds.Parse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.me.gestureGym.screens.GameBoard;
import com.me.gestureGym.screens.Splash;

import com.badlogic.gdx.Game;

public class GestureGym extends Game {
	
	@Override
	public void create() {		
		Parse.initialize("a9fgXH8y5WZxzucfA8ZrPOdQ6dEEsSLHfhykvyzY", "fvjFSvTnypy9zgLEiXbv3kgC3YhlY2zt4MvssEP3");
		this.setScreen(new Splash(this));
	}

}

package com.me.gestureGym;

import java.io.IOException;

import com.me.gestureGym.data.LocalStorageDoesNotExistException;
import com.me.gestureGym.data.NoSuchDoctorException;
import com.me.gestureGym.screens.LoadingScreen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GestureGym extends Game {
	
    public SpriteBatch batch;
    public BitmapFont font;
    
	@Override
	public void create() {	
		batch = new SpriteBatch();
        font = new BitmapFont();
        
		try {
			this.setScreen(new LoadingScreen(this));
		} catch (LocalStorageDoesNotExistException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchDoctorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void render(){
		super.render();
	}
	
	public void dispose(){
		batch.dispose();
		font.dispose();
	}

}

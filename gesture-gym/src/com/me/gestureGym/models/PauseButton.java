package com.me.gestureGym.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class PauseButton extends Actor {
	private Texture _texture = new Texture(Gdx.files.internal("data/pause.png"));
	private float _x, _y;
	
	public PauseButton(float x, float y) {
		_x = x;
		_y = y;
		
		System.out.println("created pause button at (" + x + ", " + y + ")");
		
		setBounds(_x, _y, _texture.getWidth(),_texture.getHeight());
        addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        
        setTouchable(Touchable.enabled);
        setVisible(true);
	}
	
	@Override	
    public void draw(SpriteBatch batch, float alpha){
        batch.draw(_texture, _x, _y);
    }
	
	public float getX() {
		return _x;
	}
	
	public float getY() {
		return _y;
	}
}
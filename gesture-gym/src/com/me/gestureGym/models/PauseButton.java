package com.me.gestureGym.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class PauseButton extends Actor {
	private Texture _unpaused = new Texture(Gdx.files.internal("data/pause.png"));
	private Texture _paused   = new Texture(Gdx.files.internal("data/play.png"));
	private boolean _isPaused;
	private float _x, _y;
	
	public PauseButton(float x, float y) {
		_x = x;
		_y = y;
		
		System.out.println("created pause button at (" + x + ", " + y + ")");
		
		setBounds(_x, _y, _unpaused.getWidth(), _unpaused.getHeight());
        addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
            	((PauseButton)event.getTarget()).flipPause();
                return true;
            }
        });
        
        setTouchable(Touchable.enabled);
        setVisible(true);
	}
	
	@Override	
    public void draw(SpriteBatch batch, float alpha){
		if (_isPaused) {
			batch.draw(_paused, _x, _y);
		} else {
			batch.draw(_unpaused, _x, _y);
		}
    }
	
	public float getX() {
		return _x;
	}
	
	public float getY() {
		return _y;
	}
	
	public void flipPause() {
		_isPaused = !_isPaused;
	}
}

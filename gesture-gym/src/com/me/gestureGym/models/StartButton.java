package com.me.gestureGym.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class StartButton extends Actor {
	private Texture _texture = new Texture(Gdx.files.internal("data/start_button.jpg"));
	private float _x, _y;
	
	public StartButton(float x, float y) {
		_x = x;
		_y = y;
		
		setBounds(_x, _y, _texture.getWidth(), _texture.getHeight());
        addListener(new InputListener(){
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });
        
        setTouchable(Touchable.enabled);
        setVisible(true);
	}
	
	@Override	
    public void draw(SpriteBatch batch, float alpha) {
		if (this.isVisible()) batch.draw(_texture, _x, _y);
    }
	
	public float getX() {
		return _x;
	}
	
	public float getY() {
		return _y;
	}
}
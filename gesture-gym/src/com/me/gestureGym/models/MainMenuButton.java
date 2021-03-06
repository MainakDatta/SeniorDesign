package com.me.gestureGym.models;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.me.gestureGym.controllers.Assets;

public class MainMenuButton extends Actor {
	
	private Texture _texture;
	private float _x, _y;
	private boolean _buttonType;
	private Sound _hitSound;
	
	//Takes in a boolean to determine which text to show on button
	public MainMenuButton(float x, float y, boolean isPatient) {
		_hitSound = Assets.getManager().get("data/audio/hit.wav", Sound.class);
		_x = x;
		_y = y;
        //Pick image based on boolean param
		if(isPatient)
        	_texture = Assets.getManager().get("data/ui_elements/PATIENT_VIEW.png", Texture.class);
        else
        	_texture = Assets.getManager().get("data/ui_elements/DOCTOR_VIEW.png", Texture.class);
        
		_buttonType = isPatient;
        
        
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
	
	public boolean getType() {
		return _buttonType;
	}
	
	public Sound getSound() {
		return _hitSound;
	}
	
	public void hit(){
		_hitSound.play(1.0f);
	}
}

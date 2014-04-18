package com.me.gestureGym.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class DocOptionsButton extends Actor {
	
	private Texture _texture;
	private float _x, _y;
	private boolean _buttonType;
	
	//Takes in a boolean to determine which text to show on button
	public DocOptionsButton(float x, float y, boolean isPatient) {
		_x = x;
		_y = y;
        //Pick image based on boolean param
		if(isPatient)
        	_texture = new Texture(Gdx.files.internal("data/ui_elements/PATIENT_VIEW.png"));        
        else
        	_texture = new Texture(Gdx.files.internal("data/ui_elements/DOCTOR_VIEW.png"));
        
		_buttonType = isPatient;
        
        
		setBounds(_x, _y, _texture.getWidth(), _texture.getHeight());
        addListener(new InputListener(){
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
        });

        setTouchable(Touchable.disabled);
        setVisible(false);
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
}

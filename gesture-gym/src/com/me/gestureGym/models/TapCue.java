package com.me.gestureGym.models;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.me.gestureGym.controllers.Assets;

// TODO: need an abstract class or interface to link up TapCue and the 
// unwritten SwipeCue and PinchCue

public class TapCue extends Actor{
	
	private Sound _hitSound;
	private Texture _texture;
  
    //
    
    float _x;
    float _y;
    private int _zone;
    private boolean _started = false;

    private float _startTime;
    private float _endTime;
    
    private boolean hit;
    private float _hitTime;

	// duration of the cue (may not be used from this class, but we
	// may want to just associate it with each cue)
	// private double duration;

	// time between cues in the sequence this cue was in (may not be
	// used)
	//private double timeBetweenCues;

    
	public TapCue(float x, float y, int zone, float start, float end, boolean isMT) {
		_hitSound = Assets.getManager().get("data/audio/hit.wav", Sound.class);
		
		_x = x;
		_y = y;
		_startTime = start;
		_endTime = end;
		_zone = zone;
		
		if(isMT){
			_texture = Assets.getManager().get("data/ui_elements/MTTapCue.png", Texture.class);
		}
		else{
			_texture = Assets.getManager().get("data/ui_elements/tapCue.png", Texture.class);
		}
		
        setBounds(x, y, _texture.getWidth(),_texture.getHeight());
        addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                ((TapCue)event.getTarget())._started = true;
                return true;
            }
        });
        
        setTouchable(Touchable.disabled);
        setVisible(false);
	}

    @Override	
    public void draw(SpriteBatch batch, float alpha){
        batch.draw(_texture, _x, _y);
    }
    
    @Override
    public void act(float delta){
    	if(hit){
    		if(_hitTime >= 5.0f){
	    		setVisible(false);
	    		hit = true;
	    	
    		}
    		_hitTime += delta;
    	}
    }
	
	
	public float getX() {
		return _x;
	}

	public float getY() {
		return _y;
	}

	public float getStartTime() {
		return _startTime;
	}

	public float getEndTime() {
		return _endTime;
	}
	
	public int getZone() {
		return _zone;
	}
	
	public void start() {
		_started = true;
	}
	
	void alterStartTime(float delta) {
		_startTime += delta;
	}
	
	void alterEndTime(float delta) {
		_endTime += delta;
	}
	
	public Sound getSound() {
		return _hitSound;
	}
	
	public void hit(){
		_hitSound.play(1.0f);
		setTouchable(Touchable.disabled);
		hit = true;
		_texture = Assets.getManager().get("data/ui_elements/tapCueBoom.png", Texture.class);
	}
}
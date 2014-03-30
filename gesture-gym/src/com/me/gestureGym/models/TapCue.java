package com.me.gestureGym.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;

// TODO: need an abstract class or interface to link up TapCue and the 
// unwritten SwipeCue and PinchCue

public class TapCue extends Actor{
	
    Texture _texture = new Texture(Gdx.files.internal("data/droplet.png"));
  
    //Texture hit_texture = new Texture(Gdx.files.internal("data/explosion.png"));
    
    float _x;
    float _y;
    private int _zone;
    private boolean _started = false;

    private float _startTime;
    private float _endTime;

	// duration of the cue (may not be used from this class, but we
	// may want to just associate it with each cue)
	// private double duration;

	// time between cues in the sequence this cue was in (may not be
	// used)
	//private double timeBetweenCues;

    
	public TapCue(float x, float y, int zone, float start, float end) {
		_x = x;
		_y = y;
		_startTime = start;
		_endTime = end;
		_zone = zone;
		
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
    
    public void end(SpriteBatch batch, float alpha){
        //batch.draw(hit_texture,cueX,cueY);
    }
    
    
    @Override
    public void act(float delta){
    	    	
    }
	
	
	public float getX() {
		return _x;
	}

	public float getYPosition() {
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
}
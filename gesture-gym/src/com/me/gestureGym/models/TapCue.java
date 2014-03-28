package com.me.gestureGym.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

// TODO: need an abstract class or interface to link up TapCue and the 
// unwritten SwipeCue and PinchCue

public class TapCue extends Actor{
	
    Texture texture = new Texture(Gdx.files.internal("data/droplet.png"));
	
    float cueX;
    float cueY;
    public boolean started = false;

    private float startT;
    private float endT;
	// duration of the cue (may not be used from this class, but we
	// may want to just associate it with each cue)
	// private double duration;

	// time between cues in the sequence this cue was in (may not be
	// used)
	//private double timeBetweenCues;

    
	public TapCue(float x, float y, float start, float end) {

		cueX = x;
		cueY = y;
		startT = start;
		endT = end;
		
        setBounds(x, y,texture.getWidth(),texture.getHeight());
        addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                ((TapCue)event.getTarget()).started = true;
                return true;
            }
        });
        
        
	}

    @Override	
    public void draw(SpriteBatch batch, float alpha){
        batch.draw(texture,cueX,cueY);
    }
    
    @Override
    public void act(float delta){
    	    	
    }
	
	
	public float getX() {
		return cueX;
	}

	public float getYPosition() {
		return cueY;
	}

	public float getStartTime() {
		return startT;
	}

	public float getEndTime() {
		return endT;
	}
}
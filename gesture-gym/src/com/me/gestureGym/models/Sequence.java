package com.me.gestureGym.models;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;

public class Sequence extends Group{
	// TODO: won't be only taps
	private Array<TapCue> _cues;
	private float _duration;

	// TODO: add more parameters
	public Sequence(Array<TapCue> cues, float duration) {
		_cues = cues;
        addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                ((TapCue)event.getTarget()).started = true;
                return true;
            }
        });
        
        for(TapCue tc: cues){
        	addActor(tc);
        }
	}

	public int length() {
		return _cues.size;
	}

	public TapCue getCue(int index) {
		return _cues.get(index);
	}
	
	public float getDuration() {
		return _duration;
	}

}
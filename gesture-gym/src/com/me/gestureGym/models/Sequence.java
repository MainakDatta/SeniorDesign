package com.me.gestureGym.models;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;

public class Sequence extends Group{
	// TODO: won't be only taps
	private Array<TapCue> _cues;
	private float _duration;
	private float _deltaDuration;

	public Sequence(Array<TapCue> cues, float duration, float deltaDuration) {
		_cues = cues;
		_duration = duration;
		_deltaDuration = deltaDuration;
		
        addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                ((TapCue)event.getTarget()).start();
                return true;
            }
        });
        
        for (TapCue tc: cues) {
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
	
	public float getDeltaDuration() {
		return _deltaDuration;
	}
	
	public void offsetTimestamps(float delta) {
		for (TapCue cue : _cues) {
			cue.alterStartTime(delta);
			cue.alterEndTime(delta);
		}
	}

}
package com.me.gestureGym.models;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Array;

public class Sequence extends Group{
	// TODO: won't be only taps
	private Array<TapCue> cues;

	// TODO: add more parameters
	public Sequence(Array<TapCue> cues) {
		this.cues = cues;
        addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                ((TapCue)event.getTarget()).started = true;
                return true;
            }
        });
	}

	public int length() {
		return cues.size;
	}

	public TapCue getCue(int index) {
		return cues.get(index);
	}

}
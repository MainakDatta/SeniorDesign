/*
1. Gets sequence S
2. Calls BoardRenderer(S)
3. Calls appropriate BoardRenderer method if detects input.
*/
package com.me.gestureGym.screens;

import java.util.HashMap;

import almonds.Parse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.me.gestureGym.GestureGym;
import com.me.gestureGym.controllers.SequenceGenerator;
import com.me.gestureGym.controllers.ZoneInfoWrapper;
import com.me.gestureGym.data.ZoneResponseInfo;
import com.me.gestureGym.models.Sequence;
import com.me.gestureGym.models.Zone;
import com.me.gestureGym.models.TapCue;

public class GameScreen implements Screen {
	
	final GestureGym _game;
	
	// TODO: Decide on final value for this
	private static final double SUCCESS = 0.8;
	private static final int N_ZONES = 16;
	
	private Stage _stage;
	private Sequence _currentSequence;
	private OrthographicCamera _camera;
	private final Vector2 _stageCoords = new Vector2();
	
	private Zone[] _zones;
	private ZoneResponseInfo[] _zoneInfos;
	
	//Zone Hits map will help us calculate the hit rate
	private HashMap<Zone, Integer> _zoneHits;
	
	private float _time; 
	private int _sequenceIndex = 0;
	private boolean _first = true;
	
    public GameScreen(GestureGym g){
    	Parse.initialize("a9fgXH8y5WZxzucfA8ZrPOdQ6dEEsSLHfhykvyzY",
				"et6FgY6BlRf7zbaarHBBY18g7v233x8V2HXty7DP");
    	 	
        _game = g;
    	
    	// set up information about zones
    	setupZones();
    	_zoneInfos = ZoneInfoWrapper.getZoneInfo();
    	
        // create the camera and the SpriteBatch
		_camera = new OrthographicCamera(800, 480);
		_camera.position.set(800 / 2, 480 / 2, 0f); 
		
        _stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        
        // Essentially the Controller/BoardRenderer thing that you mentioned
        Gdx.input.setInputProcessor(_stage);
        
        // Sequence s is a Group of TapCue Actors
        _currentSequence = getSequence();
        
        _stage.addActor(_currentSequence);
    }
    
    // create zones, zone hits hashmap
    private void setupZones() {
    	_zones = new Zone[N_ZONES];
    	_zoneHits = new HashMap<Zone, Integer>();
    	
    	float width = Gdx.graphics.getWidth();
    	float height = Gdx.graphics.getHeight();
    	
    	System.out.println("Screen width is " + width + ", screen height is " + height);
    	
    	int rowSize = (int) Math.sqrt(N_ZONES);
    	
    	for (int i = 0; i < N_ZONES; i++){
    		float zWidth = (float) width / rowSize;
    		float zHeight = (float) height / rowSize;
    		float zX = (float) (i % 4) * zWidth;
    		float zY = (float) (i / 4) * zHeight;
    		Zone zone = new Zone(i, zX, zY, zWidth, zHeight);
    		
//    		System.out.println("Zone " + i + ":");
//    		System.out.println("Zone width is " + zWidth + ", zone height is " + zHeight);
//    		System.out.println("Zone upper left is (" + zX + ", " + zY + ")");
    		
    		_zones[i] = zone;
    		
    		//All zones initialized to scores of 0
    		_zoneHits.put(zone, 0);
    	}
    }
	
    private Sequence getSequence() {
    	Sequence generated = SequenceGenerator.generateSequence(_zones, _zoneInfos, false);
    	return generated;
    }
    
    private boolean sequenceOver() {
    	return _sequenceIndex == _currentSequence.length();
    }
    
    private void endAndSwitchScreens() {
    	System.out.println("no cues left");
    	updateStats();
		_game.setScreen(new GameEndScreen(_game));
		dispose();
    }
    
    // un-display all cues whose end times are before the current time
    private void unshowEndedCues() {
    	int currIndex;
    	TapCue cue = null;
		boolean shouldRemove = (currIndex = _sequenceIndex - 1) >= 0 && 
				               (cue = _currentSequence.getCue(currIndex)).getEndTime() <= _time;
		
		while (shouldRemove) {
			cue.setTouchable(Touchable.disabled);
			cue.setVisible(false);
			if (!_currentSequence.removeActor(cue)) break;
			System.out.println("removed actor at (" + cue.getX() + ", " + cue.getY() + ")");
			shouldRemove = (currIndex = currIndex - 1) >= 0 && 
		                   (cue = _currentSequence.getCue(currIndex)).getEndTime() <= _time;
		}
    }
    
    // display all cues whose start times are after the current time
    private void showStartedCues() {
    	TapCue cue = null;
    	boolean shouldShow = _sequenceIndex < _currentSequence.length() &&
    			             (cue = _currentSequence.getCue(_sequenceIndex)).getStartTime() <= _time &&
    			             cue.getEndTime() > _time;
    	while (shouldShow) {
    		System.out.println("showing actor at (" + cue.getX() + ", " + cue.getY() + ")");
    		cue.setTouchable(Touchable.enabled);
    		cue.setVisible(true);
    		
    		_sequenceIndex++;
    		shouldShow = _sequenceIndex < _currentSequence.length() &&
		                  (cue = _currentSequence.getCue(_sequenceIndex)).getStartTime() <= _time &&
		                  cue.getEndTime() > _time;
    	}
    }
    
    private void handleTouch() {
    	// store input coordinates in stageCoords vector
		_stage.screenToStageCoordinates(_stageCoords.set(Gdx.input.getX(), Gdx.input.getY()));    		
		// pass coordinates to Sequence object (which is a Group of TapCue Actors)
		Actor actor = _currentSequence.hit(_stageCoords.x, _stageCoords.y, true);
		
		// checks if the tapped location is at a TapCue Actor in the Sequence Group
		if (actor != null && actor instanceof TapCue){
			TapCue tc = (TapCue) actor;
			//TODO: Display animation
			System.out.println("BOOM");
			//Add to hit total for this zone
			int zoneNum = tc.getZone();
			Zone hit = _zones[zoneNum];
			_zoneHits.put(hit, _zoneHits.get(hit) + 1);
			
			tc.setTouchable(Touchable.disabled);
			tc.setVisible(false);
			_currentSequence.removeActor(tc);
		}
    }
    
	public void render(float delta) {
		_time += delta;
		
		if (_first) {
			_first = false;
			_currentSequence.offsetTimestamps(delta);
		}
		
    	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    	
    	if (sequenceOver()) {
    		endAndSwitchScreens();
    	}
    	
    	unshowEndedCues();
    	showStartedCues();
    	
    	if (Gdx.input.isTouched()) {    		
    		handleTouch();
		}

        _stage.act(delta);
        _stage.draw();
	}

	//Creates ZoneResponseInfo jawns
	private void updateStats() {		
		//Only update if they pass threshold?
		for (Zone z: _zoneHits.keySet()){
			if (z.getNumCues() == 0) continue;
			//Total number of hits
			int totalHits = _zoneHits.get(z);
			double hitRate = totalHits / z.getNumCues();
			int zoneNum = z.getZoneNumber();
			//ONLY UPDATE IF IT BEAT OUR SUCESS THRESHOLD and is less than old duration
			if(hitRate > SUCCESS && zoneNum < _zoneInfos[zoneNum].getSuccessDuration()){
				ZoneResponseInfo zInfo = new ZoneResponseInfo(zoneNum, _currentSequence.getDuration(), hitRate);
				ZoneInfoWrapper.updateZone(zInfo);
			}	
		}				
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
				
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		_stage.dispose();
	}
}

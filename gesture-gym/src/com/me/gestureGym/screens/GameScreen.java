/*
1. Gets sequence S
2. Calls BoardRenderer(S)
3. Calls appropriate BoardRenderer method if detects input.
*/
package com.me.gestureGym.screens;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import almonds.Parse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.me.gestureGym.GestureGym;
import com.me.gestureGym.controllers.SequenceGenerator;
import com.me.gestureGym.controllers.SoundWrapper;
import com.me.gestureGym.controllers.ZoneInfoWrapper;
import com.me.gestureGym.data.ZoneResponseInfo;
import com.me.gestureGym.models.PauseButton;
import com.me.gestureGym.models.PlayButton;
import com.me.gestureGym.models.Sequence;
import com.me.gestureGym.models.TapCue;
import com.me.gestureGym.models.Zone;

public class GameScreen implements Screen {
	final GestureGym _game;
	
	// TODO: Decide on final value for this
	private static final double SUCCESS = 0.6;
	private static final int N_ZONES = 16;
	private static final int PAUSE_BUTTON_SIZE = 128;
	
	private static final int GAME_RUNNING = 0;
	private static final int GAME_PAUSED = 1;
	
	private int _gameStatus = 0;
	
	private Stage _stage;
	private Sequence _currentSequence;
	private PauseButton _pauseButton;
	private PlayButton _playButton;
	private OrthographicCamera _camera;
	private final Vector2 _stageCoords = new Vector2();
	
	private Zone[] _zones;
	private ZoneResponseInfo[] _zoneInfos;
	
	//Zone Hits map will help us calculate the hit rate
	private HashMap<Zone, Integer> _zoneHits;
	
	private float _timeLeft = 180;
	private float _time; 
	private int _sequenceIndex = 0;
	private boolean _first = true;
	
	private Sound _backgroundMusic;
	private long _backgroundMusicId;
	
	private AssetManager manager = new AssetManager();
	
    public GameScreen(GestureGym g){
    	Parse.initialize("a9fgXH8y5WZxzucfA8ZrPOdQ6dEEsSLHfhykvyzY",
				"et6FgY6BlRf7zbaarHBBY18g7v233x8V2HXty7DP");
    	 	
        _game = g;
       
        _backgroundMusic = SoundWrapper.getBackgroundMusic();
        _backgroundMusicId = _backgroundMusic.play(1.0f);
        _backgroundMusic.setLooping(_backgroundMusicId, true);
    	
    	// set up information about zones
    	setupZones();
    	_zoneInfos = ZoneInfoWrapper.getZoneInfo();
    	
        // create the camera and the SpriteBatch
		_camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		_camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); 
		
        _stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        
        // Essentially the Controller/BoardRenderer thing that you mentioned
        Gdx.input.setInputProcessor(_stage);
        
        // Sequence s is a Group of TapCue Actors
        _currentSequence = getSequence();
        
        _stage.addActor(_currentSequence);
        
        _pauseButton = new PauseButton(Gdx.graphics.getWidth() - PAUSE_BUTTON_SIZE, 0);
        _stage.addActor(_pauseButton);
        
        _playButton = new PlayButton(Gdx.graphics.getWidth() - PAUSE_BUTTON_SIZE, 0);
        _stage.addActor(_playButton);
    }
    
    // create zones, zone hits hashmap
    private void setupZones() {
    	_zones = new Zone[N_ZONES];
    	_zoneHits = new HashMap<Zone, Integer>();
    	
    	float width = Gdx.graphics.getWidth();
    	float height = Gdx.graphics.getHeight();
    	
    	System.out.println("Screen width is " + width + ", screen height is " + height);
    	
    	float cueAreaWidth = width - 256;
    	float cueAreaHeight = height - 256;

    	int rowSize = (int) Math.sqrt(N_ZONES);
    	

    	for (int i = 0; i < N_ZONES; i++){
    		float zWidth = (float) cueAreaWidth / rowSize;
    		float zHeight = (float) cueAreaHeight / rowSize;
    		float zX = (float) (i % 4) * zWidth;
    		float zY = (float) (i / 4) * zHeight;
    		Zone zone = new Zone(i, zX, zY, zWidth, zHeight);
    		
    		System.out.println("Zone " + i + ":");
    		System.out.println("Zone width is " + zWidth + ", zone height is " + zHeight);
    		System.out.println("Zone lower left is (" + zX + ", " + zY + ")");
    		_zones[i] = zone;
    		//All zones initialized to scores of 0
    		_zoneHits.put(zone, 0);
    	}

    }
	
    private Sequence getSequence() {
    	boolean far = Math.random() < 0.5;
    	Sequence generated = SequenceGenerator.generateSequence(_zones, _zoneInfos, far);
    	return generated;
    }
    
    private boolean sequenceOver() {
    	return _sequenceIndex == _currentSequence.length() &&
    		   !_currentSequence.getCue(_sequenceIndex - 1).isVisible();
    }
    
    private void getNewSequence() {
    	updateStats();
    	_currentSequence = getSequence();
    	_currentSequence.offsetTimestamps(_time);
    	_sequenceIndex = 0;
    	_stage.addActor(_currentSequence);
    }
    
    private void endAndSwitchScreens() {
    	_backgroundMusic.stop(_backgroundMusicId);
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
			//cue.setVisible(false);
			if (!_currentSequence.removeActor(cue)) break;
			//System.out.println("removed actor at (" + cue.getX() + ", " + cue.getY() + ")");
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
    		//System.out.println("showing actor at (" + cue.getX() + ", " + cue.getY() + ")");
    		cue.setTouchable(Touchable.enabled);
    		cue.setVisible(true);
    		
    		_sequenceIndex++;
    		shouldShow = _sequenceIndex < _currentSequence.length() &&
		                  (cue = _currentSequence.getCue(_sequenceIndex)).getStartTime() <= _time &&
		                  cue.getEndTime() > _time;
    	}
    }
    
    private void setCuesUntouchable() {
    	for (int i = 0; i < _currentSequence.length(); i++) {
    		_currentSequence.getCue(i).setTouchable(Touchable.disabled);
    	}
    }
    
    private void pauseGame() {
    	setCuesUntouchable();
		_gameStatus = GAME_PAUSED;
		_pauseButton.setTouchable(Touchable.disabled);
		_pauseButton.setVisible(false);
		_playButton.setTouchable(Touchable.enabled);
		_playButton.setVisible(true);
    }
    
    private void unpauseGame() {
    	showStartedCues();
		_gameStatus = GAME_RUNNING;
		_playButton.setTouchable(Touchable.disabled);
		_playButton.setVisible(false);
		_pauseButton.setTouchable(Touchable.enabled);
		_pauseButton.setVisible(true);
    }
    
    private void handleTouch() {
    	// store input coordinates in stageCoords vector
		_stage.screenToStageCoordinates(_stageCoords.set(Gdx.input.getX(), Gdx.input.getY()));    		
		// pass coordinates to Sequence object (which is a Group of TapCue Actors)
		Actor actor = _stage.hit(_stageCoords.x, _stageCoords.y, true);
		
		if (actor != null && actor instanceof PauseButton) {
			pauseGame();
		}
		
		else if (actor != null && actor instanceof PlayButton) {
			unpauseGame();
		}
		
		// checks if the tapped location is at a TapCue Actor in the Sequence Group
		else if (actor != null && actor instanceof TapCue){
			//System.out.println("TAPPED");
			TapCue tc = (TapCue) actor;
			//TODO: Display animation
//			System.out.println("BOOM");
			//Add to hit total for this zone
			int zoneNum = tc.getZone();
//			System.out.println("Hit in zone " + zoneNum + "!");
			Zone hit = _zones[zoneNum];
			_zoneHits.put(hit, _zoneHits.get(hit) + 1);
//			System.out.println("Zone: " + zoneNum + " " + _zoneHits.get(hit) + " hits!");
			
			// when hit, it plays the sound and changes image
			tc.hit();
			//_currentSequence.removeActor(tc);
		}
    }
    
	public void render(float delta) {		
		if (_gameStatus == GAME_RUNNING) {
						
			_time += delta;
			
			if (_first) {
				_backgroundMusic = Gdx.audio.newSound(Gdx.files.internal("data/invaders_must_die.mp3"));
		        _backgroundMusicId = _backgroundMusic.play(1.0f);
		        _backgroundMusic.setLooping(_backgroundMusicId, true);
				_first = false;
				_currentSequence.offsetTimestamps(delta);
			} else {
				_timeLeft -= delta;
			}
			
			if (_timeLeft <= 0) {
				endAndSwitchScreens();
			}
			
	    	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	    	
	    	//Debugging cue nums
//	    	_game.batch.begin();
//	    	for (int i = 0; i < N_ZONES; i++){	    		
//	    		Zone z  = _zones[i];
//	    		//String text = "Zone " + z.getZoneNumber();
//	    		//System.out.println(text);
//	    		BitmapFont font = new BitmapFont();
//	    		font.setColor(0.0f, 0.0f, 1.0f, 1.0f); // tint font blue	    		
//	    		///font.draw(_game.batch, text, z.getX(), z.getY());
//
//	    	}
	    	//_game.batch.end();

	    	if (sequenceOver()) {
	    		getNewSequence();
	    	}
	    	
	    	unshowEndedCues();
	    	showStartedCues();
		}
		
		if (Gdx.input.justTouched()) {    		
    		handleTouch();
		}
		
		_stage.act(delta);
        _stage.draw();
	}

	//Creates ZoneResponseInfo jawns
	private void updateStats() {
		HashSet<Zone> zonesHit = new HashSet<Zone>();
		int numerator = 0;
		int denominator = 0;
		for (Zone z : _zoneHits.keySet()) {
			if (z.getNumCues() == 0) continue;
			numerator += _zoneHits.get(z);
			denominator += z.getNumCues();
			zonesHit.add(z);
		}
		
		for (Zone z: zonesHit) {
			int zoneNum = z.getZoneNumber();
			int totalHits = _zoneHits.get(z);
			double hitRate = (double) totalHits / z.getNumCues();
			System.out.println("Hit rate at zone " + zoneNum + " : " + hitRate);			
			//ONLY UPDATE IF IT BEAT OUR SUCESS THRESHOLD and is less than old duration
			if(hitRate > SUCCESS && _currentSequence.getDuration() < _zoneInfos[zoneNum].getSuccessDuration()){
				ZoneResponseInfo zInfo = new ZoneResponseInfo(zoneNum, _currentSequence.getDuration(), hitRate);
				System.out.println("Updating zone " + zoneNum + " to duration " + _currentSequence.getDuration());
				ZoneInfoWrapper.updateZone(zInfo); 
			} else {
				System.out.println("Keeping zone " + zoneNum + " at duration " + _zoneInfos[zoneNum].getSuccessDuration());
			}
		}
		
		if (((double) numerator / denominator) > 0.95 && _currentSequence.getDuration() > 1.0) {
			HashSet<Zone> adjacents = getAdjacentZones(zonesHit);
			float newDuration = (float) (_currentSequence.getDuration() - .25 * _currentSequence.getDeltaDuration());
			for (Zone z : adjacents) {
				int zoneNum = z.getZoneNumber();
				if (newDuration < _zoneInfos[zoneNum].getSuccessDuration()) {
					ZoneResponseInfo zInfo = new ZoneResponseInfo(zoneNum, newDuration, 0.95);
					System.out.println("Updating zone " + zoneNum + " to duration " + newDuration + 
							" because they did well in an adjacent zone.");
					ZoneInfoWrapper.updateZone(zInfo);
				}
			}
		}
	}
	
	private static HashSet<Integer> getAdjacentZones(int zone) {
		int rowSize = (int) Math.sqrt(16);
		
		boolean inTopRow = zone / rowSize == 0;
		boolean inBottomRow = zone / rowSize == rowSize - 1;
		boolean inLeftColumn = zone % rowSize == 0;
		boolean inRightColumn = zone % rowSize == rowSize - 1;
		
		HashSet<Integer> opts = new HashSet<Integer>();
		if (!inTopRow)      opts.add(zone - rowSize);
		if (!inLeftColumn)  opts.add(zone - 1);
		if (!inRightColumn) opts.add(zone + 1);
		if (!inBottomRow)   opts.add(zone + rowSize);
		
		return opts;
	}
	
	private HashSet<Zone> getAdjacentZones(HashSet<Zone> zones) {
		HashSet<Zone> out = new HashSet<Zone>();
		for (Zone z : zones) {
			for (int i : getAdjacentZones(z.getZoneNumber())) {
				Zone toAdd = _zones[i];
				if (!zones.contains(toAdd)) {
					out.add(toAdd);
				}
			}
		}
		
		return out;
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
		pauseGame();
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

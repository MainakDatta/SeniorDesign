/*
1. Gets sequence S
2. Calls BoardRenderer(S)
3. Calls appropriate BoardRenderer method if detects input.
*/
package com.me.gestureGym.screens;

import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.me.gestureGym.GestureGym;
import com.me.gestureGym.controllers.Assets;
import com.me.gestureGym.controllers.MTSequenceGenerator;
import com.me.gestureGym.controllers.SequenceGenerator;
import com.me.gestureGym.controllers.ZoneInfoWrapper;
import com.me.gestureGym.data.ZoneResponseInfo;
import com.me.gestureGym.models.PauseButton;
import com.me.gestureGym.models.PlayButton;
import com.me.gestureGym.models.Sequence;
import com.me.gestureGym.models.TapCue;
import com.me.gestureGym.models.Zone;

public class GameScreen implements Screen {
	final GestureGym _game;
	private boolean _isMultiTouchGame;
	
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
	private final Vector2 _stageCoords2 = new Vector2();
	
	private Zone[] _zones;
	private ZoneResponseInfo[] _zoneInfos;
	
	//Zone Hits map will help us calculate the hit rate
	private HashMap<Zone, Integer> _zoneHits;
	
	private float _timeLeft = 180;
	private float _time; 
	private int _sequenceIndex = 0;
	private boolean _first = true;
	
	private Music _backgroundMusic;
	
	private Table score_display;
	private TextField points;
	private int score_points;
	
	private Table time_display;
	private TextField seconds;
	private float time_seconds;
	
    public GameScreen(GestureGym g, boolean isMultiTouchGame){

    	_game = g;
        _isMultiTouchGame = isMultiTouchGame;
        
        // get loaded audio file
        if(!_isMultiTouchGame)
        	_backgroundMusic = Assets.getManager().get("data/audio/broken_reality.mp3", Music.class);
        else 
        	_backgroundMusic = Assets.getManager().get("data/audio/invaders_must_die.mp3", Music.class);
        
        _backgroundMusic.play();
        _backgroundMusic.setLooping(true);
    	
    	// set up information about zones
    	setupZones();
    	_zoneInfos = ZoneInfoWrapper.getZoneInfo(_isMultiTouchGame);
    	
        // create the camera and the SpriteBatch
		_camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		_camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); 
		
        _stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        
        // Essentially the Controller/BoardRenderer thing that you mentioned
        Gdx.input.setInputProcessor(_stage);

        Image img = new Image(Assets.getManager().get("data/background.png", Texture.class));
        img.setFillParent(true);
        _stage.addActor(img);
        
        // used for score and time display
        Image score = new Image(Assets.getManager().get("data/ui_elements/ui_score.png", Texture.class));
        TextFieldStyle tfs = new TextFieldStyle();
        tfs.fontColor = Color.BLACK;
        
        tfs.font = Assets.getManager().get("data/gabriola.fnt", BitmapFont.class);
        
        score_points = 0;
        points = new TextField("" + score_points, tfs);
        
        score_display = new Table();
        score_display.add(score);
        score_display.add(points);
        score_display.validate();
        
        _stage.addActor(score_display);
        
        Image time = new Image(Assets.getManager().get("data/ui_elements/ui_time.png", Texture.class));
        time_seconds = 180.0f;
        seconds = new TextField("" + time_seconds, tfs);
        
        time_display = new Table();
        time_display.add(time);
        time_display.add(seconds);
        time_display.validate();
        
        _stage.addActor(time_display);
        
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
    	if (_isMultiTouchGame) {
    		return MTSequenceGenerator.generateSequence(_zones, _zoneInfos);
    	} else {
    		boolean far = Math.random() < 0.5;
    		return SequenceGenerator.generateSequence(_zones, _zoneInfos, far);
    	}
    }
    
    private boolean sequenceOver() {
    	return _sequenceIndex == _currentSequence.length() &&
    		   _currentSequence.getCue(_sequenceIndex - 1).getTouchable() == Touchable.disabled;
    }
    
    private void getNewSequence() {
    	System.out.println("Updating stats");
    	updateStats();
    	System.out.println("Getting new sequence");
    	_currentSequence.getCue(_sequenceIndex - 2).setVisible(false);
    	_currentSequence.getCue(_sequenceIndex - 1).setVisible(false);
    	_currentSequence = getSequence();
    	_time = 0;
    	_sequenceIndex = 0;
    	_stage.addActor(_currentSequence);
    }
    
    private void endAndSwitchScreens() {
    	_backgroundMusic.stop();
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
		_backgroundMusic.pause();
		_pauseButton.setTouchable(Touchable.disabled);
		_pauseButton.setVisible(false);
		_playButton.setTouchable(Touchable.enabled);
		_playButton.setVisible(true);
    }
    
    private void unpauseGame() {
    	showStartedCues();
		_gameStatus = GAME_RUNNING;
		_backgroundMusic.play();
		_playButton.setTouchable(Touchable.disabled);
		_playButton.setVisible(false);
		_pauseButton.setTouchable(Touchable.enabled);
		_pauseButton.setVisible(true);
    }
    
    private void handleTouch() {
    	
    	boolean firstFingerTouching = Gdx.input.isTouched(0);
		boolean secondFingerTouching = Gdx.input.isTouched(1);
		
		//Set all coordinates to -1 by default
		int firstX = -1;
		int firstY = -1;
		
		int secondX = -1;
		int secondY = -1;
		
		if(firstFingerTouching){
			System.out.println("Touched at ( " + Gdx.input.getX(0) + 
					", " +  Gdx.input.getY(0)+ ")");
			firstX = Gdx.input.getX(0);
			firstY = Gdx.input.getY(0);
		}
		
		if(secondFingerTouching){
			System.out.println("Touched second finger at ( " + Gdx.input.getX(1) + 
					", " +  Gdx.input.getY(1)+ ")");
			secondX = Gdx.input.getX(1);
			secondY = Gdx.input.getY(1);
		}
    	
    	// store input coordinates in stageCoords vector
		//Vector for finger 1
		_stage.screenToStageCoordinates(_stageCoords.set(firstX, firstY));    	
		//Vector for finger 2
		_stage.screenToStageCoordinates(_stageCoords2.set(secondX, secondY));	
		
		
		// pass coordinates to Sequence object (which is a Group of TapCue Actors)
		Actor actor = _stage.hit(_stageCoords.x, _stageCoords.y, true);
		//Actor for 2nd finger		
		Actor actor2 = _stage.hit(_stageCoords2.x, _stageCoords2.y, true);
		
		if ((actor != null && actor instanceof PauseButton) 
				|| (actor2 != null && actor instanceof PauseButton)){
			pauseGame();
		}
		
		if ((actor != null && actor instanceof PlayButton) 
				|| (actor2 != null && actor instanceof PlayButton)){
			unpauseGame();
		}
		
		boolean cueOneHit = false;
		boolean cueTwoHit = false;
		// checks if the tapped location is at a TapCue Actor in the Sequence Group
		if (actor != null && actor instanceof TapCue){
			TapCue tc = (TapCue) actor;
			// when hit, it plays the sound and changes image
			cueOneHit = true;
			//If its a multitouch-game, then dont allow cues to be hit unless hti together
			if(!_isMultiTouchGame){
				//Add to hit total for this zone
				int zoneNum = tc.getZone();
				Zone hit = _zones[zoneNum];
				_zoneHits.put(hit, _zoneHits.get(hit) + 1);
				tc.hit();	
				score_points += 10;
			}
		}
		//Check actor 2
		if (actor2 != null && actor2 instanceof TapCue){
			TapCue tc = (TapCue) actor2;
			// when hit, it plays the sound and changes image
			cueTwoHit = true;
			//If its a multitouch-game, then dont allow cues to be hit unless hti together
			if(!_isMultiTouchGame){
				//Add to hit total for this zone
				int zoneNum = tc.getZone();
				Zone hit = _zones[zoneNum];
				_zoneHits.put(hit, _zoneHits.get(hit) + 1);
				tc.hit();	
				score_points += 10;
			}
		}
		//May or may not want this feature
		//If its a multi-touch game, cues only count as "hit" if both are hit at once
		if(_isMultiTouchGame && cueTwoHit && cueOneHit){
			TapCue tc = (TapCue) actor;
			TapCue tc2 = (TapCue) actor2;
			//Add to hit total for this zone
			int zoneNum = tc.getZone();
			int zoneNum2 = tc2.getZone();
			//Cue 1
			Zone hit = _zones[zoneNum];
			_zoneHits.put(hit, _zoneHits.get(hit) + 1);
			//Cue 2
			Zone hit2 = _zones[zoneNum2];
			_zoneHits.put(hit2, _zoneHits.get(hit2) + 1);

			tc.hit();
			tc2.hit();
			score_points += 20;
		}
		
    }
    
	public void render(float delta) {		
		if (_gameStatus == GAME_RUNNING) {
			
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			
			_time += delta;
			
			if (_first) {
//				_backgroundMusic = Assets.getManager().get("data/audio/invaders_must_die.mp3", Music.class);
//		        _backgroundMusic.play();
//		        _backgroundMusic.setLooping(true);
				_first = false;
				_currentSequence.offsetTimestamps(delta);
			} else {
				_timeLeft -= delta;
			}
			
			if (_timeLeft <= 0) {
				endAndSwitchScreens();
			}
			
			
	    	if (sequenceOver()) {
	    		getNewSequence();
	    	}
	    	
	    	unshowEndedCues();
	    	showStartedCues();
		}
		
		if (Gdx.input.justTouched()) {    		
    		handleTouch();
		}
		
		points.setText("" + score_points);
		
        time_seconds -= delta;
		seconds.setText("" + ((int)time_seconds));
		
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
				ZoneInfoWrapper.updateZone(zInfo, _isMultiTouchGame); 
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
					ZoneInfoWrapper.updateZone(zInfo, _isMultiTouchGame);
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
		_stage.setViewport(width, height, true);
		
		// temporary hardcode values
		score_display.setSize(128*4, 128);
	    score_display.setPosition(0, height-128);
	    
	    time_display.setSize(128*3, 128);
	    time_display.setPosition(width - 128*3, height - 128);
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

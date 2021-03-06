package com.me.gestureGym.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.me.gestureGym.GestureGym;
import com.me.gestureGym.controllers.ZoneInfoWrapper;
import com.me.gestureGym.models.BackButton;
import com.me.gestureGym.models.MainMenuButton;
import com.me.gestureGym.models.StartButton;

// main menu

public class GameStartScreen implements Screen {

	private GestureGym game;

	private Stage _stage;
	private final Vector2 _stageCoords = new Vector2();
	private OrthographicCamera camera;
	private StartButton _multi_touch;	
	private StartButton _single_touch;		
	private MainMenuButton _patient_view;
	private MainMenuButton _doc_view;
	private BackButton backButton;
	
	private static final int START_BUTTON_WIDTH = 256;
	
	private SpriteBatch spriteBatch;
	private Texture splash;

	float w;
	float h;
	
	/**
	 * Constructor for the splash screen
	 * 
	 * @param g
	 *            Game which called this splash screen.
	 */
	public GameStartScreen(GestureGym g) {
		game = g;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		spriteBatch = new SpriteBatch();
		
		// Scene-graph
		_stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		
		// GameStartScreen comes before LoadingScreen
		// Therefore some file have to be handled here
		splash = new Texture(Gdx.files.internal("data/title.png"));
		int offset = 64;
		boolean patient = true;
		//patient button
		_patient_view = new MainMenuButton((float) ((Gdx.graphics.getWidth()/2.0) + (offset)), offset/2, patient);
        _stage.addActor(_patient_view);
		//doctor button
        _doc_view = new MainMenuButton((float) ((Gdx.graphics.getWidth()/2.0) - (START_BUTTON_WIDTH + offset)), offset/2, !patient);
        _stage.addActor(_doc_view);
		
        //GAMEPLAY BUTTONS
        boolean multi_touch = true;
		//Multi- button
		_multi_touch = new StartButton((float) ((Gdx.graphics.getWidth()/2.0) + (offset)), offset/2, multi_touch);
		_multi_touch.setVisible(false);
		_multi_touch.setTouchable(Touchable.disabled);
        _stage.addActor(_multi_touch);
		//Single-touch button
        _single_touch = new StartButton((float) ((Gdx.graphics.getWidth()/2.0) - (START_BUTTON_WIDTH + offset)), offset/2, !multi_touch);
        _single_touch.setVisible(false);
        _single_touch.setTouchable(Touchable.disabled);
        _stage.addActor(_single_touch);

        //Back button
		backButton = new BackButton(0, 0);
		backButton.setVisible(false);
		backButton.setTouchable(Touchable.disabled);
        _stage.addActor(backButton);
	}
	

	@Override
	public void render(float delta) {		
		
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();
		
		float x = (Gdx.graphics.getWidth() - splash.getWidth())/2;
		float y = Gdx.graphics.getHeight() - splash.getHeight();
		spriteBatch.draw(splash, x, y);
		spriteBatch.end();

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		
		if (Gdx.input.justTouched()){
			handleTouch();
		}
		_stage.act(delta);
        _stage.draw();
	}

	private void handleTouch() {
		//store input coordinates in stageCoords vector
		_stage.screenToStageCoordinates(_stageCoords.set(Gdx.input.getX(), Gdx.input.getY()));    		
		// pass coordinates to Sequence object (which is a Group of TapCue Actors)
		Actor actor = _stage.hit(_stageCoords.x, _stageCoords.y, true);
		if (actor != null && actor instanceof MainMenuButton) {
			//Check which button it was
			MainMenuButton mm = (MainMenuButton) actor;
			mm.hit();
			boolean patient = mm.getType(); 			
			if(patient){
				_patient_view.setVisible(false);
				_patient_view.setTouchable(Touchable.disabled);
				_doc_view.setVisible(false);
				_doc_view.setTouchable(Touchable.disabled);
				//Set game mode buttons visible
				_single_touch.setVisible(true);
				_single_touch.setTouchable(Touchable.enabled);
				_multi_touch.setVisible(true);
				_multi_touch.setTouchable(Touchable.enabled);
				backButton.setVisible(true);
				backButton.setTouchable(Touchable.enabled);
			}
			else{
				//Take to doctor button
				game.setScreen(new DocViewScreen(game));
				dispose();
			}
		}
		
		else if (actor != null && actor instanceof StartButton) {
			//Check which button it was
			StartButton st = (StartButton) actor;
			st.hit();
			boolean multi = st.getType();
			//game.setScreen(new LoadingScreen(game, multi));
			ZoneInfoWrapper.getZoneInfo(multi);
        	if(multi){
	    		if(ZoneInfoWrapper.multiTouchIsReady()){
	    			game.setScreen(new GameScreen(game, true));
	    		}
	        	else{
	        		System.out.println("Error starting multi-touch game");
	        	}
        	}	
        	else{
	        	if(ZoneInfoWrapper.singleTouchIsReady()){        		
	        		game.setScreen(new GameScreen(game, false)); // CURRENTLY STARTS SINGLE TOUCH GAME
	        	}
	        	else{
	        		System.out.println("Error starting single-touch game");
	        	}
        	}
			dispose();
		}
		else if (actor != null && actor instanceof BackButton) {
			BackButton b = (BackButton) actor;
			b.hit();
			_patient_view.setVisible(true);
			_patient_view.setTouchable(Touchable.enabled);
			_doc_view.setVisible(true);
			_doc_view.setTouchable(Touchable.enabled);
			//Set game mode buttons visible
			_single_touch.setVisible(false);
			_single_touch.setTouchable(Touchable.disabled);
			_multi_touch.setVisible(false);
			_multi_touch.setTouchable(Touchable.disabled);
			backButton.setVisible(false);
			backButton.setTouchable(Touchable.disabled);
		}

		
	}

	@Override
	public void show() {
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

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
		splash.dispose();
		spriteBatch.dispose();
		_stage.dispose();
	}
}
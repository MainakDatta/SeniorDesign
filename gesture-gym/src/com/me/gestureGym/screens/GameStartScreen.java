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
import com.me.gestureGym.GestureGym;
import com.me.gestureGym.models.PauseButton;
import com.me.gestureGym.models.StartButton;

// main menu

public class GameStartScreen implements Screen {

	private GestureGym game;

	private Stage _stage;
	private final Vector2 _stageCoords = new Vector2();
	private OrthographicCamera camera;
	private StartButton _startButton;	
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
		//Start button
        _startButton = new StartButton((float) ((Gdx.graphics.getWidth()/2.0) - START_BUTTON_WIDTH/2), 0);
        _stage.addActor(_startButton);

	}

	@Override
	public void render(float delta) {		
		
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();
		spriteBatch.draw(splash, 0, 0);
		spriteBatch.draw(splash, 0, 0, w, h, 0, 0, splash.getWidth(),splash.getHeight(), false, false);
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
		
		if (actor != null && actor instanceof StartButton) {
			game.setScreen(new LoadingScreen(game, false));
			dispose();
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
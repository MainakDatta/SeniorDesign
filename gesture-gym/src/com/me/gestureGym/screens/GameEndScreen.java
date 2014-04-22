package com.me.gestureGym.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.me.gestureGym.GestureGym;
import com.me.gestureGym.models.EndScreenButton;

public class GameEndScreen implements Screen {
	final GestureGym _myGame;
	private OrthographicCamera _camera;
	private EndScreenButton _restartButton;
	private EndScreenButton _mainMenuButton;
	
	private final Vector2 _stageCoords = new Vector2();
	
	private static final int BUTTON_WIDTH = 256;
	private static final int BUTTON_HEIGHT = 128;
	
	private Stage _stage;
	
	private boolean _restartMT;
	
	public GameEndScreen(final GestureGym g, boolean gameWasMT) {
		_myGame = g;
		_camera = new OrthographicCamera();
		_camera.setToOrtho(false, 800, 480);
				
		// Scene-graph
		_stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		
		_restartMT = gameWasMT;
		_restartButton = new EndScreenButton((float) (Gdx.graphics.getWidth() / 4.0 - BUTTON_WIDTH / 2.0),
				                             (float) (Gdx.graphics.getHeight() / 2.0 - BUTTON_HEIGHT / 2.0),
				                             true);
		_mainMenuButton = new EndScreenButton((float) (3.0 * Gdx.graphics.getWidth() / 4.0 - BUTTON_WIDTH / 2.0),
                                              (float) (Gdx.graphics.getHeight() / 2.0 - BUTTON_HEIGHT / 2.0),
                                              false);
		_stage.addActor(_restartButton);
		_stage.addActor(_mainMenuButton);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		_camera.update();
		_myGame.batch.setProjectionMatrix(_camera.combined);

		if (Gdx.input.justTouched()) {
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
		
		if (actor != null && actor instanceof EndScreenButton) {
			//Check which button it was
			EndScreenButton st = (EndScreenButton) actor;
			boolean restart = st.getType();
			if (restart) {
				_myGame.setScreen(new GameScreen(_myGame, _restartMT));
			} else {
				_myGame.setScreen(new GameStartScreen(_myGame));
			}
			dispose();
		}
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void show() {
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
		// TODO Auto-generated method stub
		_stage.dispose();
	}

}

package com.me.gestureGym.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.me.gestureGym.GestureGym;

public class GameEndScreen implements Screen {
	final GestureGym _myGame;
	private OrthographicCamera _camera;
	
	public GameEndScreen(final GestureGym g) {
		_myGame = g;
		_camera = new OrthographicCamera();
		_camera.setToOrtho(false, 800, 480);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		_camera.update();
		_myGame.batch.setProjectionMatrix(_camera.combined);
		
		_myGame.batch.begin();
		_myGame.font.draw(_myGame.batch, "Tap anywhere to play again!", 200, 200);
		_myGame.batch.end();
		
		if (Gdx.input.justTouched())
			_myGame.setScreen(new GameScreen(_myGame, false)); // CURRENTLY STARTS SINGLE TOUCH GAME ALWAYS
			dispose();
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
		
	}

}

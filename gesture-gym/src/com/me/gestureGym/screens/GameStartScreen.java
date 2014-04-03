package com.me.gestureGym.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.me.gestureGym.GestureGym;

// main menu

public class GameStartScreen implements Screen {

	private GestureGym game;

	private OrthographicCamera camera;
	
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
		
		// GameStartScreen comes before LoadingScreen
		// Therefore some file have to be handled here
		splash = new Texture(Gdx.files.internal("data/title.png"));

	}

	@Override
	public void render(float delta) {		
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		spriteBatch.begin();
		spriteBatch.draw(splash, 0, 0);
		spriteBatch.draw(splash, 0, 0, w, h, 0, 0, splash.getWidth(),splash.getHeight(), false, false);
		spriteBatch.end();

		camera.update();
		game.batch.setProjectionMatrix(camera.combined);
		
		game.batch.begin();
		game.font.draw(game.batch, "Tap anywhere to begin!", Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/5);
		game.batch.end();
		
		if (Gdx.input.justTouched()){
			game.setScreen(new LoadingScreen(game));
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
		// TODO Auto-generated method stub

	}
}
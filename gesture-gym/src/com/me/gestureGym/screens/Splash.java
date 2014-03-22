package com.me.gestureGym.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.me.gestureGym.GestureGym;

// main menu

public class Splash implements Screen {

	final GestureGym myGame;

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
	public Splash(final GestureGym g) {
		myGame = g;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
		spriteBatch = new SpriteBatch();
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
		myGame.batch.setProjectionMatrix(camera.combined);
		
		myGame.batch.begin();
		myGame.font.draw(myGame.batch, "Welcome to Drop!!! ", 100, 150);
		myGame.font.draw(myGame.batch, "Tap anywhere to begin!", 100, 100);
		myGame.batch.end();
		
		if (Gdx.input.justTouched())
			myGame.setScreen(new GameBoard(myGame));
			dispose();
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
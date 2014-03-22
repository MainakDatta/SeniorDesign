/*
1. Gets sequence S
2. Calls BoardRenderer(S)
3. Calls appropriate BoardRenderer method if detects input.
*/
package com.me.gestureGym.screens;

import java.util.Iterator;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.me.gestureGym.GestureGym;
import com.me.gestureGym.models.Sequence;
//import com.me.gestureGym.controllers.BoardRenderer;

public class GameBoard implements Screen {
	
	final GestureGym myGame;
	
	Texture dropImage;
	OrthographicCamera camera;
	Array<Ellipse> drops;
	long lastCueTime;
	private Sequence sequence;
    private SpriteBatch spriteBatch;
    private Texture splsh;
    private TextureRegion region;
    float w;
    float h;

  
    public GameBoard(GestureGym g){
        myGame = g;
        
        dropImage = new Texture(Gdx.files.internal("droplet.png"));

        // create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		drops = new Array<Ellipse>();
    }	
	
    private void getSequence(){
    	//Will call our beautiful algorithm later    		
    	//Returns hardcoded sequence  for now
    	
    	Ellipse e = new Ellipse(400, 400, 64, 64);
    	drops.add(e);
    	lastCueTime = TimeUtils.nanoTime();
    	
    }
    
    
    @Override
	public void render(float delta) {
		 //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    	//		renderer.startSequence();
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
 
		// tell the camera to update its matrices.
		camera.update();
 
		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		myGame.batch.setProjectionMatrix(camera.combined);
 
		// begin a new batch and draw the bucket and
		// all drops
		myGame.batch.begin();
		myGame.font.draw(myGame.batch, "Cues Hit: " + dropsGathered, 0, 480);

		for (Ellipse e : drops) {
			myGame.batch.draw(dropImage, e.x, e.y);
		}
		myGame.batch.end();
 
		// process user input
		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}
		
		if (Gdx.input.isKeyPressed(Keys.LEFT)){
			//bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)){
			//bucket.x += 200 * Gdx.graphics.getDeltaTime();
		}
		// make sure the bucket stays within the screen bounds
		if (bucket.x < 0)
			bucket.x = 0;
		if (bucket.x > 800 - 64)
			bucket.x = 800 - 64;
 
		// check if we need to create a new raindrop
		if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
			spawnRaindrop();
 
		// move the raindrops, remove any that are beneath the bottom edge of
		// the screen or that hit the bucket. In the later case we play back
		// a sound effect as well.
		Iterator<Rectangle> iter = raindrops.iterator();
		while (iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if (raindrop.y + 64 < 0)
				iter.remove();
			if (raindrop.overlaps(bucket)) {
				dropsGathered++;
				dropSound.play();
				iter.remove();
			}
		}

    	
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
		
//		sequence = getSequence();
//		renderer = new BoardRenderer(sequence);
		
    	w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();	
        spriteBatch = new SpriteBatch();
        splsh = new Texture(Gdx.files.internal("data/libgdx.png"));		
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

	}
}

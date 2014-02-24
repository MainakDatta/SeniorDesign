/*
1. Gets sequence S
2. Calls BoardRenderer(S)
3. Calls appropriate BoardRenderer method if detects input.
*/
package com.me.gestureGym.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.me.gestureGym.models.Sequence;

public class GameBoard implements Screen {

    private SpriteBatch spriteBatch;
    private Texture splsh;
    private Game myGame;
    private TextureRegion region;
    float w;
    float h;
	
  
    public GameBoard(Game g){
        myGame = g;
    }	
	
	
    private Sequence getSequence(){
    	//Will call our beautiful algorithm later
    	
    	
    	//Returns hardcoded sequence  for now
		return null;
    }
    
    
    @Override
	public void render(float delta) {
		 //Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(splsh, 0, 0);
        spriteBatch.draw(splsh, 0, 0, w, h, 
        		0, 0, splsh.getWidth(), splsh.getHeight(), false, false);
        spriteBatch.end();
        
       if(Gdx.input.justTouched())
                myGame.setScreen(new GameBoard(myGame));
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void show() {
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

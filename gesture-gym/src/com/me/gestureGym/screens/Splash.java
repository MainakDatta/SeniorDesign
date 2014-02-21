package com.me.gestureGym.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Splash implements Screen
{
        private SpriteBatch spriteBatch;
        private Texture splsh;
        private Game myGame;
        
        /**
         * Constructor for the splash screen
         * @param g Game which called this splash screen.
         */
        public Splash(Game g){
                myGame = g;
        }

        @Override
        public void render(float delta){
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                spriteBatch.begin();
                spriteBatch.draw(splsh, 0, 0);
                spriteBatch.end();
                
                //if(Gdx.input.justTouched())
                        //myGame.setScreen(new GameScreen());
        }
        
        @Override
	    public void show(){
        	float w = Gdx.graphics.getWidth();
    		float h = Gdx.graphics.getHeight();	
            spriteBatch = new SpriteBatch();
            splsh = new Texture(Gdx.files.internal("data/title.png"));
    		TextureRegion region = new TextureRegion(splsh, 0, 0, w, h);
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
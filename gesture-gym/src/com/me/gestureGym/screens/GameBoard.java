/*
1. Gets sequence S
2. Calls BoardRenderer(S)
3. Calls appropriate BoardRenderer method if detects input.
*/
package com.me.gestureGym.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.me.gestureGym.GestureGym;
import com.me.gestureGym.models.Sequence;
//import com.me.gestureGym.controllers.BoardRenderer;
import com.me.gestureGym.models.TapCue;

public class GameBoard implements Screen {
	
	final GestureGym myGame;
	
	private Stage stage;
	
	OrthographicCamera camera;
	long lastCueTime;

    public GameBoard(GestureGym g){
        myGame = g;

        // create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		
        stage = new Stage(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),true);
        Gdx.input.setInputProcessor(stage);
        
        Sequence s = getSequence();

    }	
	
    private Sequence getSequence(){
    	Array<TapCue> cues = new Array<TapCue>();
    	
    	for(int i = 0; i < 10; i++){
    		float x = (float) (Gdx.graphics.getWidth() * Math.random());
    		float y = (float) (Gdx.graphics.getHeight() * Math.random());
    		TapCue tc = new TapCue(x, y, 2, 2);
        	
    		// hook up action listening
    		tc.setTouchable(Touchable.enabled);
        	stage.addActor(tc);
        	cues.add(tc);
    	}
    	
    	return new Sequence(cues, 5, 2);
    }
    
    private final Vector2 stageCoords = new Vector2();
    
    @Override
	public void render(float delta) {
    	
    	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

    	if (Gdx.input.isTouched()) {
    		stage.screenToStageCoordinates(stageCoords.set(Gdx.input.getX(), Gdx.input.getY()));
			Actor actor = stage.hit(stageCoords.x, stageCoords.y, true);
			if (actor != null && actor instanceof TapCue){
				TapCue tc = (TapCue) actor;
				stage.getRoot().removeActor(tc);
			}
		}

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

	}

	@Override
	public void resize(int width, int height) {

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

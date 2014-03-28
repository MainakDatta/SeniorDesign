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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.utils.Array;
import com.me.gestureGym.GestureGym;
import com.me.gestureGym.models.Sequence;
//import com.me.gestureGym.controllers.BoardRenderer;
import com.me.gestureGym.models.TapCue;

public class GameScreen implements Screen {
	
	final GestureGym myGame;
	
	private Stage stage;
	private Sequence seq;
	OrthographicCamera camera;
	long lastCueTime;
	
	// this variable will be updated somehow in the constructor or something ... 
	private float duration;
	private float time; 
	private int timePointer = 0;
	
    public GameScreen(GestureGym g){
    	time = 0f;
    	
        myGame = g;

        // create the camera and the SpriteBatch
		camera = new OrthographicCamera(800, 480);
		camera.position.set(800/2, 480/2, 0f); 
		
        stage = new Stage(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),true);
        
        // Essentially the Controller/BoardRenderer thing that you mentioned
        Gdx.input.setInputProcessor(stage);
        
        // Sequence s is a Group of TapCue Actors
        seq = getSequence();
        
        stage.addActor(seq);
    }	
	
    private Sequence getSequence(){
    	Array<TapCue> cues = new Array<TapCue>();
    	
    	// this value would be assigned in the constructor or something
    	duration = 5f;
    	
    	float absoluteStart = 0f;
    	
    	float start = absoluteStart;
    	float end = start + duration;
		
    	for(int i = 0; i < 10; i++){
    		
        	float x = (float) (Gdx.graphics.getWidth() * Math.random());
    		float y = (float) (Gdx.graphics.getHeight() * Math.random());
    		System.out.println("x: " + x + " y: " + y);
    		
    		TapCue tc = new TapCue(x, y, start, end);
      		tc.setTouchable(Touchable.enabled);
            tc.setVisible(false); 	
	
        	cues.add(tc);
        	
        	start += duration;
        	end += duration;
        	
    	}
    	
    	// TapCue Actors are added to Sequence Group in the Sequence class constructor
    	return new Sequence(cues, absoluteStart, end);
    }
    
    private final Vector2 stageCoords = new Vector2();
    
    @Override
	public void render(float delta) {
    	
    	if(timePointer > 0){
			TapCue prevCue = seq.getCue(timePointer - 1);
			if(prevCue.getEndTime() <= time){
				seq.removeActor(prevCue);
				
			}
		}
    	//System.out.println("" + time);
    	if(timePointer < seq.length()){
    		
    		TapCue currentCue = seq.getCue(timePointer); 
    		
    		if(currentCue.getStartTime() <= time){
        		currentCue.setVisible(true);
        		timePointer++;
        	}
    		
    		/*
    		 *  currently, each cue is disjoint
    		 *  when a new cue is drawn, the previous disappears
    		 *  
    		 *  TO-DO: cues should not be disjoint (there should be some overlap)
    		 */

    		
    		
    	}
    	
    	
    		
    	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

    	if (Gdx.input.isTouched()) {
    		
    		// store input coordinates in stageCoords vector
    		stage.screenToStageCoordinates(stageCoords.set(Gdx.input.getX(), Gdx.input.getY()));
    		
    		// pass coordinates to Sequence object (which is a Group of TapCue Actors)
    		Actor actor = seq.hit(stageCoords.x, stageCoords.y, true);
			
			// checks if the tapped location is at a TapCue Actor in the Sequence Group
    		if (actor != null && actor instanceof TapCue){
				TapCue tc = (TapCue) actor;
				seq.removeActor(tc);
			}
		}

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        
        time += delta;
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

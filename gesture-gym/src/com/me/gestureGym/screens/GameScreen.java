/*
1. Gets sequence S
2. Calls BoardRenderer(S)
3. Calls appropriate BoardRenderer method if detects input.
*/
package com.me.gestureGym.screens;

import java.util.HashMap;

import almonds.Parse;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.me.gestureGym.GestureGym;
import com.me.gestureGym.controllers.SequenceGenerator;
import com.me.gestureGym.controllers.ZoneInfoWrapper;
import com.me.gestureGym.data.ZoneResponseInfo;
import com.me.gestureGym.models.Sequence;
import com.me.gestureGym.models.Zone;
import com.me.gestureGym.models.TapCue;

public class GameScreen implements Screen {
	
	final GestureGym myGame;
	//TENTATIVE VALUE
	private static final double SUCCESS = 0.8;
	
	private Stage stage;
	private Sequence seq;
	OrthographicCamera camera;
	long lastCueTime;
	Zone[] allZones;
	ZoneResponseInfo[] information;
	//Zone Hits map will help us calculate the hit rate
	HashMap<Zone, Integer> zoneHits;
	
	//CRUCIAL SUCCESS_DUR variable
	private float duration;
	
	private float time; 
	private int timePointer = 0;
	
    public GameScreen(GestureGym g){
    	Parse.initialize("a9fgXH8y5WZxzucfA8ZrPOdQ6dEEsSLHfhykvyzY",
				"et6FgY6BlRf7zbaarHBBY18g7v233x8V2HXty7DP");
    	
    	time = 0f;
    	duration = 2f;    	
        myGame = g;
    	
    	//Make 16 zones
    	float width = Gdx.graphics.getWidth();
    	float height = Gdx.graphics.getHeight();
    	
    	System.out.println("Screen width is " + width + ", screen height is " + height);
    	
    	//ZOne shenanigans
    	zoneHits = new HashMap<Zone, Integer>();
    	allZones = new Zone[16];
    	for(int i = 0; i< 16; i++){
    		float z_width = (float) ((0.25)* width);
    		float z_height = (float) ((0.25)* height);
    		float zone_x = (float) ((i % 4 * 0.25) * width);
    		float zone_y = (float) ((i / 4 * 0.25) * height);
    		Zone zone = new Zone(i,zone_x, zone_y ,z_width, z_height);
    		
    		System.out.println("Zone " + i + ":");
    		System.out.println("Zone width is " + z_width + ", zone height is " + z_height);
    		System.out.println("Zone upper left is (" + zone_x + ", " + zone_y + ")");
    		
    		allZones[i] = zone;
    		//All zones initialized to scores of 0
    		zoneHits.put(zone, 0);
    	}    	
    	//Get cuttent zoneresponseinfo shit
    	information = ZoneInfoWrapper.getZoneInfo();
    	
        // create the camera and the SpriteBatch
		camera = new OrthographicCamera(800, 480);
		camera.position.set(800/2, 480/2, 0f); 
		
        stage = new Stage(width, height, true);
        
        // Essentially the Controller/BoardRenderer thing that you mentioned
        Gdx.input.setInputProcessor(stage);
        
        // Sequence s is a Group of TapCue Actors
        seq = getSequence();
        
        stage.addActor(seq);
    }	
	
    private Sequence getSequence(){    		
    	//TODO:Lots of stuff will happen here
    	Sequence generated = SequenceGenerator.generateSequence(allZones, information, false);
    	duration = generated.getDuration();
    	//	THIS CODE WILL BE GONE--------------------------------------------------------------------------	
//    	Array<TapCue> cues = new Array<TapCue>();    	        	
//    	float absoluteStart = 0f;    	
//    	float start = absoluteStart;
//    	//duration is potential SUCESS_DUR
//    	float end = start + duration;
//		
//    	for(int i = 0; i < 10; i++){	
//        	float x = (float) (Gdx.graphics.getWidth() * Math.random());
//    		float y = (float) (Gdx.graphics.getHeight() * Math.random());
//    		System.out.println("x: " + x + " y: " + y);    		
//    		TapCue tc = new TapCue(x, y, i, start, end);
//      		tc.setTouchable(Touchable.enabled);		
//        	cues.add(tc);        	
//        	start += duration;
//        	end += duration;        	
//    	}    	
    	//THIS CODE WILL BE GONE-----------------------------------------------------------------------------
    	// TapCue Actors are added to Sequence Group in the Sequence class constructor
    	return generated;
    }
   

    private final Vector2 stageCoords = new Vector2();
    
	public void render(float delta) {
    	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

    	if(timePointer > 0){
			TapCue prevCue = seq.getCue(timePointer - 1);
			if(prevCue.getEndTime() <= time){
				//System.out.println("removing actor at (" + prevCue.getX() + ", " + prevCue.getY() + ")");
				seq.removeActor(prevCue);				
			}
		}
    	
    	//System.out.println("" + time);
    	if(timePointer < seq.length()){  
    		
    		TapCue currentCue = seq.getCue(timePointer);

    		if(currentCue.getStartTime() <= time){
        		System.out.println("time: " + timePointer);
        		System.out.println("x: " + currentCue.getX() + ", y: " + currentCue.getY());
        		System.out.println("start: " + currentCue.getStartTime() + ", end: " + currentCue.getEndTime());
        		currentCue.setTouchable(Touchable.enabled);
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
    	
    	if (Gdx.input.isTouched()) {    		
    		// store input coordinates in stageCoords vector
    		stage.screenToStageCoordinates(stageCoords.set(Gdx.input.getX(), Gdx.input.getY()));    		
    		// pass coordinates to Sequence object (which is a Group of TapCue Actors)
    		Actor actor = seq.hit(stageCoords.x, stageCoords.y, true);
			
    		// checks if the tapped location is at a TapCue Actor in the Sequence Group
    		if (actor != null && actor instanceof TapCue){
				TapCue tc = (TapCue) actor;
				//TODO: Display animation
				System.out.println("BOOM");
				//Add to hit total for this zone
				int zoneNum = tc.getZone();
				Zone hit = allZones[zoneNum];
				int hitTotal = zoneHits.get(hit);
				zoneHits.put(allZones[tc.getZone()], hitTotal + 1);
				
				seq.removeActor(tc);
				
				//Check if sequence is done
				if (seq.length()== 0){					
					updateStats();
				}
			}
		}

        stage.act(delta);
        stage.draw();        
        time += delta;
	}

	//Creates ZoneResponseInfo jawns
	private void updateStats() {
		//Only update if they pass threshold?
		for(Zone z: zoneHits.keySet()){
			//Total mumber of hits
			int totalHits = zoneHits.get(z);
			double hitRate = totalHits/z.getNumCues();
			int zoneNum = z.getZoneNumber();
			//ONLY UPDATE IF IT BEAT OUR SUCESS THRESHOLD and is less than old duration
			if(hitRate > SUCCESS && zoneNum < information[zoneNum].getSuccessDuration()){
				ZoneResponseInfo zInfo = new ZoneResponseInfo(zoneNum, duration, hitRate);
				ZoneInfoWrapper.updateZone(zInfo);
			}	
		}				
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
		stage.dispose();
	}
}

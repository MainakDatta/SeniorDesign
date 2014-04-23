package com.me.gestureGym.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.me.gestureGym.GestureGym;
import com.me.gestureGym.controllers.Assets;
import com.me.gestureGym.models.LoadingBar;


public class LoadingScreen implements Screen {

    private GestureGym _game;

    private Stage stage;

    private Image logo;
    private Image loadingFrame;
    private Image loadingBarHidden;
    private Image screenBg;
    private Image loadingBg;

    private float startX, endX;
    private float percent;

    private Actor loadingBar;

    
    public LoadingScreen(GestureGym game) {

        _game = game;
        
        // Initialize the stage where we will place everything
        stage = new Stage();

        // PREPARE ALL MEDIA FILES FOR USE
        Assets.load();
        
        // Get our texture atlas from the manager
        TextureAtlas atlas = Assets.getManager().get("data/loading.pack", TextureAtlas.class);
        
        // Grab the regions from the atlas and create some images
        logo = new Image(atlas.findRegion("libgdx-logo"));
        loadingFrame = new Image(atlas.findRegion("loading-frame"));
        loadingBarHidden = new Image(atlas.findRegion("loading-bar-hidden"));
        screenBg = new Image(atlas.findRegion("screen-bg"));
        loadingBg = new Image(atlas.findRegion("loading-frame-bg"));

        // Add the loading bar animation
        Animation anim = new Animation(0.05f, atlas.findRegions("loading-bar-anim") );
        anim.setPlayMode(Animation.LOOP_REVERSED);
        loadingBar = new LoadingBar(anim);
        // Or if you only need a static bar, you can do
        // loadingBar = new Image(atlas.findRegion("loading-bar1"));
        // Add all the actors to the stage
        stage.addActor(screenBg);
        stage.addActor(loadingBar);
        stage.addActor(loadingBg);
        stage.addActor(loadingBarHidden);
        stage.addActor(loadingFrame);
        stage.addActor(logo);
    }
    
    @Override
    public void show() {        
    	//
    }

    @Override
    public void resize(int width, int height) {
        // Set our screen to always be XXX x 480 in size
        width = 480 * width / height;
        height = 480;
        stage.setViewport(width , height, false);

        // Make the background fill the screen
        screenBg.setSize(width, height);

        // Place the logo in the middle of the screen and 100 px up
        logo.setX((width - logo.getWidth()) / 2);
        logo.setY((height - logo.getHeight()) / 2 + 100);

        // Place the loading frame in the middle of the screen
        loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY((stage.getHeight() - loadingFrame.getHeight()) / 2);

        // Place the loading bar at the same spot as the frame, adjusted a few px
        loadingBar.setX(loadingFrame.getX() + 15);
        loadingBar.setY(loadingFrame.getY() + 5);

        // Place the image that will hide the bar on top of the bar, adjusted a few px
        loadingBarHidden.setX(loadingBar.getX() + 35);
        loadingBarHidden.setY(loadingBar.getY() - 3);
        // The start position and how far to move the hidden loading bar
        startX = loadingBarHidden.getX();
        endX = 440;

        // The rest of the hidden bar
        loadingBg.setSize(450, 50);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setY(loadingBarHidden.getY() + 3);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
    	Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
        // Interpolate the percentage to make it more smooth
        percent = Interpolation.linear.apply(percent, Assets.getManager().getProgress(), 0.1f);
        // Update positions (and size) to match the percentage
        loadingBarHidden.setX(startX + endX * percent);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setWidth(450 - 450 * percent);
        loadingBg.invalidate();

        // Show the loading screen
        stage.act();
        stage.draw();

        if (Assets.getManager().update()) { // Load some, will return true if done loading
        	//Another check here to see if array is loaded
//        	if(multiTouch){
//        		if(ZoneInfoWrapper.multiTouchIsReady()){
//        			_game.setScreen(new GameScreen(_game, true));
//        			dispose();
//        		}
//        	}
//        	else{
//	        	if(ZoneInfoWrapper.singleTouchIsReady()){        		
//	        		_game.setScreen(new GameScreen(_game, false)); // CURRENTLY STARTS SINGLE TOUCH GAME
//	        		dispose();
//	        	}
//        	}
        	if (Gdx.input.isTouched()) {
        		_game.setScreen(new GameStartScreen(_game));
        		dispose();
        	}
        }
    }

    @Override
    public void hide() {
        // Dispose the loading assets as we no longer need them
    	
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
		Assets.getManager().unload("data/loading.pack");
		stage.dispose();    	
		
	}
}
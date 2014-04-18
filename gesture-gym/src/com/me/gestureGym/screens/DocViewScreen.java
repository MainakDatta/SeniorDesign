package com.me.gestureGym.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.me.gestureGym.GestureGym;
import com.me.gestureGym.controllers.Assets;
import com.me.gestureGym.models.DocOptionsButton;
import com.me.gestureGym.models.EndScreenButton;

public class DocViewScreen implements Screen {
	final GestureGym _myGame;
	private OrthographicCamera _camera;
	private DocOptionsButton _checkPatientButton;
	private DocOptionsButton _giveTabletButton;
	
	private final Vector2 _stageCoords = new Vector2();
	
	private static final int BUTTON_WIDTH = 256;
	private static final int BUTTON_HEIGHT = 128;
	
	private SpriteBatch _spriteBatch;
	
	private Stage _stage;
	
	private TextFieldStyle tfs;
	private TextField patient_name;
	
	public DocViewScreen(final GestureGym g) {
		_myGame = g;
		_camera = new OrthographicCamera();
		_camera.setToOrtho(false, 800, 480);
		
		_spriteBatch = new SpriteBatch();
		//Text Field to enter patient name
        tfs = new TextFieldStyle();
        tfs.fontColor = Color.BLACK;        
        tfs.font = new BitmapFont();
       
        patient_name = new TextField("Type name of patient", tfs);
        patient_name.setVisible(false);
        patient_name.setTouchable(Touchable.disabled);
		
		// Scene-graph
		_stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		
		_checkPatientButton = new DocOptionsButton((float) (Gdx.graphics.getWidth() / 4.0 - BUTTON_WIDTH / 2.0),
				                             (float) (Gdx.graphics.getHeight() / 2.0 - BUTTON_HEIGHT / 2.0),
				                             true);
		_giveTabletButton = new DocOptionsButton((float) (3.0 * Gdx.graphics.getWidth() / 4.0 - BUTTON_WIDTH / 2.0),
                                              (float) (Gdx.graphics.getHeight() / 2.0 - BUTTON_HEIGHT / 2.0),
                                              false);
		_stage.addActor(_checkPatientButton);
		_stage.addActor(_giveTabletButton);
		_stage.addActor(patient_name);

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		_camera.update();
		_myGame.batch.setProjectionMatrix(_camera.combined);
		
		if (Gdx.input.justTouched()) {
			handleTouch();
		}
		
		_stage.act(delta);
        _stage.draw();
	}
	
	private void handleTouch() {
		//store input coordinates in stageCoords vector
		_stage.screenToStageCoordinates(_stageCoords.set(Gdx.input.getX(), Gdx.input.getY()));    		
		// pass coordinates to Sequence object (which is a Group of TapCue Actors)
		Actor actor = _stage.hit(_stageCoords.x, _stageCoords.y, true);
		
		if (actor != null && actor instanceof DocOptionsButton) {
			//Check which button it was
			DocOptionsButton dbt = (DocOptionsButton) actor;
			boolean checkPatient = dbt.getType();
			if (checkPatient) {
				//Leads to check Patient option
				
			} else {
//				//Give tablet button
//				_checkPatientButton.setVisible(false);
//				_giveTabletButton.setVisible(false);
//				_checkPatientButton.setTouchable(Touchable.disabled);
//				_giveTabletButton.setTouchable(Touchable.disabled);
//				patient_name.setVisible(true);
//				patient_name.setTouchable(Touchable.enabled);
			}
			//dispose();
		}
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

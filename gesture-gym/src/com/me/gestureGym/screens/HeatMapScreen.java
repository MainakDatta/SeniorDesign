package com.me.gestureGym.screens;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.me.gestureGym.GestureGym;
import com.me.gestureGym.controllers.Assets;
import com.me.gestureGym.data.DataWrapper;
import com.me.gestureGym.data.HistoricalZoneResponseInfo;
import com.me.gestureGym.data.ImproperFileFormatException;
import com.me.gestureGym.data.LocalStorageDoesNotExistException;
import com.me.gestureGym.data.ZoneResponseInfo;
import com.me.gestureGym.models.BackButton;

public class HeatMapScreen implements Screen {

	private GestureGym game;
	private Stage stage;

	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	private float[][] times;

	private BackButton _backButton;
	private final Vector2 _stageCoords = new Vector2();

	private String[] patientList;
	private String[] patientData;
	private ButtonGroup bg;
	
	private SelectBox patientDropdown;
	private String currentPatient;
	private SelectBox dataDropdown;
	private CheckBox singleTouch;
	private CheckBox multiTouch;
	private boolean prev;
	
	// used for UI adjustments
	private float offset = 128f;
	
	public HeatMapScreen(GestureGym g) {
		
		game = g;
		
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		shapeRenderer = new ShapeRenderer();
		
		times = new float[4][4];
		try {
			String patient = DataWrapper.getCurrentPatient();
			ZoneResponseInfo[] info = DataWrapper.getMostRecentSingleTouchData(patient);
			
			int sqrt = (int) Math.sqrt(info.length);
			int row = -1;
			
			for(int i = 0; i < info.length; i++){
				
				if (i%sqrt == 0){
					row++;
				}
				//System.out.println(row + " " + i % sqrt + " " + info[i].getSuccessDuration());
				times[(row)][(i % sqrt)] = info[i].getSuccessDuration(); 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		//BACK BUTTON CODE
        _backButton = new BackButton(0, 0);
        stage.addActor(_backButton);
	
        ArrayList<String> pList;
		try {
			currentPatient = DataWrapper.getCurrentPatient();
			pList = DataWrapper.getAllPatients();
			patientList = new String[pList.size()];
	        for(int i = 0; i < pList.size(); i++ ){
	            patientList[i] = pList.get(i);
	        }
		} catch (LocalStorageDoesNotExistException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        patientData = new String[]{""};
        
		Skin skin = Assets.getManager().get("data/uiskin.json", Skin.class);
		
		patientDropdown = new SelectBox(patientList, skin);
	
		patientDropdown.setSelection(currentPatient);
		
		bg = new ButtonGroup();
		
		singleTouch = new CheckBox(" Single-Touch", skin);
		multiTouch = new CheckBox(" Multi-Touch", skin);
		
		bg.add(singleTouch, multiTouch);
		bg.setChecked("Single-Touch");
		bg.setMaxCheckCount(1);
		bg.setMinCheckCount(1);
		
		dataDropdown = new SelectBox(patientData, skin);
		updateHistoryList(true);
		
		Label instructions = new Label("Please select a patient and a play record!", skin);

		Table window = new Table(skin);
		window.setPosition(offset, Gdx.graphics.getHeight() - (3*offset/4));
		//window.setSize(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);

		window.row();
		window.add(instructions).center();
		
		window.row();
		window.add(patientDropdown).colspan(5).width(2*offset);
		window.add(singleTouch).colspan(10).width(3*offset/2);
		window.add(multiTouch).colspan(10).width(3*offset/2);
		window.add(dataDropdown).colspan(5).width(2*offset);
		window.pack();

		stage.addActor(window);
	}

	private void handleTouch() throws ParseException {
		
		if(singleTouch.isChecked() && !prev){
			updateHistoryList(true);
			prev = true;
			
		}
		if(multiTouch.isChecked() && prev){
			updateHistoryList(false);
			prev = false;
		}
		
		//store input coordinates in stageCoords vector
		stage.screenToStageCoordinates(_stageCoords.set(Gdx.input.getX(), Gdx.input.getY()));    		
		// pass coordinates to Sequence object (which is a Group of TapCue Actors)
		Actor actor = stage.hit(_stageCoords.x, _stageCoords.y, true);
		
		if(actor != null && actor instanceof BackButton){
			//We are in main doc View and want to go back to main Screen
			game.setScreen(new DocViewScreen(game));
			dispose();
		}
		
		
		if(actor == patientDropdown){
			System.out.println("HERE MOTHERFUCKER");
			if(!patientDropdown.getSelection().equals(currentPatient)){
				currentPatient = patientDropdown.getSelection();
			}
		
		}
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		generateRegularHeatMap();
		
		if (Gdx.input.justTouched()) {
			try {
				handleTouch();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		stage.act(delta);
		stage.draw();
	}
	
	private void updateHistoryList(boolean mode) throws ParseException{
		String curr;
		try {
			curr = DataWrapper.getCurrentPatient();
			ArrayList<String> dates = new ArrayList<String>();
			ArrayList<HistoricalZoneResponseInfo[]> data;
			
			if(mode){
				data = DataWrapper.getAllSingleTouchData(curr);
			}
			else{
				data = DataWrapper.getAllMultiTouchData(curr);
			}
		
			for(HistoricalZoneResponseInfo[] h : data){
				dates.add(h[0].getDate());
			}
			
			patientData = dates.toArray(new String[0]);
			dataDropdown.setItems(patientData);
			dataDropdown.invalidate();
		
		} catch (LocalStorageDoesNotExistException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ImproperFileFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	// not used
	private Color[][] generateSimpleGradients(){
		
		float fast = 02f;
		float slow = 2f;
		
		for(int i = 0; i < times.length; i++){
			for(int j = 0; j < times[i].length; j++){
				
				if(times[i][j] > slow){
					slow = times[i][j];
				}
				if(times[i][j] < fast){
					fast = times[i][j];
				}
			}
		}
		
		// delta value
		slow += 0.025f;
		
		Color[][] grads = new Color[4][4];
		for(int i = 0; i < times.length; i++){
			for(int j = 0; j < times[i].length; j++){
				float red = (slow - times[i][j]) / (slow - fast);
				//System.out.println("i: " + i + " j: " + j + " time: " + times[i][j]+ " red: " +red);
				grads[i][j] = new Color(red, 0, 0, 1);
			}
		}
		
		return grads;
	}
	
	private Color[][] generateBetterGradients(){
		
		Color[][] grads = new Color[4][4];
		float red, green, blue;
		for(int i = 0; i < times.length; i++){
			for(int j = 0; j < times[i].length; j++){
				if (times[i][j] >= 1.25f) { // blue to green segment
		            blue = ((times[i][j] - 1.25f) / (2.0f - 1.25f));
					green = 1-blue;
					red = 0;
				} else if (times[i][j] >= 0.5) { // green to red segment
					green = ((times[i][j] - 0.5f) / (1.25f - 0.5f));
					red =  1-green;
				    blue = 0;
				} else {
					red = 1;
					green = 0;
					blue = 0;
				}
				grads[i][j] = new Color(red, green, blue, 1);
			}
		}
		
		return grads;
	}
		
	private void generateRegularHeatMap(){
		
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		
		
		
		float bW = (width - 2*offset) / 4;
		float bH = (height - 2*offset) / 4;
		
		Color[][] reds = generateBetterGradients();
		
		camera.update();
		
		shapeRenderer.begin(ShapeType.Filled);

		for(int i = 0; i < reds.length; i++){
			for(int j = 0; j < reds[i].length; j++){
				shapeRenderer.setColor(reds[i][j]);
				shapeRenderer.rect(offset + (bW*j) , offset + (bH*i), bW, bH);
			}
		}
		
		shapeRenderer.end();
		

		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.WHITE);
		for(int i = 1; i < reds.length; i++){
			shapeRenderer.line(offset, offset + (i * bH), width-offset, offset + (i * bH));
		}
		for(int j = 1; j < reds[0].length; j++){
			shapeRenderer.line(offset + (j * bW), offset, offset + (j * bW) , height - offset);
		}
		shapeRenderer.end();
	}
	
	@SuppressWarnings("unused")
	private void generateSmoothHeatMap(){
		
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();
		
		float offset = 128f;
		
		float borderWidth = (width - 2*offset) / 4;
		float borderHeight = (height - 2*offset) / 4;
		
		float boxWidth = (width - offset) / 5;
		float boxHeight = (height - offset) / 5;
		
		camera.update();
		
		Color[][] reds = generateSimpleGradients();
		Color edge = new Color (0f, 0f, 0f, 1f);
		
		shapeRenderer.begin(ShapeType.Filled);
		
		
		for(int i = 0; i < 5; i++){
			for(int j = 0; j < 5; j++){
				
				Color bottomLeft = edge;
				Color bottomRight = edge;
				Color topRight = edge;
				Color topLeft = edge;
				
				// four corner cases
				if(i == 0 && j == 0){
					topRight = reds[i][j];					
				}
				else if(i == 0 && j == reds.length){
					topLeft = reds[i][j-1];
				}				
				else if(i == reds.length && j == 0){
					bottomRight = reds[i-1][j];
				}
				else if(i == reds.length && j == reds.length){
					bottomLeft = reds[i-1][j-1];
				}
				// four general sides
				else if(i == 0){
					topRight = reds[i][j];
					topLeft = reds[i][j-1];
				}
				else if(i == reds.length){
					
					bottomLeft = reds[i-1][j-1];
					bottomRight = reds[i-1][j];
				}
				else if(j == 0){
					bottomRight = reds[i-1][j];
					topRight = reds[i][j];
				}
				else if(j == reds.length){
					bottomLeft = reds[i-1][j-1];
					topLeft = reds[i][j-1];
				}
				// internals
				else{
					bottomLeft = reds[i-1][j-1];
					bottomRight = reds[i-1][j];
					topRight = reds[i][j];
					topLeft = reds[i][j-1];
				}

				shapeRenderer.rect(offset/2 + (boxWidth * j), offset/2 +(boxHeight*i), boxWidth, boxHeight, bottomLeft, bottomRight, topRight, topLeft);
			}
		}
		
		shapeRenderer.setColor(0, 0, 0, 1);
		shapeRenderer.rect(0, 0, offset, height);
		shapeRenderer.rect(0, 0, width, offset);
		shapeRenderer.rect(width-offset, 0, offset, height);
		shapeRenderer.rect(0, height-offset, width, offset);
		shapeRenderer.end();
		

		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.WHITE);
		for(int i = 1; i < reds.length; i++){
			shapeRenderer.line(offset, offset + (i * borderHeight), width-offset, offset + (i * borderHeight));
		}
		for(int j = 1; j < reds[0].length; j++){
			shapeRenderer.line(offset + (j * borderWidth), offset, offset + (j * borderWidth) , height - offset);
		}
		shapeRenderer.end();
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
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
		shapeRenderer.dispose();
		stage.dispose();
	}
	

}

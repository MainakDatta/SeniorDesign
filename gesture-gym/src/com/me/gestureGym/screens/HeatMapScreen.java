package com.me.gestureGym.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.me.gestureGym.GestureGym;
import com.me.gestureGym.data.DataWrapper;
import com.me.gestureGym.data.ZoneResponseInfo;

public class HeatMapScreen implements Screen{
    
	private GestureGym game;
    private Stage stage;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
	private float[][] times;
	
	public HeatMapScreen(GestureGym g){
		
		game = g;
		stage = new Stage();
		
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
				System.out.println(row + " " + i % sqrt + " " + info[i].getSuccessDuration());
				times[(row)][(i % sqrt)] = info[i].getSuccessDuration(); 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//times = new float[][]{{1.99f, 1.93f, 1.97f, 2f}, {2f, 1.94f, 1.97f, 2f}, {1.89f, 2f, 1.93f, 2f}, {2f, 1.99f, 1.98f, 2f}};
		
	}
	
	@Override
	public void show() {
		// like Processing's setup()
		// perform one time operations here
		
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		generateRegularHeatMap();
		
	}

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
				if (times[i][j] >= 1.80f) { // blue to green segment
		            blue = ((times[i][j] - 1.80f) / (2.0f - 1.80f));
		            green = 1-blue;
		            red = 0;
				} else if (times[i][j] >= 1.0f) { // green to red segment
					green = ((times[i][j] - 1.0f) / (1.80f - 1.0f));
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
		
		float offset = 128f;
		
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

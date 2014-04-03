package com.me.gestureGym;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "gesture-gym";
		cfg.useGL20 = false;
		cfg.width = 1280;
		cfg.height = 768;
		
		new LwjglApplication(new GestureGym(), cfg);
	}
}

package com.me.gestureGym;

import almonds.Parse;

import com.me.gestureGym.screens.Splash;

import com.badlogic.gdx.Game;

public class GestureGym extends Game {
	
	@Override
	public void create() {		
		Parse.initialize("a9fgXH8y5WZxzucfA8ZrPOdQ6dEEsSLHfhykvyzY", "fvjFSvTnypy9zgLEiXbv3kgC3YhlY2zt4MvssEP3");
		this.setScreen(new Splash(this));
	}

}

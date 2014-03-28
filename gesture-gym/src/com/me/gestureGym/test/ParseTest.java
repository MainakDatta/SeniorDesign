package com.me.gestureGym.test;

import com.me.gestureGym.data.*;

import junit.framework.*;
import almonds.*;

public class ParseTest extends TestCase {
	
	@Override
	public void setUp() {
		Parse.initialize("a9fgXH8y5WZxzucfA8ZrPOdQ6dEEsSLHfhykvyzY",
				"et6FgY6BlRf7zbaarHBBY18g7v233x8V2HXty7DP");
	}
	
	public void testPutAndGet() throws ParseException {
		ParseWrapper w = new ParseWrapper();
		w.clean(2000);
		for (int i = 0; i < 16; i++) {
			ZoneInfo info = w.getZoneInfo(i);
			assertEquals(i, info.getZoneNumber());
			assertEquals((int) 2000.0, (int) info.getSuccessDuration());
			assertEquals((int) 1.0, (int) info.getHitRate());
		}
		
		ZoneInfo testIn = new ZoneInfo(0, 1000, .96);
		w.putZoneInfo(testIn);
		ZoneInfo testOut = w.getZoneInfo(0);
		assertEquals(testIn.getZoneNumber(), testOut.getZoneNumber());
		assertEquals((int) testIn.getSuccessDuration(), (int) testOut.getSuccessDuration());
		assertEquals((int) testIn.getHitRate(), (int) testOut.getHitRate());
	}
}

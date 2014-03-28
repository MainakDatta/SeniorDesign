package com.me.gestureGym.data;

import almonds.*;

public class ParseWrapper {
	
	public ParseWrapper() {}
	
	public void putZoneInfo(ZoneInfo info) throws ParseException {
		ParseObject o = new ParseObject("ZoneInfo");
		o.put("zoneNumber", info.getZoneNumber());
		o.put("successDuration", info.getSuccessDuration());
		o.put("hitRate", info.getHitRate());
		o.save();
	}
}

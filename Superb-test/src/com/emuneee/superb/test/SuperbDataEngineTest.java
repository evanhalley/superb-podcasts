package com.emuneee.superb.test;

import com.emuneee.superb.engine.SuperbDataEngine;
import com.emuneee.superb.ui.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class SuperbDataEngineTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private static final String sUrl = "http://feeds.feedburner.com/AndroidCentralPodcast?format=xml";

	public SuperbDataEngineTest() {
		super(MainActivity.class);
	}

	public void testSDEAndroidCentralPodcast() {
		SuperbDataEngine sde = new SuperbDataEngine(getActivity());
		sde.addPodcast("http://feeds.feedburner.com/AndroidCentralPodcast?format=xml");
		sde.close();
	}

}

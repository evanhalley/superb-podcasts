package com.emuneee.superb.test;

import com.emuneee.superb.engine.utils.DateParser;
import com.emuneee.superb.ui.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class DateParserTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	public DateParserTest() {
		super(MainActivity.class);
	}

	public void testDateParserAndroidCentralDate() {
		DateParser dateParser = new DateParser("Fri, 09 Nov 2012 04:54:32 +0000");
		assertEquals(true, dateParser.canParse());
	}

}

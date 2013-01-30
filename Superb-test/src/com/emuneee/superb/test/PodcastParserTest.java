package com.emuneee.superb.test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.emuneee.superb.engine.Channel;
import com.emuneee.superb.engine.Episode;
import com.emuneee.superb.engine.utils.HttpUtils;
import com.emuneee.superb.engine.utils.PodcastParser;
import com.emuneee.superb.ui.MainActivity;

import android.test.ActivityInstrumentationTestCase2;

public class PodcastParserTest extends
		ActivityInstrumentationTestCase2<MainActivity> {

	private static final String sUrl = "http://feeds.feedburner.com/AndroidCentralPodcast?format=xml";

	public PodcastParserTest() {
		super(MainActivity.class);
	}

	public void testParserAndroidCentralPodcast() {
		try {
			HttpURLConnection conn = HttpUtils.getUrlConnection(sUrl);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			PodcastParser handler = new PodcastParser(sUrl);
			InputSource is = new InputSource(conn.getInputStream());

			xr.setContentHandler(handler);
			xr.parse(is);

			Map<String, Episode> episodes = handler.getEpisodes();
			Channel channel = handler.getChannel();
			assertNotNull(channel);
			assertEquals("Android Central Podcast", channel.getTitle());
			assertEquals(
					"http://www.mobilenations.com/broadcasting/podcast_android_central_1200.jpg",
					channel.getImageUrl());
			assertEquals(
					"Android podcast - Get all the latest news on the Android Platform with Phil and Mickey: Android Apps, the Droid, Nexus One, and more. ",
					channel.getDescription());
		} catch (IOException e) {

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

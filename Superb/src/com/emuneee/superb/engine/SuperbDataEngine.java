/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.engine;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.emuneee.superb.engine.data.ChannelDataSource;
import com.emuneee.superb.engine.data.ChannelDataSource.OrderBy;
import com.emuneee.superb.engine.data.EpisodeDataSource;
import com.emuneee.superb.engine.data.SuperbDBHelper;
import com.emuneee.superb.engine.utils.HttpUtils;
import com.emuneee.superb.engine.utils.PodcastParser;

import android.content.Context;
import android.util.Log;

/**
 * Manages podcast data, from parsing to storage and retrieval
 * 
 * @author Evan
 * 
 */
public class SuperbDataEngine {
	private static final String sTag = "SuperbDataEngine";
	private SuperbDBHelper mDBHelper;
	private ChannelDataSource mChannelDS;
	private EpisodeDataSource mEpisodeDS;

	public SuperbDataEngine(Context context) {
		this.mDBHelper = new SuperbDBHelper(context);
		this.mChannelDS = new ChannelDataSource(mDBHelper);
		this.mEpisodeDS = new EpisodeDataSource(mDBHelper);
		mChannelDS.open();
		mEpisodeDS.open();
	}

	public void close() {
		Log.d(sTag, "Closing...");
		mDBHelper.close();
		mChannelDS.close();
		mEpisodeDS.close();
	}

	public EpisodeDataSource getEpisodeDataSource() {
		return mEpisodeDS;
	}

	public ChannelDataSource getChannelDataSource() {
		return mChannelDS;
	}

	/**
	 * Validates a channel is a valid podcast before proceeding
	 * 
	 * @param entry
	 * @return
	 */
	public static boolean validateChannel(
			AbstractMap.SimpleEntry<Channel, Map<String, Episode>> entry) {
		boolean result = true;
		Channel channel = entry.getKey();
		Map<String, Episode> episodes = entry.getValue();

		if (channel.getTitle() == null || channel.getTitle().length() == 0) {
			Log.w(sTag, "Channel title is empty or null");
			return false;
		}

		if (channel.getUrl() == null || channel.getUrl().length() == 0) {
			Log.w(sTag, "Channel url is empty or null");
			return false;
		}

		if (episodes == null || episodes.size() == 0) {
			Log.w(sTag, "Channel has no episodes");
			return false;
		}

		return result;
	}

	/**
	 * Adds a podcast to the database
	 * 
	 * @param url
	 */
	public boolean addChannel(String url) {
		boolean result = false;
		int episodesInserted;
		// lets see if we already have this channel in the db
		if (mChannelDS.getChannel(url) == null) {
			// parse the url
			AbstractMap.SimpleEntry<Channel, Map<String, Episode>> entry = parseChannel(url);
			// validate that the channel and episodes are of the correct format
			if (entry != null && validateChannel(entry)) {
				// insert the channel into the database
				long id = mChannelDS.insertChannel(entry.getKey());
				if (id > -1) {
					// insert the episodes into the database
					episodesInserted = mEpisodeDS.insertEpisodes(entry
							.getValue().values(), id);
					Log.d(sTag, "Episodes to insert: "
							+ entry.getValue().values().size());
					Log.d(sTag, "Episodes inserted: " + episodesInserted);
					result = episodesInserted > 0;
				}
			}
		}
		return result;
	}

	/**
	 * TODO this needs to be moved to an async task so we can update the UI
	 * after each podcast has been updated
	 */
	public void updateChannel() {
		// get a list of existing podcast channels
		List<Channel> channels = mChannelDS.getAllChannels(OrderBy.TITLE_ASC);
		for (Channel channel : channels) {
			// get a map of existing episodes
			Map<String, Episode> existingEpisodes = mEpisodeDS.getEpisodes(
					channel.getId(), EpisodeDataSource.OrderBy.PUBLISHED_DSC);
			// parse the podcast url
			AbstractMap.SimpleEntry<Channel, Map<String, Episode>> entries = parseChannel(channel
					.getUrl());
			if (entries != null && validateChannel(entries)) {
				Collection<Episode> parsedEpisodes = entries.getValue()
						.values();
				// lets compare existing and parsed episodes
				for (Episode episode : parsedEpisodes) {
					if (!existingEpisodes.containsKey(episode.getGuid())) {
						mEpisodeDS.insertEpisode(episode);
					}
				}
				// now lets up the update date
				channel.setUpdateDatetime(Calendar.getInstance()
						.getTimeInMillis());
				mChannelDS.updateChannel(channel);
			}
		}

	}

	/**
	 * Parses a podcast, will return null if there was an error while parsing
	 * 
	 * @param url
	 * @return
	 */
	public static AbstractMap.SimpleEntry<Channel, Map<String, Episode>> parseChannel(
			String url) {
		AbstractMap.SimpleEntry<Channel, Map<String, Episode>> entry = null;
		try {
			HttpURLConnection conn = HttpUtils.getUrlConnection(url);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			PodcastParser handler = new PodcastParser(url);
			InputSource is = new InputSource(conn.getInputStream());
			xr.setContentHandler(handler);
			xr.parse(is);
			entry = new SimpleEntry<Channel, Map<String, Episode>>(
					handler.getChannel(), handler.getEpisodes());
		} catch (IOException e) {
			Log.w(sTag, "There was an error while parsing a podcast");
			Log.w(sTag, e.getMessage());
		} catch (SAXException e) {
			Log.w(sTag, "There was an error while parsing a podcast");
			Log.w(sTag, e.getMessage());
		} catch (ParserConfigurationException e) {
			Log.w(sTag, "There was an error while parsing a podcast");
			Log.w(sTag, e.getMessage());
		}
		return entry;
	}
}

/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.engine.utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.emuneee.superb.engine.Channel;
import com.emuneee.superb.engine.Episode;

/**
 * SAX parser that parses podcast XML
 * 
 * @author Evan
 * 
 */
public class PodcastParser extends DefaultHandler {
	private static final String sTag = "PodcastParser";
	private Channel mChannel;
	private Map<String, Episode> mEpisodes;
	private Episode mCurrentItem;
	private boolean mGetVal;
	private boolean mInChannel;
	private boolean mInItem;
	private StringBuilder mValue = new StringBuilder();
	private DateParser mDateParser;
	
	public PodcastParser(String podcastUrl) {
		super();
		mChannel = new Channel();
		mEpisodes = new HashMap<String, Episode>();
		mChannel.setUrl(podcastUrl);
		mChannel.setUpdateDatetime(Calendar.getInstance().getTimeInMillis());
	}
	
	public Map<String, Episode> getEpisodes() {
		return mEpisodes;
	}
	
	public Channel getChannel() {
		return mChannel;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (mGetVal) {
			mValue.append(new String(ch, start, length));
		}
	}
	
	private void resetValue() {
		mValue.delete(0, mValue.length());
		mGetVal = false;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (qName.equalsIgnoreCase("channel")) {
			mInChannel = false;
		} else if (qName.equalsIgnoreCase("item")) {
			mInItem = false;
		}
		
		if (mInItem) {
			if (qName.equalsIgnoreCase("title")) {
				mCurrentItem.setTitle(mValue.toString());
			} else if (qName.equalsIgnoreCase("link")) {
				mCurrentItem.setUrl(mValue.toString());
			} else if (qName.equalsIgnoreCase("pubDate")) {
				if(mDateParser == null) {
					mDateParser = new DateParser(mValue.toString());
				}
				mCurrentItem.setPublishDatetime(mDateParser.parseDate(mValue.toString()));
			} else if (qName.equalsIgnoreCase("description")) {
				mCurrentItem.setDescription(mValue.toString());
			} else if (qName.equalsIgnoreCase("guid")) {
				mCurrentItem.setGuid(mValue.toString());
			}
		} else if (mInChannel) {
			if (qName.equalsIgnoreCase("title")) {
				mChannel.setTitle(mValue.toString());
			} else if (qName.equalsIgnoreCase("description")) {
				mChannel.setDescription(mValue.toString());
			}
		}
		resetValue();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		mGetVal = true;
		if (qName.equalsIgnoreCase("channel")) {
			mInChannel = true;
		} else if (qName.equalsIgnoreCase("item")) {
			mInItem = true;
			if (mCurrentItem != null) {
				if(mCurrentItem.getRemoteContentUrl() != null) {
					Log.v(sTag, "Adding newly parsed episode: " + mCurrentItem);
					mEpisodes.put(mCurrentItem.getGuid(), mCurrentItem);
				} else {
					Log.i(sTag, "Parsed episode doesn't contain a remote content url, skipping");
				}
			}
			mCurrentItem = new Episode();
		}
		
		if (mInChannel && !mInItem) {
			if (qName.equalsIgnoreCase("media:thumbnail")) {
				String url = attributes.getValue("url");
				mChannel.setImageUrl(url);
			} else if (qName.equalsIgnoreCase("itunes:image")) {
				String url = attributes.getValue("href");
				mChannel.setImageUrl(url);
			}
		} 
		
		if (mInItem) {
			if (localName.equalsIgnoreCase("enclosure")) {
				long contentSize = Long
						.parseLong(attributes.getValue("length"));
				String contentUrl = attributes.getValue("url");
				String type = attributes.getValue("type");
				mCurrentItem.setContentSize(contentSize);
				mCurrentItem.setContentUrl(contentUrl);
				mCurrentItem.setMimeType(type);
			}
		}
	}
}

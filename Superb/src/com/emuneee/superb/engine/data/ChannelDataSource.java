/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.engine.data;

import java.util.ArrayList;
import java.util.List;

import com.emuneee.superb.engine.Channel;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

/**
 * Abstracts channels and the underlying database
 * 
 * @author Evan
 * 
 */
public class ChannelDataSource extends DataSource {
	
	public enum OrderBy {
		TITLE_ASC("title ASC"),
		TITLE_DSC("title DESC"),
		UPDATED_ASC("updateDatetime ASC"),
		UPDATED_DSC("updateDatetime DESC");
		
		private String mOrderBy;
		
		OrderBy(String orderBy) {
			mOrderBy = orderBy;
		}
		
		String getOrderBy() { return mOrderBy; }
	}
	
	private static final String sTag = "ChannelDataSource";

	public ChannelDataSource(SuperbDBHelper helper) {
		super(helper);
		
	}

	/**
	 * Inserts a channel into the database
	 * 
	 * @param channel
	 * @return success of the insert operation
	 */
	public boolean updateChannel(Channel channel) {
		Log.v(sTag, "Updating channel in database: " + channel);
		long id = mDatabase.update("channel", channelToContentValues(channel),
				"_id = " + channel.getId(), null);
		return id != -1;
	}

	/**
	 * Inserts a channel into the database
	 * 
	 * @param channel
	 * @return id of inserted channel
	 */
	public long insertChannel(Channel channel) {
		Log.v(sTag, "Inserting channel into database: " + channel);
		long id = mDatabase.insert("channel", null,
				channelToContentValues(channel));
		return id;
	}
	
	/**
	 * Returns a channel with the specified url
	 * @param url
	 * @return channel
	 */
	public Channel getChannel(String url) {
		Channel channel = null;
		Cursor cursor = mDatabase.query("channel", null, "url = '" + url + "'",
				null, null, null, null);
		if (cursor.moveToFirst()) {
			channel = cursorToChannel(cursor);
		}
		cursor.close();
		return channel;
	}

	/**
	 * Returns a channel specified by the id
	 * 
	 * @param channelId
	 *            id of the channel to retrieve
	 * @return channel or null
	 */
	public Channel getChannel(long channelId) {
		Channel channel = null;
		Cursor cursor = mDatabase.query("channel", null, "_id = " + channelId,
				null, null, null, null);
		if (cursor.moveToFirst()) {
			channel = cursorToChannel(cursor);
		}
		cursor.close();
		return channel;
	}

	/**
	 * Returns a list of all channel objects
	 * 
	 * @param orderBy
	 * @return
	 */
	public List<Channel> getAllChannels(OrderBy orderBy) {
		List<Channel> channels = new ArrayList<Channel>();
		Cursor cursor = mDatabase.query("channel", null, null, null, null,
				null, orderBy.getOrderBy());

		while (cursor.moveToNext()) {
			channels.add(cursorToChannel(cursor));
		}
		cursor.close();
		return channels;
	}

	/**
	 * Converts a channel to a content values object
	 * 
	 * @param channel
	 * @return
	 */
	private static ContentValues channelToContentValues(Channel channel) {
		ContentValues values = new ContentValues();
		values.put("title", channel.getTitle());
		values.put("url", channel.getUrl());
		values.put("updateDatetime", channel.getUpdateDatetime());
		values.put("description", channel.getDescription());
		values.put("imageUrl", channel.getImageUrl());
		return values;
	}

	/**
	 * Creates a channel from a database cursor object
	 * 
	 * @param cursor
	 * @return
	 */
	private static Channel cursorToChannel(Cursor cursor) {
		Channel channel = new Channel(cursor.getLong(cursor
				.getColumnIndex("_id")), cursor.getString(cursor
				.getColumnIndex("title")), cursor.getString(cursor
				.getColumnIndex("url")), cursor.getLong(cursor
				.getColumnIndex("updateDatetime")), cursor.getString(cursor
				.getColumnIndex("description")), cursor.getString(cursor
				.getColumnIndex("imageUrl")));
		Log.v(sTag, "Channel retrieved from database: " + channel);
		return channel;
	}
}

/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.engine;

import java.util.Date;

import com.emuneee.superb.engine.data.Status;

/**
 * Channel episode
 * 
 * @author Evan
 * 
 */
public class Episode {
	private long mId = -1;
	private String mTitle;
	private String mUrl;
	private String mDescription;
	private long mPublishDatetime;
	private String mRemoteContentUrl;
	private String mLocalContentUrl;
	private String mImageUrl;
	private long mContentSize = -1;
	private int mContentDuration = -1;
	private long mChannelId = -1;
	private int mStatusId = 0;
	private String mMimeType;
	private String mGuid;
	private String mChannelTitle;
	
	public Episode() {
		super();
	}
	
	/**
	 * 
	 * @param id
	 * @param title
	 * @param url
	 * @param description
	 * @param publishDatetime
	 * @param contentUrl
	 * @param contentSize
	 * @param duration
	 * @param channelId
	 */
	public Episode(long id, String title, String url, String description,
			long publishDatetime, String contentUrl, long contentSize,
			int duration, long channelId, String mimeType, String guid) {
		super();
		this.mId = id;
		this.mTitle = title;
		this.mUrl = url;
		this.mDescription = description;
		this.mPublishDatetime = publishDatetime;
		this.mRemoteContentUrl = contentUrl;
		this.mContentSize = contentSize;
		this.mContentDuration = duration;
		this.mChannelId = channelId;
		this.mMimeType = mimeType;
		this.mGuid = guid;
	}
	
	/**
	 * 
	 * @param title
	 * @param url
	 * @param description
	 * @param publishDatetime
	 * @param contentUrl
	 * @param contentSize
	 * @param duration
	 * @param channelId
	 */
	public Episode(String title, String url, String description,
			long publishDatetime, String contentUrl, long contentSize,
			int duration, long channelId, String mimeType, String guid) {
		super();
		this.mId = -1;
		this.mTitle = title;
		this.mUrl = url;
		this.mDescription = description;
		this.mPublishDatetime = publishDatetime;
		this.mRemoteContentUrl = contentUrl;
		this.mContentSize = contentSize;
		this.mContentDuration = duration;
		this.mChannelId = channelId;
		this.mMimeType = mimeType;
		this.mGuid = guid;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return mId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return mUrl;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return mDescription;
	}

	/**
	 * @return the publishDatetime
	 */
	public long getPublishDatetime() {
		return mPublishDatetime;
	}

	/**
	 * @return the contentUrl
	 */
	public String getRemoteContentUrl() {
		return mRemoteContentUrl;
	}

	/**
	 * @return the contentSize
	 */
	public long getContentSize() {
		return mContentSize;
	}

	/**
	 * @return the duration
	 */
	public int getContentDuration() {
		return mContentDuration;
	}

	/**
	 * @return the channelId
	 */
	public long getChannelId() {
		return mChannelId;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		mId = id;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		mTitle = title;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		mUrl = url;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		mDescription = description;
	}

	/**
	 * @param publishDatetime the publishDatetime to set
	 */
	public void setPublishDatetime(long publishDatetime) {
		mPublishDatetime = publishDatetime;
	}

	/**
	 * @param contentUrl the contentUrl to set
	 */
	public void setContentUrl(String contentUrl) {
		mRemoteContentUrl = contentUrl;
	}

	/**
	 * @param contentSize the contentSize to set
	 */
	public void setContentSize(long contentSize) {
		mContentSize = contentSize;
	}

	/**
	 * @param contentDuration the contentDuration to set
	 */
	public void setContentDuration(int contentDuration) {
		mContentDuration = contentDuration;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId(long channelId) {
		mChannelId = channelId;
	}

	/**
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mMimeType;
	}

	/**
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		mMimeType = mimeType;
	}

	/**
	 * @return the guid
	 */
	public String getGuid() {
		return mGuid;
	}

	/**
	 * @param guid the guid to set
	 */
	public void setGuid(String guid) {
		mGuid = guid;
	}

	/**
	 * @return the statusId
	 */
	public int getStatusId() {
		return mStatusId;
	}

	/**
	 * @param statusId the statusId to set
	 */
	public void setStatusId(int statusId) {
		mStatusId = statusId;
	}
	
	public void setStatus(Status status) {
		mStatusId = status.ordinal();
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return mImageUrl;
	}

	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		mImageUrl = imageUrl;
	}
	
	public Status getStatus() {
		return Status.values()[mStatusId];
	}
	
	public String getPublishDatetimeStr() {
		return new Date(mPublishDatetime).toString();
	}

	/**
	 * @return the localContentUrl
	 */
	public String getLocalContentUrl() {
		return mLocalContentUrl;
	}

	/**
	 * @param localContentUrl the localContentUrl to set
	 */
	public void setLocalContentUrl(String localContentUrl) {
		mLocalContentUrl = localContentUrl;
	}
	
	/**
	 * @return the mChannelTitle
	 */
	public String getChannelTitle() {
		return mChannelTitle;
	}

	/**
	 * @param mChannelTitle the mChannelTitle to set
	 */
	public void setChannelTitle(String mChannelTitle) {
		this.mChannelTitle = mChannelTitle;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id = ").append(mId).append(",");
		sb.append("title = ").append(mTitle).append(",");
		sb.append("url = ").append(mUrl).append(",");
		//sb.append("description = ").append(mDescription).append(",");
		sb.append("publishDatetime = ").append(mPublishDatetime).append(",");
		sb.append("remoteContentUrl = ").append(mRemoteContentUrl).append(",");
		sb.append("localContentUrl = ").append(mLocalContentUrl).append(",");
		sb.append("contentSize = ").append(mContentSize).append(",");
		sb.append("contentDuration = ").append(mContentDuration).append(",");
		sb.append("channelId = ").append(mChannelId).append(",");
		sb.append("statusId = ").append(mStatusId).append(",");
		sb.append("mimeType = ").append(mMimeType).append(",");
		sb.append("guid = ").append(mGuid);
		return sb.toString();
	}
}
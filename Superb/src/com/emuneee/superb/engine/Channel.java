/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.engine;

/**
 * A podcast channel
 * 
 * @author Evan
 * 
 */
public class Channel {
	private long mId = -1;
	private String mTitle;
	private String mUrl;
	private long mUpdateDatetime;
	private String mDescription;
	private String mImageUrl;

	public Channel() {
		super();
	}
	
	/**
	 * 
	 * @param id
	 * @param name
	 * @param url
	 * @param updateDatetime
	 * @param description
	 * @param imageUrl
	 */
	public Channel(long id, String name, String url, long updateDatetime,
			String description, String imageUrl) {
		super();
		this.mId = id;
		this.mTitle = name;
		this.mUrl = url;
		this.mUpdateDatetime = updateDatetime;
		this.mDescription = description;
		this.mImageUrl = imageUrl;
	}
	
	public Channel(String name, String url, long updateDatetime,
			String description, String imageUrl) {
		super();
		this.mId = -1;
		this.mTitle = name;
		this.mUrl = url;
		this.mUpdateDatetime = updateDatetime;
		this.mDescription = description;
		this.mImageUrl = imageUrl;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return mId;
	}

	/**
	 * @return the name
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
	 * @return the updateDatetime
	 */
	public long getUpdateDatetime() {
		return mUpdateDatetime;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return mDescription;
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return mImageUrl;
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
	 * @param updateDatetime the updateDatetime to set
	 */
	public void setUpdateDatetime(long updateDatetime) {
		mUpdateDatetime = updateDatetime;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		mDescription = description;
	}

	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		mImageUrl = imageUrl;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("id = ").append(mId).append(",");
		sb.append("name = ").append(mTitle).append(",");
		sb.append("url = ").append(mUrl).append(",");
		sb.append("updateDatetime = ").append(mUpdateDatetime).append(",");
		sb.append("description = ").append(mDescription).append(",");
		sb.append("imageUrl = ").append(mImageUrl);
		return super.toString();
	}
	
	
}

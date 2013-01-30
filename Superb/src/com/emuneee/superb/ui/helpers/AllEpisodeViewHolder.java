/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.helpers;

import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Evan
 * 
 */
public class AllEpisodeViewHolder {
	private TextView mTextViewTitle;
	private ImageView mImageViewChannelArt;
	

	/**
	 * @return the textView
	 */
	public TextView getTextViewTitle() {
		return mTextViewTitle;
	}

	/**
	 * @param textView
	 *            the textView to set
	 */
	public void setTextViewTitle(TextView textView) {
		mTextViewTitle = textView;
	}

	/**
	 * @return the imageViewChannelArt
	 */
	public ImageView getImageViewChannelArt() {
		return mImageViewChannelArt;
	}

	/**
	 * @param imageViewChannelArt the imageViewChannelArt to set
	 */
	public void setImageViewChannelArt(ImageView imageViewChannelArt) {
		mImageViewChannelArt = imageViewChannelArt;
	}

}

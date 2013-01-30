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
public class ChannelViewHolder {
	private ImageView mImageView;
	private TextView mTextView;

	/**
	 * @return the imageView
	 */
	public ImageView getImageView() {
		return mImageView;
	}

	/**
	 * @param imageView
	 *            the imageView to set
	 */
	public void setImageView(ImageView imageView) {
		mImageView = imageView;
	}

	/**
	 * @return the textView
	 */
	public TextView getTextView() {
		return mTextView;
	}

	/**
	 * @param textView
	 *            the textView to set
	 */
	public void setTextView(TextView textView) {
		mTextView = textView;
	}

}

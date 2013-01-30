/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.helpers;

import android.widget.TextView;

/**
 * @author Evan
 * 
 */
public class EpisodeViewHolder {
	private TextView mTextViewTitle;
	private TextView mTextViewSubtitle;
	

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

	public TextView getTextViewSubtitle() {
		return mTextViewSubtitle;
	}

	public void setTextViewSubtitle(TextView mTextViewSubtitle) {
		this.mTextViewSubtitle = mTextViewSubtitle;
	}

}

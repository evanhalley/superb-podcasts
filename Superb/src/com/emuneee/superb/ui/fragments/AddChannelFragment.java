/**
 * 
 */
package com.emuneee.superb.ui.fragments;

import com.emuneee.superb.R;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author evan
 * 
 */
public class AddChannelFragment extends DialogFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL,
				android.R.style.Theme_Holo_Light_Dialog);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_add_channel, container,
				false);
		getDialog().setTitle(R.string.title_add_channel_dialog);
		return v;
	}
}

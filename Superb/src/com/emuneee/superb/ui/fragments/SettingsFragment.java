/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.fragments;

import com.emuneee.superb.R;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

/**
 * @author Evan
 * 
 */
public class SettingsFragment extends PreferenceFragment implements
		OnPreferenceClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preference_main);
	}

	@Override
	public void onResume() {
		super.onResume();
		Context context = getActivity();

		findPreference(context.getString(R.string.title_appearance))
				.setOnPreferenceClickListener(this);
		findPreference(context.getString(R.string.title_syncing))
				.setOnPreferenceClickListener(this);
		findPreference(context.getString(R.string.title_storage))
				.setOnPreferenceClickListener(this);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		int titleResId = preference.getTitleRes();
		Fragment fragment = null;
		String tag = null;

		switch (titleResId) {
		case R.string.title_appearance:
			fragment = new AppearanceSettingsFragment();
			tag = AppearanceSettingsFragment.TAG;
			break;
		case R.string.title_syncing:
			fragment = new SyncSettingsFragment();
			tag = SyncSettingsFragment.TAG;
			break;
		case R.string.title_storage:
			fragment = new StorageSettingsFragment();
			tag = StorageSettingsFragment.TAG;
			break;
		}

		if (fragment != null) {
			FragmentManager manager = getActivity().getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(android.R.id.content, fragment, tag);
			transaction.addToBackStack(null);
			transaction.commit();
		}

		return true;
	}

}

/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.fragments;

import com.emuneee.superb.R;
import com.emuneee.superb.engine.data.ChannelDataSource.OrderBy;
import com.emuneee.superb.ui.MainActivity;
import com.emuneee.superb.ui.bitmap.ImageWorker;
import com.emuneee.superb.ui.helpers.MenuViewAdapter;
import com.emuneee.superb.ui.helpers.Const;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Evan
 * 
 */
public class MenuViewFragment extends Fragment implements OnItemClickListener {
	public static final String TAG = "MenuViewFragment";
	protected AdapterView mAdapterView;
	protected MenuViewAdapter mAdapter;
	protected ImageWorker mImageWorker;
	protected SlidingMenu mSlidingMenu;
	protected OrderBy mOrderBy = OrderBy.TITLE_ASC;
	protected MainActivity mMainActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mMainActivity = (MainActivity) getActivity();
		mSlidingMenu = ((SlidingActivity) getActivity()).getSlidingMenu();
		// get the sort preference
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mOrderBy = OrderBy.values()[pref.getInt(Const.PREF_CHANNEL_ORDER_BY,
				OrderBy.TITLE_ASC.ordinal())];
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_list, container, false);
		mAdapterView = (ListView) view.findViewById(R.id.list_view_channels);
		mAdapter = new MenuViewAdapter(mMainActivity, mMainActivity
				.getDataEngine().getChannelDataSource()
				.getAllChannels(mOrderBy), R.layout.channel_list_item);
		mAdapterView.setAdapter(mAdapter);
		mAdapterView.setOnItemClickListener(this);
		return view;
	}

	public void refreshMenu() {
		mAdapter.clear();
		mAdapter.addAll(mMainActivity.getDataEngine().getChannelDataSource()
				.getAllChannels(mOrderBy));
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		Fragment fragment = null;
		String fragmentTag = null;

		if (position == MenuViewAdapter.PLAYLIST_IDX) {
			// load playlist fragment
		} else if (position == MenuViewAdapter.ALL_EPISODES_IDX) {
			// load all episodes fragment
			fragmentTag = AllEpisodesListFragment.TAG;
			fragment = new AllEpisodesListFragment();
		} else {
			// load the list of episodes for this channel
			fragmentTag = EpisodeListFragment.TAG;
			fragment = new EpisodeListFragment();
			Bundle args = new Bundle();
			args.putLong(Const.BUNDLE_CHANNEL_ID,
					mAdapter.getItem(position - MenuViewAdapter.OFFSET).getId());
			fragment.setArguments(args);
		}

		if (fragment != null) {
			FragmentManager manager = getActivity().getFragmentManager();
			FragmentTransaction transaction = manager.beginTransaction();
			transaction.replace(R.id.fragment_container, fragment, fragmentTag);
			transaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			transaction.commit();
			mSlidingMenu.showContent();
		}
	}
}

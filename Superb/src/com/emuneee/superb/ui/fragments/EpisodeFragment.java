/**
 * Copyright (C) 2012 Evan Halley
 * emuneee apps
 */
package com.emuneee.superb.ui.fragments;

import java.util.ArrayList;
import java.util.List;

import com.emuneee.superb.R;
import com.emuneee.superb.engine.Episode;
import com.emuneee.superb.engine.data.EpisodeDataSource.OrderBy;
import com.emuneee.superb.engine.utils.DownloadHelper.QueueEpisodesTask;
import com.emuneee.superb.ui.MainActivity;
import com.emuneee.superb.ui.SettingsActivity;
import com.emuneee.superb.ui.helpers.Const;
import com.emuneee.superb.ui.helpers.ImportChannelDialogManager;
import com.emuneee.superb.ui.helpers.ImportChannelDialogManager.ImportChannelListener;
import com.emuneee.superb.ui.tasks.ImportChannelTask;
import com.emuneee.superb.ui.tasks.TaskListener;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivity;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

/**
 * Shows a list of episodes
 * 
 * @author Evan
 * 
 */
public abstract class EpisodeFragment extends Fragment implements
		OnItemClickListener, OnItemLongClickListener, ImportChannelListener {
	public static final String TAG = "EpisodeListFragment";
	protected ArrayAdapter<Episode> mAdapter;
	protected AdapterView<ListAdapter> mAdapterView;
	protected long mChannelId;
	protected SlidingMenu mSlidingMenu;
	protected OrderBy mOrderBy = OrderBy.PUBLISHED_DSC;
	protected MainActivity mMainActivity;

	public abstract void update();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mSlidingMenu = ((SlidingActivity) getActivity()).getSlidingMenu();
		// get the sort preference
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mOrderBy = OrderBy.values()[pref.getInt(Const.PREF_EPISODE_ORDER_BY,
				OrderBy.PUBLISHED_DSC.ordinal())];
		mMainActivity = (MainActivity) getActivity();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view,
			final int position, long arg3) {
		final Context context = getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		// TODO reduce dialog list depending on episode status
		builder.setItems(R.array.episode_long_press,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						switch (id) {
						case 0:
							// TODO add to playlist
							break;
						case 1:
							// TODO delete episode
							break;
						case 2:
							QueueEpisodesTask task = mMainActivity
									.getDownloadHelper().new QueueEpisodesTask();
							List<Episode> episode = new ArrayList<Episode>();
							episode.add(mAdapter.getItem(position));
							task.execute(episode);
							break;
						}
					}
				});
		builder.create().show();
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		FragmentManager manager = getActivity().getFragmentManager();
		PlayerFragment fragment = (PlayerFragment) manager
				.findFragmentByTag(PlayerFragment.TAG);
		fragment.setCurrentEpisode(mAdapter.getItem(position).getId());
		mSlidingMenu.showSecondaryMenu();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.fragment_episode, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent intent = new Intent(getActivity(), SettingsActivity.class);
			startActivity(intent);
			break;
		case R.id.menu_refresh:
			update();
			break;
		case R.id.menu_add_channel:
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			Fragment prev = getFragmentManager().findFragmentByTag("dialog");
			if (prev != null) {
				ft.remove(prev);
			}
			ft.addToBackStack(null);

			// Create and show the dialog.
			DialogFragment newFragment = new AddChannelFragment();
			newFragment.show(ft, "dialog");
			
			//ImportChannelDialogManager
			//		.showChannelUrlDialog(getActivity(), this);
			break;
		}

		return false;
	}

	@Override
	public void onChannelImport(String channelUrl) {
		ImportChannelTask task = new ImportChannelTask(mMainActivity,
				channelUrl, new AddChannelTaskListener(
						(MainActivity) getActivity()));
		task.execute();
	}

	private class AddChannelTaskListener implements TaskListener<Boolean> {
		private MainActivity mMainActivity;

		public AddChannelTaskListener(MainActivity mainActivity) {
			mMainActivity = mainActivity;
		}

		@Override
		public void onPostExecute(Boolean result) {
			if (result) {
				mMainActivity.getMenuViewFragment().refreshMenu();
			}
		}
	}
}
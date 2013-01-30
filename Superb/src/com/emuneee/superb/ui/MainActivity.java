package com.emuneee.superb.ui;

import java.util.Calendar;

import com.emuneee.superb.R;
import com.emuneee.superb.engine.SuperbDataEngine;
import com.emuneee.superb.engine.utils.DownloadHelper;
import com.emuneee.superb.receivers.UpdateReceiver;
import com.emuneee.superb.services.PlayerService;
import com.emuneee.superb.services.PlayerService.State;
import com.emuneee.superb.ui.bitmap.ImageCache;
import com.emuneee.superb.ui.bitmap.ImageCache.ImageCacheParams;
import com.emuneee.superb.ui.bitmap.ImageFetcher;
import com.emuneee.superb.ui.bitmap.ImageWorker;
import com.emuneee.superb.ui.fragments.AllEpisodesListFragment;
import com.emuneee.superb.ui.fragments.EpisodeFragment;
import com.emuneee.superb.ui.fragments.MenuViewFragment;
import com.emuneee.superb.ui.fragments.PlayerFragment;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivity;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Initializes application startup
 * 
 * @author Evan
 * 
 */
public class MainActivity extends SlidingActivity {
	private static final String sTag = "MainActivity";
	private static final String sCacheDir = "/image-cache";
	private static final int sReceiverId = 89324;
	private SuperbDataEngine mDataEngine;
	private ImageWorker mImageWorker;
	private ImageCacheParams mCacheParams;
	private PlayerService mPlayerService;
	private DownloadHelper mDownloadHelper;
	private boolean mIsBound;
	private PlayerFragment mPlayerFragment;
	private MenuViewFragment mMenuViewFragment;
	private EpisodeFragment mEpisodeFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setupUpdaterReceiver();
		mDataEngine = new SuperbDataEngine(this);
		mDownloadHelper = new DownloadHelper(this);
		mCacheParams = new ImageCacheParams(sCacheDir);
		mImageWorker = new ImageFetcher(this, 400);
		mImageWorker.setImageCache(ImageCache.findOrCreateCache(this,
				mCacheParams));
		// bind to our player service
		doBindService();

		// configure the sliding menu
		setBehindContentView(R.layout.menu_frame_main);
		SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// configure left menu
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setShadowDrawable(R.drawable.shadow);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		// configure right menu
		slidingMenu.setSecondaryMenu(R.layout.menu_frame_player);
		slidingMenu.setSecondaryShadowDrawable(R.drawable.shadow_player);
		slidingMenu.setFadeDegree(0.35f);
		instantiateFragments(null);
	}

	/**
	 * Instantiates the three primary fragments (brand new or from the in state
	 * bundle)
	 * 
	 * @param inState
	 */
	private void instantiateFragments(Bundle inState) {
		FragmentManager manager = getFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();

		if (inState != null) {
			mPlayerFragment = (PlayerFragment) manager.getFragment(inState,
					PlayerFragment.TAG);
			mMenuViewFragment = (MenuViewFragment) manager.getFragment(inState,
					MenuViewFragment.TAG);
			mEpisodeFragment = (AllEpisodesListFragment) manager.getFragment(
					inState, AllEpisodesListFragment.TAG);
		} else {
			mPlayerFragment = new PlayerFragment();
			mMenuViewFragment = new MenuViewFragment();
			mEpisodeFragment = new AllEpisodesListFragment();
			// add list of podcasts to main sliding menu
			transaction.add(R.id.menu_frame_main, mMenuViewFragment,
					MenuViewFragment.TAG);
			// add player fragment to player sliding menu
			transaction.add(R.id.menu_frame_player, mPlayerFragment,
					PlayerFragment.TAG);
			// finally load the last viewed fragment
			transaction.add(R.id.fragment_container, mEpisodeFragment,
					AllEpisodesListFragment.TAG);
			transaction.commit();
		}
	}

	@Override
	protected void onRestoreInstanceState(Bundle inState) {
		instantiateFragments(inState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		FragmentManager manager = getFragmentManager();
		manager.putFragment(outState, AllEpisodesListFragment.TAG,
				mEpisodeFragment);
		manager.putFragment(outState, PlayerFragment.TAG, mPlayerFragment);
		manager.putFragment(outState, MenuViewFragment.TAG, mMenuViewFragment);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		doUnbindService();
		mDownloadHelper.destroy();
		mDataEngine.close();
	}

	private void setupUpdaterReceiver() {
		Intent intent = new Intent(this, UpdateReceiver.class);
		int refreshIntervalMultiplier = Integer.parseInt(PreferenceManager
				.getDefaultSharedPreferences(this).getString(
						getString(R.string.key_sync_interval), "4"));
		PendingIntent sender = PendingIntent.getBroadcast(
				getApplicationContext(), sReceiverId, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, Calendar
				.getInstance().getTimeInMillis()
				+ AlarmManager.INTERVAL_FIFTEEN_MINUTES
				* refreshIntervalMultiplier,
				AlarmManager.INTERVAL_FIFTEEN_MINUTES
						* refreshIntervalMultiplier, sender);
	}

	public MenuViewFragment getMenuViewFragment() {
		return mMenuViewFragment;
	}

	public EpisodeFragment getEpisodeFragment() {
		return mEpisodeFragment;
	}

	public ImageWorker getImageWorker() {
		return mImageWorker;
	}

	public SuperbDataEngine getDataEngine() {
		return mDataEngine;
	}

	public PlayerService getPlayerService() {
		return mPlayerService;
	}

	public DownloadHelper getDownloadHelper() {
		return mDownloadHelper;
	}

	// SERVICE RELATED FUNCTIONS

	private void doBindService() {
		Intent playerIntent = new Intent(this, PlayerService.class);
		startService(playerIntent);
		Intent updaterIntent = new Intent(this, DownloadHelper.class);
		startService(updaterIntent);
		// Establish a connection with the service. We use an explicit
		// class name because we want a specific service implementation that
		// we know will be running in our own process (and thus won't be
		// supporting component replacement by other applications).
		bindService(playerIntent, mPlayerSvcConnection, BIND_AUTO_CREATE);
		mIsBound = true;
	}

	private void doUnbindService() {
		if (mIsBound) {
			// Detach our existing connection.
			unbindService(mPlayerSvcConnection);
			mIsBound = false;
			State state = mPlayerService.getState();
			if (state == State.Paused) {
				Log.d(sTag,
						"Service has been unbinded and player paused, lets die!");
				mPlayerService.stop();
				stopService(new Intent(this, PlayerService.class));
			} else if (state == State.Uninitialized || state == State.Stopped) {
				Log.d(sTag,
						"Service has been unbinded and player uninitialized/stopped, lets die!");
				stopService(new Intent(this, PlayerService.class));
			}
			mPlayerService = null;
		}
	}

	private ServiceConnection mPlayerSvcConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			// This is called when the connection with the service has been
			// established, giving us the service object we can use to
			// interact with the service. Because we have bound to a explicit
			// service that we know is running in our own process, we can
			// cast its IBinder to a concrete class and directly access it.
			mPlayerService = ((PlayerService.LocalBinder) service).getService();
			mPlayerService.setImageCache(mImageWorker.getImageCache());
			mPlayerFragment.registerPlayerService(mPlayerService);
		}

		public void onServiceDisconnected(ComponentName className) {
			// This is called when the connection with the service has been
			// unexpectedly disconnected -- that is, its process crashed.
			// Because it is running in our same process, we should never
			// see this happen.
			mPlayerService = null;
		}
	};
}
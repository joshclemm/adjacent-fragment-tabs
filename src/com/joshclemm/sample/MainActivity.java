package com.joshclemm.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

/**
 * Manage two fragments with different display modes. In portrait, displayed
 * with a TabHost and ViewPager. In landscape, displayed side-by-side.
 */
public class MainActivity extends FragmentActivity {

	private static String TAG_ONE = "one";
	private static String TAG_TWO = "two";
	private ScreenFragment mFragmentOne;
	private ScreenFragment mFragmentTwo;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initFragments();
		
		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		if (viewPager != null) {
			// Portrait. Fragments as 2 tabs.
			TabHost mTabHost = (TabHost) findViewById(R.id.tabhost);
			mTabHost.setup();
			
			TabsPagerAdapter adapter = new TabsPagerAdapter(this, mTabHost, viewPager);
			adapter.addTab(mTabHost.newTabSpec("one").setIndicator("One"), mFragmentOne, TAG_ONE);
			adapter.addTab(mTabHost.newTabSpec("two").setIndicator("Two"), mFragmentTwo, TAG_TWO);
			
			
		} else {
			// Landscape. Same fragments side by side.
			getSupportFragmentManager().beginTransaction()
			.add(R.id.pane_one, mFragmentOne, TAG_ONE)
			.add(R.id.pane_two, mFragmentTwo, TAG_TWO)
			.commit();
		}
	}
	
	private void initFragments() {
		
		FragmentManager fragmentManager = getSupportFragmentManager();

		mFragmentOne = (ScreenFragment) fragmentManager.findFragmentByTag(TAG_ONE);
		mFragmentTwo = (ScreenFragment) fragmentManager.findFragmentByTag(TAG_TWO);

		FragmentTransaction remove = fragmentManager.beginTransaction();
		if (mFragmentOne == null) {
			mFragmentOne = ScreenFragment.newInstance(R.layout.fragment_one);
		}
		else {
			remove.remove(mFragmentOne);
		}
		if (mFragmentTwo == null) {
			mFragmentTwo = ScreenFragment.newInstance(R.layout.fragment_two);
		}
		else {
			remove.remove(mFragmentTwo);
		}
		if (!remove.isEmpty()) {
			remove.commit();
			fragmentManager.executePendingTransactions();
		}
	}
	
	public static class ScreenFragment extends Fragment {

	    public static ScreenFragment newInstance(int layout) {
	    	ScreenFragment fragment = new ScreenFragment();
	    	Bundle args = new Bundle();
	    	args.putInt("layout", layout);
	    	fragment.setArguments(args);
	        return fragment;
	    }

		private int mLayout;
	    
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	mLayout = getArguments().getInt("layout");
	    	super.onCreate(savedInstanceState);
	    }
	    
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			return inflater.inflate(mLayout, container, false);
		}

	}
}

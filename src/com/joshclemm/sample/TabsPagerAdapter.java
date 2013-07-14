package com.joshclemm.sample;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

public class TabsPagerAdapter extends PagerAdapter implements TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
	
	private final FragmentManager fragmentManager;
	private final List<Fragment> fragmentList;
	private final List<String> fragmentTagList;
	
	private FragmentTransaction currentTransaction = null;
	private Fragment currentPrimaryItem = null;

	private final Context mContext;
	private final TabHost mTabHost;
	private final ViewPager mViewPager;

	public TabsPagerAdapter(FragmentActivity activity, TabHost tabHost,
			ViewPager pager) {
		fragmentManager = activity.getSupportFragmentManager();
		fragmentList = new ArrayList<Fragment>();
		fragmentTagList = new ArrayList<String>();
		mContext = activity;
		
		mTabHost = tabHost;
		mViewPager = pager;
		mTabHost.setOnTabChangedListener(this);
		mViewPager.setAdapter(this);
		mViewPager.setOnPageChangeListener(this);
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (currentTransaction == null) {
			currentTransaction = fragmentManager.beginTransaction();
		}

		String tag = fragmentTagList.get(position);
		Fragment fragment = fragmentList.get(position);
		
		currentTransaction.add(container.getId(), fragment, tag);
		if (fragment != currentPrimaryItem) {
			fragment.setMenuVisibility(false);
			fragment.setUserVisibleHint(false);
		}

		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// With two pages, fragments should never be destroyed.
		// With more than 2, need to address this, or not if using viewPager.setOffscreenPageLimit(n); 
		throw new AssertionError();
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position,
			Object object) {
		Fragment fragment = (Fragment) object;
		if (fragment != currentPrimaryItem) {
			if (currentPrimaryItem != null) {
				currentPrimaryItem.setMenuVisibility(false);
				currentPrimaryItem.setUserVisibleHint(false);
			}
			if (fragment != null) {
				fragment.setMenuVisibility(true);
				fragment.setUserVisibleHint(true);
			}
			currentPrimaryItem = fragment;
		}
	}

	@Override
	public void finishUpdate(ViewGroup container) {
		if (currentTransaction != null) {
			currentTransaction.commitAllowingStateLoss();
			currentTransaction = null;
			fragmentManager.executePendingTransactions();
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return ((Fragment) object).getView() == view;
	}
	
	public void addTab(TabHost.TabSpec tabSpec, Fragment fragment, String fragmentTag) {
		fragmentList.add(fragment);
		fragmentTagList.add(fragmentTag);
		tabSpec.setContent(new DummyTabFactory(mContext));
		mTabHost.addTab(tabSpec);
		notifyDataSetChanged();
	}

	@Override
	public void onTabChanged(String tabId) {
		int position = mTabHost.getCurrentTab();
		if (mViewPager.getCurrentItem() != position) {
			mViewPager.setCurrentItem(position);
		}
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
	}

	@Override
	public void onPageSelected(int position) {
		// Unfortunately when TabHost changes the current tab, it kindly
		// also takes care of putting focus on it when not in touch mode.
		// The jerk.
		// This hack tries to prevent this from pulling focus out of our
		// ViewPager.
		TabWidget widget = mTabHost.getTabWidget();
		int oldFocusability = widget.getDescendantFocusability();
		widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		mTabHost.setCurrentTab(position);
		widget.setDescendantFocusability(oldFocusability);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
	}
	
	class DummyTabFactory implements TabHost.TabContentFactory {
		private final Context mContext;

		public DummyTabFactory(Context context) {
			mContext = context;
		}

		@Override
		public View createTabContent(String tag) {
			View v = new View(mContext);
			v.setMinimumWidth(0);
			v.setMinimumHeight(0);
			return v;
		}
	}
}
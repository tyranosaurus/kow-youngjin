package com.estsoft.mblockchain.kow.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.estsoft.mblockchain.kow.R;
import com.estsoft.mblockchain.kow.fragments.CompleteFragment;
import com.estsoft.mblockchain.kow.fragments.FavoriteFragment;
import com.estsoft.mblockchain.kow.fragments.MyPageFragment;
import com.estsoft.mblockchain.kow.fragments.WaitingFragment;


public class MainPagerAdapter extends FragmentPagerAdapter {

	public static final int NUM_ITEMS = 4;
	public static final int COMPLETE_POS = 0;
	public static final int WAITING_POS = 1;
	public static final int FAVORITES_POS = 2;
	public static final int MYPAGE_POS = 3;

	private Context context;

	public MainPagerAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case COMPLETE_POS:
				return CompleteFragment.newInstance();
			case WAITING_POS:
				return WaitingFragment.newInstance();
			case FAVORITES_POS:
				return FavoriteFragment.newInstance();
			case MYPAGE_POS:
				return MyPageFragment.newInstance();
			default:
				return null;
		}
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case COMPLETE_POS:
				return context.getString(R.string.complete);
			case WAITING_POS:
				return context.getString(R.string.waiting);
			case FAVORITES_POS:
				return context.getString(R.string.favorite);
			case MYPAGE_POS:
				return context.getString(R.string.mypage);
			default:
				return "";
		}
	}

	@Override
	public int getCount() {
		return NUM_ITEMS;
	}
}

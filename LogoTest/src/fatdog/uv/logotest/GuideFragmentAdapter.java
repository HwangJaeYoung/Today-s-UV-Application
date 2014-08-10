package fatdog.uv.logotest;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class GuideFragmentAdapter extends FragmentStatePagerAdapter {
	// 여기에 이미지 리소스를 넣어주면 됨.
	protected static final int[] CONTENT = new int[] { R.drawable.info1, R.drawable.info2, R.drawable.info3, R.drawable.info4, R.drawable.info5 };

	private int mCount = CONTENT.length;

	public GuideFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	// fragment 생성부분
	@Override
	public Fragment getItem(int position) {
		return GuideFragment.newInstance(CONTENT[position % CONTENT.length]);
	}

	@Override
	public int getCount() {
		return mCount;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return null;
	}
}
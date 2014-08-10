package fatdog.uv.logotest;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class GuideViewpager extends FragmentActivity {
	GuideFragmentAdapter mAdapter;
	ViewPager mPager;
	PageIndicator mIndicator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_circles);

		mAdapter = new GuideFragmentAdapter(getSupportFragmentManager());

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);

		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator = indicator;
		indicator.setViewPager(mPager);

		final float density = getResources().getDisplayMetrics().density;

		int black = Color.parseColor("#222222");
		int white = Color.parseColor("#FFFFFF");

		indicator.setBackgroundColor(black);// 맨밑 감싸고있는 한줄 색깔 없음으로
		indicator.setRadius(3.6f * density);// stroke 의 제곱 값이 되야함
		// indicator.setPageColor(yellow);//고정된 5개의 원 색깔
		indicator.setFillColor(white);// 움직이는 원 색깔(채워줌)
		indicator.setStrokeColor(white);// 원을 감싸고있는 둘레
		indicator.setStrokeWidth(0.6f * density);
	}

	@Override
	public void finish() {
		super.finish();
		// 종료될 때 사용 하는 애니메이션
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}
}
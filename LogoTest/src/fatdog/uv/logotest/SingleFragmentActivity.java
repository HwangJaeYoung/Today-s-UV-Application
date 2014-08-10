package fatdog.uv.logotest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.view.WindowManager;

public abstract class SingleFragmentActivity extends FragmentActivity {
	protected abstract Fragment createFragment();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// 액션바를 없애기 위해서 선언
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 해당 프래그먼트를 인플레이트
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_container);
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

		if (fragment == null) {
			fragment = createFragment();
			fm.beginTransaction().add(R.id.fragmentContainer, fragment)
					.commit();
		}
	}

	// 해당 액티비티가 종료 될 때
	@Override
	public void finish() {
		super.finish();
		// 종료될 때 사용 하는 애니메이션
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}
}
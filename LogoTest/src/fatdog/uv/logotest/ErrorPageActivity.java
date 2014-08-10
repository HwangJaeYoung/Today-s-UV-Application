package fatdog.uv.logotest;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kakao.GlobalApplication;

public class ErrorPageActivity extends Activity {
	private BackPressCloseHandler backPressCloseHandler;
	private String android_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 액션바를 없애기 위해서 선언
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noinfomation_layout);
		backPressCloseHandler = new BackPressCloseHandler(this);
		
		Typeface type = Typeface.createFromAsset(getAssets(),"sandol-light.otf");
		TextView text = (TextView)findViewById(R.id.errorText);
		text.setTypeface(type);
		
		// 구글 애널리틱스 사용을 위해 구성하는 중...
		android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);

		if (!(android_id.equals("fff44b9db8f6e742") || android_id.equals("67fb4b171ac37355"))) {
			Tracker t = ((GlobalApplication) getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
			t.setScreenName("ErrorPageActivity");
			t.send(new HitBuilders.AppViewBuilder().build());
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		// 구글 애널리틱스 사용을 위한 시작
		// Activity의 사용자가 어떤 액션을 진행하는지 추적을 시작하는 코드이고
		GoogleAnalytics.getInstance(this).reportActivityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		// 구글 애널리틱스 보고를 위해
		// Activity의 추적을 끝내는 코드입니다.
		GoogleAnalytics.getInstance(this).reportActivityStop(this);
	}

	@Override
	public void onBackPressed() {
		backPressCloseHandler.onBackPressed();
	}
}
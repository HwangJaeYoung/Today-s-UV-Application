package fatdog.uv.logotest;

import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kakao.GlobalApplication;

public class A_Main extends SingleFragmentActivity {
	private BackPressCloseHandler backPressCloseHandler;
	private String android_id;

	@Override
	public Fragment createFragment() {
		return new F_Main();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		backPressCloseHandler = new BackPressCloseHandler(this);
		// 구글 애널리틱스 사용을 위해 구성하는 중...

		android_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		
		if (!(android_id.equals("fff44b9db8f6e742") || android_id.equals("67fb4b171ac37355"))) {
			Tracker t = ((GlobalApplication) getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
			t.setScreenName("A_Main");
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

	// 오후 12시 40분에 한 번만 파싱하면 된다.
	@Override
	public void onResume() {
		super.onResume();

		// 한번만 실행하게 하려고 만든 것
		// 최초에는 init에 값이 없기 때문에 false를 던지고
		// 그 다음부터는 true를 주기 때문에 한번만 실행한다.
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Boolean init = prefs.getBoolean("init", false);

		if (init == false) {

			Intent intent = null;

			AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

			intent = new Intent(this, NotiService.class); // 파싱을 가능하게 하는 서비스호출
			PendingIntent serviceSender = PendingIntent.getService(this, 0, intent, 0);

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY, 12); // 12시
			calendar.set(Calendar.MINUTE, 45); // 45분에 알람을 시작한다. <-안전빵

			// 주기는 24시간 단위이다. 푸쉬떄리기전 한번만 호출 하면 된다.
			am.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),24 * 60 * 60 * 1000, serviceSender);

			// 한 번만 실행하기 위해서 true를 주는 코드
			prefs = PreferenceManager.getDefaultSharedPreferences(this);
			prefs.edit().putBoolean("init", true).apply();
		}
	}

	@Override
	public void onBackPressed() {
		backPressCloseHandler.onBackPressed();
	}
}
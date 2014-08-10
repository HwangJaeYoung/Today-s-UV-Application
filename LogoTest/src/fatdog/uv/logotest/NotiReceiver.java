package fatdog.uv.logotest;

import java.util.Calendar;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class NotiReceiver extends BroadcastReceiver {
	private NotificationManager mNM;
	private Notification mNoti;
	private PowerManager mPM;
	private PowerManager.WakeLock mWakeLock;

	@Override
	public void onReceive(Context context, Intent intent) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int hour = calendar.get(Calendar.HOUR_OF_DAY); // 13시
		int minute = calendar.get(Calendar.MINUTE); // 10분에 알람을 시작한다.

		if (hour == 13 && minute < 10) {

			mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			// notification메세지를 눌렸을 때 앱을 실행시키지 않고
			// 그냥 상세 메시지를 제거하는 식으로만 한다.
			Intent startIntent = new Intent();

			PendingIntent mPendingIntent = PendingIntent.getActivity(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

			// NotiService에서 파싱한 데이터를 저장한다.
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			String serviceValue = prefs.getString("serviceParsing", "none");

			int serviceInt = Integer.parseInt(serviceValue);
			ReturnUVSPF uvText = new ReturnUVSPF();
			String uvMessage = uvText.returnSPFText(serviceInt);

			// notification메시지 구성
			mNoti = new NotificationCompat.Builder(context)
					.setContentTitle("오늘의 자외선 지수 : " + serviceValue + "단계")
					.setContentText(uvMessage).setSmallIcon(R.drawable.appicon)
					.setTicker("오늘의자외선").setAutoCancel(true)
					.setContentIntent(mPendingIntent).build();

			mNoti.vibrate = new long[] { 100, 200, 300, 400 }; // 진동을 울린다.

			mNM.notify(7777, mNoti); // notification 발생

			// 자고있는 디바이스를 깨우기 위한 구성
			mPM = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			mWakeLock = mPM.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
					| PowerManager.FULL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, "MY ALWAYS ON TAG");
			mWakeLock.acquire(5 * 1000); // 5초 후에 디바이스를 절전시킨다.
		}
	}
}
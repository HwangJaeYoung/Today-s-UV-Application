package fatdog.uv.logotest;

import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

// 디바이스를 부팅시켰을 때 최초로 실행하는 브로드캐스트

public class NotiBroadcast extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		// UserSetting에서 푸쉬 온/오프가 가능한데 그 때 저장된 값을 가지고 온다.
		Boolean pushOnOff = prefs.getBoolean("PushOnOff", false);
		
		if (pushOnOff == true) { // 푸쉬를 사용한다고 했을 때
			Intent intentBoot = null;
			PendingIntent sender;
			AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

			// notification메세지를 실행하기위해 브로드캐스트 리시버 호출
			intentBoot = new Intent(context, NotiReceiver.class);
			sender = PendingIntent.getBroadcast(context, 0, intentBoot, 0);
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY, 13); // 오전 11시
			calendar.set(Calendar.MINUTE, 02); // 정각에 푸쉬

			am.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, sender);
		}
	}
}
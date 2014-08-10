package fatdog.uv.logotest;

import java.util.Calendar;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kakao.GlobalApplication;

import de.passsy.holocircularprogressbar.HoloCircularProgressBar;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainView {
	// 둥근 그래프를 위해
	private HoloCircularProgressBar mHoloCircularProgressBar_Today;
	private HoloCircularProgressBar mHoloCircularProgressBar_Tomorrow;
	private HoloCircularProgressBar mHoloCircularProgressBar_After_Tomorrow;
	private ObjectAnimator mProgressBarAnimator;

	private FrameLayout mSunBackground; // 고정 배경
	
	// 각종 지수를 환산하기 위해서
	private ReturnUVSPF returnUS;
	private LocalSearch locSearch;

	// 지역
	private TextView mLocalName; // 지역의 이름
	private TextView mTodayMessage; // 위험 상황을 보여줌
	private TextView mTodayRating; // 등급으로 위험상황을 보여줌
	private TextView mSpfTodayValue;// 오늘의 spf값
	private TextView mSpfTodayBehavior; // 오늘의 자외선 지수에 대한 spf

	// 내일
	private TextView mTomorrowSpfRating; // 내일 자외선 값
	private TextView mTomorrowTextRating; // 내일 자외선 등급
	private TextView mTomorrowSpfValue; // 내일 자외선 spf지수

	// 모레
	private TextView mAfterTomorrowSpfRating; // 내일 모래 자외선 값(3)
	private TextView mAfterTomorrowTextRating; // 내일 모레 자외선 등급(보통)
	private TextView mAfterTomorrowSpfValue; // 내일모레 자외선 spf지수(15-20)

	// 문자열로 반환되는 값을 알맞게 변환시켜주기위한 임시변수
	private float mUvToday;
	private float mUvTomorrow;
	private float mUvAfterTomorrow;
	int uvTomorrowNum;
	int uvAfterTomorrowNum;

	private Button mUserSetting; // 설정버튼
	private RelativeLayout localSearch; // 지역검색
	private String finalLocation; // 최종 위치한 지역 또는 사용자가 지정한 지역
	private int uvTodayNum; // 숫자형으로 변환된 자외선 지수를 사용하기 위해서
	// 바꿀 지역을 검색할 때 사용하는 버튼에서 적용. 굳이 지역변수로 두지 않고 뺀
	// 이유는 mTodayRating 버튼을 눌렸을때 사용하기 위해서임.

	// 굳이 손대지 않아도됨
	// Main으로 받아온 매개변수를 저장하기 위한 것
	private View v;
	private Intent result;
	private FragmentActivity act;
	// 서치버튼 잘 안눌리는것 해결..
	private Button searchButton;
	private UserSetting usSetting;
	private Boolean initCheck;// 푸쉬 온오프
	private SharedPreferences prefs; // 푸쉬버튼

	public MainView(FragmentActivity activity, Intent intent, View v) {
		this.v = v;
		this.act = activity;
		this.result = intent;

		// pushCheck 부분 때문에 선언한 것들 두번째 앱실행시는 무조건 true이기 때문에
		// 밑의 if를 수행하지 않는다.
		prefs = PreferenceManager.getDefaultSharedPreferences(activity);
		initCheck = prefs.getBoolean("initCheck", false);

		// 앱 설치 후 최초 1회만 실행하게 된다. 강제로 PushCheck를 true로 만들기 위해서
		if (initCheck == false)
			prefs.edit().putBoolean("PushCheck", true).apply();
	}

	public void viewConstruct() {
		returnUS = new ReturnUVSPF();

		// 오늘
		mLocalName = (TextView) v.findViewById(R.id.local_name);// 지역이름
		mUserSetting = (Button) v.findViewById(R.id.user_setting);// 설정
		mTodayMessage = (TextView) v.findViewById(R.id.today_warning_message);// 해가 없어요
		mTodayRating = (TextView) v.findViewById(R.id.today_warning_rating);// *단계
		localSearch = (RelativeLayout) v.findViewById(R.id.search);// 지역검색 레이아웃
		mSpfTodayValue = (TextView) v.findViewById(R.id.spf_today);// 오늘의 spf지수
		mSpfTodayBehavior = (TextView) v.findViewById(R.id.spf_today_value);// 오늘행동강령

		// 내일 (3/보통/15-20)
		mTomorrowSpfRating = (TextView) v.findViewById(R.id.tomorrow_spf_rating);
		mTomorrowTextRating = (TextView) v.findViewById(R.id.tomorrow_spf_text_rating);
		mTomorrowSpfValue = (TextView) v.findViewById(R.id.tomorrow_spf_value);

		// 모레
		mAfterTomorrowSpfRating = (TextView) v.findViewById(R.id.after_tomorrow_spf_rating);
		mAfterTomorrowTextRating = (TextView) v.findViewById(R.id.after_tomorrow_text_rating);
		mAfterTomorrowSpfValue = (TextView) v.findViewById(R.id.after_tomorrow_spf_value);

		// 태양 이미지
		mSunBackground = (FrameLayout) v.findViewById(R.id.sun_background);

		// 서치버튼
		searchButton = (Button) v.findViewById(R.id.search_icon);

		// 내일과 모레 SPF 라 써있는 텍스트
		TextView mAfterTomorrowSpf = (TextView) v.findViewById(R.id.after_tomorrow_spf);

		// 폰트 적용
		GlobalApplication.setDefaultFont(act.getApplicationContext(),"DEFAULT", "sandol-light.otf");

		mHoloCircularProgressBar_Today = (HoloCircularProgressBar) v.findViewById(R.id.holoCircularProgressBar);
		mHoloCircularProgressBar_Tomorrow = (HoloCircularProgressBar) v.findViewById(R.id.holoCircularProgressBar_tomorrow);
		mHoloCircularProgressBar_After_Tomorrow = (HoloCircularProgressBar) v.findViewById(R.id.holoCircularProgressBar_after_tomorrow);

		// 지역의 이름 변경
		finalLocation = result.getStringExtra("finalLocation");
		mLocalName.setText(finalLocation);

		String uvToday = result.getStringExtra("uvToday");
		String uvTomorrow = result.getStringExtra("uvTomorrow");
		String uvAfterTomorrow = result.getStringExtra("uvAfterTomorrow");

		// //////////////////// 오늘의 자외선 지수 표시 ////////////////////////
		if (uvToday.equals("")) // 18시 발표 경우는 공백을 제공해서 바꿔야함
			uvToday = "0";
		mTodayRating.setText(uvToday + "단계"); // 자외선의 등급 표시

		if (!uvToday.equals(""))
			uvTodayNum = Integer.parseInt(uvToday);
		mTodayMessage.setText(returnUS.returnGradeText(uvTodayNum));

		// //////////////////// 내일의 자외선 지수 표시 ////////////////////////
		if (uvTomorrow.equals(""))
			uvTomorrow = "0";
		mTomorrowSpfRating.setText(uvTomorrow);

		if (!uvTomorrow.equals("")) {
			uvTomorrowNum = Integer.parseInt(uvTomorrow);
			mTomorrowTextRating.setText(returnUS.returnGradeText(uvTomorrowNum));
		}

		// ////////////////////내일 모레의 자외선 지수 표시 ////////////////////////
		if (uvAfterTomorrow.equals("")){ // 06시 발표 경우는 공백을 제공해서 바꿔야함
			uvAfterTomorrow = "-";
		}
		mAfterTomorrowSpfRating.setText(uvAfterTomorrow);
		if(uvAfterTomorrow.equals("-"))
			uvAfterTomorrow = "0";
		
		if (!uvAfterTomorrow.equals("")) {
			uvAfterTomorrowNum = Integer.parseInt(uvAfterTomorrow);
			mAfterTomorrowTextRating.setText(returnUS.returnGradeText(uvAfterTomorrowNum));
		}

		// 자외선 값에 따라 그래프 변경
		mUvToday = (Float.parseFloat(uvToday) / 11);
		mUvTomorrow = (Float.parseFloat(uvTomorrow) / 11);
		mUvAfterTomorrow = (Float.parseFloat(uvAfterTomorrow) / 11);

		// 자외선 그래프 색 바꾸기
		// 오늘
		animate(mHoloCircularProgressBar_Today, null, mUvToday, 5500);
		mHoloCircularProgressBar_Today.setMarkerProgress(1f);
		mHoloCircularProgressBar_Today.setProgressBackgroundColor(Color.parseColor("#f5f0ec"));
		mHoloCircularProgressBar_Today.setProgressColor(Color.parseColor(returnUS.returnColor(uvTodayNum)));
		// 내일
		animate(mHoloCircularProgressBar_Tomorrow, null, mUvTomorrow, 5500);
		mHoloCircularProgressBar_Tomorrow.setMarkerProgress(1f);
		mHoloCircularProgressBar_Tomorrow.setProgressBackgroundColor(Color.parseColor("#f5f0ec"));
		mHoloCircularProgressBar_Tomorrow.setProgressColor(Color.parseColor(returnUS.returnColor(uvTomorrowNum)));
		// 모레
		animate(mHoloCircularProgressBar_After_Tomorrow, null,mUvAfterTomorrow, 5500);
		mHoloCircularProgressBar_After_Tomorrow.setMarkerProgress(1f);
		mHoloCircularProgressBar_After_Tomorrow.setProgressBackgroundColor(Color.parseColor("#f5f0ec"));
		mHoloCircularProgressBar_After_Tomorrow.setProgressColor(Color.parseColor(returnUS.returnColor(uvAfterTomorrowNum)));

		// 자외선 지수에 따른 spf수치 표시(15-20)
		mSpfTodayValue.setText("SPF " + returnUS.returnSPFGrade(uvTodayNum));// 오늘
		mTomorrowSpfValue.setText(returnUS.returnSPFGrade(uvTomorrowNum));// 내일
		mAfterTomorrowSpfValue.setText(returnUS.returnSPFGrade(uvAfterTomorrowNum));// 모레

		// 오늘 자외선 지수에따른 행동강령
		mSpfTodayBehavior.setText(returnUS.returnSPFText(uvTodayNum));

		// 모레 18시 이후 변경되는 텍스트
		if (uvAfterTomorrow.equals("0")) {
			mAfterTomorrowTextRating.setText("18시 이후");
			mAfterTomorrowSpf.setText("업데이트");
			mAfterTomorrowSpfValue.setText("예정");
		}

		// 자외선 지수에 따른 행동사항 표시

		// 태양 이미지 바꾸기
		if (uvTodayNum < 2)
			mSunBackground.setBackgroundResource(R.drawable.sun1);
		else if (uvTodayNum < 4)
			mSunBackground.setBackgroundResource(R.drawable.sun2);
		else if (uvTodayNum < 6)
			mSunBackground.setBackgroundResource(R.drawable.sun3);
		else if (uvTodayNum < 8)
			mSunBackground.setBackgroundResource(R.drawable.sun4);
		else if (uvTodayNum < 10)
			mSunBackground.setBackgroundResource(R.drawable.sun5);
		else
			mSunBackground.setBackgroundResource(R.drawable.sun6);

		
		////////////////// 푸쉬버튼 구성 ///////////////////

		final Button mPushButton = (Button) v.findViewById(R.id.push_setting);
		prefs = PreferenceManager.getDefaultSharedPreferences(act);
		// mUserSetting에서 푸쉬 온/오프가 가능한데 그 때 저장된 값을 가지고 온다.
		Boolean firstPushCheck = prefs.getBoolean("PushOnOff", false);

		// 푸쉬 온/오프를 확인하여 버튼의 상태를 앱을 껏다켜도 그대로 유지시켜 준다.
		if (firstPushCheck == true)
			mPushButton.setBackgroundResource(R.drawable.push_check_selector);
		else
			mPushButton.setBackgroundResource(R.drawable.push_uncheck_selector);

		mPushButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				// 두 번 실행하게 하지 않기 위해서 선언
				prefs = PreferenceManager.getDefaultSharedPreferences(act);
				prefs.edit().putBoolean("initCheck", true).apply();

				Boolean pushCheck = prefs.getBoolean("PushCheck", false);

				// 처음에는 알림버튼이 꺼져있을 것이기 때문에 처음 누르면 알림이 활성하되게 한다.
				// 그래서 pushCheck는 처음 누르면 true로 무조건 들어가게 된다.
				if (pushCheck == true) {
					mPushButton.setBackgroundResource(R.drawable.push_check_selector);
					Toast.makeText(act, "알림 기능이 활성화 되었습니다.", Toast.LENGTH_LONG).show();
					Intent intent = null;
					AlarmManager am;
					PendingIntent sender;

					am = (AlarmManager) act.getSystemService(Context.ALARM_SERVICE);

					intent = new Intent(act, NotiReceiver.class);
					sender = PendingIntent.getBroadcast(act, 0, intent, 0);

					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis());
					calendar.set(Calendar.HOUR_OF_DAY, 13); // 오후 1시
					calendar.set(Calendar.MINUTE, 02); // 정각에 푸쉬

					// 24시간 주기로 알림을 실행한다.
					am.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, sender);

					// 푸쉬를 on시키고 그에 대한 값을 저장한다.
					// 저장하는 이유는 디바이스 부팅시 푸쉬 온/오프를 관리하기 위해서이다.
					prefs = PreferenceManager.getDefaultSharedPreferences(act);
					prefs.edit().putBoolean("PushOnOff", true).apply();

					// push의 온/오프 상태를 체크하기 위해서 만든 것 약간은 조건 검사를 위한 부가적인 기능이다.
					// 헷갈릴 수도 있기 때문에 흐름을 잘 따라야 할 것 같다.
					prefs = PreferenceManager.getDefaultSharedPreferences(act);
					prefs.edit().putBoolean("PushCheck", false).apply();
				}

				else if (pushCheck == false) {

					mPushButton.setBackgroundResource(R.drawable.push_uncheck_selector);

					Toast.makeText(act, "알림 기능이 비활성화 되었습니다.", Toast.LENGTH_LONG).show();

					// 똑같은 주소의 AlaramManager객체 리턴
					AlarmManager tempAlarm = (AlarmManager) act.getSystemService(Context.ALARM_SERVICE);

					// 아래와 같이 하면 푸쉬사용을 했을때 구성했던 PendingIntent객체와
					// 똑같은 주소의 객체를 리턴하여 준다.
					Intent intent = new Intent(act, NotiReceiver.class);
					PendingIntent sender = PendingIntent.getBroadcast(act, 0, intent, 0);

					if (tempAlarm != null) { // 알람을 중지한다.
						tempAlarm.cancel(sender);
					}

					// 푸쉬를 off시키고 그에 대한 값을 저장한다.
					prefs = PreferenceManager.getDefaultSharedPreferences(act);
					prefs.edit().putBoolean("PushOnOff", false).apply();

					// push의 온/오프 상태를 체크하기 위해서 만든 것
					// 헷갈릴 수도 있기 때문에 흐름을 잘 따라야 할 것 같다.
					prefs = PreferenceManager.getDefaultSharedPreferences(act);
					prefs.edit().putBoolean("PushCheck", true).apply();
				}
			}

		});

		// 지역검색 버튼을 눌렸을 때
		localSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Tracker t = ((GlobalApplication) act.getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);

				t.send(new HitBuilders.EventBuilder()
						.setCategory("LocalSearch")
						.setAction("Press LocalSearch")
						.setLabel("Local Search").build());

				locSearch = new LocalSearch(act, v);
				locSearch.localSearch();
			}
		});

		// 지역검색 버튼을 눌렸을 때
		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				locSearch = new LocalSearch(act, v);
				locSearch.localSearch();
			}
		});

		// 사용자 설정 부분을 눌렸을 때
		mUserSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Tracker t = ((GlobalApplication) act.getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);

				t.send(new HitBuilders.EventBuilder()
						.setCategory("UserSetting")
						.setAction("Press UserSetting").setLabel("UserSetting")
						.build());

				if (usSetting == null)
					usSetting = new UserSetting(act, v);
				usSetting.userSettingStart();
			}
		});
	}

	private void animate(final HoloCircularProgressBar progressBar, final AnimatorListener listener, final float progress, final int duration) {

		mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress",progress);
		mProgressBarAnimator.setDuration(duration);

		mProgressBarAnimator.addListener(new AnimatorListener() {
			@Override
			public void onAnimationEnd(final Animator animation) {
				progressBar.setProgress(progress);
			}
			@Override
			public void onAnimationCancel(final Animator animation) { }
			@Override
			public void onAnimationRepeat(final Animator animation) { }
			@Override
			public void onAnimationStart(final Animator animation) { }
		});
		
		if (listener != null) {
			mProgressBarAnimator.addListener(listener);
		}
		
		mProgressBarAnimator.reverse();
		mProgressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				progressBar.setProgress((Float) animation.getAnimatedValue());
			}
		});
		
		progressBar.setMarkerProgress(progress);
		mProgressBarAnimator.start();
	}
}
package fatdog.uv.logotest;

import org.json.JSONException;
import org.json.JSONObject;

import com.kakao.GlobalApplication;

import de.passsy.holocircularprogressbar.HoloCircularProgressBar;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

public class LocalSearch {
	private HoloCircularProgressBar mHoloCircularProgressBar_Today;
	private HoloCircularProgressBar mHoloCircularProgressBar_Tomorrow;
	private HoloCircularProgressBar mHoloCircularProgressBar_After_Tomorrow;
	private ObjectAnimator mProgressBarAnimator;

	private ReturnUVSPF returnUS; // 각종 지수를 환산하기 위해서
	private FrameLayout mSunBackground; // 고정 배경

	private Long areaNo; // 위치한 지역
	private FragmentActivity act;
	private ProgressDialog pDialog; // 설정 프로그레스 바
	// F_MAIN에서 하는 일을 똑같이 하므로 코드가 겹치는 경향이 없지않아 있음.
	private String finalLocation; // 최종 위치한 지역 또는 사용자가 지정한 지역
	private TextView mLocalName; // 지역의 이름
	// 오늘
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

	//잡
	private int uvTodayNum;
	private int uvTomorrowNum;
	private int uvAfterTomorrowNum;
	private Dialog dialog;
	private View v;

	private float mUvToday;
	private float mUvTomorrow;
	private float mUvAfterTomorrow;

	public LocalSearch(FragmentActivity activity, View v) {
		this.act = activity;
		this.v = v;
		uvTodayNum = 0;
	}

	public void localSearch() {
		returnUS = new ReturnUVSPF();

		View innerView = act.getLayoutInflater().inflate(R.layout.local_setting_view, null);
		//전체 배경
		mSunBackground = (FrameLayout) v.findViewById(R.id.sun_background);
		mLocalName = (TextView) v.findViewById(R.id.local_name);// 지역이름
		//전체 폰트적용
		GlobalApplication.setDefaultFont(act.getApplicationContext(),"DEFAULT", "sandol-light.otf");

		mHoloCircularProgressBar_Today = (HoloCircularProgressBar) v.findViewById(R.id.holoCircularProgressBar);
		mHoloCircularProgressBar_Tomorrow = (HoloCircularProgressBar) v.findViewById(R.id.holoCircularProgressBar_tomorrow);
		mHoloCircularProgressBar_After_Tomorrow = (HoloCircularProgressBar) v.findViewById(R.id.holoCircularProgressBar_after_tomorrow);
		
		//오늘
		mTodayRating = (TextView) v.findViewById(R.id.today_warning_rating);
		mSpfTodayValue = (TextView) v.findViewById(R.id.spf_today);// 오늘의 spf지수
		mTodayMessage = (TextView) v.findViewById(R.id.today_warning_message);//위험
		mSpfTodayBehavior = (TextView) v.findViewById(R.id.spf_today_value);

		// 내일
		mTomorrowSpfRating = (TextView) v.findViewById(R.id.tomorrow_spf_rating);
		mTomorrowTextRating = (TextView) v.findViewById(R.id.tomorrow_spf_text_rating);
		mTomorrowSpfValue = (TextView) v.findViewById(R.id.tomorrow_spf_value);

		// 모레
		mAfterTomorrowSpfRating = (TextView) v.findViewById(R.id.after_tomorrow_spf_rating);
		mAfterTomorrowTextRating = (TextView) v.findViewById(R.id.after_tomorrow_text_rating);
		mAfterTomorrowSpfValue = (TextView) v.findViewById(R.id.after_tomorrow_spf_value);

		ArrayAdapter<CharSequence> adapt;
		adapt = ArrayAdapter.createFromResource(act, R.array.strArr,R.layout.custom_simple_list_item);

		ListView listView = (ListView) innerView.findViewById(R.id.listView);
		listView.setAdapter(adapt);
		listView.setOnItemClickListener(new ListViewItemClickListener());

		dialog = new Dialog(act, R.style.CustomDialog);
		dialog.setContentView(innerView);

		dialog.getWindow().setLayout(
				(int) (act.getWindow().peekDecorView().getWidth() * 0.7),
				(int) (act.getWindow().peekDecorView().getHeight() * 0.8));

		LayoutParams params = dialog.getWindow().getAttributes();
		params.y = -((int) (act.getWindow().peekDecorView().getHeight() * 0.05));
		// parmas.y 가 -면 위로 올라가고 +값이면 아래로 내려감

		dialog.setCanceledOnTouchOutside(true);
		dialog.show(); // 알림창 띄우기
	}

	private class ListViewItemClickListener implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {

			if (position == 0) {
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
				areaNo = prefs.getLong("locationCode", 0L);
				finalLocation = prefs.getString("local", null);
			} else {
				SpecificLocationCode sp = new SpecificLocationCode();
				areaNo = sp.areaNum(position);
			}

			if (position != 0) {
				TextView tv = (TextView) view;
				finalLocation = (String) tv.getText();
			}

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
			prefs.edit().putLong("areaNo", areaNo).apply();
			new anotherLocation(act.getApplicationContext()).execute();

			dialog.dismiss();
		}
	}

	private class anotherLocation extends AsyncTask<Void, Void, JSONObject> {
		Context ctx;

		anotherLocation(Context ctx) {
			this.ctx = ctx;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(act);
			pDialog.setMessage("잠시만 기다려주세요...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		public JSONObject doInBackground(Void... params) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
			Long areaNumber = prefs.getLong("areaNo", 0L);
			JSONObject result = new runJson(areaNumber).runJsonExcute();
			return result;
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			String uvToday = null;
			String uvTomorrow = null;
			String uvAfterTomorrow = null;

			try {
				uvToday = result.getString("today");
				uvTomorrow = result.getString("tomorrow");
				uvAfterTomorrow = result.getString("theDayAfterTomorrow");

				if (uvToday.equals("")) // 저녁의 경우는 공백을 제공해서 바꿔야함
					uvToday = "0";

			} catch (JSONException e) {
				e.printStackTrace();
			}

			mLocalName.setText(finalLocation);			

			// 별거아니지만 spf 글자적용하기 위해서

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
			
			// 자외선 지수에 따른 spf수치 표시(15-20)
			mSpfTodayValue.setText("SPF " + returnUS.returnSPFGrade(uvTodayNum));// 오늘
			mTomorrowSpfValue.setText(returnUS.returnSPFGrade(uvTomorrowNum));// 내일
			mAfterTomorrowSpfValue.setText(returnUS.returnSPFGrade(uvAfterTomorrowNum));// 모레

			TextView mAfterTomorrowSpf = (TextView) v.findViewById(R.id.after_tomorrow_spf);
			
			// 모레 18시 이후 변경되는 텍스트
			if (uvAfterTomorrow.equals("0")) {
				mAfterTomorrowTextRating.setText("18시 이후");
				mAfterTomorrowSpf.setText("업데이트");
				mAfterTomorrowSpfValue.setText("예정");
			}
			// 자외선 지수에 따른 행동사항 표시
			mSpfTodayBehavior.setText(returnUS.returnSPFText(uvTodayNum));

			mUvToday = (Float.parseFloat(uvToday) / 11);
			mUvTomorrow = (Float.parseFloat(uvTomorrow) / 11);
			mUvAfterTomorrow = (Float.parseFloat(uvAfterTomorrow) / 11);

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

			if (pDialog.isShowing())
				pDialog.dismiss();
		}
	}

	private void animate(final HoloCircularProgressBar progressBar, final AnimatorListener listener, final float progress, final int duration) {

		mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
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

	// 바뀐 오늘의 자외선 지수를 반환해 준다.
	public int getChangeLocalNum() {
		return uvTodayNum;
	}
}
package fatdog.uv.logotest;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.kakao.GlobalApplication;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class UserSetting {
	
	private FragmentActivity act;
	private TextView info;
	private TextView info_detail;
	private TextView kakao;
	private TextView kakao_detail;
	private TextView behavior;
	private TextView behavior_detail;
	private TextView widget;
	private TextView maker_detail;

	// 행동강령
	private TextView topLevel;
	private TextView topEx;
	private TextView lowEx;
	private TextView normalEx;
	private TextView highEx;
	private TextView warningEx;
	private TextView dangerEx;
	private Typeface type;
	private View v;
	private boolean hasPermanentMenuKey;
	private Dialog behaviorDialog;
	private DefineWidgetLocation defineWLocation;
	private KaKaoLink kakaolink;

	public UserSetting(FragmentActivity activity, View v) {
		this.act = activity;
		this.v = v;
	}

	public boolean checkApi() {
		int version = android.os.Build.VERSION.SDK_INT;

		if (version >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			if (ViewConfiguration.get(act).hasPermanentMenuKey()) // 물리버튼이 여부
				return true; // 있다
			else
				return false; // 없다
		} else
			return false;
	}

	@SuppressWarnings("deprecation")
	public void userSettingStart() {

		final Dialog dialog;

		View innerView = act.getLayoutInflater().inflate(R.layout.alert_view, null);

		hasPermanentMenuKey = checkApi();

		if (!hasPermanentMenuKey)
			innerView = act.getLayoutInflater().inflate(R.layout.alert_view_softkey, null);

		// 여기서부터 객체참조 및 폰트적용
		info = (TextView) innerView.findViewById(R.id.infoText);
		info_detail = (TextView) innerView.findViewById(R.id.infoText_detail);
		kakao = (TextView) innerView.findViewById(R.id.kakaoText);
		kakao_detail = (TextView) innerView.findViewById(R.id.kakaoText_detail);
		behavior = (TextView) innerView.findViewById(R.id.behaviorText);
		behavior_detail = (TextView) innerView.findViewById(R.id.behaviorText_detail);
		widget = (TextView) innerView.findViewById(R.id.widgetText);
		maker_detail = (TextView) innerView.findViewById(R.id.widgetText_detail);

		type = Typeface.createFromAsset(act.getAssets(), "sandol-light.otf");
		
		info.setTypeface(type);
		info_detail.setTypeface(type);
		kakao.setTypeface(type);
		kakao_detail.setTypeface(type);
		behavior.setTypeface(type);
		behavior_detail.setTypeface(type);
		widget.setTypeface(type);
		maker_detail.setTypeface(type);

		dialog = new Dialog(act, R.style.CustomDialog);
		dialog.setContentView(innerView);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		dialog.getWindow().setLayout(
				(int) (act.getWindow().peekDecorView().getWidth() * 0.7),
				(int) (act.getWindow().peekDecorView().getHeight() * 0.5));

		LayoutParams params = dialog.getWindow().getAttributes();
		params.y = -((int) (act.getWindow().peekDecorView().getHeight() * 0.05));
		// parmas.y 가 -면 위로 올라가고 +값이면 아래로 내려감
		dialog.getWindow().setAttributes(params);
		
		innerView.findViewById(R.id.relative1).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Tracker t = ((GlobalApplication) act.getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
						t.send(new HitBuilders.EventBuilder()
						.setCategory("App Info")
						.setAction("Press App Info")
						.setLabel("App Info")
						.build());
						
						Intent i = new Intent(act, GuideViewpager.class);
						
						act.startActivity(i);
						dialog.dismiss();
					}
				});
		
		innerView.findViewById(R.id.relative2).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Tracker t = ((GlobalApplication) act.getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);		
						t.send(new HitBuilders.EventBuilder()
						.setCategory("Kakao")
						.setAction("Press Kakao")
						.setLabel("Kakao")
						.build());
						
						if (kakaolink == null)
							kakaolink = new KaKaoLink(act, v);
						kakaolink.startKaKao();
						dialog.dismiss();
					}
				});
		
		innerView.findViewById(R.id.relative3).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Tracker t = ((GlobalApplication) act.getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
						t.send(new HitBuilders.EventBuilder()
						.setCategory("Behavior")
						.setAction("Press Behavior")
						.setLabel("Behavior")
						.build());
						
						View behaviorView = act.getLayoutInflater().inflate(R.layout.behaviorview, null);

						topLevel = (TextView) behaviorView.findViewById(R.id.top_level);
						topEx = (TextView) behaviorView.findViewById(R.id.top_ex);
						lowEx = (TextView) behaviorView.findViewById(R.id.low_ex);
						normalEx = (TextView) behaviorView.findViewById(R.id.normal_ex);
						highEx = (TextView) behaviorView.findViewById(R.id.high_ex);
						warningEx = (TextView) behaviorView.findViewById(R.id.warning_ex);
						dangerEx = (TextView) behaviorView.findViewById(R.id.danger_ex);

						topLevel.setTypeface(type);
						topEx.setTypeface(type);
						lowEx.setTypeface(type);
						normalEx.setTypeface(type);
						highEx.setTypeface(type);
						warningEx.setTypeface(type);
						dangerEx.setTypeface(type);

						behaviorDialog = new Dialog(act, R.style.CustomDialog);
						behaviorDialog.setContentView(behaviorView);
						
						hasPermanentMenuKey = checkApi();

						if (!hasPermanentMenuKey) {
							behaviorDialog.getWindow().setLayout(
									(int) (act.getWindow().peekDecorView().getWidth() * 0.8),
									(int) (act.getWindow().peekDecorView()
											.getHeight() * 0.85));// 0.78
						} else {
							behaviorDialog.getWindow().setLayout(
									(int) (act.getWindow().peekDecorView()
											.getWidth() * 0.8),
									(int) (act.getWindow().peekDecorView()
											.getHeight() * 0.78));
						}
						
						LayoutParams behaviorparams = behaviorDialog.getWindow().getAttributes();
						behaviorparams.y = -((int) (act.getWindow().peekDecorView().getHeight() * 0.05));
						// parmas.y 가 -면 위로 올라가고 +값이면 아래로 내려감
						behaviorDialog.getWindow().setAttributes(behaviorparams);
						behaviorDialog.show();
						
						dialog.dismiss();
					}
				});
		
		innerView.findViewById(R.id.relative4).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Tracker t = ((GlobalApplication) act.getApplication()).getTracker(GlobalApplication.TrackerName.APP_TRACKER);
						t.send(new HitBuilders.EventBuilder()
						.setCategory("Setting Widget")
						.setAction("Press Setting Widget")
						.setLabel("Setting Widget")
						.build());
						
						if (defineWLocation == null)
							defineWLocation = new DefineWidgetLocation(act);
						defineWLocation.callWidgetLocationSearch();
						dialog.dismiss();
					}
				});
		dialog.show(); // 알림창 띄우기
	}
}
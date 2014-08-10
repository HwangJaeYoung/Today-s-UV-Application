package fatdog.uv.logotest;

import android.app.AlertDialog;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;

public class KaKaoLink {

	private KakaoLink kakaoLink;
	private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;
	private FragmentActivity act;
	private View v;
	private String localNameText;
	private String viewRatingText;
	private String viewAlertText;

	public KaKaoLink(FragmentActivity activity, View v) {
		this.act = activity;
		this.kakaoTalkLinkMessageBuilder = null;
		this.kakaoLink = null;
		this.v = v;
	}

	// 외부에서 카카오톡을 실행 하기 위한 메소드
	// 이것만 호출해주면 카카오톡 메시지를 실행시켜준다.

	public void startKaKao() {
		// 현재 뷰에 표시되고 있는 정보를 가지고 온다.
		// 지역정보
		TextView localName = (TextView) v.findViewById(R.id.local_name);
		localNameText = (String) localName.getText();

		// 등급정보
		TextView viewRating = (TextView) v.findViewById(R.id.today_warning_rating);
		viewRatingText = (String) viewRating.getText();

		// 등급텍스트 정보
		TextView viewAlert = (TextView) v.findViewById(R.id.today_warning_message);
		viewAlertText = (String) viewAlert.getText();

		try {
			kakaoLink = KakaoLink.getKakaoLink();
			kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
			sendKakaoTalkLink();
		} catch (KakaoParameterException e) {
			e.printStackTrace();
		}
	}

	// 카카오톡 메시지를 보내게 하는 메소드
	private void sendKakaoTalkLink() {
		try {
			kakaoTalkLinkMessageBuilder.addText(
					"[오늘의 자외선]" + "\n" + localNameText + "입니다." + "\n"
							+ "오늘의 자외선 지수는 " + viewRatingText + "\n"
							+ viewAlertText + "입니다.").addAppButton(
					"앱으로 연결           ");
			kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder.build(), act);
		} catch (KakaoParameterException e) {
			alert(e.getMessage());
		}
	}

	// 카카오톡 실행 오류시 경고 메세지
	private void alert(String message) {
		new AlertDialog.Builder(act)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.app_name).setMessage(message)
				.setPositiveButton(android.R.string.ok, null).create().show();
	}
}
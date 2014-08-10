package fatdog.uv.logotest;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DefineWidgetLocation {

	private FragmentActivity act;
	private Dialog dialog;
	private String finalLocation;
	
	public DefineWidgetLocation(FragmentActivity act) {
		this.act = act;
	}

	public void callWidgetLocationSearch() {

		View innerView = act.getLayoutInflater().inflate(R.layout.widget_setting_view, null);
		Typeface type = Typeface.createFromAsset(act.getAssets(), "sandol-light.otf");
		TextView text = (TextView) innerView.findViewById(R.id.widgetLocalTitle);
		text.setTypeface(type);

		ArrayAdapter<CharSequence> adapt;
		adapt = ArrayAdapter.createFromResource(act, R.array.strArrWidget, R.layout.custom_simple_list_item);
		ListView listView = (ListView) innerView.findViewById(R.id.widgetListView);
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
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			TextView tv = (TextView) view;
			finalLocation = (String) tv.getText();
			// 최종적으로 변경 버튼을 실행 했을 때 바뀌게 된다.
			// 취소를 해도 상관이 없다.
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(act);
			prefs.edit().putString("localTest", "true").apply(); // 지역을 변경했는지의 여부 확인
			prefs.edit().putString("widgetlocal", finalLocation).apply();
			prefs.edit().putInt("widgetNumber", position).apply();
			Toast.makeText(act, "[" + finalLocation + "]" + "로 지역이 변경되었습니다." + "\n" +
			"30분 후 위젯의 지역이 업데이트 됩니다.", Toast.LENGTH_LONG).show( );
			dialog.dismiss();
		}
	}
}
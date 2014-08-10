package fatdog.uv.logotest;

import org.json.JSONException;
import org.json.JSONObject;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class MyAppWidget extends AppWidgetProvider {

	// 한 시간마다 onUpdate가 호출된다.
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
		RemoteViews updateViews;
		ComponentName watchWidget;

		updateViews = new RemoteViews(context.getPackageName(), R.layout.widget_word);
		watchWidget = new ComponentName(context, MyAppWidget.class);
		widgetTask widget = new widgetTask(appWidgetManager, updateViews, watchWidget, context);
		widget.execute();
	}

	class widgetTask extends AsyncTask<Void, Void, JSONObject> {
		private AppWidgetManager appWidgetManager;
		private RemoteViews updateViews;
		private ComponentName watchWidget;
		private Context ctx;
		private int uvTodayNum;
		private ReturnUVSPF returnUS;

		public widgetTask(AppWidgetManager a, RemoteViews r, ComponentName c, Context ctx) {
			appWidgetManager = a;
			updateViews = r;
			watchWidget = c;
			this.ctx = ctx;
		}

		// JSON을 파싱해오기 위한 백그라운드
		@Override
		public JSONObject doInBackground(Void... params) {
			JSONObject result = null;
			String local = null;
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);

			// 상황에 따라서 위젯지역을 가져온다.
			// true면 사용자가 지역을 설정한것이고,
			// 아니면 기본접속 했을 떄의 위치를 가지고 온다.
			String wlChange = prefs.getString("localTest", "false");

			if (wlChange.equals("true")) // 선택한 위젯 지역으로 설정한다.
			{
				// MyAppWidget에서 가지고 온 값을 가지고 처리한다.
				int areaNo = prefs.getInt("widgetNumber", 0); // 인덱스번호를 가지고 온다.
				local = prefs.getString("widgetlocal", null); // 사용자가 변경한 지역을 가지고 온다.
				SpecificLocationCode sp = new SpecificLocationCode();
				Long location = sp.areaNum(areaNo + 1);
				result = new runJson(location).runJsonExcute();

				try {
					result.put("location", local);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else if (wlChange.equals("false")) { // 초기에 접속했던 지역으로 설정한다.
				local = prefs.getString("local", "none");
				char[] charArray = local.toCharArray();
				String modifyLocation = ""; // 변경된 지역 이름을 저장한다.

				// 서울특별시 광진구 이면 서울특별시만 가지고 온다.
				for (int i = 0; i < charArray.length; i++) {
					if (charArray[i] != ' ')
						modifyLocation += charArray[i];
					else
						break;
				}

				LocationCode loc = new LocationCode(local);
				Long location = loc.searchLocation();
				result = new runJson(location).runJsonExcute();

				try {
					result.put("location", modifyLocation);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return result; // 파싱한 JSONObject를 반환.
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);
			returnUS = new ReturnUVSPF();
			String uvToday = null;
			String alertText = null;
			String widgetLocal = null;

			try {
				// 반영할 위젯의 위치를 가지고 온다.
				widgetLocal = result.getString("location");

				// 자외선 지수를 저장한다.
				uvToday = result.getString("today");

				if (uvToday.equals(""))
					uvToday = "0";

				if (!uvToday.equals(""))
					uvTodayNum = Integer.parseInt(uvToday);
				alertText = returnUS.returnGradeText(uvTodayNum);
			} catch (JSONException e) {
				Log.i("js", "widgetJsonError");
				e.printStackTrace();
			}

			// 위젯의 내용을 업데이트 해주는 부분
			if (appWidgetManager != null) {
				int color = Color.parseColor(returnUS.returnColor(uvTodayNum));
				updateViews.setTextViewText(R.id.widget_local, widgetLocal);
				updateViews.setTextViewText(R.id.uvToday, uvToday);
				updateViews.setTextViewText(R.id.uvToday_alert, alertText);
				updateViews.setTextColor(R.id.uvToday, color);
				updateViews.setTextColor(R.id.uvToday_alert, color);
				updateViews.setInt(R.id.uvToday_color, "setBackgroundColor", color);
				appWidgetManager.updateAppWidget(watchWidget, updateViews);
			}
		}
	}
}
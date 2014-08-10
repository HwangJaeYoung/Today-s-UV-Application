package fatdog.uv.logotest;

import java.io.IOException;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class NotiService extends Service {
	private Geocoder coder;
	private double lattitude;
	private double longitude;
	private List<Address> list;
	private boolean isGPSEnabled;
	private boolean isNetworkEnabled;
	private LocationManager locationManager;
	private boolean startNetworkSearch = true;
	private boolean startGPSSearch = true;
	private String finalLocation;
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
	private static final long MIN_TIME_BW_UPDATES = 0;
	private LocationListener networkListener;
	private LocationListener gpsListener;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		coder = new Geocoder(this);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// GPS가 수신가능한 상태인지 판단한다.
		isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		// Network 수신가능한 상태인지 판단한다.
		isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		// 위치가 바꼈을때 사용 할 네트워크 수신자를 위한 리스너
		networkListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// 지속적으로 onLocationChanged가 호출 될텐데 그 현상을 막기 위해서
				// startNetworkSearch의 boolean값을 이용하여 한 번만 실행하게 한다.
				if (startNetworkSearch == true) {
					startNetworkSearch = false;
					if (location != null) {
						// 새로운 위치로 업데이트 한다.
						lattitude = location.getLatitude();
						longitude = location.getLongitude();
						Log.i("js", "net enter listener");
					} else {
						Log.i("js", "net enter null");
						// 만약 위치가 바뀌지 않았을 경우 이전의 데이터를 가지고 온다.
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						lattitude = location.getLatitude();
						longitude = location.getLongitude();
					}

					if (lattitude != 0 && longitude != 0) // 위치정보를 받아 왔을 경우에
						searchLocation();
					else
						replaceToGPS(); // 그럼에도 불구하고 위치를 받아 오지 못했을 경우
						// Network -> GPS로 한번 더 기회를 준다.
				}
			}

			public void onStatusChanged(String provider, int status,Bundle extras) {}
			public void onProviderEnabled(String provider) { }
			public void onProviderDisabled(String provider) { }
		};

		// 위치가 바꼈을때 사용 할 GPS 수신자를 위한 리스너
		gpsListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if (startGPSSearch == true) {
					startGPSSearch = false;
					if (location != null) {
						lattitude = location.getLatitude();
						longitude = location.getLongitude();
						Log.i("js", "gps enter listener");
					} else {
						Log.i("js", "gps enter null");
						location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						lattitude = location.getLatitude();
						longitude = location.getLongitude();
					}

					if (lattitude != 0 && longitude != 0) // 위치정보를 받아 왔을 경우에
						searchLocation();
				}
			}

			public void onStatusChanged(String provider, int status, Bundle extras) { }
			public void onProviderEnabled(String provider) { }
			public void onProviderDisabled(String provider) { }
		};

		// 우선은 네트워크 수신자를 사용하게 한다.
		if (isNetworkEnabled) {
			Log.i("js", "if state enter network");
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, networkListener);
		}
		return START_NOT_STICKY;
	}

	// GPS연결을 한다.
	public void replaceToGPS() {
		if (isGPSEnabled) {
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, gpsListener);
		}
	}

	public void searchLocation() {
		String si = null;
		String gu = null;

		try {
			list = coder.getFromLocation(lattitude, longitude, 6);
		} catch (IOException e) {
			Log.i("js", "error");
			e.printStackTrace();
		}

		for (Address add : list) {
			if (add != null) {
				si = add.getAdminArea(); // 시 정보
				gu = add.getLocality(); // 구 정보
				if (gu == null)
					gu = add.getSubLocality(); // 창원시 처리하기 위해서
				break;
			}
		}

		finalLocation = si + " " + gu;

		if (finalLocation.equals("경상남도 창원시 의창구"))
			finalLocation = "경상남도 창원시";
		if (finalLocation.equals("경상남도 창원시 성산구"))
			finalLocation = "경상남도 창원시";
		if (finalLocation.equals("경상남도 창원시 마산합포구"))
			finalLocation = "경상남도 창원시";
		if (finalLocation.equals("경상남도 창원시 마산회원구"))
			finalLocation = "경상남도 창원시";
		if (finalLocation.equals("경상남도 창원시 진해구"))
			finalLocation = "경상남도 창원시";

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.edit().putString("pushLocation", finalLocation).apply();
		new FetchItemsTask(getApplicationContext()).execute();
	}

	private class FetchItemsTask extends AsyncTask<Void, Void, JSONObject> {

		Context ctx;

		FetchItemsTask(Context ctx) {
			this.ctx = ctx;
		}

		@Override
		public JSONObject doInBackground(Void... params) {
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
			String local = prefs.getString("pushLocation", "서울특별시 광진구");
			LocationCode loc = new LocationCode(local);
			Long location = loc.searchLocation();
			JSONObject result = new runJson(location).runJsonExcute();
			return result;
		}

		// 받은 데이터를 처리하는 부분
		@Override
		protected void onPostExecute(JSONObject result) {
			super.onPostExecute(result);

			if (result != null) {
				String uvToday = null;

				try {
					// 파싱해온 데이터에서 필요한것을 가지고 온다.
					uvToday = result.getString("today");

					if (uvToday.equals("")) // 18시 발표 경우는 공백을 제공해서 바꿔야함
						uvToday = "0";

					// 파싱한 데이터를 Notification에서 사용하기 위해서 저장한다.
					// SharedPreferences말고는 마땅히 전달할 방법이 없으므로
					SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
					prefs.edit().putString("serviceParsing", uvToday).apply();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 사용하지 않는 메소드
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
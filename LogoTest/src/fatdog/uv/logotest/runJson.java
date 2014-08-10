package fatdog.uv.logotest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class runJson {
	private Long areaNo; // 지역코드
	private String json;
	private JSONObject jsonObj; // jsonObj, jsonFirst, jsonSecond는 json구조를 파악하기위해 정의.
	private JSONObject jsonFirst;
	private JSONObject jsonSecond;
	private String jsonurl = "http://203.247.66.146/iros/RetrieveLifeIndexService/getUltrvLifeList?ServiceKey=Dt4G7hV1JCZaZ7qgQ2QXdAjRgo/W7N4R1JsEO9Kez7YX/lX0LflHSeeXcJ/Nc1u3B0csKUqDxQjs9qUZN1HJrw==&_type=json";
	private JSONObject uvObject; // 지역코드를 결과로 가져온 자외선 정보 오브젝트

	public runJson(Long areaNo) {
		this.areaNo = areaNo;
	}

	public JSONObject runJsonExcute() {
		try {
			// 자외선 지수 파싱
			jsonurl += "&areaNo=" + areaNo;
			json = getUrl(jsonurl);
			jsonObj = new JSONObject(json);
			jsonFirst = jsonObj.getJSONObject("Response");
			jsonSecond = jsonFirst.getJSONObject("Body");
			uvObject = jsonSecond.getJSONObject("IndexModel");
		} catch (JSONException e) {
			Log.i("js", "here");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return uvObject;
	}
	
	// json형태를 가지고 오는 코드, 실무안드책에서 가져온 그대로...
	public byte[] getUrlBytes(String urlSpec) throws IOException {
		URL url = new URL(urlSpec);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = connection.getInputStream();

			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}

			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = in.read(buffer)) > 0) {
				out.write(buffer, 0, bytesRead);
			}
			out.close();
			return out.toByteArray();
		}

		finally {
			connection.disconnect();
		}
	}

	public String getUrl(String urlSpec) throws IOException {
		return new String(getUrlBytes(urlSpec));
	}
}
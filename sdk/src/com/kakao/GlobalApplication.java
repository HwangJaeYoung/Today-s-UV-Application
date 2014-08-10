/**
 * Copyright 2014 Kakao Corp.
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao;

import java.lang.reflect.Field;
import java.util.HashMap;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.kakao.helper.SystemInfo;
import com.kakao.helper.Utility;
import com.kakao.sdk.R;

/**
 * 이미지를 캐시를 앱 수준에서 관리하기 위한 애플리케이션 객체이다.
 *
 * @author MJ
 */
public class GlobalApplication extends Application {
	HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    static final String APP_KEY_PROPERTY = "com.kakao.sdk.AppKey";
    private static final String PROPERTY_ID = "UA-51557450-1";
    private static volatile GlobalApplication instance = null;
    private ImageLoader imageLoader;
    private String appKey;
    
    // 구글 애널리틱스에서 사용하기 위한 생성자
	public GlobalApplication() {
		super();
	}

    /**
     * singleton 애플리케이션 객체를 얻는다.
     * @return singleton 애플리케이션 객체
     */
    public static GlobalApplication getGlobalApplicationContext() {
        if(instance == null)
            throw new IllegalStateException("this application does not inherit com.kakao.GlobalApplication");
        return instance;
    }

    /**
     * 이미지 로더, 이미지 캐시, 요청 큐를 초기화한다.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        SystemInfo.initialize();
        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        ImageLoader.ImageCache imageCache = new ImageLoader.ImageCache() {
            final LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>(3);

            @Override
            public void putBitmap(String key, Bitmap value) {
                imageCache.put(key, value);
            }

            @Override
            public Bitmap getBitmap(String key) {
                return imageCache.get(key);
            }
        };

        imageLoader = new ImageLoader(requestQueue, imageCache);
        appKey = Utility.getMetadata(this, APP_KEY_PROPERTY);
    }

    public static void setDefaultFont(Context ctx, String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(ctx.getAssets(), fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field StaticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            StaticField.setAccessible(true);
            StaticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    /**
     * 이미지 로더를 반환한다.
     * @return 이미지 로더
     */
    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public String getAppKey(){
        return appKey;
    }
    /**
     * 애플리케이션 종료시 singleton 어플리케이션 객체 초기화한다.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        instance = null;
    }
    
    // 구글 애널리틱스에서 사용하는 enum객체
	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions 
	}

	// 구글 애널리틱스에 필요한 메소드
	// 다른 패키지에서 접근하려면 public으로 해줘야 함
	public synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
			Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics
					.newTracker(PROPERTY_ID)
					: (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics
							.newTracker(R.xml.global_tracker) : analytics
							.newTracker(R.xml.ecommerce_tracker);
			mTrackers.put(trackerId, t);
		}
		return mTrackers.get(trackerId);
	}
}
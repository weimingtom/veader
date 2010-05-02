package com.android.ray.veader;

import java.util.Locale;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.Log;

public class MyApplication extends Application {

	public static final String APP_NAME = "pdb reader";

	private DataHelper dataHelper;

	public DataHelper getDataHelper() {
		return this.dataHelper;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this.getBaseContext());
		String _loc = prefs.getString("locale", "zh_TW");
		Configuration config;
		String languageToLoad = _loc;
		Locale locale;

		config = new Configuration();
		config.locale = Locale.ENGLISH;
		if (_loc.equals(Locale.TRADITIONAL_CHINESE.toString())) {
			config.locale = Locale.TRADITIONAL_CHINESE;
		}
		if (_loc.equals(Locale.SIMPLIFIED_CHINESE.toString())) {
			config.locale = Locale.SIMPLIFIED_CHINESE;
		}

		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());

		// Log.d(APP_NAME, "APPLICATION onCreate");
		this.dataHelper = new DataHelper(this);
	}

	@Override
	public void onTerminate() {
		Log.d(APP_NAME, "APPLICATION onTerminate");
		super.onTerminate();
	}

	public void setDataHelper(DataHelper dataHelper) {
		this.dataHelper = dataHelper;
	}
}

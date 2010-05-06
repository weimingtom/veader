package org.ray.veader;

import java.util.Locale;

import com.android.ray.veader.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Preferences extends PreferenceActivity {
	private static final int DIAG_COLOR = 3;
	private String fontsize;
	int prefColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		// SharedPreferences customSharedPreference = getSharedPreferences(
		// "fontcolor", Activity.MODE_PRIVATE);
		SharedPreferences colorpref = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		// SharedPreferences.Editor editor = customSharedPreference.edit();
		prefColor = colorpref.getInt("fontcolor", -16777216);

		Log.d("fontoclor", String.valueOf(prefColor));

		Preference customPref = (Preference) findPreference("fontcolor");
		customPref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {

					public boolean onPreferenceClick(Preference preference) {

						showDialog(DIAG_COLOR);

						return true;
					}

				});

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this.getBaseContext());
		String _fontsize = prefs.getString("fontsize", "0");
		Log.d("fontsize?", _fontsize);
		String _locale = prefs.getString("locale", "en_US");
		Log.d("locale?", _locale);
		Preference prefFontsize = (Preference) findPreference("fontsize");
		prefFontsize.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						
						preference.setSummary(getString(R.string.pref_configval)
								+ String.valueOf(newValue));

						SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(Preferences.this
										.getBaseContext());
						SharedPreferences.Editor editor = prefs.edit();
						
						editor.putString("fontsize", String.valueOf(newValue));
						editor.commit();
						//editor.notifyAll();
						
						return true;

					}

				});
		// prefFontsize.setTitle(prefFontsize.getTitle()+":"+_fontsize);
		
		Preference plocale = (Preference) findPreference("locale");
		plocale.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub
						preference.setSummary(getString(R.string.pref_configval)
								+ String.valueOf(newValue));

						SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(Preferences.this
										.getBaseContext());
						SharedPreferences.Editor editor = prefs.edit();

						editor.putString("locale", String.valueOf(newValue));
						editor.commit();
						//editor.notifyAll();
						
					     //String languageToLoad = "fr-rFR";
						String languageToLoad = (String) newValue;
					        //Locale locale = new Locale(languageToLoad);
					       
					        Configuration config = new Configuration();
					        config = new Configuration();
							config.locale = Locale.ENGLISH;
							if (languageToLoad.equals(Locale.TRADITIONAL_CHINESE.toString())) {
								config.locale = Locale.TRADITIONAL_CHINESE;
							}
							if (languageToLoad.equals(Locale.SIMPLIFIED_CHINESE.toString())) {
								config.locale = Locale.SIMPLIFIED_CHINESE;
							}
					       // config.locale = locale;
					        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
					        
						
						return true;

					}

				});
		// prefFontsize.setTitle(prefFontsize.getTitle()+":"+_fontsize);
		plocale.setSummary(getString(R.string.pref_configval) + ":" + _locale);
	}

	private class _colorpicker extends ColorPickerDialog {

		public _colorpicker(Context context) {
			super(context);

			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			this.setColorToViews(prefColor);

		}

		@Override
		public void onColorPicked(View view, int newColor) {

			this.dismiss();
			Log.d("color", String.valueOf(newColor));

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(Preferences.this
							.getBaseContext());
			SharedPreferences.Editor editor = prefs.edit();

			editor.putInt("fontcolor", newColor);
			editor.commit();
			TextView txtday = (TextView) Preferences.this
					.findViewById(R.id.txtfontcolorday);
			TextView txtnight = (TextView) Preferences.this
					.findViewById(R.id.txtfontcolornight);
			txtday.setTextColor(newColor);
			txtnight.setTextColor(newColor);

		}

	}

	protected Dialog onCreateDialog(int id) {
		Log.d("creatingdialog", Integer.toString(id));
		switch (id) {
		case DIAG_COLOR:
			return new _colorpicker(this);
		}
		return null;
	}
}

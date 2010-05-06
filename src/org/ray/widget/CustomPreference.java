package org.ray.widget;


import org.ray.veader.Preferences;
import org.ray.veader.R;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CustomPreference extends Preference {
    public int mColor;
   Context ctx;
    // This is the constructor called by the inflater
    public CustomPreference(Context context, AttributeSet attrs) {
        super(context, attrs);       
        ctx = context;
        setWidgetLayoutResource(R.layout.custompreference);       
    }
    
    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        SharedPreferences prefs = ctx.getSharedPreferences("fontcolor",  Activity.MODE_PRIVATE);
        SharedPreferences _p =  PreferenceManager.getDefaultSharedPreferences(ctx.getApplicationContext());
        mColor = _p.getInt("fontcolor", 0); 
        Log.d("bb", String.valueOf(mColor));
TextView txtday = (TextView)view.findViewById(R.id.txtfontcolorday);
TextView txtnight = (TextView)view.findViewById(R.id.txtfontcolornight);
txtday.setTextColor(mColor);
txtnight.setTextColor(mColor);
persistInt(mColor);
        //SharedPreferences customSharedPreference = getSharedPreferences();
		// SharedPreferences.Editor editor = customSharedPreference.edit();
		//prefColor= customSharedPreference.getInt("fontcolor", 0);
    }
   
   @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, 0);
    }

 @Override
 protected void onClick() {
/*  // TODO Auto-generated method stub
  int nKeep = mClickCounter + 1;  
  callChangeListener(nKeep);//add lock
  mClickCounter = nKeep;
  persistInt(mClickCounter);
  notifyChanged();//notify UI to refresh
  
*/  
	 super.onClick();
 }
 public void setval(int _color){
	 persistInt(_color);
 }
 @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        if (restoreValue) {
            // Restore state
        	mColor = getPersistedInt(mColor);
        } else {
            // Set state
            int value = (Integer) defaultValue;
            mColor = value;
            persistInt(mColor);
        }
    }    
}
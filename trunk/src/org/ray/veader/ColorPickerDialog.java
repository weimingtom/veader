package org.ray.veader;

import org.ray.widget.ColorCircle;
import org.ray.widget.ColorSlider;
import org.ray.widget.OnColorChangedListener;

import com.android.ray.veader.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class ColorPickerDialog extends Dialog implements OnColorChangedListener {

	public ColorPickerDialog(Context context) {
	
		super(context, R.style.myCoolDialog);
	
	}
	TextView txtsamplefont, txtsamplefont2;
	ColorCircle mColorCircle;
	ColorSlider mSaturation;
	ColorSlider mValue;

	Intent mIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		txtsamplefont = (TextView) findViewById(R.id.txtsamplefont);
		txtsamplefont2 = (TextView) findViewById(R.id.txtsamplefontb);
		setContentView(R.layout.colorpicker);

		// Get original color

		mIntent = new Intent();
		this.setTitle(R.string.title_fontcolor);

		// setTheme(R.style.Theme_Translucent);

		mColorCircle = (ColorCircle) findViewById(R.id.colorcircle);

		mColorCircle.setOnColorChangedListener(this);
		int color = 0;
		final ColorPickerState state = (ColorPickerState) onRetainNonConfigurationInstance();
		if (state != null) {
			color = state.mColor;
		} else {
			// color = mIntent.getIntExtra(FlashlightIntents.EXTRA_COLOR,
			// Color.BLACK);
		}

		mColorCircle.setColor(color);

		mSaturation = (ColorSlider) findViewById(R.id.saturation);
		mSaturation.setOnColorChangedListener(this);
		mSaturation.setColors(color, Color.BLACK);

		mValue = (ColorSlider) findViewById(R.id.value);
		mValue.setOnColorChangedListener(this);
		mValue.setColors(Color.WHITE, color);
	}

	class ColorPickerState {
		int mColor;
	}

	public void setColorToViews(int color) {
		mColorCircle.setColor(color);
		txtsamplefont = (TextView) findViewById(R.id.txtsamplefont);
		txtsamplefont2 = (TextView) findViewById(R.id.txtsamplefontb);
		txtsamplefont.setTextColor(color);
		mSaturation.setColors(color, 0xff000000);
		txtsamplefont2.setTextColor(color);
		mValue.setColors(Color.WHITE,color );
	}

	public Object onRetainNonConfigurationInstance() {
		ColorPickerState state = new ColorPickerState();
		state.mColor = this.mColorCircle.getColor();
		return state;
	}

	public int toGray(int color) {
		int a = Color.alpha(color);
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		int gray = (r + g + b) / 3;
		return Color.argb(a, gray, gray, gray);
	}
	public void onColorChanged(View view, int newColor) {
		if (view == mColorCircle) {
			mValue.setColors(0xFFFFFFFF, newColor);
			mSaturation.setColors(newColor, 0xff000000);
		} else if (view == mSaturation) {
			mColorCircle.setColor(newColor);
			mValue.setColors(0xFFFFFFFF, newColor);
		} else if (view == mValue) {
			mColorCircle.setColor(newColor);
		}
		txtsamplefont = (TextView) findViewById(R.id.txtsamplefont);
		txtsamplefont2 = (TextView) findViewById(R.id.txtsamplefontb);
		txtsamplefont.setTextColor(newColor);

		txtsamplefont2.setTextColor(newColor);
	}

	public void onColorPicked(View view, int newColor) {
		// We can return result
		// mIntent.putExtra("veader.fontcolor", newColor);

		// setResult(RESULT_OK, mIntent);
		// finish();
	}
}
package com.android.ray.veader;


import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.widget.TextView;
import android.graphics.Color;

public class gestureActivity extends Activity implements OnGestureListener
{    
    private LinearLayout main;    
    private TextView viewA;
   
    private GestureDetector gestureScanner;
     
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
       
        gestureScanner = new GestureDetector(this);
       
        main = new LinearLayout(this);
        main.setBackgroundColor(Color.GRAY);
        main.setLayoutParams(new LinearLayout.LayoutParams(320,480));
       
        viewA = new TextView(this);
        viewA.setBackgroundColor(Color.YELLOW);
        viewA.setTextColor(Color.BLACK);
        viewA.setTextSize(16);
        viewA.setLayoutParams(new LinearLayout.LayoutParams(320,80));
        main.addView(viewA);
       
        setContentView(main);
    }
   
    @Override
    public boolean onTouchEvent(MotionEvent me)
    {
     return gestureScanner.onTouchEvent(me);
    }
   
    @Override
    public boolean onDown(MotionEvent e)
    {
     viewA.setText("-" + "DOWN" + "-");
     return true;
    }
   
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
     viewA.setText("-" + "FLING" + "-");
     return true;
    }
   
    @Override
    public void onLongPress(MotionEvent e)
    {
     viewA.setText("-" + "LONG PRESS" + "-");
    }
   
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
     viewA.setText("-" + "SCROLL" + "-");
     return true;
    }
   
    @Override
    public void onShowPress(MotionEvent e)
    {
     viewA.setText("-" + "SHOW PRESS" + "-");
    }    
   
    @Override
    public boolean onSingleTapUp(MotionEvent e)    
    {
     viewA.setText("-" + "SINGLE TAP UP" + "-");
     return true;
    }
} 
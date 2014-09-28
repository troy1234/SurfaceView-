package com.example.aaa_androidtest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {
	private MyView_Three paletteView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		
		
		paletteView = (MyView_Three) findViewById(R.id.myview_three);
	}

	public void printLog(String message) {
		Log.v("tag", message);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		return paletteView.onTouchEvent(event);
	}
}

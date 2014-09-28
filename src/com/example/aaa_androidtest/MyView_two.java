package com.example.aaa_androidtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

public class MyView_two extends View {

	public MyView_two(Context context) {
		super(context);
	}

	public MyView_two(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyView_two(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	private int x = 0;
	private int y = 0;
	private int r = 30;
	
	private Paint mPaint = null;
	Bitmap bitmap = null,backgroundBitmap=null;
	Canvas mcanvas = null;
	public static final int SNAP_VELOCITY = 2500;
	private VelocityTracker mVelocityTracker;
	{
		setBackgroundResource(R.drawable.cp365_newbg);
		setFocusable(true);
		mPaint = new Paint();
		mPaint.setAlpha(0);
		mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		mPaint.setAntiAlias(true);
		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.bg_kuaisan);
		bitmap = Bitmap.createBitmap(400, 800, Config.ARGB_8888);
		mcanvas = new Canvas();
		mcanvas.setBitmap(bitmap);
		mcanvas.drawBitmap(bm, 0, 0, null);
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				createVelocityTracker(event);
				Log.v("tag", "speed:"+getScrollVelocity());
				if(getScrollVelocity()>SNAP_VELOCITY)return true;
				x = (int) event.getX();
				y = (int) event.getY();
				invalidate(x-r, y-r, x+r, y+r);
				return true;
			}
		});
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		mcanvas.drawCircle(x, y, r, mPaint);
		canvas.drawBitmap(bitmap, 0, 0, null);
		super.onDraw(canvas);
	}
	
	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getXVelocity()+(int)mVelocityTracker.getYVelocity();
		return Math.abs(velocity);
	}

	private void createVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}
	
	
}

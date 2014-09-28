package com.example.aaa_androidtest;

import java.util.LinkedList;
import java.util.Queue;

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
import android.view.SurfaceView;
import android.view.View;

public class MyView extends SurfaceView {

	public MyView(Context context) {
		super(context);
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	private final static String TAG = "DrawView";
	 private int mX = 0;
	 private int mY = 0;
	private int r = 40;
	LinkedList<Integer> queue_X;
	LinkedList<Integer> queue_Y;
	DrawThread mThread;

	public LinkedList<Integer> getQueueX() {
		if (queue_X == null)
			queue_X = new LinkedList<Integer>();
		return queue_X;
	}

	public LinkedList<Integer> getQueueY() {
		if (queue_Y == null)
			queue_Y = new LinkedList<Integer>();
		return queue_Y;
	}

	private Paint mPaint = null;
	Bitmap bitmap = null, backgroundBitmap = null;
	Canvas mcanvas = null;
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
		mThread=new DrawThread();
		mThread.start();
		this.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				getQueueX().addFirst((int) event.getX());
				getQueueY().addFirst((int) event.getY());
				return true;
			}
		});
	}

	int drawCount = 0, touchCount = 0;
	long time = 0;

	@Override
	protected void onDraw(Canvas canvas) {
//		long currentTime = System.currentTimeMillis();
//		Log.v("tag", "ondraw:" + drawCount + " ,touchcount:" + touchCount + ",draw time:" + (currentTime - time));
//		time = currentTime;
//		mcanvas.drawCircle(mX, mY, r, mPaint);
//		canvas.drawBitmap(bitmap, 0, 0, null);
		super.onDraw(canvas);
	}
	
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		long currentTime = System.currentTimeMillis();
		Log.v("tag", "ondraw:" + drawCount + " ,touchcount:" + touchCount + ",draw time:" + (currentTime - time));
		time = currentTime;
		mcanvas.drawCircle(mX, mY, r, mPaint);
		canvas.drawBitmap(bitmap, 0, 0, null);
		
		super.draw(canvas);
	}

	class DrawThread extends Thread {
		@Override
		public void run() {
			while (true) {
				if (getQueueX().size() > 0&&getQueueY().size()>0) {
					int x = getQueueX().remove();
					int y = getQueueY().remove();
					mX=x;
					mY=y;
					postInvalidate(x - r, y - r, x + r, y + r);
				}
			}
		}

	}

}

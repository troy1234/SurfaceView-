package com.example.aaa_androidtest;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MyView_Three extends SurfaceView implements Runnable,
		SurfaceHolder.Callback {
	// 画板的坐标以及宽高
	private int bgBitmapX = 0;
	private int bgBitmapY = 0;
	private int bgBitmapHeight = 1280;
	private int bgBitmapWidth = 720;
	//手指刮开的面积
	int currentSize = 0;
	//手指滑动动作添加
	private int currentPaintIndex = -1;
	//手指刮开部分颜色
	int currentColor = 0;
	// 存储所有的动作
	private ArrayList<Action> actionList = null;
	// 当前的画笔实例
	private Action curAction = null;
	// 线程结束标志位
	boolean mLoop = true;
	
	SurfaceHolder mSurfaceHolder = null;
	// 绘图区蒙版图片
	Bitmap mMaskBitmap = null;
	// 绘图区背景图片
	Bitmap mBgBitmap=null;
	// 临时画板用来显示之前已经绘制过的图像
	Canvas canvasTemp=null;
	//绘制一张空图片
	Bitmap newbit = null;
	//是否已经记载过底图
	boolean isLoadBgFinish=false;

	public MyView_Three(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		actionList = new ArrayList<Action>();
		mSurfaceHolder = this.getHolder();
		mSurfaceHolder.addCallback(this);
		this.setFocusable(true);
		mLoop = true;

		mMaskBitmap = ((BitmapDrawable) (getResources()
				.getDrawable(R.drawable.xxxy_mask))).getBitmap();
		
		mMaskBitmap=ScalePicture(mMaskBitmap,bgBitmapWidth,bgBitmapHeight);
		
		// 绘制蒙版图（上层图）
		newbit = Bitmap.createBitmap(bgBitmapWidth, bgBitmapHeight,Config.ARGB_4444);
		canvasTemp = new Canvas(newbit);
		canvasTemp.drawBitmap(mMaskBitmap, bgBitmapX, bgBitmapY, null);
		
		mBgBitmap = ((BitmapDrawable) (getResources()
				.getDrawable(R.drawable.xxxy_open_bottom))).getBitmap();
		
		mBgBitmap=ScalePicture(mBgBitmap,bgBitmapWidth,bgBitmapHeight);
		
		new Thread(this).start();

	}

	public MyView_Three(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		int antion = event.getAction();
		if (antion == MotionEvent.ACTION_CANCEL) {
			return false;
		}

		float touchX = event.getX();
		float touchY = event.getY();

		// 点击时
		if (antion == MotionEvent.ACTION_DOWN) {
			// 检测点击点是否在主绘图区
			currentSize = 40;
			if (testTouchMainPallent(touchX, touchY)) {
				setCurAction(getRealX(touchX), getRealY(touchY));
				clearSpareAction();
			}
		}
		// 拖动时
		if (antion == MotionEvent.ACTION_MOVE) {
			if (curAction != null) {
				curAction.move(getRealX(touchX), getRealY(touchY));
			}
		}
		// 抬起时
		if (antion == MotionEvent.ACTION_UP) {
			if (curAction != null) {
				curAction.move(getRealX(touchX), getRealY(touchY));
				actionList.add(curAction);
				currentPaintIndex++;
				curAction = null;
			}
		}
		return super.onTouchEvent(event);

	}

	// 检测点击事件，是否在主绘图区
	public boolean testTouchMainPallent(float x, float y) {
		if (x > bgBitmapX && y > bgBitmapY && x < bgBitmapX + bgBitmapWidth
				&& y < bgBitmapY + bgBitmapHeight) {
			return true;
		}

		return false;
	}

	// 后退前进完成后，缓存的动作
	private void clearSpareAction() {
		for (int i = actionList.size() - 1; i > currentPaintIndex; i--) {
			actionList.remove(i);
		}
	}

	// 根据接触点x坐标得到画板上对应x坐标
	public float getRealX(float x) {
		return x;
	}

	// 根据接触点y坐标得到画板上对应y坐标
	public float getRealY(float y) {
		return y;
	}

	// 得到当前画笔的类型，并进行实例
	public void setCurAction(float x, float y) {
		// curAction = new MyPoint(x, y, currentColor);

		curAction = new MyEraser(x, y, currentSize, currentColor);
	}

	// 绘图
	protected void Draw() {
		Canvas canvas = mSurfaceHolder.lockCanvas();
		if (mSurfaceHolder == null || canvas == null) {
			return;
		}
		// 画主画板
		drawMainPallent(canvas);
		
		mSurfaceHolder.unlockCanvasAndPost(canvas);
	}
	
	/**
	 * 缩放图片的方法 描 述：<br/>
	 * 作 者：yangyuheng<br/>
	 * 历 史: (版本) 作者 时间 注释 <br/>
	 * 
	 * @param bitmapOrg
	 *            待缩放的原图
	 * @param newWidth
	 *            新图的宽
	 * @param newHeight
	 *            新图的高
	 * @return
	 */
	public Bitmap ScalePicture(Bitmap bitmapOrg, int newWidth, int newHeight) {

		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);

		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);

		return resizedBitmap;
	}
	

	// 画主画板
	private void drawMainPallent(Canvas canvas) {
		// 画板绘图区背景图片
		canvas.drawBitmap(mBgBitmap, bgBitmapX, bgBitmapX, null);
		
		for (int i = 0; i <= currentPaintIndex; i++) {
			actionList.get(i).draw(canvasTemp);
		}
		// 画当前画笔痕迹
		if (curAction != null) {
			curAction.draw(canvasTemp);
		}

		// 将这张上层图绘制在页面上，这样修改上层图就可以了
		canvas.drawBitmap(newbit, bgBitmapX, bgBitmapX, null);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (mLoop) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			synchronized (mSurfaceHolder) {
				Draw();
			}
		}
	}

}

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
	// ����������Լ����
	private int bgBitmapX = 0;
	private int bgBitmapY = 0;
	private int bgBitmapHeight = 1280;
	private int bgBitmapWidth = 720;
	//��ָ�ο������
	int currentSize = 0;
	//��ָ�����������
	private int currentPaintIndex = -1;
	//��ָ�ο�������ɫ
	int currentColor = 0;
	// �洢���еĶ���
	private ArrayList<Action> actionList = null;
	// ��ǰ�Ļ���ʵ��
	private Action curAction = null;
	// �߳̽�����־λ
	boolean mLoop = true;
	
	SurfaceHolder mSurfaceHolder = null;
	// ��ͼ���ɰ�ͼƬ
	Bitmap mMaskBitmap = null;
	// ��ͼ������ͼƬ
	Bitmap mBgBitmap=null;
	// ��ʱ����������ʾ֮ǰ�Ѿ����ƹ���ͼ��
	Canvas canvasTemp=null;
	//����һ�ſ�ͼƬ
	Bitmap newbit = null;
	//�Ƿ��Ѿ����ع���ͼ
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
		
		// �����ɰ�ͼ���ϲ�ͼ��
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

		// ���ʱ
		if (antion == MotionEvent.ACTION_DOWN) {
			// ��������Ƿ�������ͼ��
			currentSize = 40;
			if (testTouchMainPallent(touchX, touchY)) {
				setCurAction(getRealX(touchX), getRealY(touchY));
				clearSpareAction();
			}
		}
		// �϶�ʱ
		if (antion == MotionEvent.ACTION_MOVE) {
			if (curAction != null) {
				curAction.move(getRealX(touchX), getRealY(touchY));
			}
		}
		// ̧��ʱ
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

	// ������¼����Ƿ�������ͼ��
	public boolean testTouchMainPallent(float x, float y) {
		if (x > bgBitmapX && y > bgBitmapY && x < bgBitmapX + bgBitmapWidth
				&& y < bgBitmapY + bgBitmapHeight) {
			return true;
		}

		return false;
	}

	// ����ǰ����ɺ󣬻���Ķ���
	private void clearSpareAction() {
		for (int i = actionList.size() - 1; i > currentPaintIndex; i--) {
			actionList.remove(i);
		}
	}

	// ���ݽӴ���x����õ������϶�Ӧx����
	public float getRealX(float x) {
		return x;
	}

	// ���ݽӴ���y����õ������϶�Ӧy����
	public float getRealY(float y) {
		return y;
	}

	// �õ���ǰ���ʵ����ͣ�������ʵ��
	public void setCurAction(float x, float y) {
		// curAction = new MyPoint(x, y, currentColor);

		curAction = new MyEraser(x, y, currentSize, currentColor);
	}

	// ��ͼ
	protected void Draw() {
		Canvas canvas = mSurfaceHolder.lockCanvas();
		if (mSurfaceHolder == null || canvas == null) {
			return;
		}
		// ��������
		drawMainPallent(canvas);
		
		mSurfaceHolder.unlockCanvasAndPost(canvas);
	}
	
	/**
	 * ����ͼƬ�ķ��� �� ����<br/>
	 * �� �ߣ�yangyuheng<br/>
	 * �� ʷ: (�汾) ���� ʱ�� ע�� <br/>
	 * 
	 * @param bitmapOrg
	 *            �����ŵ�ԭͼ
	 * @param newWidth
	 *            ��ͼ�Ŀ�
	 * @param newHeight
	 *            ��ͼ�ĸ�
	 * @return
	 */
	public Bitmap ScalePicture(Bitmap bitmapOrg, int newWidth, int newHeight) {

		int width = bitmapOrg.getWidth();
		int height = bitmapOrg.getHeight();

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// ��������ͼƬ�õ�matrix����
		Matrix matrix = new Matrix();

		// ����ͼƬ����
		matrix.postScale(scaleWidth, scaleHeight);

		// �����µ�ͼƬ
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width, height, matrix, true);

		return resizedBitmap;
	}
	

	// ��������
	private void drawMainPallent(Canvas canvas) {
		// �����ͼ������ͼƬ
		canvas.drawBitmap(mBgBitmap, bgBitmapX, bgBitmapX, null);
		
		for (int i = 0; i <= currentPaintIndex; i++) {
			actionList.get(i).draw(canvasTemp);
		}
		// ����ǰ���ʺۼ�
		if (curAction != null) {
			curAction.draw(canvasTemp);
		}

		// �������ϲ�ͼ������ҳ���ϣ������޸��ϲ�ͼ�Ϳ�����
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

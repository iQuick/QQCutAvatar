package me.imli.qqcutavatar.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;

public class CutAvatarView extends ImageView {

	/** 圆环半径大小 */
	private static final float DEF_FRAME_WIDTH = 4.0f;
	/** 圆占屏幕的比例大小 */
	private static final float DEF_CLICK_SIZE = 1 / 3f;
	
	// 最大/小缩放系数
	private static final float MAX_SCALE = 5.0f;
	private static final float MIN_SCALE = 0.3f;
	
	private boolean isTouCutBitmap = false;
	
	/** Matrix矩阵 */
	Matrix mMatrix;
	/** 用于保存原矩阵 */
	Matrix mSaveMatrix;
	/** 位图对象 */
	private Bitmap mImgBitmap;
	/** 屏幕的分辨率 */

	/** 初始状态 */
	static final int NONE = 0;
	/** 拖动 */
	static final int DRAG = 1;
	/** 缩放 */
	static final int ZOOM = 2;
	/** 当前模式 */
	int mCurMode = NONE;

	/** 圆的半径 */
	private float mCircleRadius;
	/** 蒙版画笔 */
	private Paint mRectPaint, mCirclePaint;
	/** 圆环画笔 */
	private Paint mRoundPaint;
	private RectF mRectF;

	public CutAvatarView(Context context) {
		super(context);
		init();
	}

	public CutAvatarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CutAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	private void init() {
		mMatrix = new Matrix();
		mSaveMatrix = new Matrix();
		// 初始化画笔
		mRectPaint = new Paint();
		mRectPaint.setAntiAlias(true);
		mRectPaint.setColor(0xB0000000);
		mCirclePaint = new Paint();
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		mCirclePaint.setColor(0xffffffff);
		mRoundPaint = new Paint();
		mRoundPaint.setColor(0xffffffff);
		mRoundPaint.setStyle(Style.STROKE);
		mRoundPaint.setAntiAlias(true);
		mRoundPaint.setStrokeWidth(DEF_FRAME_WIDTH);
	}
	
	/**
	 * 截取头像
	 * @return
	 */
	public Bitmap clip() {
		return clip(false);
	}
	
	
	/**
	 * 截取头像
	 * @param isCircle 是滞是圆形
	 * @return
	 */
	public Bitmap clip(boolean isCircle) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		// 为了不带半透明的背景，从新刷新下imageview 好获干净的位图 然后截取
		invalidate();
		// 截图
		isTouCutBitmap = true;
		setDrawingCacheEnabled(true);
		Bitmap bitmap = getDrawingCache().copy(getDrawingCache().getConfig(), false);
		setDrawingCacheEnabled(false);
		Bitmap head = Bitmap.createBitmap((int)mCircleRadius * 2, (int)mCircleRadius * 2, Config.ARGB_8888);
		Canvas canvas = new Canvas(head);
		if (isCircle) {
			canvas.drawRoundRect(new RectF(0, 0, 2 * mCircleRadius, 2 * mCircleRadius), mCircleRadius, mCircleRadius, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		}
		
		RectF dst = new RectF(-bitmap.getWidth() / 2 + mCircleRadius, -getHeight() / 2 + mCircleRadius, bitmap.getWidth() - bitmap.getWidth() / 2 + mCircleRadius, getHeight() - getHeight() / 2 + mCircleRadius);
		canvas.drawBitmap(bitmap, null, dst, paint);
		isTouCutBitmap = false;
		return head;
	}
	
	// 计算圆半径及是否需要进行缩放
	private void comSize(int width, int height) {
		if (width == 0 || height == 0) return;
		if (width < height) {
			mCircleRadius = width * DEF_CLICK_SIZE;
		} else {
			mCircleRadius = height * DEF_CLICK_SIZE;
		}

		// 获取图片
		BitmapDrawable bd = (BitmapDrawable) this.getDrawable();
		if (bd != null) {
			mImgBitmap = bd.getBitmap();
		}
		// 设置ScaleType为ScaleType.MATRIX，这一步很重要
		setScaleType(ScaleType.MATRIX);
		setImageBitmap(mImgBitmap);

		// 居中
		center(true, true);
		setImageMatrix(mMatrix);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		comSize(getWidth(), getHeight());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// 在截取头像中
		if (isTouCutBitmap) return;
		if (mRectF == null || mRectF.isEmpty()) mRectF = new RectF(0, 0, getWidth(), getHeight());
		int sc = canvas.saveLayer(mRectF, null, Canvas.MATRIX_SAVE_FLAG
				| Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
				| Canvas.FULL_COLOR_LAYER_SAVE_FLAG
				| Canvas.CLIP_TO_LAYER_SAVE_FLAG | Canvas.ALL_SAVE_FLAG);
		// 绘制蒙版
		canvas.drawRect(mRectF, mRectPaint);
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, mCircleRadius, mCirclePaint);
		canvas.restoreToCount(sc);
		// 绘制圆环
		canvas.drawCircle(getWidth() / 2, getHeight() / 2, mCircleRadius, mRoundPaint);
	}

	/** 存储float类型的x，y值，就是你点下的坐标的X和Y */
	private PointF mPre = new PointF(), mMid = new PointF();
	private float dist = 1f;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mCurMode = DRAG;
			mSaveMatrix.set(mMatrix);
			mPre.set(event.getX(), event.getY());
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mCurMode = ZOOM;
			mSaveMatrix.set(mMatrix);
			dist = spacing(event);
			midPoint(mMid, event);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mCurMode == DRAG) {
				mMatrix.set(mSaveMatrix);
				mMatrix.postTranslate(event.getX() - mPre.x, event.getY() - mPre.y);
			} else if (mCurMode == ZOOM) {
				float scale = spacing(event) / dist;;
				mMatrix.set(mSaveMatrix);
				mMatrix.getValues(mMXValues);
				if (mMXValues[0] * scale > MAX_SCALE) {
					scale = MAX_SCALE / mMXValues[0];
				} else if (mMXValues[0] * scale < MIN_SCALE) {
					scale = MIN_SCALE / mMXValues[0];
				}
				mMatrix.postScale(scale, scale, mMid.x, mMid.y);
			} else if (mCurMode == NONE) {
			}
			checkBoundary(mMatrix);
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mCurMode = NONE;
			break;
		case MotionEvent.ACTION_UP:
			mCurMode = NONE;
			break;
		default:
			break;
		}
		setImageMatrix(mMatrix);
		return true;
	}
	
	float[] mMXValues = new float[9];
	/**
	 * 检查是否越界
	 * @param matrix
	 */
	protected void checkBoundary(Matrix matrix) {
		if (matrix == null) return;
		matrix.getValues(mMXValues);
		
		// 计算缩放是否小于要截取的大小
		float size = mImgBitmap.getWidth() < mImgBitmap.getHeight() ? mImgBitmap.getWidth() : mImgBitmap.getHeight();
        if (mMXValues[0] < mCircleRadius * 2 / size || mMXValues[4] < mCircleRadius * 2 / size) {
        	mMXValues[0] = mCircleRadius * 2 / size;
        	mMXValues[4] = mCircleRadius * 2 / size;
        }
        // 计算位移是否越界
        if (mMXValues[2] > getWidth() / 2 - mCircleRadius) {
        	mMXValues[2] = getWidth() / 2 - mCircleRadius;
        }
        if (mMXValues[5] > getHeight() / 2 - mCircleRadius) {
        	mMXValues[5] = getHeight() / 2 - mCircleRadius;
        }
        if (mMXValues[2] < - mImgBitmap.getWidth() * mMXValues[0] + getWidth() / 2 + mCircleRadius) {
        	mMXValues[2]= - mImgBitmap.getWidth() * mMXValues[0] + getWidth() / 2 + mCircleRadius;
        }
        if (mMXValues[5] < - mImgBitmap.getHeight() * mMXValues[4] + getHeight() / 2 + mCircleRadius) {
        	mMXValues[5]= - mImgBitmap.getHeight() * mMXValues[4] + getHeight() / 2 + mCircleRadius;
        }
        matrix.setValues(mMXValues);
	}
	
	/**
	 * 横向、纵向居中
	 */
	protected void center(boolean horizontal, boolean vertical) {

		// 判断是否要进行缩放
		float size = mImgBitmap.getWidth() > mImgBitmap.getHeight() ? mImgBitmap.getHeight() : mImgBitmap.getWidth();
		if (size < mCircleRadius * 2 || size < mCircleRadius * 2) {
			float scale = mCircleRadius * 2 / size;
			mMatrix.setScale(scale, scale);
			setImageMatrix(mMatrix);
		}
		
		// 居中
		Matrix m = new Matrix();
		m.set(mMatrix);
		RectF rect = new RectF(0, 0, mImgBitmap.getWidth(), mImgBitmap.getHeight());
		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			// 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
			int screenHeight = getHeight();
			if (height < screenHeight) {
				deltaY = (screenHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < screenHeight) {
				deltaY = this.getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			int screenWidth = getWidth();
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < screenWidth) {
				deltaX = screenWidth - rect.right;
			}
		}
		mMatrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * 两点的距离
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * 两点的中点
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}

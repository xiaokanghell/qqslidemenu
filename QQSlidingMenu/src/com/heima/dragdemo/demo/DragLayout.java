package com.heima.dragdemo.demo;

import android.R.color;
import android.animation.FloatEvaluator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.PropertyValuesHolder;
import com.nineoldandroids.animation.TypeEvaluator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class DragLayout extends FrameLayout {

	private TextView redView, blueView;
	private ViewDragHelper viewDragHelper;

	public DragLayout(Context context) {
		super(context);
		init();
	}

	public DragLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DragLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		viewDragHelper = ViewDragHelper.create(this, callback);
		
	}

	/**
	 * 当xml读取完成时走这个方法，可以在这里初始化ChildView
	 */
	@Override
	protected void onFinishInflate() {
		redView = (TextView) getChildAt(0);
		blueView = (TextView) getChildAt(1);

	}

	
	// @Override
	// protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	// super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	// //要测量我自己的子View
	// // int size = getResources().getDimension(R.dimen.width);//100dp
	// // int measureSpec =
	// MeasureSpec.makeMeasureSpec(redView.getLayoutParams().width,MeasureSpec.EXACTLY);
	// // redView.measure(measureSpec,measureSpec);
	// // blueView.measure(measureSpec, measureSpec);
	//
	// //如果说没有特殊的对子View的测量需求，可以用如下方法
	// measureChild(redView, widthMeasureSpec, heightMeasureSpec);
	// measureChild(blueView, widthMeasureSpec, heightMeasureSpec);
	// }


	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		int l = getPaddingLeft() ;
		int t = getPaddingTop();
		System.out.println("blueLeft :" + l);
		blueView.layout(l, t, l + blueView.getMeasuredWidth(),
				t + blueView.getMeasuredHeight());
		redView.layout(l, blueView.getBottom(), l + redView.getMeasuredWidth(),
				blueView.getBottom() + redView.getMeasuredHeight());

		// 此处无需调用父类的onLayout方法，因为继承的是framelayout
		// super.onLayout(changed, left, top, right, bottom);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// 将触摸事件交给ViewDragHelper来解析处理
		viewDragHelper.processTouchEvent(event);
		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		// 让ViewDragHelper帮我们判断是否应该拦截
		boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
		return result;
	}

	private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {

			return child == redView || child == blueView;
		}

		/**
		 * 获取view水平方向的拖拽范围,但是目前不能限制边界,返回的值目前用在手指抬起的时候view缓慢移动的动画世界的计算上面; 最好不要返回0
		 */
		@Override
		public int getViewHorizontalDragRange(View child) {
			int dtx = getMeasuredWidth() - child.getMeasuredWidth();

			return dtx;
		}

		/**
		 * 获取view垂直方向的拖拽范围，最好不要返回0
		 */
		@Override
		public int getViewVerticalDragRange(View child) {

			int dtx = getMeasuredHeight() - child.getMeasuredHeight();
			return dtx;
		}

		/**
		 * 控制child在水平方向的移动 left:
		 * 表示ViewDragHelper认为你想让当前child的left改变的值,left=chile.getLeft()+dx dx:
		 * 本次child水平方向移动的距离 return: 表示你真正想让child的left变成的值
		 */
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			// 不能超出边界
			if (left < 0)
				left = 0;
			if (left > (getMeasuredWidth() - child.getMeasuredWidth()))
				left = getMeasuredWidth() - child.getMeasuredWidth();

			return left;
		}

		/**
		 * 控制child在垂直方向的移动 top:
		 * 表示ViewDragHelper认为你想让当前child的top改变的值,top=chile.getTop()+dy dy:
		 * 本次child垂直方向移动的距离 return: 表示你真正想让child的top变成的值
		 */
		@Override
		public int clampViewPositionVertical(View child, int top, int dy) {
			// 不能超出边界
			if (top < 0)
				top = 0;
			if (top > getMeasuredHeight() - child.getMeasuredHeight())
				top = getMeasuredHeight() - child.getMeasuredHeight();

			return top;
		}

		/**
		 * 当child的位置改变的时候执行,一般用来做其他子View的伴随移动 changedView：位置改变的child
		 * left：child当前最新的left top: child当前最新的top dx: 本次水平移动的距离 dy: 本次垂直移动的距离
		 */
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {

			if (changedView == redView) {
				// 让blueView一起移动

				top = top - blueView.getMeasuredHeight();
				blueView.layout(left, top, left + blueView.getMeasuredWidth(),
						top + blueView.getMeasuredHeight());
			} else if (changedView == blueView) {
				top = top + blueView.getMeasuredHeight();
				redView.layout(left, top, left + redView.getMeasuredWidth(),
						top + redView.getMeasuredHeight());
			}
			// int和int相乘得到一个int，小数的int直接舍成了0，所有需要乘以1.0f先转换成float值
			float fraction = changedView.getLeft() * 1.0f
					/ (getMeasuredWidth() - changedView.getMeasuredWidth());
			System.out.println("fraction + " + fraction);
			excuteAnim(fraction);
		}

		/**
		 * 手指抬起的执行该方法， releasedChild：当前抬起的view xvel: x方向的移动的速度 正：向右移动， 负：向左移动
		 * yvel: y方向移动的速度
		 */
		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			// 居中位置的值
			int middleValue = (getMeasuredWidth() - releasedChild
					.getMeasuredWidth()) / 2;
			if (releasedChild.getLeft() < middleValue) {// 要移动还用重写父类的computeScroll()方法
				// 执行移动到左边的动画
				viewDragHelper.smoothSlideViewTo(releasedChild, 0,
						releasedChild.getTop());
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			} else {
				// y移动到右边
				viewDragHelper.smoothSlideViewTo(releasedChild,
						getMeasuredWidth() - releasedChild.getMeasuredWidth(),
						releasedChild.getTop());
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			}

		}

	};

	public void computeScroll() {
		if (viewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(DragLayout.this);
		}
	}

	/**
	 * 执行属性动画
	 * 
	 * @param fraction
	 */
	protected void excuteAnim(float fraction) {
		// fraction: 0 - 1
		// 缩放
		
		ViewHelper.setScaleX(blueView, 1 + 0.5F*fraction);
		ViewHelper.setScaleY(blueView, 1 + 0.5F*fraction);
		
		//旋转
//		ViewHelper.setRotationX(blueView, 360*fraction);//x轴方向旋转
//		ViewHelper.setRotationY(blueView, 360*fraction);//y轴方向旋转
		
		//平移
//		ViewHelper.setTranslationX(blueView, 100*fraction);
		
		////透明
//		ViewHelper.setAlpha(blueView, 1 -fraction);
		
		
		//设置过度颜色的渐变
//		blueView.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.BLUE, Color.BLACK));
		//设置背景颜色的渐变
//		setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.TRANSPARENT, Color.RED));
	};

}

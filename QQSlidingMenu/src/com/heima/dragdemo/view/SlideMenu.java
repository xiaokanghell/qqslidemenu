package com.heima.dragdemo.view;

import com.heima.dragdemo.ColorUtil;
import com.heima.dragdemo.R;
import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.view.ViewHelper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class SlideMenu extends FrameLayout {

	private View layout_main, layout_menu;
	private int dragRange;
	private int width;
	private DragState mCurrentState = DragState.Close;

	public SlideMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlideMenu(Context context) {
		super(context);
		init();
	}

	private void init() {
		viewDragHelper = ViewDragHelper.create(this, callback);
		floatEvaluator = new FloatEvaluator();
	}

	/**
	 * xml读取完成时，初始化view
	 */
	@Override
	protected void onFinishInflate() {
		// 简单的异常处理,让这个view只能有两个子view，一个侧拉栏，一个主界面
		if (getChildCount() != 2) {
			throw new IllegalArgumentException(
					"SlideMenu can only have 2 childView!");
		}
		layout_main = findViewById(R.id.layout_main);
		layout_menu = findViewById(R.id.layout_menu);

	}

	/**
	 * 该方法在onMeasure执行完之后执行，那么可以在该方法中初始化自己和子View的宽高
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width = getMeasuredWidth();// 控件的宽度
		dragRange = (int) (width * 0.6f);// 主view移动的范围

	}

	private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {

			return child == layout_main || child == layout_menu;//
		}

		@Override
		public int getViewHorizontalDragRange(View child) {

			return (int) dragRange;// 范围，此处不要写0
		}

		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {

			if (child == layout_main) {// 限制边界
				if (left < 0)
					left = 0;
				if (left > dragRange)
					left = dragRange;
			}

			return left;
		}

		/**
		 * 控制child在垂直方向的移动 top:
		 * 表示ViewDragHelper认为你想让当前child的top改变的值,top=chile.getTop()+dy dy:
		 * 本次child垂直方向移动的距离 return: 表示你真正想让child的top变成的值
		 */
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {

			if (changedView == layout_menu) {
				// 固定住menu，不让其移动
				layout_menu.layout(0, 0, layout_menu.getMeasuredWidth(),
						layout_menu.getMeasuredHeight());

				// 添加限制条件，让main移动
				int newLeft = layout_main.getLeft() + dx;
				if (newLeft < 0)
					newLeft = 0;
				if (newLeft > dragRange)
					newLeft = dragRange;
				layout_main.layout(newLeft, layout_main.getTop(), newLeft
						+ layout_main.getMeasuredWidth(),
						layout_main.getBottom());
			}

			// 计算滑动比例值
			float fraction = layout_main.getLeft() * 1.0f / dragRange;
			excuteAnim(fraction);// 执行动画
			if (listener != null)
				listener.onDraging(fraction);

		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {

			if (layout_main.getLeft() < dragRange / 2) {// 松开手时，靠近左边就关闭菜单，
				close();
			} else {
				open();// 靠近右边打开菜单
			}
			// 速度大于300时，也直接关闭或打开
			System.out.println("速度 = " + xvel);
			//向右划速度为正，向左划速度为负
			if (xvel < -300)
				close();
			if (xvel > 300)
				open();

		}

	};

	public void computeScroll() {
		if (viewDragHelper.continueSettling(true)) {
			ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
		}

	};

	/**
	 * 执行动画
	 * 
	 * @param fraction
	 */
	protected void excuteAnim(float fraction) {
		// main的缩放动画
		ViewHelper.setScaleX(layout_main,
				floatEvaluator.evaluate(fraction, 1.0f, 0.8f));
		ViewHelper.setScaleY(layout_main,
				floatEvaluator.evaluate(fraction, 1.0f, 0.8f));

		// menu位移
		ViewHelper.setTranslationX(
				layout_menu,
				floatEvaluator.evaluate(fraction,
						-layout_menu.getMeasuredWidth() * 0.5f, 0));
		// menu缩放
		ViewHelper.setScaleX(layout_menu,
				floatEvaluator.evaluate(fraction, 0.6f, 1));
		ViewHelper.setScaleY(layout_menu,
				floatEvaluator.evaluate(fraction, 0.6f, 1));
		// 修改menu透明度
		ViewHelper.setAlpha(layout_menu,
				floatEvaluator.evaluate(fraction, 0.3f, 1.0f));

		// 给SlideMenu的背景添加黑色的遮罩效果,SRC_OVER表示遮盖效果
		getBackground().setColorFilter(
				(Integer) ColorUtil.evaluateColor(fraction, Color.BLACK,
						Color.TRANSPARENT), Mode.SRC_OVER);
	}

	private ViewDragHelper viewDragHelper;
	private FloatEvaluator floatEvaluator;
	private onStateChangedListener listener;// 定义监听拖拽状态发生改变

	public boolean onTouchEvent(android.view.MotionEvent event) {
		viewDragHelper.processTouchEvent(event);// 交给viewDragHelper处理触摸事件
		return true;
	};

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);// 让viewdragViewer自己判断是否需要拦截触摸事件
		return result;
	}

	public void close() {
		viewDragHelper.smoothSlideViewTo(layout_main, 0, layout_main.getTop());
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
		if (mCurrentState != DragState.Close) {
			mCurrentState = DragState.Close;
			// 判断状态，设置回调
			listener.onStateChanged(mCurrentState);
		}
	}

	private void open() {
		viewDragHelper.smoothSlideViewTo(layout_main, dragRange,
				layout_main.getTop());
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
		
		if (mCurrentState != DragState.Open) {
			mCurrentState = DragState.Open;
			// 判断状态，设置回调
			listener.onStateChanged(mCurrentState);
		}
	}

	public void setonStateChangedListener(onStateChangedListener listener) {
		this.listener = listener;

	}

	public interface onStateChangedListener {// 定义接口，外部实现回调

		/**
		 * 拖拽状态改变回调
		 * 
		 * @param state
		 */
		public void onStateChanged(DragState state);

		/**
		 * 正在拖拽的回调
		 * 
		 * @param fraction
		 */
		public void onDraging(float fraction);

	}

	public enum DragState {// 定义两种状态
		Open, Close;
	}

	public DragState getCurrentState() {

		return mCurrentState;
	}
}

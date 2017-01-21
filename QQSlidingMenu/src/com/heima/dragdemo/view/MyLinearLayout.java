package com.heima.dragdemo.view;

import com.heima.dragdemo.view.SlideMenu.DragState;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class MyLinearLayout extends LinearLayout {

	private SlideMenu slideMenu;

	public MyLinearLayout(Context context) {
		super(context);

	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

	}

	public void setSlideMenu(SlideMenu slideMenu) {
		this.slideMenu = slideMenu;

	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		if (slideMenu != null && slideMenu.getCurrentState() == DragState.Open) {
			// 当前状态为打开时，拦截事件，禁止儿子消费
			return true;
		}

		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (slideMenu != null && slideMenu.getCurrentState() == DragState.Open) {
			// 当前状态为打开时，禁止儿子消费
			if (event.getAction() == MotionEvent.ACTION_UP) {
				// 点击时关闭菜单
				slideMenu.close();
			}
			return true;
		}

		return super.onTouchEvent(event);
	}

}

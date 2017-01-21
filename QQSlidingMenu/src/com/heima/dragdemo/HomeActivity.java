package com.heima.dragdemo;

import java.util.Random;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.heima.dragdemo.view.MyLinearLayout;
import com.heima.dragdemo.view.SlideMenu;
import com.heima.dragdemo.view.SlideMenu.DragState;
import com.heima.dragdemo.view.SlideMenu.onStateChangedListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

public class HomeActivity extends Activity {
	private ListView main_listview, menu_listview;
	private SlideMenu slide_menu;
	private ImageView iv_head;
	private MyLinearLayout layout_main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		initView();
		initData();

	}

	private void initData() {
		main_listview.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Constant.NAMES) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView view = (TextView) super.getView(position, convertView,
						parent);
				// 设置移动中的动画
				// 先缩小到某个值
				ViewHelper.setScaleX(view, 0.5f);
				ViewHelper.setScaleY(view, 0.5f);
				// 再执行属性动画恢复正常
				ViewPropertyAnimator.animate(view).scaleX(1).setDuration(260)
						.start();
				ViewPropertyAnimator.animate(view).scaleY(1).setDuration(260)
						.start();

				return view;
			}
		});

		menu_listview.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				TextView view = (TextView) super.getView(position, convertView,
						parent);
				view.setTextColor(Color.WHITE);
				return view;
			}
		});

		slide_menu.setonStateChangedListener(new onStateChangedListener() {

			@Override
			public void onDraging(float fraction) {
				iv_head.setAlpha(1 - fraction);// 设置透明度
			}

			@Override
			public void onStateChanged(DragState state) {
				if (state == DragState.Close) {
					// System.out.println("关闭了");
					// 震动小头像
					ViewPropertyAnimator.animate(iv_head).translationXBy(15)
							.setDuration(500)
							.setInterpolator(new CycleInterpolator(4)).start();
				} else if (state == DragState.Open) {
					// System.out.println("打开了");
					// 随机滚动到istview的某个条目
					menu_listview.smoothScrollToPosition(new Random()
							.nextInt(menu_listview.getCount()));

				}
			}
		});
		layout_main.setSlideMenu(slide_menu);

	}

	private void initView() {
		setContentView(R.layout.activity_home);
		main_listview = (ListView) findViewById(R.id.main_listview);
		menu_listview = (ListView) findViewById(R.id.menu_listview);
		slide_menu = (SlideMenu) findViewById(R.id.slide_menu);
		iv_head = (ImageView) findViewById(R.id.iv_head);
		layout_main = (MyLinearLayout) findViewById(R.id.layout_main);
	}

}

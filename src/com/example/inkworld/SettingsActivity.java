package com.example.inkworld;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {

	private Toast toast = null;
	private ImageButton imageButton2;
	private RadioGroup fontsizeSelect;
	private RadioButton rbFontsize_s, rbFontsize_m, rbFontsize_l;
	private ImageView back;
	private RelativeLayout about_us, clearCache;
	private TextView cacheinfo;
	private MyApplication myapp;

	private TextView tt_info;

	private WindowManager mWindowManager;

	private View nightView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_settings);

		// 实例化一个Toast对象
		toast = new Toast(SettingsActivity.this);

		myapp = (MyApplication) getApplication();

		imageButton2 = (ImageButton) this.findViewById(R.id.imageButton2);
		fontsizeSelect = (RadioGroup) this.findViewById(R.id.fontsizeSelect);

		clearCache = (RelativeLayout) this.findViewById(R.id.item3);

		about_us = (RelativeLayout) this.findViewById(R.id.item4);
		cacheinfo = (TextView) this.findViewById(R.id.cacheinfo);

		back = (ImageView) this.findViewById(R.id.back);

		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SettingsActivity.this.finish();
			}
		});

		if (myapp.getlunaMode()) {
			imageButton2.setBackgroundResource(R.drawable.luna_on);
		} else {
			imageButton2.setBackgroundResource(R.drawable.luna_off);
		}

		imageButton2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// startThread();

				if (!myapp.getlunaMode()) {
					imageButton2.setBackgroundResource(R.drawable.luna_on);

					myapp.setlunaMode(myapp.MODE_NIGHT);
				} else {
					imageButton2.setBackgroundResource(R.drawable.luna_off);

					myapp.setlunaMode(myapp.MODE_DAY);
				}

				showToast(myapp.getlunaMode());

				mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

				myapp = (MyApplication) getApplication();
				if (nightView != null) {
					mWindowManager.removeView(nightView);
					nightView = null;
				}
				if (myapp.getlunaMode()) {
					WindowManager.LayoutParams params = new WindowManager.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT,
							android.view.WindowManager.LayoutParams.TYPE_APPLICATION,
							WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
									| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
							PixelFormat.TRANSLUCENT);
					params.gravity = Gravity.BOTTOM;

					if (nightView == null) {
						nightView = new TextView(SettingsActivity.this);
						nightView.setBackgroundColor(0xa0000000);
						mWindowManager.addView(nightView, params);
					}

				} else {
					if (nightView != null) {
						mWindowManager.removeView(nightView);
					}
				}

			}
		});

		rbFontsize_s = (RadioButton) findViewById(R.id.fontsize_s);
		rbFontsize_m = (RadioButton) findViewById(R.id.fontsize_m);
		rbFontsize_l = (RadioButton) findViewById(R.id.fontsize_l);

		switch (myapp.getFontSize()) {
		case 0:
			rbFontsize_s.setChecked(true);
			rbFontsize_m.setChecked(false);
			rbFontsize_l.setChecked(false);
			break;

		case 1:
			rbFontsize_s.setChecked(false);
			rbFontsize_m.setChecked(true);
			rbFontsize_l.setChecked(false);
			break;

		case 2:
			rbFontsize_s.setChecked(false);
			rbFontsize_m.setChecked(false);
			rbFontsize_l.setChecked(true);
			break;

		default:
			break;
		}

		fontsizeSelect
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						switch (checkedId) {
						case R.id.fontsize_s:

							myapp.setFontSize(myapp.FONTSIZE_SMALL);

							break;

						case R.id.fontsize_m:

							myapp.setFontSize(myapp.FONTSIZE_MIDDLE);

							break;

						case R.id.fontsize_l:

							myapp.setFontSize(myapp.FONTSIZE_LARGE);

							break;

						default:
							break;
						}
					}
				});

		about_us.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingsActivity.this,
						AboutActivity1.class);
				startActivity(intent);
			}
		});

		clearCache.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				cacheinfo.setText("0  M");
			}
		});

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		myapp = (MyApplication) getApplication();
		if (nightView != null) {
			mWindowManager.removeView(nightView);
			nightView = null;
		}
		if (myapp.getlunaMode()) {
			WindowManager.LayoutParams params = new WindowManager.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
					android.view.WindowManager.LayoutParams.TYPE_APPLICATION,
					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
							| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.TRANSLUCENT);
			params.gravity = Gravity.BOTTOM;

			if (nightView == null) {
				nightView = new TextView(this);
				nightView.setBackgroundColor(0xa0000000);
				mWindowManager.addView(nightView, params);
			}

		} else {
			if (nightView != null) {
				mWindowManager.removeView(nightView);
			}
		}
	}

	private void showToast(Boolean flag) {
		View toastView = getLayoutInflater().inflate(R.layout.layout_toast,
				null);
		tt_info = (TextView) toastView.findViewById(R.id.tv_info);

		if (flag) {
			tt_info.setText("开启夜间模式！");
		} else {
			tt_info.setText("关闭夜间模式！");
		}

		// 实例化一个Toast对象
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(toastView);
		toast.show();

		Message delayMsg = m_Handler.obtainMessage(1);
		m_Handler.sendMessageDelayed(delayMsg, 500);

	}

	private Handler m_Handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 1: {
				if (toast != null) {
					toast.cancel();
				}
				break;
			}
			}
		}
	};

}

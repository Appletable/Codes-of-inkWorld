package com.example.inkworld;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class AboutActivity1 extends Activity {

	private TextView tt_info;

	private LinearLayout back;
	private View fengexian1, fengexian2;
	private Button submit;
	private TextView aboutXumo, advice, update, infoAbout;
	
	private ScrollView sv_infoAbout;
	
	private LinearLayout editLayout;

	private MyApplication Myapp;

	private WindowManager mWindowManager;

	private View nightView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_about1);
		back = (LinearLayout) this.findViewById(R.id.back);
		aboutXumo = (TextView) this.findViewById(R.id.xumo);
		advice = (TextView) this.findViewById(R.id.editAdvice);
		update = (TextView) this.findViewById(R.id.textView5);
		infoAbout = (TextView) this.findViewById(R.id.infoAbout);
		sv_infoAbout=(ScrollView)this.findViewById(R.id.sv_infoAbout);
		submit = (Button) this.findViewById(R.id.button1);

		editLayout = (LinearLayout) this.findViewById(R.id.linearLayout1);

		fengexian1 = (View) this.findViewById(R.id.fengexian1);
		fengexian2 = (View) this.findViewById(R.id.fengexian2);

		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();

			}
		});

		aboutXumo.setOnClickListener(new View.OnClickListener() {
			Boolean flag = true;

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (infoAbout.getText().toString().equals("")
						|| infoAbout.getText().toString().equals(null)) {
					infoAbout
							.setText("\u3000\u3000"
									+ "续墨是一款发动用户群体集思广益“共筹”精彩故事片段或长文的应用。旨在为个人小创作提供空间的同时为所有参与者与浏览者带来欢乐。"
									+ "\n\u3000\u3000"
									+ "续墨产品中的文章及片段版权为编辑用户与续墨团队所有，如有特殊原因需要转载或是其他创作，请与我们联系。"
									+ "\n\n\u3000\u3000"
									+ "续墨团队所有成员感谢您的使用！");
				}
				if (flag) {
					sv_infoAbout.setVisibility(View.VISIBLE);
					advice.setTextColor(0xffa09f9f);
					advice.setClickable(false);
					update.setClickable(false);
					update.setTextColor(0xffa09f9f);
					fengexian1.setVisibility(View.VISIBLE);
					flag = !flag;
				} else {
					sv_infoAbout.setVisibility(View.GONE);
					advice.setTextColor(0xff494949);
					update.setTextColor(0xff494949);
					advice.setClickable(true);
					update.setClickable(true);
					fengexian1.setVisibility(View.GONE);
					flag = !flag;
				}

			}
		});

		advice.setOnClickListener(new View.OnClickListener() {
			Boolean flag = true;

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (flag) {
					editLayout.setVisibility(View.VISIBLE);
					fengexian2.setVisibility(View.VISIBLE);
					aboutXumo.setTextColor(0xffa09f9f);
					update.setTextColor(0xffa09f9f);
					aboutXumo.setClickable(false);
					update.setClickable(false);
					// advice.setBackgroundColor(0xBFFDF7);
					flag = !flag;
				} else {
					editLayout.setVisibility(View.GONE);
					fengexian2.setVisibility(View.GONE);
					aboutXumo.setTextColor(0xff494949);
					update.setTextColor(0xff494949);
					aboutXumo.setClickable(true);
					update.setClickable(true);
					flag = !flag;
				}

			}
		});
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showToast();

			}
		});
		update.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(AboutActivity1.this, "检查更新中..",
						Toast.LENGTH_SHORT).show();

			}
		});

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		Myapp = (MyApplication) getApplication();
		if (nightView != null) {
			mWindowManager.removeView(nightView);
			nightView = null;
		}
		if (Myapp.getlunaMode()) {
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

	private void showToast() {
		View toastView = getLayoutInflater().inflate(R.layout.layout_toast,
				null);
		tt_info = (TextView) toastView.findViewById(R.id.tv_info);
		tt_info.setText("建议提交成功！");
		// 实例化一个Toast对象
		Toast toast = new Toast(AboutActivity1.this);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setView(toastView);
		toast.show();

	}

}

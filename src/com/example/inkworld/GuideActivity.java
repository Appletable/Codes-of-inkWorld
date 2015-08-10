package com.example.inkworld;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class GuideActivity extends Activity {

	private ViewPager mPager;
	private LinearLayout mDotsLayout;
	private ImageButton mBtn;

	private List<View> viewList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_guide);

		mPager = (ViewPager) findViewById(R.id.guide_viewpager);
		mDotsLayout = (LinearLayout) findViewById(R.id.guide_dots);
		mBtn = (ImageButton) findViewById(R.id.guide_btn);

		initPager();
		mPager.setAdapter(new ViewPagerAdapter(viewList));
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
					if (i == arg0) {
						mDotsLayout.getChildAt(i).setSelected(true);
					} else {
						mDotsLayout.getChildAt(i).setSelected(false);
					}
				}
				if (arg0 == mDotsLayout.getChildCount() - 1) {
					mBtn.setVisibility(View.VISIBLE);
				} else {
					mBtn.setVisibility(View.GONE);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		mBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				openHome();
			}
		});
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.menu, menu); return true; }
	 */

	private void initPager() {
		viewList = new ArrayList<View>();
		int[] images = new int[] { R.drawable.new01, R.drawable.new02,
				R.drawable.new03, R.drawable.new04 };

		for (int i = 0; i < images.length; i++) {
			viewList.add(initView(images[i]));
		}
		initDots(images.length);
	}

	private void initDots(int count) {
		for (int j = 0; j < count; j++) {
			initDot().setVisibility(View.INVISIBLE);
			mDotsLayout.addView(initDot());

		}
		mDotsLayout.getChildAt(0).setSelected(true);
	}

	private View initDot() {
		return LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.layout_dot, null);
	}

	private View initView(int res) {
		View view = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.item_guide, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.iguide_img);
		imageView.setImageResource(res);
		return view;
	}

	private void openHome() {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.putExtra("flag", true);
		startActivity(intent);
		finish();
	}

	class ViewPagerAdapter extends PagerAdapter {

		private List<View> data;

		public ViewPagerAdapter(List<View> data) {
			super();
			this.data = data;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return data.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			container.addView(data.get(position));
			return data.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(data.get(position));
		}

	}

}

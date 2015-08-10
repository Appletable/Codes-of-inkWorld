package com.example.inkworld;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MypartakeActivity extends Activity {

	private ImageView back;

	private RadioGroup first_follow;


	private PullToRefreshListView lv;

	private SimpleAdapter adapter;

	private MyApplication Myapp;

	private WindowManager mWindowManager;

	private View nightView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mypartake);

		back = (ImageView) this.findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		first_follow = (RadioGroup) findViewById(R.id.rg_first_follow);

		first_follow.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub

				switch (checkedId) {
				case R.id.rb_first:

					break;

				case R.id.rb_follow:

					break;

				}
			}
		});

		// 加载数据
		lv = (PullToRefreshListView) findViewById(R.id.lv_mylist);

		lv.setMode(Mode.BOTH);

		ILoadingLayout startLabels = lv.getLoadingLayoutProxy(true, false);
		startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
		startLabels.setRefreshingLabel("正在载入...");// 刷新时
		startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

		ILoadingLayout endLabels = lv.getLoadingLayoutProxy(false, true);
		endLabels.setPullLabel("加载更多...");// 刚下拉时，显示的提示
		endLabels.setRefreshingLabel("正在载入...");// 刷新时
		endLabels.setReleaseLabel("放开加载更多...");// 下来达到一定距离时，显示的提示

		final List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < 5; i++) {
			Map<String, Object> listItem1 = new HashMap<String, Object>();
			listItem1.put("profile", R.drawable.icon_tx);
			listItem1.put("nickname", "嘻嘻");
			listItem1.put("title", "这个年龄该有的智慧，不该有的智慧");

			listItem1.put("labeltext", "90后");
			listItem1.put("context", "今天中午遇到了一个非常厉害的创业团队，叫做续墨....");
			listItem1.put("time", "05-12 10:04");
			listItem1.put("xumonum", "已有34人续墨");
			listItem1.put("zannum", "41");
			listItems.add(listItem1);

			Map<String, Object> listItem2 = new HashMap<String, Object>();
			listItem2.put("profile", R.drawable.icon_tx);
			listItem2.put("nickname", "哈哈");
			listItem2.put("title", "这个年龄该有的智慧，不该有的智慧");

			listItem2.put("labeltext", "中二少年");
			listItem2.put("context", "今天中午遇到了一个不知道叫什么的非常厉害的创业团队....");
			listItem2.put("time", "05-12 09:08");
			listItem2.put("xumonum", "已有45人续墨");
			listItem2.put("zannum", "52");
			listItems.add(listItem2);

		}

		adapter = new SimpleAdapter(this, listItems, R.layout.activity_item,
				new String[] { "profile", "nickname", "title", "labeltext",
						"context", "time", "xumonum", "zannum" }, new int[] {
						R.id.iv_profile, R.id.tv_nickname, R.id.title,
						R.id.tv_labeltext, R.id.tv_context, R.id.tv_time,
						R.id.tv_xumonum, R.id.tv_zannum });
		lv.setAdapter(adapter);

		lv.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						return null;
					}

					protected void onPostExecute(Void result) {

						Map<String, Object> listItem = new HashMap<String, Object>();
						listItem.put("profile", R.drawable.icon_tx);
						listItem.put("nickname", "new");
						listItem.put("title", "这个年龄该有的智慧，不该有的智慧");

						listItem.put("labeltext", "xxx");

						listItem.put("context", "下拉刷新成功啦。。。。。。。");
						listItem.put("time", "05-12 10:14");

						listItem.put("xumonum", "已有xx人续墨");
						listItem.put("zannum", "xx");

						listItems.add(0, listItem);

						adapter.notifyDataSetChanged();
						lv.onRefreshComplete();

					};

				}.execute();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {

						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						return null;
					}

					protected void onPostExecute(Void result) {

						for (int i = 0; i < 10; i++) {
							Map<String, Object> listItem = new HashMap<String, Object>();
							listItem.put("profile", R.drawable.icon_tx);
							listItem.put("nickname", "new");
							listItem.put("title", "这个年龄该有的智慧，不该有的智慧");

							listItem.put("labeltext", "xxx");
							listItem.put("context", "上拉加载成功啦。。。。。。。");

							listItem.put("time", "05-12 10:14");
							listItem.put("xumonum", "已有xx人续墨");
							listItem.put("zannum", "xx");

							listItems.add(listItem);
						}

						adapter.notifyDataSetChanged();
						lv.onRefreshComplete();

					};

				}.execute();
			}
		});

		// item加载点击事件
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unchecked")
				Map<String, Object> item = (Map<String, Object>) arg0
						.getItemAtPosition(arg2);
				Intent intent = new Intent(MypartakeActivity.this,
						ContentActivity.class);
				// intent.putExtra("profile", (String)item.get("profile"));
				intent.putExtra("time", (String) item.get("time"));
				intent.putExtra("nickname", (String) item.get("nickname"));
				intent.putExtra("title", (String) item.get("title"));
				intent.putExtra("labeltext", (String) item.get("labeltext"));
				intent.putExtra("zannum", (String) item.get("zannum"));
				startActivity(intent);
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

}

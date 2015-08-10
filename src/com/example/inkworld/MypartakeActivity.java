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

		// ��������
		lv = (PullToRefreshListView) findViewById(R.id.lv_mylist);

		lv.setMode(Mode.BOTH);

		ILoadingLayout startLabels = lv.getLoadingLayoutProxy(true, false);
		startLabels.setPullLabel("����ˢ��...");// ������ʱ����ʾ����ʾ
		startLabels.setRefreshingLabel("��������...");// ˢ��ʱ
		startLabels.setReleaseLabel("�ſ�ˢ��...");// �����ﵽһ������ʱ����ʾ����ʾ

		ILoadingLayout endLabels = lv.getLoadingLayoutProxy(false, true);
		endLabels.setPullLabel("���ظ���...");// ������ʱ����ʾ����ʾ
		endLabels.setRefreshingLabel("��������...");// ˢ��ʱ
		endLabels.setReleaseLabel("�ſ����ظ���...");// �����ﵽһ������ʱ����ʾ����ʾ

		final List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < 5; i++) {
			Map<String, Object> listItem1 = new HashMap<String, Object>();
			listItem1.put("profile", R.drawable.icon_tx);
			listItem1.put("nickname", "����");
			listItem1.put("title", "���������е��ǻۣ������е��ǻ�");

			listItem1.put("labeltext", "90��");
			listItem1.put("context", "��������������һ���ǳ������Ĵ�ҵ�Ŷӣ�������ī....");
			listItem1.put("time", "05-12 10:04");
			listItem1.put("xumonum", "����34����ī");
			listItem1.put("zannum", "41");
			listItems.add(listItem1);

			Map<String, Object> listItem2 = new HashMap<String, Object>();
			listItem2.put("profile", R.drawable.icon_tx);
			listItem2.put("nickname", "����");
			listItem2.put("title", "���������е��ǻۣ������е��ǻ�");

			listItem2.put("labeltext", "�ж�����");
			listItem2.put("context", "��������������һ����֪����ʲô�ķǳ������Ĵ�ҵ�Ŷ�....");
			listItem2.put("time", "05-12 09:08");
			listItem2.put("xumonum", "����45����ī");
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
						listItem.put("title", "���������е��ǻۣ������е��ǻ�");

						listItem.put("labeltext", "xxx");

						listItem.put("context", "����ˢ�³ɹ�����������������");
						listItem.put("time", "05-12 10:14");

						listItem.put("xumonum", "����xx����ī");
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
							listItem.put("title", "���������е��ǻۣ������е��ǻ�");

							listItem.put("labeltext", "xxx");
							listItem.put("context", "�������سɹ�����������������");

							listItem.put("time", "05-12 10:14");
							listItem.put("xumonum", "����xx����ī");
							listItem.put("zannum", "xx");

							listItems.add(listItem);
						}

						adapter.notifyDataSetChanged();
						lv.onRefreshComplete();

					};

				}.execute();
			}
		});

		// item���ص���¼�
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

package com.example.inkworld;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MycollectionActivity extends Activity {
	private LinearLayout back;
	private CheckBox edit;

	private PullToRefreshListView lv;

	private MyCollectionAdapter adapter;

	private MyApplication Myapp;

	private WindowManager mWindowManager;

	private View nightView = null;

	// private Boolean isDeletable = false; //item�Ƿ��ǿ�ɾ���ģ����Ƿ������Ͻǵ�����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mycollection);

		back = (LinearLayout) this.findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		// ��������
		lv = (PullToRefreshListView) findViewById(R.id.lv_mylist);

		lv.setMode(Mode.PULL_FROM_END);

		// ILoadingLayout startLabels = lv.getLoadingLayoutProxy(true, false);
		// startLabels.setPullLabel("����ˢ��...");// ������ʱ����ʾ����ʾ
		// startLabels.setRefreshingLabel("��������...");// ˢ��ʱ
		// startLabels.setReleaseLabel("�ſ�ˢ��...");// �����ﵽһ������ʱ����ʾ����ʾ

		ILoadingLayout endLabels = lv.getLoadingLayoutProxy(false, true);
		endLabels.setPullLabel("���ظ���...");// ������ʱ����ʾ����ʾ
		endLabels.setRefreshingLabel("��������...");// ˢ��ʱ
		endLabels.setReleaseLabel("�ſ����ظ���...");// �����ﵽһ������ʱ����ʾ����ʾ

		final List<MyCollectionItem> listItems = new ArrayList<MyCollectionItem>();

			listItems.add(new MyCollectionItem(R.drawable.yangsheng, "yangsheng",
					"��X���⴫", -1, "����֮ĩ������ʱ�֡��ܲ�һ������������վ�ڱ�X��У�ſڣ�̾�˿��������Ǻǣ����ջ��������ˡ�������X���ǲܲ�����ܿ��ĵط���үү�����Ǳ�X��У�����ڼ����ѧУ��ϰ����˵һ���������ǲܲپͱ������˱�X��ͨ��רҵ���ܲٵ��ϰֲ��Ա����ĺûƢ��û���¾��ǳ���ͦ˧�����˲ܼҵ�����Ů�����ڼҵĵ�λ�����֪����˾��ܲܲٲ�����ڲ�����ү����Ƥ���£������ջ�����������ү�ӵİ��š�Ҳ�գ��Ͼ����ʡ��ܺ顢�ĺ���ĺ�Ԩ������С���������ֵ�Ҳ���ڱ�X�󣬶��Ͷ��ɡ�",
					"У԰   ���   90��", "15-07-26 19:38:27", "����26����ī", "0"));
			listItems.add(new MyCollectionItem(R.drawable.zh2, "zh2",
					"����", -1, "�����������꣬����������һ̨Xbox�������˽�ģ������û�Ǵ�Ļ�����2001��Ŀ�ʽ��һ����ɫӲ���ĺ��ӡ��Һ͸���һ�����˺ܶ���Ϸ���ǳ����ģ�ֱ��������ҵĸ���ȥ���ˡ� ֮���ʮ��ʱ�������Ҳû��������̨��Ϸ���� Ȼ�������ٶ�������ʱ���ҷ�����һЩ����...... �Һ͸�������һ�����һ��������Ϸ�С�ԽҰ��ս�������ڵ�ʱ��������ǿ�ܺ������Ϸ�� �������������������Ϸʱ���ҷ�����һ�����������飡",
					"����", "15-07-26 18:25:11", "����1����ī", "0"));
			listItems.add(new MyCollectionItem(R.drawable.zhenzi, "zhenzi",
					"����", -1, "��ǰ�и����ң��������������� \nһ�����������ֳ�����Կ�׺��ڹ�������ţ��ߵ��ھӼ������ԡ�����ʱ�֣���������͵���Ķ����ص�������ܷ����Լ���Ҳʧ���ˡ� \n���Ǿ������Ҹ��ؾ�ס��һ��û�в��ҵ��ˣ���Ϊÿ���˶��ӱ�������͵�������������ٴӱ�������͵��������ȥ��ֱ�����һ����ȥ��һ�����������ԡ��ù�ó��Ҳ�Ͳ��ɱ�������򷽺�������˫����ƭ�������Ǹ��������Եķ��������������Ҳ������ƭ��������Ȥ���������ӵ�Ҳƽ�ȣ�û�и��˺����ˡ� \n�� ��һ�죭����������ô����û��֪��������֮���и���ʵ�˵��˸õض��ӡ������ϣ���û��Я����Ƶس��ţ�ȴ���ڼ�����̶�С˵��\n�� �����ˣ��������ţ���û��ȥ��",
					"��", "15-07-26 18:19:42", "����5����ī", "0"));
			listItems.add(new MyCollectionItem(R.drawable.zh, "zh",
					"�������", -1, "��ͷ�������������Ⱥ�İ���;�г����ģ�����ͬ��ĺ�Ǻ����ۣ�������Ѫ���䵶�����������򸧵ı������ĺ�ǡ�\nƬ��֮�䣬�������������Χ���ܻ����ˣ��쳣�鶯���������Ծ��顣�����ţ�������������ͷͬ��һ��ӿ��ǰ��������ǵصĴ󳱡�\n��ʱ���������Լ�����Ⱥ�����ޱȣ����֮ǰ�����޵У�����֮�����鸩�ס�",
					"���", "15-07-26 18:13:33", "����0����ī", "0"));
			listItems.add(new MyCollectionItem(R.drawable.zh20, "zh20",
					"һ�����˵Ĺ���", -1, "�� ǰ�и������˼�ֻ�����ݴ��ؼҡ����ճ��ţ�����������˵����Ե����������ϻ��������Ͼ�Ȼ���̵��������ڶ������������˵������ཷ��˿�����ϻ����� �������ཷ��˿�����������������˵������������ݣ����ϻ��������������������������ݣ���������˵��Է����⣬���ϻ�����ʲô��û�С���",
					"����   ����   ����", "15-07-26 19:38:27", "����0����ī", "0"));
			

		adapter = new MyCollectionAdapter(this, listItems);
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

						// Map<String, Object> listItem = new HashMap<String,
						// Object>();
						// listItem.put("profile", R.drawable.icon_tx);
						// listItem.put("nickname", "new");
						// listItem.put("title", "���������е��ǻۣ������е��ǻ�");
						//
						// listItem.put("labeltext", "xxx");
						//
						// listItem.put("context", "����ˢ�³ɹ�����������������");
						// listItem.put("time", "05-12 10:14");
						//
						// listItem.put("xumonum", "����xx����ī");
						// listItem.put("zannum", "xx");
						//
						// listItems.add(0, listItem);

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

						if (edit.isChecked()) {
							for (int i = 0; i < 10; i++) {

								listItems.add(new MyCollectionItem(
										R.drawable.icon_tx, "new",
										"���������е��ǻۣ������е��ǻ�", 0,
										"�������سɹ�����������������", "xxx", "05-12 10:14",
										"����xx����ī", "xx"));
							}
						} else {
							for (int i = 0; i < 10; i++) {

								listItems.add(new MyCollectionItem(
										R.drawable.icon_tx, "new",
										"���������е��ǻۣ������е��ǻ�", -1,
										"�������سɹ�����������������", "xxx", "05-12 10:14",
										"����xx����ī", "xx"));
							}
						}

						adapter.notifyDataSetChanged();
						lv.onRefreshComplete();

					};

				}.execute();
			}
		});

		edit = (CheckBox) this.findViewById(R.id.edit);
		edit.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (edit.isChecked()) {
					// ���еļӻ�����
					int itemsLength = listItems.size();
					for (int i = 0; i < itemsLength; i++) {
						MyCollectionItem tempItem = listItems.get(i);
						tempItem.setCollectionCheckStatus(0);
					}
					adapter.notifyDataSetChanged();

				} else {

					// ���������ɾ������Щ��

					// �ڱ�ҳ��ȥ��ѡ��ɾ������Щ������ǣ�
					int itemsLength = listItems.size();
					for (int i = 0; i < itemsLength; i++) {
						MyCollectionItem tempItem = listItems.get(i);
						if (tempItem.getCollectionCheckStatus() == 1) {
							listItems.remove(i);
							i--;
							itemsLength--;
						}
					}
					// ȥ��ʣ����Ļ�����
					itemsLength = listItems.size();
					for (int i = 0; i < itemsLength; i++) {
						MyCollectionItem tempItem = listItems.get(i);
						tempItem.setCollectionCheckStatus(-1);
					}
					adapter.notifyDataSetChanged();

					Toast.makeText(MycollectionActivity.this, "ɾ���ɹ�",
							Toast.LENGTH_SHORT).show();

				}
			}
		});

		// item���ص���¼�
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (edit.isChecked()) {
					// ɾ���������Ǻͻ������л�
					MyCollectionItem tempItem = listItems.get(position - 1);
					if (tempItem.getCollectionCheckStatus() == 0) {
						tempItem.setCollectionCheckStatus(1);
					} else {
						tempItem.setCollectionCheckStatus(0);
					}
					adapter.notifyDataSetChanged();
				} else {
					// �鿴�������������
					Intent intent = new Intent(MycollectionActivity.this,
							ContentActivity.class);
					startActivity(intent);
				}

			}
		});
	}

	// ҹ��ģʽ
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

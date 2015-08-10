package com.example.inkworld;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class ContentActivity extends Activity {

	// ʵ����һ��Toast����
	Toast toast = null;
	private TextView tv_info;
	private TextView title, labeltext;
	private TextView item1, item2, item3, item4, item5;
	private View headView = null, footView = null;
	private TextView tvTitlenickname, tvTitletime, tvTitletext, tvTitlezan;
	private ImageView ivTitleprofile;
	//
	// private TextView tvXuxietext;
	//
	// private ImageView ivXuxieimage;

	// private int page = 0;

	private PopupWindow moreSelections, share, report, loginWindow;

	private Button cancelButton, loginButton;

	private ImageView icon_sc, icon_sq;
	private ImageView share_cancel, report_cancel;
	private LinearLayout back, operation;
	private LinearLayout xumo;
	private LinearLayout sc, sq, fx, jb;
	private TextView tv_sc, tv_sq;
	private LinearLayout share_Pengyouquan, share_weChat, share_Weibo,
			share_qZone, share_qq;
	private WindowManager mWindowManager;
	private View nightView = null;

	private MyApplication myapp;
	private List<BaseItem> listItems;
	private Topic empty_topic;

	private String username = "";
	private String usercode = "";

	private String aid, pid;

	private SharedPreferences pref;

	private ContentAdapter adapter;
	private PullToRefreshListView lv;
	private ListView actualListView;
	private ArrayList<String> para_url = new ArrayList<String>();
	private String test_url;
	private String articleId = "";

	private Set<TopicWorkerTask> taskCollection;

	/**
	 * ͼƬ���漼���ĺ����࣬���ڻ����������غõ�ͼƬ���ڳ����ڴ�ﵽ�趨ֵʱ�Ὣ�������ʹ�õ�ͼƬ�Ƴ�����
	 */
	private android.support.v4.util.LruCache<String, Topic> mMemoryCache;

	/**
	 * ͼƬӲ�̻�������ࡣ
	 */
	private DiskLruCache mDiskLruCache;

	private static final int CONFIG_CACHE_MOBILE_TIMEOUT = 3600000; // 1 hour
	private static final int CONFIG_CACHE_WIFI_TIMEOUT = 300000; // 5 minute

	private HashMap<String, Topic> map;

	private int page_num_start = 0;
	private int page_num_end = 0;
	private int up_or_down = 0; // 0��ʾ���¸��£�1��ʾ���ϸ���
	private int first_load = 1; // �Ƿ��Ǹս���ĵ�һ�μ���

	private int add_num = 0;

	private Boolean has_xuxie_button = false;
	
	private String share_title = null,share_text = null;
	

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 4) { // ���¸��³ɹ�
				Log.d("adapter", "para_url's size = " + para_url.size());
				String[] stockArr = new String[para_url.size()];
				int start_para = msg.arg1;
				for (int i = start_para; i < para_url.size(); i++)
					listItems.add(empty_topic);
				stockArr = para_url.toArray(stockArr);
				add_num = para_url.size() - start_para;
				for (int i = start_para; i < para_url.size(); i++)
					loadTopic(stockArr[i]);

			} else if (msg.what == 5) {
				Toast.makeText(ContentActivity.this, "�������ӳ���������������",
						Toast.LENGTH_SHORT).show();

			} else if (msg.what == 6) { // ���¸���ʧ�ܣ��Ѿ������һƪ
				Toast.makeText(ContentActivity.this, "�Ѿ������һƪ����",
						Toast.LENGTH_SHORT).show();

				// ������д����İ�ť
				if (!has_xuxie_button) {
					listItems.add(new ContentFootItem(1));
					adapter.notifyDataSetChanged();
					has_xuxie_button = true;
				}
			} else if (msg.what == 7) // ���ϸ��³ɹ�
			{
				String[] stockArr = new String[para_url.size()];
				for (int i = 0; i < 10; i++)
					listItems.add(0, empty_topic);
				stockArr = para_url.toArray(stockArr);
				for (int i = 0; i < 10; i++)
					loadTopic(stockArr[i]);

			} else if (msg.what == 8) // ���ϸ���ʧ�ܣ��Ѿ��ǵ�һҳ
			{
				Toast.makeText(ContentActivity.this, "�Ѿ��ǵ�һƪ����",
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	private Handler topic_handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 4) {
				String url = (String) msg.obj;
				Topic topic = map.get(url);
				try {
					listItems.set(para_url.indexOf(url), topic);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				add_num--;
				adapter.notifyDataSetChanged();
//				if (add_num == 0 && first_load == 0) {
//					actualListView.smoothScrollBy(200, 100);
//				}

			} else {

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_content);

		Intent intent = getIntent();
		articleId = intent.getStringExtra("articleId");
		Log.d("article adapter", "articleid = " + articleId);

		map = new HashMap<String, Topic>();

		myapp = (MyApplication) getApplication();
		test_url = myapp.getparagraphlist_url() + articleId;
		// ��������
		lv = (PullToRefreshListView) findViewById(R.id.lv_mylist);
		lv.setMode(Mode.BOTH);
		// final ListView actualListView = lv.getRefreshableView();
		actualListView = lv.getRefreshableView();

		taskCollection = new HashSet<TopicWorkerTask>();
		mMemoryCache = myapp.getLruCache();
		mDiskLruCache = myapp.getDiskLruCache();

		empty_topic = new Topic(0, "", "", "", "", "", "", "", "", "");

		// ʵ����һ��Toast����
		toast = new Toast(ContentActivity.this);

		/*
		 * ����������Ӧ����
		 */
		back = (LinearLayout) this.findViewById(R.id.back);
		operation = (LinearLayout) this.findViewById(R.id.moreSelect);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		// �������

		operation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showPopupWindow(arg0);
			}
		});

		/*
		 * ���ܣ���������
		 */

		ILoadingLayout startLabels = lv.getLoadingLayoutProxy(true, false);
		startLabels.setPullLabel("����ˢ��...");// ������ʱ����ʾ����ʾ
		startLabels.setRefreshingLabel("��������...");// ˢ��ʱ
		startLabels.setReleaseLabel("�ſ�ˢ��...");// �����ﵽһ������ʱ����ʾ����ʾ

		ILoadingLayout endLabels = lv.getLoadingLayoutProxy(false, true);
		endLabels.setPullLabel("���ظ���...");// ������ʱ����ʾ����ʾ
		endLabels.setRefreshingLabel("��������...");// ˢ��ʱ
		endLabels.setReleaseLabel("�ſ����ظ���...");// �����ﵽһ������ʱ����ʾ����ʾ

		listItems = new ArrayList<BaseItem>();



		adapter = new ContentAdapter(ContentActivity.this, listItems);
		actualListView.setAdapter(adapter);

		lv.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
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


						up_or_down = 1;

						GetParaFromServer();

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


						up_or_down = 0;
						first_load = 0;
						GetParaFromServer();


						lv.onRefreshComplete();

					};

				}.execute();
			}
		});

		GetParaFromServer();

	}

	@Override
	protected void onPause() {
		super.onPause();
		myapp.fluchCache();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// �˳�����ʱ�������е���������
		cancelAllTasks();
	}

	// ��ʾ��������
	private void showPopupWindow(View view) {
		// ��ȡ�Զ��岼���ļ�operation.xml����ͼ
		View popupWindow_view = getLayoutInflater().inflate(R.layout.operation,
				null, false);
		// ����PopupWindowʵ��,160/300�ֱ��ǿ�Ⱥ͸߶�LayoutParams.MATCH_PARENT
		moreSelections = new PopupWindow(popupWindow_view,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

		moreSelections.setFocusable(true);

		ColorDrawable cd = new ColorDrawable(0x00000000);
		moreSelections.setBackgroundDrawable(cd);

		// ���ö���Ч��
		// moreSelections.setAnimationStyle(R.style.AnimationFade);
		// ���õ��Ч��
		sc = (LinearLayout) popupWindow_view.findViewById(R.id.sc);
		icon_sc = (ImageView) popupWindow_view.findViewById(R.id.imageView1);
		tv_sc = (TextView) popupWindow_view.findViewById(R.id.tv_sc);
		sq = (LinearLayout) popupWindow_view.findViewById(R.id.sq);
		icon_sq = (ImageView) popupWindow_view.findViewById(R.id.imageView2);
		tv_sq = (TextView) popupWindow_view.findViewById(R.id.tv_sq);
		fx = (LinearLayout) popupWindow_view.findViewById(R.id.fx);
		// icon_fx=(ImageView)popupWindow_view.findViewById(R.id.imageView3);
		jb = (LinearLayout) popupWindow_view.findViewById(R.id.jb);
		// icon_jb=(ImageView)popupWindow_view.findViewById(R.id.imageView4);
		sc.setOnClickListener(new View.OnClickListener() {
			Boolean flag = false;

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!flag) {
					icon_sc.setBackgroundResource(R.drawable.icon_sc_press);
					tv_sc.setTextColor(0xff12d8e6);
					flag = !flag;

				} else {
					icon_sc.setBackgroundResource(R.drawable.icon_sc);
					tv_sc.setTextColor(0xff000000);
					flag = !flag;

				}
				showToast(arg0, flag);

			}
		});
		sq.setOnClickListener(new View.OnClickListener() {
			Boolean flag = false;

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!flag) {
					icon_sq.setBackgroundResource(R.drawable.icon_sq_press);
					tv_sq.setTextColor(0xff12d8e6);
					flag = !flag;

				} else {
					icon_sq.setBackgroundResource(R.drawable.icon_sq);
					tv_sq.setTextColor(0xff000000);
					flag = !flag;
				}

				String[] stockArr = new String[para_url.size()];
				for (int i = 0; i < para_url.size(); i++)
					loadTopic(stockArr[i]);

				showToast(arg0, flag);
			}
		});
		fx.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				showShareWindow(arg0);
				moreSelections.dismiss();

			}
		});
		jb.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showReportWindow(arg0);
				moreSelections.dismiss();
			}
		});
		// ������λ����ʾ��ʽ
		moreSelections.showAsDropDown(operation, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		// ��������ط���ʧ
		popupWindow_view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (moreSelections != null && moreSelections.isShowing()) {
					moreSelections.dismiss();
					moreSelections = null;
				}
				return false;
			}
		});

	}

	@SuppressWarnings("deprecation")
	private void showShareWindow(View view) {
		View shareView = getLayoutInflater().inflate(R.layout.layout_share,
				null, false);
		share = new PopupWindow(shareView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				ColorDrawable cd = new ColorDrawable(0x000000);
				share.setBackgroundDrawable(cd);
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 0.7f;
				getWindow().setAttributes(lp);
			}
		}, 300);

		share.setOutsideTouchable(true);
		share.setBackgroundDrawable(new BitmapDrawable());
		share.setFocusable(true);

		// ���ö���Ч��
		share.setAnimationStyle(R.style.AnimationFade);
		// ���ؼ��˳�
		shareView.setFocusableInTouchMode(true);
		shareView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					share.dismiss();
					share = null;
					return true;
				}

				return false;
			}
		});

		// ������λ����ʾ��ʽ
		share.showAtLocation(lv, Gravity.BOTTOM, 0, 0);

		// ��������ط���ʧ

		share_cancel = (ImageView) shareView.findViewById(R.id.share_cancel);
		share_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				share.dismiss();
			}
		});

		share_Pengyouquan = (LinearLayout) shareView
				.findViewById(R.id.pengyouquan);

		share_weChat = (LinearLayout) shareView.findViewById(R.id.weChat);

		share_Weibo = (LinearLayout) shareView.findViewById(R.id.sinamircoblog);

		share_qZone = (LinearLayout) shareView.findViewById(R.id.QQsns);

		share_qq = (LinearLayout) shareView.findViewById(R.id.QQfirends);

		share_Pengyouquan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Toast.makeText(ContentActivity.this, "��������Ȧ",
//						Toast.LENGTH_SHORT).show();
//
//				ShareParams sp = new ShareParams();
//				sp.setTitle("���Է���ı���");
//				sp.setText("���Է�����ı�");
//				sp.setImagePath(Environment.getExternalStorageDirectory()
//						+ "/inkWorld/Profile.png");
//				Platform wechatmoments = ShareSDK
//						.getPlatform(WechatMoments.NAME);
//				// ִ��ͼ�ķ���
//				wechatmoments.share(sp);
			}
		});
		share_weChat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				ShareParams sp = new ShareParams();
//				sp.setTitle("���Է���ı���");
//				sp.setText("���Է�����ı�");
//				sp.setImagePath(Environment.getExternalStorageDirectory()
//						+ "/inkWorld/Profile.png");
//
//				Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
//				// ִ��ͼ�ķ���
//				wechat.share(sp);

			}
		});
		share_Weibo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				ShareParams sp = new ShareParams();
//				sp.setText("���Է�����ı�");
//
//				sp.setImagePath(Environment.getExternalStorageDirectory()
//						+ "/inkWorld/Profile.png");
//
//				Platform weibo = ShareSDK.getPlatform(SinaWeibo.NAME);
//				weibo.SSOSetting(true);
//
//				// ִ��ͼ�ķ���
//				weibo.share(sp);

			}
		});
		share_qZone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ShareParams sp = new ShareParams();
				sp.setTitle(share_title);
				sp.setTitleUrl("http://10.108.179.246/download_page.jpg"); // ����ĳ�����
				sp.setText(share_text);
				/*sp.setImagePath(Environment.getExternalStorageDirectory()
						+ "/inkWorld/Profile.png");*/
				
				/*InputStream is = null;
				Bitmap bmp = null;
				is = getResources().openRawResource(R.drawable.logo_launcher);
				bmp = BitmapFactory.decodeStream(is);
				sp.setImageData(bmp);*/
				
				sp.setImageUrl("http://10.108.179.246/logo_launcher.png");

				Platform qzone = ShareSDK.getPlatform(QZone.NAME);
				// ִ��ͼ�ķ���
				qzone.share(sp);

			}
		});
		share_qq.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				ShareParams sp = new ShareParams();
//				sp.setTitle("���Է���ı���");
//				sp.setTitleUrl("http://sharesdk.cn"); // ����ĳ�����
//				sp.setText("���Է�����ı�");
//				sp.setImagePath(Environment.getExternalStorageDirectory()
//						+ "/inkWorld/Profile.png");
//
//				Platform qq = ShareSDK.getPlatform(QQ.NAME);
//				// qzone. setPlatformActionListener (new); // ���÷����¼��ص�
//				// ִ��ͼ�ķ���
//				qq.share(sp);

			}
		});

		// �����ָ�
		share.setOnDismissListener(new OnDismissListener() {
			// ��dismiss�лָ�͸����
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
	}

	@SuppressWarnings("deprecation")
	private void showReportWindow(View view) {

		View reportView = getLayoutInflater().inflate(R.layout.layout_report,
				null, true);
		report = new PopupWindow(reportView, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, true);

		report.setFocusable(true);
		report.setOutsideTouchable(true);
		report.setBackgroundDrawable(new BitmapDrawable());

		// �����䰵
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// execute the task
				ColorDrawable cd = new ColorDrawable(0x000000);
				report.setBackgroundDrawable(cd);
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 0.7f;
				getWindow().setAttributes(lp);
			}
		}, 300);

		// ���ö���Ч��
		report.setAnimationStyle(R.style.AnimationFade);
		// ���ؼ��˳�
		reportView.setFocusableInTouchMode(true);
		reportView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					report.dismiss();
					report = null;
					return true;
				}

				return false;
			}
		});

		// ������λ����ʾ��ʽ
		report.showAtLocation(lv, Gravity.BOTTOM, 0, 0);

		// �����ָ�
		report.setOnDismissListener(new OnDismissListener() {
			// ��dismiss�лָ�͸����
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});

		report_cancel = (ImageView) reportView.findViewById(R.id.report_cancel);
		report_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				report.dismiss();
			}
		});

		item1 = (TextView) reportView.findViewById(R.id.item1);
		item2 = (TextView) reportView.findViewById(R.id.item2);
		item3 = (TextView) reportView.findViewById(R.id.item3);
		item4 = (TextView) reportView.findViewById(R.id.item4);
		item5 = (TextView) reportView.findViewById(R.id.item5);

		item1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showToast(arg0, null);
			}
		});
		item2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showToast(arg0, null);
			}
		});
		item3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showToast(arg0, null);
			}
		});
		item4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showToast(arg0, null);
			}
		});
		item5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showToast(arg0, null);
			}
		});

	}

	// ҹ��ģʽ
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
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

	private void showToast(View view, Boolean flag) {
		View toastView = getLayoutInflater().inflate(R.layout.layout_toast,
				null);
		tv_info = (TextView) toastView.findViewById(R.id.tv_info);

		switch (view.getId()) {
		case R.id.sc:
			if (flag) {
				tv_info.setText("�ղسɹ���");
			} else {
				tv_info.setText("ȡ���ղسɹ���");
			}

			break;

		case R.id.sq:
			if (flag) {
				tv_info.setText("�����ǩ�ɹ���");
			} else {
				tv_info.setText("ȡ����ǩ�ɹ���");
			}
			break;
		default:
			tv_info.setText("�ٱ��ɹ���");
			break;
		}

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

	private void post_json_praise(final int position) {
		new Thread(new Runnable() {
			@Override
			public void run() {

				StringBuilder sb = new StringBuilder();
				String serverurl = myapp.getpraise_url();

				pref = getSharedPreferences("username_password", MODE_PRIVATE);

				username = pref.getString("username", "");
				usercode = myapp.getvalidation_code();

				HttpURLConnection urlConnection = null;
				OutputStreamWriter out = null;
				BufferedReader br = null;
				try {
					URL url = new URL(serverurl);
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setDoOutput(true);
					urlConnection.setRequestMethod("POST");
					urlConnection.setUseCaches(false);
					urlConnection.setConnectTimeout(10000);
					urlConnection.setReadTimeout(10000);
					urlConnection.setRequestProperty("Content-Type",
							"application/json");
					// urlConnection.setRequestProperty("Host",
					// "android.schoolportal.gr");
					urlConnection.connect();

					String paragragh_id = para_url.get(position).substring(47);

					// Create JSONObject here
					JSONObject jsonParam = new JSONObject();
					jsonParam.put("uid", username);
					jsonParam.put("pid", paragragh_id);
					jsonParam.put("code", usercode);

					String temp = jsonParam.toString();

					out = new OutputStreamWriter(urlConnection
							.getOutputStream());
					// out.write(jsonParam.toString());
					// out.write( URLEncoder.encode(temp,"UTF-8"));
					out.write(temp);
					Log.d("ServerCom", "temp: " + temp);
					out.close();

					Log.d("ServerCom", "" + urlConnection.getResponseCode());

					int HttpResult = urlConnection.getResponseCode();
					if (HttpResult == HttpURLConnection.HTTP_OK) {
						br = new BufferedReader(new InputStreamReader(
								urlConnection.getInputStream(), "utf-8"));
						String line = null;
						while ((line = br.readLine()) != null) {
							// sb.append(line + "\n");
							sb.append(line);

						}
						br.close();

						// System.out.println(""+sb.toString());
						Log.d("ServerCom",
								"from server: (praise)" + sb.toString());
//						if (sb.toString().equals("0")) {
//							Toast.makeText(ContentActivity.this, "���޳ɹ���",
//									Toast.LENGTH_SHORT).show();
//						}
//						else if(sb.toString().equals("1"))
//						{
//							Toast.makeText(ContentActivity.this, "ȡ���޳ɹ���",
//									Toast.LENGTH_SHORT).show();
//						}

					} else {
						// System.out.println(urlConnection.getResponseMessage());
						Log.d("ServerCom",
								"not ok: " + urlConnection.getResponseMessage());
					}

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					if (urlConnection != null) {
						urlConnection.disconnect();
					}
					try {
						if (out != null) {
							out.close();
						}
						if (br != null) {
							br.close();
						}
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();

	}

	private void GetParaFromServer() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				HttpURLConnection urlConnection = null;
				InputStream in = null;

				OutputStreamWriter out = null;
				DataOutputStream testout = null;

				StringBuffer buffer = null;
				int i = 0;
				byte[] bytes;
				JSONArray res;
				try {
					final URL url = new URL(test_url);
					urlConnection = (HttpURLConnection) url.openConnection();

					// Post method
					urlConnection.setDoOutput(true);
					urlConnection.setRequestMethod("POST");
					urlConnection.setUseCaches(false);
					urlConnection.setConnectTimeout(10000);
					urlConnection.setReadTimeout(10000);
					urlConnection.setRequestProperty("Content-Type",
							"application/json");

					testout = new DataOutputStream(urlConnection
							.getOutputStream());
					JSONObject jsonParam = new JSONObject();
					if (up_or_down == 0) {
						jsonParam.put("page", Integer.toString(page_num_end)); // ����ˢ��
					} else {
						if (page_num_start != 0) {
							page_num_start--;
							jsonParam.put("page",
									Integer.toString(page_num_start));// ����ˢ��
						} else { // �޷�����ˢ�£��Ѿ��ǵ�һҳ����Toast
							Message message = new Message();
							message.what = 8;
							handler.sendMessage(message);
							return;
						}
					}
					String temp = jsonParam.toString();
					testout.write(temp.getBytes());
					testout.flush();
					testout.close();
					Log.d("ContentActivity",
							"" + urlConnection.getResponseCode());

					int HttpResult = urlConnection.getResponseCode();
					if (HttpResult == HttpURLConnection.HTTP_OK) {
						in = urlConnection.getInputStream();
						bytes = new byte[1024];
						buffer = new StringBuffer();
						while ((i = in.read(bytes)) != -1) {
							buffer.append(new String(bytes, 0, i, "UTF-8"));
						}

						String response2 = buffer.toString();
						Log.d("ContentActivity", response2);
						res = new JSONArray(response2);

						if (up_or_down == 0) { // ����ˢ��
							if ((para_url.size() - (page_num_end - page_num_start) * 10) < res
									.length()) {
								Message message = new Message();
								message.what = 4;
								message.arg1 = para_url.size();
								for (i = para_url.size() % 10; i < res.length(); i++) {
									JSONObject jsonObject = res
											.getJSONObject(i);
									String urli = jsonObject.getString("url");
									Log.d("ContentActivity", "url is " + urli);
									para_url.add(urli);
								}
								handler.sendMessage(message);
							} else {
								Message message = new Message();
								message.what = 6;
								handler.sendMessage(message);
							}
							if (res.length() == 10) {
								page_num_end++;
							}
						} else { // ����ˢ��
							Message message = new Message();
							message.what = 7;
							// message.arg1 = para_url.size();
							for (i = 9; i >= 0; i--) { // ���ϸ���һ����10�����䣬����ӵ�ǰ��
								JSONObject jsonObject = res.getJSONObject(i);
								String urli = jsonObject.getString("url");
								Log.d("ContentActivity", "url is " + urli);
								para_url.add(0, urli);
							}
							handler.sendMessage(message);
						}

					} else {
						Log.d("ContentActivity",
								"not ok: " + urlConnection.getResponseMessage());
						Message message = new Message();
						message.what = 5;
						handler.sendMessage(message);
					}

				} catch (final IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (urlConnection != null) {
						urlConnection.disconnect();
					}
					try {

						if (in != null) {
							in.close();
						}

						if (out != null) {
							out.close();
						}
						if (testout != null) {
							testout.close();
						}

					} catch (final IOException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();

	}

	public class ContentAdapter extends BaseAdapter {

		private LayoutInflater inflater;
		Context context;
		private List<BaseItem> ls;

		ViewHolder1 holder1 = null;
		ViewHolder2 holder2 = null;

		final int VIEW_TYPE = 2;
		final int TYPE_1 = 0;
		final int TYPE_2 = 1;

		public ContentAdapter(Context context, List<BaseItem> ls) {
			this.inflater = LayoutInflater.from(context);
			this.context = context;
			this.ls = ls;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return (ls == null) ? 0 : ls.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return ls.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			if (ls.get(position) == null) {
				return 0;
			}

			return ls.get(position).getItem_type();
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub

			if (ls.get(position) != null) {
				int type = getItemViewType(position);
				if (convertView == null) {
					switch (type) {
					case TYPE_1:
						convertView = inflater.inflate(
								R.layout.activity_content_item, parent, false);
						holder1 = new ViewHolder1();
						// ��ȡ�ؼ�
						holder1.tvTitle = (TextView) convertView
								.findViewById(R.id.title);
						holder1.tvTag = (TextView) convertView
								.findViewById(R.id.tag);
						holder1.tvAuthor = (TextView) convertView
								.findViewById(R.id.author);
						holder1.ivPortraitImage = (ImageView) convertView
								.findViewById(R.id.PortraitImage);
						holder1.tvTime = (TextView) convertView
								.findViewById(R.id.time);
						holder1.tvContent = (TextView) convertView
								.findViewById(R.id.content);
						holder1.tvPraise = (TextView) convertView
								.findViewById(R.id.praise);
						holder1.llPraise = (LinearLayout) convertView
								.findViewById(R.id.praise_button);
						holder1.cbPraise = (CheckBox) convertView
								.findViewById(R.id.praise_checkbox);
						convertView.setTag(holder1);
						break;
					case TYPE_2:
						convertView = inflater.inflate(R.layout.xuxie_item,
								null);
						holder2 = new ViewHolder2();
						// ��ȡ�ؼ�
						holder2.llXumo = (LinearLayout) convertView
								.findViewById(R.id.xumo);
						convertView.setTag(holder2);
						break;
					default:
						break;
					}
				} else {
					switch (type) {
					case TYPE_1:
						holder1 = (ViewHolder1) convertView.getTag();// ȡ��ViewHolder����
						break;
					case TYPE_2:
						holder2 = (ViewHolder2) convertView.getTag();// ȡ��ViewHolder����
						break;
					default:
						break;
					}
				}

				switch (type) {
				case TYPE_1:
					// ��ȡ����
					final Topic topict = (Topic) ls.get(position);
					Log.d("position", "position = " + position);
					if (!(topict.getfloorNum().equals("1"))) {		//����һ¥��û�б���
						LinearLayout llTitleandTag = (LinearLayout) convertView
								.findViewById(R.id.ll_titleandtag);
						llTitleandTag.setVisibility(View.GONE);

					} else {										//��һ¥
						
						Intent intent = getIntent();
						String article_title = intent.getStringExtra("title");
						String article_tag = intent.getStringExtra("tag");
						share_title = article_title;
						
						LinearLayout llTitleandTag = (LinearLayout) convertView
								.findViewById(R.id.ll_titleandtag);
						llTitleandTag.setVisibility(View.VISIBLE);
						
						holder1.tvTitle.setText(article_title);
						
						switch (myapp.getFontSize()) {
						case 0:
							holder1.tvTitle.setTextSize(16);
							break;

						case 1:
							holder1.tvTitle.setTextSize(20);
							break;

						case 2:
							holder1.tvTitle.setTextSize(24);
							break;

						default:
							break;
						}
						
						holder1.tvTag.setText(article_tag);
					}
					holder1.tvAuthor.setText(topict.getAuthor());
					byte[] data = topict.getBitmap();
					if (data != null) {
						Log.d("adapter", "ddddddddddddata size: " + data.length);
						holder1.ivPortraitImage.setImageBitmap(BitmapFactory
								.decodeByteArray(data, 0, data.length));
					}
					
					try {
						String systemTime = topict.getcreateTime();
						Log.d("Time", systemTime);
						Date date = new Date();
						date.setTime(Long.parseLong(systemTime+"000"));
						
						SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
						Log.d("Time", "format is " + sdf.format(date));
						
						holder1.tvTime.setText(sdf.format(date));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					holder1.tvContent.setText(topict.getContent());
					if(topict.getfloorNum().equals("1"))
					{
						share_text = topict.getContent();
					}

					switch (myapp.getFontSize()) {
					case 0:
						holder1.tvContent.setTextSize(14);
						break;

					case 1:
						holder1.tvContent.setTextSize(18);
						break;

					case 2:
						holder1.tvContent.setTextSize(22);
						break;

					default:
						break;
					}
					
					pref = getSharedPreferences("username_password", MODE_PRIVATE);

					username = pref.getString("username", "");
					
					SharedPreferences praises = getSharedPreferences("PraiseInfos",
							Activity.MODE_PRIVATE); // ���Ȼ�ȡһ�� SharedPreferences ����
					
					String praised_key = para_url.get(Integer.parseInt(topict.getfloorNum().isEmpty()?"1":topict.getfloorNum())-1) + "&uid=" + username;
					String praised = praises.getString(praised_key, "1");
					Log.d("praise", "praise: get key=" + praised_key + ",value=" + praised);
					if(praised.equals("1"))		//��uidδ����pid����
					{
						holder1.cbPraise.setChecked(false);
					}
					else
					{
						holder1.cbPraise.setChecked(true);
					}
					holder1.tvPraise.setText(topict.getPraise());
					holder1.llPraise
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub

									if(username.isEmpty())		//�û�δ��¼���޷�����
									{
										showLoginWindow(v);		//��ʾ�û���¼
									}
									else
									{
										post_json_praise(Integer.parseInt(topict.getfloorNum().isEmpty()?"1":topict.getfloorNum())-1);
										CheckBox cbpraise = (CheckBox) v
												.findViewById(R.id.praise_checkbox);
										TextView tvpraise = (TextView) v
												.findViewById(R.id.praise);
										
										SharedPreferences praises = getSharedPreferences("PraiseInfos",
												Activity.MODE_PRIVATE); // ���Ȼ�ȡһ�� SharedPreferences ����
										
										String praised_key = para_url.get(Integer.parseInt(topict.getfloorNum().isEmpty()?"1":topict.getfloorNum())-1) + "&uid=" + username;
										String praised = praises.getString(praised_key, "1");
										
										Log.d("praise", "praise: read key=" + praised_key + ",value=" + praised);
										
										if(praised.equals("1"))
										{
	
											cbpraise.setChecked(true);
											topict.setPraise("" + (Integer.parseInt(topict
													.getPraise()) + 1));
											tvpraise.setText(topict.getPraise());
	
											//�޸�sharedprefs ����
											praises.edit().putString(praised_key, "0").commit();
											Log.d("praise", "praise: write key=" + praised_key + ",value=" + "0");
											
										}
										else
										{
	
											cbpraise.setChecked(false);
											topict.setPraise("" + (Integer.parseInt(topict
													.getPraise()) - 1));
											tvpraise.setText(topict.getPraise());
											
											praises.edit().putString(praised_key, "1").commit();
											Log.d("praise", "praise: write key=" + praised_key + ",value=" + "1");
											
										}
									}
								}
							});
					break;
				case TYPE_2:
					holder2.llXumo
							.setOnClickListener(new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									if (myapp.getFlag_login()) {
										Intent intent = new Intent(
												ContentActivity.this,
												ContinueActivity.class);
										intent.putExtra("articleId", articleId);
										Log.d("article adapter",
												"parse articleId : "
														+ articleId);
										startActivityForResult(intent,1);
									} else {
										showLoginWindow(v);
									}
								}
							});
					break;
				default:
					break;

				}

			} else {

			}

			return convertView;
		}

		public class ViewHolder1 {
			TextView tvTitle;
			TextView tvTag;
			ImageView ivPortraitImage;
			TextView tvAuthor;
			TextView tvTime;
			TextView tvContent;
			TextView tvPraise;
			CheckBox cbPraise;
			LinearLayout llPraise;

		}

		public class ViewHolder2 {
			LinearLayout llXumo;

		}

	}

	/**
	 * ����Topic���󡣴˷�������LruCache�м��������Ļ�пɼ���floor��Topic����
	 * ��������κ�һ��floor��Topic�����ڻ����У��ͻῪ���첽�߳�ȥ����topic��
	 */
	public void loadTopic(String url) {
		FileInputStream fileInputStream = null;
		FileDescriptor fileDescriptor = null;
		try {
			Topic topic = myapp.getTopicFromMemoryCache(url);

			if (topic == null) {
				TopicWorkerTask task = new TopicWorkerTask();

				taskCollection.add(task);

				task.execute(url);
				Log.d("adapter", "task added.");
			} else {
				map.put(url, topic);
				try {
					listItems.set(para_url.indexOf(url), topic);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				adapter.notifyDataSetChanged();
				Log.d("adapter", "read from cache.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.d("adapter", "load topic error.");
		} finally {
			if (fileDescriptor == null && fileInputStream != null) {
				try {
					fileInputStream.close();
				} catch (IOException e) {
				}
			}

		}

	}

	/**
	 * ȡ�������������ػ�ȴ����ص�����
	 */
	public void cancelAllTasks() {
		if (taskCollection != null) {
			for (TopicWorkerTask task : taskCollection) {
				task.cancel(false);
			}
		}
	}

	class TopicWorkerTask extends AsyncTask<String, Void, Topic> {

		/**
		 * topic��URL��ַ
		 */
		private String url;

		@Override
		protected Topic doInBackground(String... params) {
			url = params[0];
			Log.d("adapter", "url in task is " + url);
			FileDescriptor fileDescriptor = null;
			FileInputStream fileInputStream = null;
			Snapshot snapShot = null;
			try {
				// ����ͼƬURL��Ӧ��key
				final String key = myapp.hashKeyForDisk(url);
				Log.d("adapter", "hash key in task is " + key);

				File filetemp = new File(mDiskLruCache.getDirectory(), key
						+ ".0");// ע���׺��

				int flag = 0;// 0�����أ�1����
				long expired_time;
				int compared_time = Integer.MAX_VALUE;

				if (myapp.isNetConnected()) {
					if (myapp.isMobileConnected()) {
						compared_time = CONFIG_CACHE_MOBILE_TIMEOUT;
						Log.d("adapter", "mobile network");
					}
					if (myapp.isWifiConnected()) {
						compared_time = CONFIG_CACHE_WIFI_TIMEOUT;
						Log.d("adapter", "wifi network");
					}

				} else {
					Log.d("adapter", "no network");

				}

				if (filetemp.lastModified() != 0) {
					expired_time = System.currentTimeMillis()
							- filetemp.lastModified();
					Log.d("adapter", "expired time is: " + expired_time);
					if (expired_time > compared_time) {
						flag = 1;
					}
				}

				if (flag == 1) {
					mDiskLruCache.remove(key);
				}

				// ����key��Ӧ�Ļ���
				snapShot = mDiskLruCache.get(key);
				if (snapShot == null) {
					Log.d("adapter", "snapshot is null");
					// ���û���ҵ���Ӧ�Ļ��棬��׼�����������������ݣ���д�뻺��
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						Log.d("adapter", "editor added.");
						// Topic�Ĵ��л������download������ʵ��
						if (downloadUrlToStream(url, outputStream)) {
							editor.commit();
						} else {
							editor.abort();
						}
					}
					// ���汻д����ٴβ���key��Ӧ�Ļ���
					snapShot = mDiskLruCache.get(key);
				}
				if (snapShot != null) {
					fileInputStream = (FileInputStream) snapShot
							.getInputStream(0);
					fileDescriptor = fileInputStream.getFD();
				}

				Topic topic = null;
				if (fileInputStream != null) {
					ObjectInputStream ois = new ObjectInputStream(
							fileInputStream);
					try {
						topic = (Topic) ois.readObject();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					ois.close();
				}
				if (topic != null) {
					// ��Topic������ӵ��ڴ滺�浱��

					myapp.addTopicToMemoryCache(url, topic);
				}

				return topic;

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (fileDescriptor == null && fileInputStream != null) {
					try {
						fileInputStream.close();
					} catch (IOException e) {
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Topic topic) {
			Log.d("adapter", "on postexecute.");
			super.onPostExecute(topic);

			// ����url����Ӧ��topic����hashmap��

			map.put(url, topic);
			Log.d("adapter", "map put complete.");

			/*
			 * try { listItems.set(para_url.indexOf(url), topic); } catch
			 * (Exception e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } adapter.notifyDataSetChanged();
			 * Log.d("adapter", "adapter refresh complete.");
			 */

			Message message = new Message();
			message.what = 4;
			message.obj = url;
			topic_handler.sendMessage(message);

			taskCollection.remove(this);

		}

		/**
		 * ����HTTP���󣬲���ȡTopic����
		 * 
		 * @param Url
		 *            topic��URL��ַ
		 * @return ���������topic����д��outputStream��
		 */
		private boolean downloadUrlToStream(String urlString,
				OutputStream outputStream) {
			HttpURLConnection urlConnection = null;
			InputStream in = null;
			// BufferedReader reader = null;
			ObjectOutputStream oos = null;
			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();

			OutputStreamWriter out = null;
			DataOutputStream testout = null;

			Topic topic = null;
			byte[] bitmapbyte = null;
			byte[] bytes = null;
			StringBuffer buffer = null;
			int i = 0;
			try {
				final URL url = new URL(urlString);
				urlConnection = (HttpURLConnection) url.openConnection();

				// Post method
				urlConnection.setDoOutput(true);
				urlConnection.setRequestMethod("POST");
				urlConnection.setUseCaches(false);
				urlConnection.setConnectTimeout(8000);
				urlConnection.setReadTimeout(8000);
				urlConnection.setRequestProperty("Content-Type",
						"application/json");
				urlConnection.connect();

				JSONObject jsonParam = new JSONObject();

				pref = getSharedPreferences("username_password", MODE_PRIVATE);
				username = pref.getString("username", "");

				jsonParam.put("uid", username);

				out = new OutputStreamWriter(urlConnection.getOutputStream());
				String temp = jsonParam.toString();

				out.write(temp);
				Log.d("ServerCom", "temp: " + temp);
				out.close();

				int HttpResult = urlConnection.getResponseCode();
				if (HttpResult == HttpURLConnection.HTTP_OK) {
					br = new BufferedReader(new InputStreamReader(
							urlConnection.getInputStream(), "utf-8"));
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}
					br.close();
					//
					// JSONObject jsonObject = new JSONObject(sb.toString());
					//
					//
					// in = urlConnection.getInputStream();
					//
					// // ����Ի�ȡ�������������ж�ȡ,UTF-8����
					// /*
					// * reader = new BufferedReader(new InputStreamReader(in));
					// * StringBuilder response = new StringBuilder();
					// */
					// bytes = new byte[1024];
					// buffer = new StringBuffer();
					// /*
					// * String line; while ((line = reader.readLine()) != null)
					// {
					// * response.append(line); //Log.d("adapter", "line: " +
					// line); }
					// */
					// while ((i = in.read(bytes)) != -1) {
					// buffer.append(new String(bytes, 0, i, "UTF-8"));
					// }

					String response2 = sb.toString();
					Log.d("adapter", "response is " + response2);

					String praise_key = urlString + "&uid=" + username;

					topic = myapp.parseJSONWithJSONObject(response2,praise_key);
					if (TextUtils.isEmpty(topic.getimageUrl())) {
						Log.d("adapter", "enter default image");
						topic.setBitmap(myapp.getdefault_image());
					} else {
						Log.d("adapter", "enter download image");
						bitmapbyte = downloadUrlToByte(topic.getimageUrl());
						topic.setBitmap(bitmapbyte);
					}

					oos = new ObjectOutputStream(outputStream);
					oos.writeObject(topic);
					oos.close();
					Log.d("adapter", "oos closed.");

					Log.d("ServerCom","from server: "+ sb.toString());
					
				} else {
					// System.out.println(urlConnection.getResponseMessage());
					Log.d("ServerCom",
							"not ok: " + urlConnection.getResponseMessage());
				}
				return true;
			} catch (final Exception e) {
				e.printStackTrace();
			} /*
			 * catch (JSONException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */finally {
				if (urlConnection != null) {
					urlConnection.disconnect();
				}
				try {
					if (oos != null) {
						oos.close();
					}
					if (in != null) {
						in.close();
					}
					/*
					 * if (reader != null) { reader.close(); }
					 */
					if (out != null) {
						out.close();
					}
					if (testout != null) {
						testout.close();
					}

				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			return false;

		}

	}

	private byte[] downloadUrlToByte(String urlString) {
		HttpURLConnection urlConnection = null;
		InputStream in = null;
		ByteArrayOutputStream outStream = null;
		byte[] buffer = null;
		try {
			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(8000);
			urlConnection.setReadTimeout(8000);
			if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return null;
			}
			Log.d("adapter", "second urlconnection ok.");
			in = urlConnection.getInputStream();
			outStream = new ByteArrayOutputStream();
			buffer = new byte[1024];
			int len = 0;
			while ((len = in.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}

			return outStream.toByteArray();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (in != null) {
					in.close();
				}
				if (outStream != null) {
					outStream.close();
				}

			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private void showLoginWindow(View view) {
		View loginView = getLayoutInflater().inflate(R.layout.login_window,
				null, true);
		loginWindow = new PopupWindow(loginView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);

		loginWindow.setFocusable(true);
		loginWindow.setOutsideTouchable(true);
		loginWindow.setBackgroundDrawable(new BitmapDrawable());

		// �����䰵
		new Handler().postDelayed(new Runnable() {
			public void run() {
				// execute the task
				ColorDrawable cd = new ColorDrawable(0x000000);
				loginWindow.setBackgroundDrawable(cd);
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 0.4f;
				getWindow().setAttributes(lp);
			}
		}, 300);

		// ���ö���Ч��
		// loginWindow.setAnimationStyle(R.style.AnimationFade);
		// ���ؼ��˳�
		loginView.setFocusableInTouchMode(true);
		loginView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					loginWindow.dismiss();
					loginWindow = null;
					return true;
				}

				return false;
			}
		});

		// ������λ����ʾ��ʽ
		loginWindow.showAtLocation(lv, Gravity.CENTER, 0, 0);

		// �����ָ�
		loginWindow.setOnDismissListener(new OnDismissListener() {
			// ��dismiss�лָ�͸����
			public void onDismiss() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});

		cancelButton = (Button) loginView.findViewById(R.id.bt_cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loginWindow.dismiss();
			}
		});
		loginButton = (Button) loginView.findViewById(R.id.bt_login);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ContentActivity.this,
						LoginActivity.class);
				startActivity(intent);
				loginWindow.dismiss();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case 1:
			//ˢ��ҳ�棬ͣ�������·�
			up_or_down = 0;

			//ɾ����д��ť
			listItems.remove(listItems.size()-1);
			has_xuxie_button=false;
			adapter.notifyDataSetChanged();
			
			GetParaFromServer();
			break;

		default:
			break;
		}
	}

}

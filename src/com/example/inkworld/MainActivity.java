package com.example.inkworld;

import java.io.BufferedOutputStream;
import android.view.ViewGroup.LayoutParams;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import libcore.io.DiskLruCache;
import libcore.io.DiskLruCache.Snapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.CanvasTransformer;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MainActivity extends SlidingFragmentActivity {

	/*
	 * 变量区
	 */

	// 判断是否要显示蒙版提示
	private boolean showAssert;
	private View rootView;
	private Fragment detailsAssertFragment;
	private ImageView guide01, guide02, guide03;

	private PopupWindow loginWindow;
	private Button cancelButton, loginButton;

	private CanvasTransformer mTransformer;
	int welcomecount;

	private SharedPreferences pref;

	public static final String SETTING_INFOS = "SettingInfos"; // SharedPreference文件名

	private MyApplication Myapp;

	private WindowManager mWindowManager;

	private View nightView = null;

	private TextView nicknameTextView;
	private ImageView user_tx;

	private FixedSpeedScroller mScroller;

	private SlidingMenu slidingMenu;

	private LinearLayout userSlide, newarticle;

	private RadioGroup jiaxinGroup;

	private PullToRefreshListView lv;

	private MainAdapter adapter;
	private Topic empty_topic;
	private List<Topic> listItems;

	private LinearLayout sy, wdsc, wdcy, ss, sz, clicktologin;

	private int page = 0;

	public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路径

	private ViewPager adViewPager;

	private List<ImageView> imageViews;// 滑动的图片集合

	private List<View> dots; // 图片标题正文的那些点

	private List<View> dotList;

	private TextView tvBannertitle;

	private int currentItem = 0; // 当前图片的索引号

	// 定义的三个指示点
	private View dot0;

	private View dot1;

	private View dot2;

	private ScheduledExecutorService scheduledExecutorService;// 按指定频率周期执行某个任务

	// 异步加载图片
	private ImageLoader mImageLoader;

	private DisplayImageOptions options;

	private String username, password;

	/**
	 * 记录所有正在下载或等待下载的任务。
	 */
	private Set<TopicWorkerTask> taskCollection;

	/**
	 * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
	 */
	private android.support.v4.util.LruCache<String, Topic> mMemoryCache;

	/**
	 * 图片硬盘缓存核心类。
	 */
	private DiskLruCache mDiskLruCache;

	private static final int CONFIG_CACHE_MOBILE_TIMEOUT = 3600000; // 1 hour
	private static final int CONFIG_CACHE_WIFI_TIMEOUT = 300000; // 5 minute

	private ArrayList<String> article_id = new ArrayList<String>();

	private HashMap<String, Topic> map;

	private String enter_article = "";

	private int up_or_down = 1; // 0向下刷新，1向上刷新

	// 轮播banner的数据
	private List<AdDomain> adList;

	private Handler article_handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 4) {
				String[] stockArr = new String[article_id.size()];
				stockArr = article_id.toArray(stockArr);
				int start_article = msg.arg1;
				for (int i = start_article; i < article_id.size(); i++) {
					listItems.add(empty_topic);
				}

				for (int i = start_article; i < article_id.size(); i++) {
					loadTopic(stockArr[i]);
				}

			} else if (msg.what == 5) {

				String[] stockArr = new String[article_id.size()];
				stockArr = article_id.toArray(stockArr);

				listItems.clear();

				for (int i = 0; i < article_id.size(); i++) {
					listItems.add(empty_topic);
				}

				for (int i = 0; i < article_id.size(); i++) {
					loadTopic(stockArr[i]);
				}

			} else if (msg.what == 6) {
				Toast.makeText(MainActivity.this, "网络连接出错，请检查网络设置",
						Toast.LENGTH_SHORT).show();
			} else {

			}

		}
	};

	private Handler topic_handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 4) {
				String id = (String) msg.obj;
				Topic topic = map.get(id);
				try {
					Log.d("article adapter", "id = " + id + " , index = "
							+ article_id.indexOf(id));
					listItems.set(article_id.indexOf(id), topic);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// add_num--;
				adapter.notifyDataSetChanged();
				// if(add_num == 0 && first_load == 0)
				// {
				// actualListView.smoothScrollBy(200, 100);
				// }

			} else {

			}

		}
	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			adViewPager.setCurrentItem(currentItem, true);
		};
	};

	Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			String res = (String) msg.obj;
			if (res.equals("refresh image")) {
				// byte[] data = Myapp.getuserImage();
				// if (data != null) {
				// Log.d("LoginTest", "enter 1");
				// Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
				// data.length);
				// user_tx.setImageBitmap(bitmap);
				// } else {
				if (fileIsExists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(getBaseContext()
							.getFilesDir() + "/Profile.png");
					Toast.makeText(MainActivity.this, "读取头像",
							Toast.LENGTH_SHORT).show();
					user_tx.setImageBitmap(bitmap);
				} else {
					Log.d("LoginTest", "enter 2");
					user_tx.setImageResource(R.drawable.icon_tx);
				}

				// }

				Myapp.setFlag_notx(false);

			} else if (res.equals("no_tx")) {
				user_tx.setImageResource(R.drawable.icon_tx);
				Myapp.setFlag_notx(true);
			}

		};
	};
	Handler handler3 = new Handler() {
		public void handleMessage(Message msg) {
			String res = (String) msg.obj;
			if (res.equals("auto_login_ok")) {
				Myapp.setUsernameString(pref.getString("username", ""));

				Log.d("LoginTest", "Uid" + Myapp.getUsernameString());

				if (!Myapp.getFlag_notx() && !fileIsExists()) {
					downloadUserImage();
				}

			} else {

			}

		};
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		empty_topic = new Topic(0, "", "", "", "", "", "", "", "", "");

		Intent intent = getIntent();
		showAssert = intent.getBooleanExtra("flag", false);

		if (showAssert) {
			rootView = findViewById(R.id.root);
			final Handler mHandler = new Handler();
			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					// LogUnit.Log(TAG, "post delay ");
					if (rootView.getWidth() > 0) {
						// LogUnit.Log(TAG,
						// "on resume"+rootView.getWidth()+"  "+rootView.getHeight());
						detailsAssertFragment = new MyJobDetailsAssertFragment();
						getSupportFragmentManager().beginTransaction()
								.add(R.id.root, detailsAssertFragment).commit();
					} else {
						mHandler.postDelayed(this, 100);
					}
				}
			}, 500);
		}

		// 分享初始化
		ShareSDK.initSDK(this);

		map = new HashMap<String, Topic>();

		pref = getSharedPreferences("username_password", MODE_PRIVATE);

		username = pref.getString("username", "");
		password = pref.getString("password", "");

		Myapp = (MyApplication) getApplication();
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
			login();
		}

		taskCollection = new HashSet<TopicWorkerTask>();
		mMemoryCache = Myapp.getLruCache();
		mDiskLruCache = Myapp.getDiskLruCache();

		initImageLoader();
		// 获取图片加载实例
		mImageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.top_banner_android)
				.showImageForEmptyUri(R.drawable.top_banner_android)
				.showImageOnFail(R.drawable.top_banner_android)
				.cacheInMemory(true).cacheOnDisk(true)
				.bitmapConfig(Bitmap.Config.ARGB_8888)
				.imageScaleType(ImageScaleType.EXACTLY).build();

		/*
		 * 添加新文章 未登录给予提示
		 */
		newarticle = (LinearLayout) this.findViewById(R.id.newarticle);
		newarticle.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (Myapp.getFlag_login()) {
					Intent intent = new Intent(MainActivity.this,
							StoryActivity.class);
					startActivityForResult(intent, 1);
				} else {
					showLoginWindow(arg0);
				}

			}
		});

		/*
		 * banner操作
		 */

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

		listItems = new ArrayList<Topic>();

		for (int i = 0; i < 10; i++) {
			listItems.add(empty_topic);
		}

		adapter = new MainAdapter(MainActivity.this, listItems);

		ListView actualListView = lv.getRefreshableView();

		actualListView.setDivider(null);

		View headView = View.inflate(this, R.layout.banner, null);
		// 广告数据
		adList = getBannerAd();

		imageViews = new ArrayList<ImageView>();

		// 点
		dots = new ArrayList<View>();
		dotList = new ArrayList<View>();
		dot0 = headView.findViewById(R.id.v_dot0);
		dot1 = headView.findViewById(R.id.v_dot1);
		dot2 = headView.findViewById(R.id.v_dot2);
		dots.add(dot0);
		dots.add(dot1);
		dots.add(dot2);

		tvBannertitle = (TextView) headView.findViewById(R.id.tv_bannertitle);

		adViewPager = (ViewPager) headView.findViewById(R.id.vp);
		adViewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
		// 设置一个监听器，当ViewPager中的页面改变时调用
		adViewPager.setOnPageChangeListener(new MyPageChangeListener());
		addDynamicView();

		try {
			Field mField = ViewPager.class.getDeclaredField("mScroller");
			mField.setAccessible(true);
			mScroller = new FixedSpeedScroller(adViewPager.getContext(),
					new AccelerateInterpolator());
			mScroller.setmDuration(300);
			mField.set(adViewPager, mScroller);
		} catch (Exception e) {
			e.printStackTrace();
		}

		actualListView.addHeaderView(headView);

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

						GetUrlFromServer();

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

						GetUrlFromServer();

						lv.onRefreshComplete();

					};

				}.execute();
			}
		});

		// item加载点击事件

		actualListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				@SuppressWarnings("unchecked")
				/*
				 * Map<String, Object> item = (Map<String, Object>) arg0
				 * .getItemAtPosition(arg2);
				 */
				Intent intent = new Intent(MainActivity.this,
						ContentActivity.class);
				Log.d("article adapter", "position = " + position + " , id = "
						+ id);

				if (article_id == null || article_id.isEmpty()
						|| ((int) id >= article_id.size())) {
					Toast.makeText(MainActivity.this, "当前无网络",
							Toast.LENGTH_SHORT).show();
				} else {
					String this_article_id = article_id.get((int) id);
					intent.putExtra("articleId", this_article_id);

					String article_title = ((TextView) view
							.findViewById(R.id.title)).getText().toString();
					String article_tag = ((TextView) view
							.findViewById(R.id.tv_labeltext)).getText()
							.toString();

					intent.putExtra("title", article_title);
					intent.putExtra("tag", article_tag);

					startActivity(intent);
				}
			}
		});

		// 佳墨新墨切换
		jiaxinGroup = (RadioGroup) findViewById(R.id.rg_xinmo_jiamo);

		jiaxinGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub

				switch (checkedId) {
				case R.id.rb_xinmo:

					break;

				case R.id.rb_jiamo:

					break;

				}
			}
		});
		// 侧边栏

		initAnimation();

		slidingMenu = getSlidingMenu();

		slidingMenu.setBehindOffsetRes(R.dimen.sliding_menu_offset);// 拉开后离边框距离

		setBehindContentView(R.layout.slidingmenu);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.ll_slidingmenu, new Fragment()).commit();

		slidingMenu.setMode(SlidingMenu.LEFT);// 菜单左边显示

		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);

		slidingMenu.setShadowDrawable(R.drawable.shadow_margin);

		slidingMenu.setFadeDegree(0.35f);

		slidingMenu.setBehindScrollScale(0.6f);

		slidingMenu.setBehindCanvasTransformer(mTransformer);

		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		sy = (LinearLayout) slidingMenu.findViewById(R.id.sy);
		wdsc = (LinearLayout) slidingMenu.findViewById(R.id.wdsc);
		wdcy = (LinearLayout) slidingMenu.findViewById(R.id.wdcy);
		ss = (LinearLayout) slidingMenu.findViewById(R.id.ss);
		sz = (LinearLayout) slidingMenu.findViewById(R.id.sz);

		sy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				slidingMenu.toggle();
			}
		});
		wdsc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						MycollectionActivity.class);
				startActivity(intent);
			}
		});
		wdcy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						MypartakeActivity.class);
				startActivity(intent);
			}
		});
		ss.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						SearchActivity.class);
				startActivity(intent);
			}
		});
		sz.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						SettingsActivity.class);
				startActivity(intent);
			}
		});

		userSlide = (LinearLayout) findViewById(R.id.userSlide);

		userSlide.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				slidingMenu.toggle();
			}
		});

		GetUrlFromServer();

	}

	// initAnimation初始化动画
	private void initAnimation() {
		mTransformer = new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (percentOpen * 0.25 + 0.75);
				canvas.scale(scale, scale, canvas.getWidth() / 2,
						canvas.getHeight() / 2);
			}

		};
	}

	@SuppressWarnings("deprecation")
	private void initImageLoader() {
		File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils
				.getOwnCacheDirectory(getApplicationContext(), IMAGE_CACHE_PATH);

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisk(true).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions)
				.memoryCache(new LruMemoryCache(12 * 1024 * 1024))
				.memoryCacheSize(12 * 1024 * 1024)
				.discCacheSize(32 * 1024 * 1024).discCacheFileCount(100)
				.discCache(new UnlimitedDiskCache(cacheDir))
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.tasksProcessingOrder(QueueProcessingType.LIFO).build();

		ImageLoader.getInstance().init(config);
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public static List<AdDomain> getBannerAd() {
		List<AdDomain> adList = new ArrayList<AdDomain>();

		AdDomain adDomain = new AdDomain();
		// adDomain.setTitle("今天中午遇到了一个非常厉害的创业团队，叫做续墨....");
		adDomain.setImgUrl("http://img4.imgtn.bdimg.com/it/u=3399783664,2912316268&fm=21&gp=0.jpg");
		adDomain.setAd(false);
		adList.add(adDomain);

		AdDomain adDomain2 = new AdDomain();
		// adDomain2.setTitle("今天中午遇到了一个不知道叫什么的非常厉害的创业团队....");
		adDomain2
				.setImgUrl("http://img5.imgtn.bdimg.com/it/u=4031657623,3240184244&fm=21&gp=0.jpg");
		adDomain2.setAd(false);
		adList.add(adDomain2);

		AdDomain adDomain3 = new AdDomain();
		// adDomain3.setTitle("有一天，黑豆在粉笔灰里摔了一跤....");
		adDomain3
				.setImgUrl("http://img2.imgtn.bdimg.com/it/u=488719445,1548728595&fm=21&gp=0.jpg");
		adDomain3.setAd(false);
		adList.add(adDomain3);

		return adList;
	}

	public class FixedSpeedScroller extends Scroller {
		private int mDuration = 1500;

		public FixedSpeedScroller(Context context) {
			super(context);
		}

		public FixedSpeedScroller(Context context, Interpolator interpolator) {
			super(context, interpolator);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy,
				int duration) {
			// Ignore received duration, use fixed one instead
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {
			// Ignore received duration, use fixed one instead
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		public void setmDuration(int time) {
			mDuration = time;
		}

		public int getmDuration() {
			return mDuration;
		}
	}

	private class MyAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return adList.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView iv = imageViews.get(position);
			if (iv.getParent() != null) {
				ViewGroup parent = (ViewGroup) (iv.getParent());
				parent.removeView(iv);
			}
			((ViewPager) container).addView(iv);
			@SuppressWarnings("unused")
			final AdDomain adDomain = adList.get(position);
			iv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

				}
			});
			return iv;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// ((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}

	}

	private class MyPageChangeListener implements OnPageChangeListener {

		private int oldPosition = 0;

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int position) {
			currentItem = position;
			AdDomain adDomain = adList.get(position);
			tvBannertitle.setText(adDomain.getTitle());
			dots.get(oldPosition).setBackgroundResource(
					R.drawable.banner_unselect);
			dots.get(position).setBackgroundResource(R.drawable.banner_select);
			oldPosition = position;
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}
	}

	private void addDynamicView() {
		for (int i = 0; i < adList.size(); i++) {
			ImageView imageView = new ImageView(this);
			mImageLoader.displayImage(adList.get(i).getImgUrl(), imageView,
					options);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageViews.add(imageView);
			dots.get(i).setVisibility(View.VISIBLE);
			dotList.add(dots.get(i));
		}
	}

	private void showLoginWindow(View view) {
		View loginView = getLayoutInflater().inflate(R.layout.login_window,
				null, true);
		loginWindow = new PopupWindow(loginView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);

		loginWindow.setFocusable(true);
		loginWindow.setOutsideTouchable(true);
		loginWindow.setBackgroundDrawable(new BitmapDrawable());

		// 背景变暗
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

		// 设置动画效果
		// loginWindow.setAnimationStyle(R.style.AnimationFade);
		// 返回键退出
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

		// 这里是位置显示方式
		loginWindow.showAtLocation(lv, Gravity.CENTER, 0, 0);

		// 背景恢复
		loginWindow.setOnDismissListener(new OnDismissListener() {
			// 在dismiss中恢复透明度
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
				Intent intent = new Intent(MainActivity.this,
						LoginActivity.class);
				startActivity(intent);
				loginWindow.dismiss();
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (slidingMenu.isMenuShowing()) {
			slidingMenu.showContent();
		} else {
			super.onBackPressed();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		startAd();
		/*
		 * 未登陆跳转到登陆 已登陆跳转到个人主页
		 */
		nicknameTextView = (TextView) this.findViewById(R.id.nickname);
		clicktologin = (LinearLayout) this.findViewById(R.id.clicktologin);

		nicknameTextView = (TextView) this.findViewById(R.id.nickname);
		user_tx = (ImageView) this.findViewById(R.id.user_tx);

		clicktologin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!Myapp.getFlag_login()) {
					Intent intent = new Intent(MainActivity.this,
							LoginActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent(MainActivity.this,
							UserpageActivity.class);
					startActivity(intent);

				}
			}
		});

		// downloadUserImage();
		// 显示头像与用户名
		if (Myapp.getFlag_login()) {
			if (fileIsExists()) {
				if (Myapp.getFlag_notx()) {
					user_tx.setImageResource(R.drawable.icon_tx);
				} else {
					Bitmap bitmap = BitmapFactory.decodeFile(getBaseContext()
							.getFilesDir() + "/Profile.png");
					user_tx.setImageBitmap(bitmap);
				}

			} else {
				user_tx.setImageResource(R.drawable.icon_tx);
			}

			// 昵称显示字数限制
			String username2 = pref.getString("username", "");

			if (username2.length() > 5) {
				username2 = username2.substring(0, 5) + "…";
			}
			nicknameTextView.setText(username2);
			if (welcomecount == 0) {
				Toast.makeText(MainActivity.this,
						"欢迎来到续墨O(∩_∩)O~~，" + pref.getString("username", ""),
						Toast.LENGTH_LONG).show();
				welcomecount = 1;
			}

		} else {
			user_tx.setBackgroundResource(R.drawable.icon_tx);
			nicknameTextView.setText("点击登录");
		}

	}

	public boolean fileIsExists() {
		try {
			File f = new File(getBaseContext().getFilesDir() + "/Profile.png");
			if (!f.exists()) {
				return false;
			} else {
				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	/**
	 * 保存图片到app指定路径
	 * 
	 * @param bm头像图片资源
	 * @param fileName保存名称
	 */
	public void saveFile(Bitmap bm) {
		try {
			String Path = getBaseContext().getFilesDir() + "";
			File dirFile = new File(Path);
			if (!dirFile.exists()) {
				dirFile.mkdirs();
			}
			File myCaptureFile = new File(getBaseContext().getFilesDir()
					+ "/Profile.png");
			BufferedOutputStream bo = null;
			bo = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			bm.compress(Bitmap.CompressFormat.PNG, 100, bo);

			bo.flush();
			bo.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void downloadUserImage() {

		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				HttpURLConnection urlConnection = null;
				InputStream in = null;
				ByteArrayOutputStream outStream = null;
				byte[] buffer = null;
				String test_url = Myapp.getdownload_userImage_url() + username;
				Log.d("LoginTest", "download image url: " + test_url);
				try {
					final URL url = new URL(test_url);
					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setRequestMethod("GET");
					urlConnection.setConnectTimeout(8000);
					urlConnection.setReadTimeout(8000);

					Log.d("LoginTest",
							"download image: "
									+ urlConnection.getResponseCode());

					if (urlConnection.getResponseCode() == 404) {
						Message msg = new Message();
						msg.obj = "no_tx";
						handler2.sendMessage(msg);
					}

					if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						in = urlConnection.getInputStream();
						outStream = new ByteArrayOutputStream();
						buffer = new byte[1024];
						int len = 0;
						while ((len = in.read(buffer)) != -1) {
							outStream.write(buffer, 0, len);
						}
						Myapp.setuserImage(outStream.toByteArray());

						saveFile(Bytes2Bimap(outStream.toByteArray()));

						Log.d("LoginTest", "download userimage ok"
								+ outStream.toByteArray().toString());

						// if (flag.equals("on create download image")) {
						// flag = "";

						Message message = new Message();
						message.obj = "refresh image";
						handler2.sendMessage(message);
						Log.d("LoginTest", "refresh image");

						// }

					} else {
						Log.d("LoginTest", "download image not ok: "
								+ urlConnection.getResponseMessage());

					}

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

			}
		}).start();

	}

	public Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	private void login() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				StringBuilder sb = new StringBuilder();
				String serverurl = Myapp.getlogin_url();

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

					// Create JSONObject here
					JSONObject jsonParam = new JSONObject();
					jsonParam.put("uid", username);
					jsonParam.put("password", password);
					jsonParam.put("type", "0");
					String temp = jsonParam.toString();

					out = new OutputStreamWriter(urlConnection
							.getOutputStream());
					// out.write(jsonParam.toString());
					// out.write( URLEncoder.encode(temp,"UTF-8"));
					out.write(temp);
					Log.d("LoginTest", "temp: " + temp);
					out.close();

					Log.d("LoginTest",
							"login code: " + urlConnection.getResponseCode());

					int HttpResult = urlConnection.getResponseCode();
					if (HttpResult == HttpURLConnection.HTTP_OK) {
						br = new BufferedReader(new InputStreamReader(
								urlConnection.getInputStream(), "utf-8"));
						String line = null;
						while ((line = br.readLine()) != null) {
							sb.append(line + "\n");
						}
						br.close();

						String result = sb.toString().trim();

						if (!TextUtils.isEmpty(result) && (!result.equals("4"))) {
							Myapp.setvalidation_code(result);
							Myapp.setFlag_login(Myapp.LOGIN);
							Message message = new Message();
							message.obj = "auto_login_ok";
							handler3.sendMessage(message);

						} else {

						}

					} else {
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

	// 夜间模式
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

	private void startAd() {

		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 6,
				TimeUnit.SECONDS);
	}

	private class ScrollTask implements Runnable {

		@Override
		public void run() {
			synchronized (adViewPager) {
				currentItem = (currentItem + 1) % imageViews.size();
				handler.obtainMessage().sendToTarget();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		scheduledExecutorService.shutdown();
		SharedPreferences settings = getSharedPreferences(SETTING_INFOS,
				Activity.MODE_PRIVATE); // 首先获取一个 SharedPreferences 对象

		Myapp = (MyApplication) getApplication();

		int lightmode = 0;
		if (Myapp.getlunaMode()) {
			lightmode = 1;
		}

		settings.edit().putInt("LightMode", lightmode).commit();

		int fontSize = 0;
		if (Myapp.getFontSize() == 1) {
			fontSize = 1;
		} else if (Myapp.getFontSize() == 2) {
			fontSize = 2;
		}

		settings.edit().putInt("fontSize", fontSize).commit();

		Boolean flag_login = false;
		if (Myapp.getFlag_login()) {
			flag_login = true;
		}
		settings.edit().putBoolean("flag_login", flag_login).commit();

		Boolean flag_notx = false;
		if (Myapp.getFlag_notx()) {
			flag_notx = true;
		}

		settings.edit().putBoolean("flag_notx", flag_notx).commit();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);

		scheduledExecutorService.shutdownNow();

		// 获取参数
		Boolean is_download = intent.getBooleanExtra("Login_ok", false);

		// 在此进行进行处理
		if (is_download) {
			// Toast.makeText(MainActivity.this, "download", Toast.LENGTH_SHORT)
			// .show();
			// downloadUserImage();
		}

	}

	private void GetUrlFromServer() {
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
					final URL url = new URL(Myapp.getnewpage_url());
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
					if (up_or_down == 0) // 向下刷新
					{
						enter_article = article_id.get(article_id.size() - 1);
						Log.d("article", "enter_article is " + enter_article);
						jsonParam.put("articleId", enter_article);
					} else // 向上刷新
					{
						jsonParam.put("articleId", "");
					}

					String temp = jsonParam.toString();
					testout.write(temp.getBytes());
					testout.flush();
					testout.close();
					Log.d("Newpage", "" + urlConnection.getResponseCode());

					int HttpResult = urlConnection.getResponseCode();
					if (HttpResult == HttpURLConnection.HTTP_OK) {
						in = urlConnection.getInputStream();
						bytes = new byte[1024];
						buffer = new StringBuffer();
						Log.d("Newpage",
								"response Length:"
										+ urlConnection.getContentLength());
						while ((i = in.read(bytes)) != -1) {
							buffer.append(new String(bytes, 0, i, "UTF-8"));
						}

						String response2 = buffer.toString();
						Log.d("Newpage", "response:" + response2);
						res = new JSONArray(response2);

						if (up_or_down == 0) // 向下刷新
						{

							Message message = new Message();
							message.what = 4;
							message.arg1 = article_id.size();

							for (i = 0; i < res.length(); i++) {
								JSONObject jsonObject = res.getJSONObject(i);
								String articleid = jsonObject
										.getString("articleId");
								Log.d("Newpage", "articleid is " + articleid);
								article_id.add(articleid);
							}

							article_handler.sendMessage(message);
						} else // 向上刷新
						{
							// 清空列表
							article_id.clear();

							// 加10篇进去
							for (i = 0; i < res.length(); i++) {
								JSONObject jsonObject = res.getJSONObject(i);
								String articleid = jsonObject
										.getString("articleId");
								Log.d("Newpage", "articleid is " + articleid);
								article_id.add(articleid);
							}

							Message message = new Message();
							message.what = 5;
							article_handler.sendMessage(message);
						}

					} else {
						Log.d("Newpage",
								"not ok: " + urlConnection.getResponseMessage());
						Message message = new Message();
						message.what = 6;
						article_handler.sendMessage(message);

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

	/**
	 * 加载Topic对象。此方法会在LruCache中检查所有屏幕中可见的floor的Topic对象，
	 * 如果发现任何一个floor的Topic对象不在缓存中，就会开启异步线程去下载topic。
	 */
	public void loadTopic(String articleId) {
		try {
			Topic topic = Myapp.getTopicFromMemoryCache(articleId);

			if (topic == null) {
				TopicWorkerTask task = new TopicWorkerTask();
				taskCollection.add(task);
				task.execute(articleId);
				Log.d("article adapter", "task added.");
			} else {
				map.put(articleId, topic);
				try {
					listItems.set(article_id.indexOf(articleId), topic);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				adapter.notifyDataSetChanged();
				Log.d("article adapter", "read from cache.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.d("article adapter", "load topic error.");
		}

	}

	class TopicWorkerTask extends AsyncTask<String, Void, Topic> {

		/**
		 * article的URL地址
		 */
		private String url, articleId;

		@Override
		protected Topic doInBackground(String... params) {
			articleId = params[0];
			url = Myapp.getarticle_url() + articleId;
			Log.d("article adapter", "url in task is " + url);
			FileDescriptor fileDescriptor = null;
			FileInputStream fileInputStream = null;
			Snapshot snapShot = null;
			try {
				// 生成图片URL对应的key
				final String key = Myapp.hashKeyForDisk(url);
				Log.d("article adapter", "hash key in task is " + key);

				File filetemp = new File(mDiskLruCache.getDirectory(), key
						+ ".0");// 注意后缀名
				int flag = 0;// 0不下载，1下载
				long expired_time;
				int compared_time = Integer.MAX_VALUE;

				if (Myapp.isNetConnected()) {
					if (Myapp.isMobileConnected()) {
						compared_time = CONFIG_CACHE_MOBILE_TIMEOUT;
						Log.d("article adapter", "mobile network");
					}
					if (Myapp.isWifiConnected()) {
						compared_time = CONFIG_CACHE_WIFI_TIMEOUT;
						Log.d("article adapter", "wifi network");
					}

				} else {
					Log.d("article adapter", "no network");

				}

				if (filetemp.lastModified() != 0) {
					expired_time = System.currentTimeMillis()
							- filetemp.lastModified();
					Log.d("article adapter", "expired time is: " + expired_time);
					if (expired_time > compared_time) {
						flag = 1;
					}
				}

				if (flag == 1) {
					mDiskLruCache.remove(key);
				}

				// 查找key对应的缓存
				snapShot = mDiskLruCache.get(key);
				if (snapShot == null) {
					Log.d("article adapter", "snapshot is null");
					// 如果没有找到对应的缓存，则准备从网络上请求数据，并写入缓存
					DiskLruCache.Editor editor = mDiskLruCache.edit(key);
					if (editor != null) {
						OutputStream outputStream = editor.newOutputStream(0);
						Log.d("article adapter", "editor added.");
						// Topic的串行化输出在download方法内实现
						if (downloadUrlToStream(url, outputStream)) {
							editor.commit();
						} else {
							editor.abort();
						}
					}
					// 缓存被写入后，再次查找key对应的缓存
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
					// 将Topic对象添加到内存缓存当中
					// addTopicToMemoryCache(params[0], topic);
					Myapp.addTopicToMemoryCache(url, topic);
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
			// TODO Auto-generated method stub
			super.onPostExecute(topic);
			map.put(articleId, topic);
			Log.d("adapter", "map put complete.");

			Message message = new Message();
			message.what = 4;
			message.obj = articleId;
			topic_handler.sendMessage(message);

			taskCollection.remove(this);

		}
	}

	private boolean downloadUrlToStream(String urlString,
			OutputStream outputStream) {
		HttpURLConnection urlConnection = null;
		InputStream in = null;
		// BufferedReader reader = null;
		ObjectOutputStream oos = null;

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

			// Get method
			urlConnection.setRequestMethod("GET");
			urlConnection.setConnectTimeout(8000);
			urlConnection.setReadTimeout(8000);

			Log.d("article adapter", "" + urlConnection.getResponseCode());

			int HttpResult = urlConnection.getResponseCode();
			if (HttpResult == HttpURLConnection.HTTP_OK) {
				in = urlConnection.getInputStream();
				Log.d("article adapter", "getResponseMessage is "
						+ urlConnection.getResponseMessage());

				// 下面对获取到的输入流进行读取,UTF-8解码
				bytes = new byte[1024];
				// char[] chars = new char[urlConnection.getContentLength()];
				buffer = new StringBuffer();

				while ((i = in.read(bytes)) != -1) {
					buffer.append(new String(bytes, 0, i, urlConnection
							.getContentEncoding() == null ? "utf-8"
							: urlConnection.getContentEncoding()));
				}

				Log.d("article adapter", "response encoding is "
						+ urlConnection.getContentEncoding());

				String response2 = new String(buffer.toString().getBytes(
						urlConnection.getContentEncoding() == null ? "utf-8"
								: urlConnection.getContentEncoding()), "utf-8");
				Log.d("article adapter", "response is " + response2);

				topic = Myapp.article_parseJSONWithJSONObject(response2);
				// Log.d("article adapter",
				// "choose:"+topic.getimageUrl()+","+(topic.getimageUrl().length()));
				if (TextUtils.isEmpty(topic.getimageUrl())) {
					Log.d("article adapter", "enter default image");
					topic.setBitmap(Myapp.getdefault_image());
				} else {
					Log.d("article adapter", "enter download image");
					bitmapbyte = downloadUrlToByte(topic.getimageUrl());
					topic.setBitmap(bitmapbyte);
				}

				oos = new ObjectOutputStream(outputStream);
				oos.writeObject(topic);
				oos.close();
				Log.d("article adapter", "oos closed.");
				return true;
			} else {
				Log.d("article adapter",
						"network not ok: " + urlConnection.getResponseMessage());

			}

		} catch (final IOException e) {
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
			Log.d("article adapter", "second urlconnection ok.");
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

	@SuppressLint("ValidFragment")
	public class MyJobDetailsAssertFragment extends Fragment {

		public MyJobDetailsAssertFragment() {

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View view = inflater.inflate(R.layout.my_jobs_details_assert, null);

			guide01 = (ImageView) view.findViewById(R.id.guide1);
			guide02 = (ImageView) view.findViewById(R.id.guide2);
			guide03 = (ImageView) view.findViewById(R.id.guide3);

			// LogUnit.Log(TAG,
			// "create assert "+rootView.getWidth()+"  "+rootView.getHeight());
			LayoutParams params = new LayoutParams(rootView.getWidth(),
					rootView.getHeight());
			if (showAssert) {
				view.setLayoutParams(params);
				view.setOnClickListener(new OnClickListener() {
					int i = 1;

					@Override
					public void onClick(View v) {
						switch (i) {
						case 1:
							i++;
							guide01.setVisibility(View.INVISIBLE);
							guide02.setVisibility(View.VISIBLE);
							guide03.setVisibility(View.INVISIBLE);
							break;

						case 2:
							i++;
							guide01.setVisibility(View.INVISIBLE);
							guide02.setVisibility(View.INVISIBLE);
							guide03.setVisibility(View.VISIBLE);
							break;

						default:
							hideTheAssert();
							break;
						}

					}
				});
				// view.setAlpha(60.0f);
				showAssert = true;
			} else {
				hideTheAssert();
			}
			return view;

		}

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (showAssert && keyCode == KeyEvent.KEYCODE_BACK) {
			hideTheAssert();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	private void hideTheAssert() {
		getSupportFragmentManager().beginTransaction()
				.remove(detailsAssertFragment).commit();

		showAssert = false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			openOptionsMenu();
			return true;
		}
		return super.onKeyDown(keyCode, event);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (resultCode) {
		case 1:
			// 刷新前10篇(向上刷新)
			up_or_down = 1;

			GetUrlFromServer();

			break;

		default:
			break;

		}
	}

}

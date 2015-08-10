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

	// private Boolean isDeletable = false; //item是否是可删除的，即是否有右上角的三角

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

		// 加载数据
		lv = (PullToRefreshListView) findViewById(R.id.lv_mylist);

		lv.setMode(Mode.PULL_FROM_END);

		// ILoadingLayout startLabels = lv.getLoadingLayoutProxy(true, false);
		// startLabels.setPullLabel("下拉刷新...");// 刚下拉时，显示的提示
		// startLabels.setRefreshingLabel("正在载入...");// 刷新时
		// startLabels.setReleaseLabel("放开刷新...");// 下来达到一定距离时，显示的提示

		ILoadingLayout endLabels = lv.getLoadingLayoutProxy(false, true);
		endLabels.setPullLabel("加载更多...");// 刚下拉时，显示的提示
		endLabels.setRefreshingLabel("正在载入...");// 刷新时
		endLabels.setReleaseLabel("放开加载更多...");// 下来达到一定距离时，显示的提示

		final List<MyCollectionItem> listItems = new ArrayList<MyCollectionItem>();

			listItems.add(new MyCollectionItem(R.drawable.yangsheng, "yangsheng",
					"北X大外传", -1, "八月之末，正午时分。曹操一个人拎着箱子站在北X大校门口，叹了口气：“呵呵，最终还是来这了……”北X大是曹操最想避开的地方，爷爷曹腾是北X大副校长，在家里和学校都习惯了说一不二，于是曹操就被塞进了北X大通信专业。曹操的老爸曹嵩本姓夏侯，没脾气没本事就是长得挺帅，做了曹家的上门女婿，在家的地位可想而知，因此尽管曹操不想呆在曹腾老爷子眼皮底下，但最终还是听从了老爷子的安排。也罢，毕竟曹仁、曹洪、夏侯惇、夏侯渊几个从小玩儿到大的兄弟也都在北X大，读就读吧。",
					"校园   恶搞   90后", "15-07-26 19:38:27", "已有26人续墨", "0"));
			listItems.add(new MyCollectionItem(R.drawable.zh2, "zh2",
					"幽灵", -1, "在我四岁那年，父亲送了我一台Xbox。你们了解的，如果我没记错的话那是2001年的款式，一个黑色硬梆梆的盒子。我和父亲一起玩了很多游戏，非常开心，直到两年后，我的父亲去世了。 之后的十年时光里，我再也没有碰过这台游戏机。 然而当我再度启动它时，我发现了一些事情...... 我和父亲曾经一起玩过一款赛车游戏叫《越野挑战赛》，在当时，这真的是款很好玩的游戏。 就在我重新启动这款游戏时，我发现了一个真正的幽灵！",
					"治愈", "15-07-26 18:25:11", "已有1人续墨", "0"));
			listItems.add(new MyCollectionItem(R.drawable.zhenzi, "zhenzi",
					"黑羊", -1, "从前有个国家，里面人人是贼。 \n一到傍晚，他们手持万能钥匙和遮光灯笼出门，走到邻居家里行窃。破晓时分，他们提着偷来的东西回到家里，总能发现自己家也失窃了。 \n他们就这样幸福地居住在一起。没有不幸的人，因为每个人都从别人那里偷东西，别人又再从别人那里偷，依次下去，直到最后一个人去第一个窃贼家行窃。该国贸易也就不可避免地是买方和卖方的双向欺骗。政府是个向臣民行窃的犯罪机构，而臣民也仅对欺骗政府感兴趣。所以日子倒也平稳，没有富人和穷人。 \n　 有一天－－到底是怎么回事没人知道－－总之是有个诚实人到了该地定居。到晚上，他没有携袋提灯地出门，却呆在家里抽烟读小说。\n　 贼来了，见灯亮着，就没进去。",
					"无", "15-07-26 18:19:42", "已有5人续墨", "0"));
			listItems.add(new MyCollectionItem(R.drawable.zh, "zh",
					"红角羚羊", -1, "那头红角羚羊是在种群的跋涉途中出生的，异于同类的红角很抢眼，如出肉见血的弯刀。妈妈首先舔抚的便是他的红角。\n片刻之间，他便在妈妈的周围奔跑欢跳了，异常灵动健旺的雄性精灵。紧接着，他便与数百万头同类一起奔涌向前，如铺天盖地的大潮。\n那时候，他觉得自己的种群威风无比，羚角之前所向无敌，铁蹄之下神灵俯首。",
					"随笔", "15-07-26 18:13:33", "已有0人续墨", "0"));
			listItems.add(new MyCollectionItem(R.drawable.zh20, "zh20",
					"一个悲伤的故事", -1, "从 前有个年轻人捡到只大田螺带回家。次日出门，他自言自语说好想吃蛋炒饭，晚上回来桌子上居然有盘蛋炒饭；第二天他自言自语，说好想吃青椒肉丝，晚上回来桌 上有了青椒肉丝；第三天他自言自语，说好想吃辣炒田螺，晚上回来，桌子上有了盘辣炒大田螺；第四天他说想吃粉蒸肉，晚上回来，什么都没有……",
					"爱情   悬疑   文艺", "15-07-26 19:38:27", "已有0人续墨", "0"));
			

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
						// listItem.put("title", "这个年龄该有的智慧，不该有的智慧");
						//
						// listItem.put("labeltext", "xxx");
						//
						// listItem.put("context", "下拉刷新成功啦。。。。。。。");
						// listItem.put("time", "05-12 10:14");
						//
						// listItem.put("xumonum", "已有xx人续墨");
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
										"这个年龄该有的智慧，不该有的智慧", 0,
										"上拉加载成功啦。。。。。。。", "xxx", "05-12 10:14",
										"已有xx人续墨", "xx"));
							}
						} else {
							for (int i = 0; i < 10; i++) {

								listItems.add(new MyCollectionItem(
										R.drawable.icon_tx, "new",
										"这个年龄该有的智慧，不该有的智慧", -1,
										"上拉加载成功啦。。。。。。。", "xxx", "05-12 10:14",
										"已有xx人续墨", "xx"));
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
					// 所有的加灰三角
					int itemsLength = listItems.size();
					for (int i = 0; i < itemsLength; i++) {
						MyCollectionItem tempItem = listItems.get(i);
						tempItem.setCollectionCheckStatus(0);
					}
					adapter.notifyDataSetChanged();

				} else {

					// 向服务器传删除了哪些项

					// 在本页面去掉选中删除的那些项（蓝三角）
					int itemsLength = listItems.size();
					for (int i = 0; i < itemsLength; i++) {
						MyCollectionItem tempItem = listItems.get(i);
						if (tempItem.getCollectionCheckStatus() == 1) {
							listItems.remove(i);
							i--;
							itemsLength--;
						}
					}
					// 去掉剩余项的灰三角
					itemsLength = listItems.size();
					for (int i = 0; i < itemsLength; i++) {
						MyCollectionItem tempItem = listItems.get(i);
						tempItem.setCollectionCheckStatus(-1);
					}
					adapter.notifyDataSetChanged();

					Toast.makeText(MycollectionActivity.this, "删除成功",
							Toast.LENGTH_SHORT).show();

				}
			}
		});

		// item加载点击事件
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (edit.isChecked()) {
					// 删除：蓝三角和灰三角切换
					MyCollectionItem tempItem = listItems.get(position - 1);
					if (tempItem.getCollectionCheckStatus() == 0) {
						tempItem.setCollectionCheckStatus(1);
					} else {
						tempItem.setCollectionCheckStatus(0);
					}
					adapter.notifyDataSetChanged();
				} else {
					// 查看：进入具体文章
					Intent intent = new Intent(MycollectionActivity.this,
							ContentActivity.class);
					startActivity(intent);
				}

			}
		});
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

}

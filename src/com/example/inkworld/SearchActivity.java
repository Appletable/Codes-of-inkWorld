package com.example.inkworld;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends Activity {

	private View fenge1, fenge2, fenge3, fenge4;

	private TextView searchTagText, searchArticleText;
	private LinearLayout ll_byTag, ll_byTitle;
	private String keyword;

	private Boolean flag_byTag = false, flag_byTitle = false;

	private TextView waitingView;
	private TextView infoByTag, infoByTitle;
	private TextView infoByTagNoResult, infoByTitleNoResult;
	private ListView byTagListView, byTitleListView;

	private ArrayList<String> byTag_articleId = new ArrayList<String>();
	private ArrayList<String> byTag_title = new ArrayList<String>();

	private ArrayList<String> byTitle_articleId = new ArrayList<String>();
	private ArrayList<String> byTitle_title = new ArrayList<String>();

	private ArrayAdapter<String> adapter1;
	private ArrayAdapter<String> adapter2;

	private ImageView back;

	private ImageButton clear;

	private EditText searchArea;

	private MyApplication myapp;

	private WindowManager mWindowManager;

	private View nightView = null;

	private Handler byTagHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.obj == "byTag exist") {

				ll_byTag.setVisibility(View.VISIBLE);
				byTagListView.setVisibility(View.VISIBLE);
				waitingView.setVisibility(View.GONE);

				infoByTag.setText("标签中包含“" + keyword + "”");
				infoByTitle.setText("标题中包含“" + keyword + "”");

				String[] stockArr1 = new String[byTag_articleId.size()];
				stockArr1 = byTag_articleId.toArray(stockArr1);

				String[] stockArr2 = new String[byTag_title.size()];
				stockArr2 = byTag_title.toArray(stockArr2);

				adapter1 = new ArrayAdapter<String>(SearchActivity.this,
						R.layout.search_item, stockArr2);

				byTagListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(SearchActivity.this,
								ContentActivity.class);
						Log.d("search", "byTag_url" + byTag_articleId);
						
						intent.putExtra("articleId", byTag_articleId.get((int) arg3));
						
						String article_title = ((TextView)arg1).getText().toString();
						String article_tag = searchArea.getText().toString();
						
						intent.putExtra("title", article_title);
						intent.putExtra("tag", article_tag);
						
						startActivity(intent);
					}
				});

				byTagListView.setAdapter(adapter1);

			} else if (msg.obj == "byTag not exist") {
				ll_byTag.setVisibility(View.GONE);
				byTagListView.setVisibility(View.GONE);
				// listofresult.setVisibility(View.GONE);
				waitingView.setVisibility(View.GONE);

			} else {

			}
		};
	};

	private Handler byTitleHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.obj == "byTitle exist") {
				ll_byTitle.setVisibility(View.VISIBLE);
				byTitleListView.setVisibility(View.VISIBLE);
				waitingView.setVisibility(View.GONE);

				infoByTag.setText("标签中包含“" + keyword + "”");

				infoByTitle.setText("标题中包含“" + keyword + "”");

				String[] stockArr1 = new String[byTitle_articleId.size()];
				stockArr1 = byTitle_articleId.toArray(stockArr1);

				String[] stockArr2 = new String[byTitle_title.size()];
				stockArr2 = byTitle_title.toArray(stockArr2);

				adapter2 = new ArrayAdapter<String>(SearchActivity.this,
						R.layout.search_item, stockArr2);

				byTitleListView
						.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(SearchActivity.this,
										ContentActivity.class);
								Log.d("search", "byTitle_url" + byTitle_articleId);
								intent.putExtra("articleId",
										byTitle_articleId.get((int) arg3));
								startActivity(intent);
							}
						});

				byTitleListView.setAdapter(adapter2);
			} else if (msg.obj == "byTitle not exist") {
				ll_byTitle.setVisibility(View.GONE);
				byTitleListView.setVisibility(View.GONE);
				// listofresult.setVisibility(View.GONE);
				waitingView.setVisibility(View.GONE);
			} else {

			}
		};
	};

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.obj == "both not exist") {
				ll_byTag.setVisibility(View.GONE);
				byTagListView.setVisibility(View.GONE);
				infoByTagNoResult.setVisibility(View.GONE);
				ll_byTitle.setVisibility(View.GONE);
				byTitleListView.setVisibility(View.GONE);
				infoByTitleNoResult.setVisibility(View.GONE);

				fenge1.setVisibility(View.GONE);
				fenge2.setVisibility(View.GONE);
				fenge3.setVisibility(View.GONE);
				fenge4.setVisibility(View.GONE);

				byTag_title.clear();
				byTag_articleId.clear();
				byTitle_title.clear();
				byTitle_articleId.clear();

				waitingView.setVisibility(View.VISIBLE);
				waitingView.setText("无搜索结果");
			} else if (msg.obj == "only byTag exist") {
				ll_byTag.setVisibility(View.VISIBLE);
				byTagListView.setVisibility(View.VISIBLE);
				infoByTagNoResult.setVisibility(View.GONE);
				ll_byTitle.setVisibility(View.VISIBLE);
				byTitleListView.setVisibility(View.GONE);
				infoByTitleNoResult.setVisibility(View.VISIBLE);

				fenge1.setVisibility(View.VISIBLE);
				fenge2.setVisibility(View.GONE);
				fenge3.setVisibility(View.VISIBLE);
				fenge4.setVisibility(View.VISIBLE);

			} else if (msg.obj == "only byTitle exist") {
				ll_byTag.setVisibility(View.VISIBLE);
				byTagListView.setVisibility(View.GONE);
				infoByTagNoResult.setVisibility(View.VISIBLE);
				ll_byTitle.setVisibility(View.VISIBLE);
				byTitleListView.setVisibility(View.VISIBLE);
				infoByTitleNoResult.setVisibility(View.GONE);

				fenge1.setVisibility(View.VISIBLE);
				fenge2.setVisibility(View.VISIBLE);
				fenge3.setVisibility(View.VISIBLE);
				fenge4.setVisibility(View.GONE);

			} else if (msg.obj == "both exist") {
				ll_byTag.setVisibility(View.VISIBLE);
				byTagListView.setVisibility(View.VISIBLE);
				infoByTagNoResult.setVisibility(View.GONE);
				infoByTitleNoResult.setVisibility(View.GONE);
				ll_byTitle.setVisibility(View.VISIBLE);
				byTitleListView.setVisibility(View.VISIBLE);
				infoByTitleNoResult.setVisibility(View.GONE);

				fenge1.setVisibility(View.VISIBLE);
				fenge2.setVisibility(View.VISIBLE);
				fenge3.setVisibility(View.VISIBLE);
				fenge4.setVisibility(View.VISIBLE);

			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_search);

		back = (ImageView) this.findViewById(R.id.back);
		searchArea = (EditText) this.findViewById(R.id.searchArea);
		clear = (ImageButton) this.findViewById(R.id.clear);

		fenge1 = this.findViewById(R.id.fenge1);
		fenge2 = this.findViewById(R.id.fenge2);
		fenge3 = this.findViewById(R.id.fenge3);
		fenge4 = this.findViewById(R.id.fenge4);

		ll_byTag = (LinearLayout) this.findViewById(R.id.ll_byTag);
		ll_byTitle = (LinearLayout) this.findViewById(R.id.ll_byTitle);
		searchArticleText = (TextView) this
				.findViewById(R.id.search_article_text);
		searchTagText = (TextView) this.findViewById(R.id.search_tag_text);
		waitingView = (TextView) this.findViewById(R.id.waiting);

		infoByTag = (TextView) this.findViewById(R.id.info_byTag);
		infoByTitle = (TextView) this.findViewById(R.id.info_byTitle);
		infoByTagNoResult = (TextView) this
				.findViewById(R.id.info_byTag_noresult);
		infoByTitleNoResult = (TextView) this
				.findViewById(R.id.info_byTitle_noresult);

		byTagListView = (ListView) this.findViewById(R.id.lv_byTag);
		byTitleListView = (ListView) this.findViewById(R.id.lv_byTitle);

		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(SearchActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				TimerTask task = new TimerTask() {

					public void run() {

						// execute the task
						finish();
					}

				};

				Timer timer = new Timer();

				timer.schedule(task, 100);
			}
		});

		searchArea.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				if (searchArea.getText().toString().equals("")) {
					clear.setVisibility(View.GONE);
					waitingView.setVisibility(View.GONE);
				} else {
					clear.setVisibility(View.VISIBLE);
					waitingView.setText("客官不要急，正在搜索中...");
					waitingView.setVisibility(View.VISIBLE);
				}

				keyword = searchArea.getText().toString().trim();

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				search();
			}
		});

		// searchInTag.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// Log.d("Search", "Tag");
		// }
		// });
		// searchInArticle.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View arg0) {
		// // TODO Auto-generated method stub
		// Log.d("Search", "Article");
		// }
		// });

		clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				searchArea.setText("");
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

	private void search() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				String search_url = myapp.getsearch_url();

				HttpURLConnection urlConnection = null;
				InputStream in = null;

				OutputStreamWriter out = null;
				DataOutputStream testout = null;

				StringBuffer buffer = null;
				int i = 0;
				byte[] bytes;
				JSONObject res;
				try {

					final URL url = new URL(search_url);

					urlConnection = (HttpURLConnection) url.openConnection();
					urlConnection.setDoOutput(true);
					urlConnection.setRequestMethod("POST");
					urlConnection.setUseCaches(false);
					urlConnection.setConnectTimeout(10000);
					urlConnection.setReadTimeout(10000);
					urlConnection.setRequestProperty("Content-Type",
							"application/json");

					urlConnection.connect();
					Log.d("Search", "Search url: " + search_url);

					// Create JSONObject here
					JSONObject jsonParam = new JSONObject();
					jsonParam.put("keyword", keyword);
					jsonParam.put("pageNum", "0");
					String temp = jsonParam.toString();

					out = new OutputStreamWriter(urlConnection
							.getOutputStream());
					// out.write(jsonParam.toString());
					// out.write( URLEncoder.encode(temp,"UTF-8"));
					out.write(temp);

					out.close();

					Log.d("search", "keyword=" + keyword);

					Log.d("Search",
							"Search code: " + urlConnection.getResponseCode());

					int HttpResult = urlConnection.getResponseCode();
					if (HttpResult == HttpURLConnection.HTTP_OK) {
						in = urlConnection.getInputStream();
						bytes = new byte[1024];
						buffer = new StringBuffer();
						while ((i = in.read(bytes)) != -1) {
							buffer.append(new String(bytes, 0, i, "UTF-8"));
						}

						String response2 = buffer.toString();
						Log.d("Search", "response:" + response2);

						res = new JSONObject(response2);

						// Log.d("search", "byTag: " + res.getString("byTag"));
						// Log.d("search", "byTitle: " +
						// res.getString("byTitle"));

						String byTagString = res.getString("byTag");
						JSONArray byTagArray = new JSONArray(byTagString);

						String byTitleString = res.getString("byTitle");
						JSONArray byTitleArray = new JSONArray(byTitleString);

						if (byTagArray.length() != 0 && !keyword.equals("")) {
							flag_byTag = false;
							Message message = new Message();
							message.obj = "byTag exist";
							byTagHandler.sendMessage(message);
							for (i = 0; i < byTagArray.length(); i++) {
								JSONObject jsonObject = byTagArray
										.getJSONObject(i);
								String articleIdi = jsonObject.getString("articleId");
								String titlei = jsonObject.getString("title");
								Log.d("Search", "byTag articleId is " + articleIdi);
								Log.d("Search", "byTag title is " + titlei);
								byTag_title.add(titlei);
								byTag_articleId.add(articleIdi);
							}
						} else {
							Log.d("search", "byTag is empty");
							flag_byTag = true;
							Message message = new Message();
							message.obj = "byTag not exist";
							byTagHandler.sendMessage(message);
						}

						if (byTitleArray.length() != 0 && !keyword.equals("")) {
							flag_byTitle = false;
							Message message = new Message();
							message.obj = "byTitle exist";
							byTitleHandler.sendMessage(message);
							for (i = 0; i < byTitleArray.length(); i++) {
								JSONObject jsonObject = byTitleArray
										.getJSONObject(i);
								String urli = jsonObject.getString("url");
								String titlei = jsonObject.getString("title");
								Log.d("Search", "byTitle url is " + urli);
								Log.d("Search", "byTitle title is " + titlei);
								byTitle_articleId.add(urli);

								byTitle_title.add(titlei);
							}
						} else {
							Log.d("search", "byTitle is empty");
							flag_byTitle = true;
							Message message = new Message();
							message.obj = "byTitle not exist";
							byTitleHandler.sendMessage(message);

						}

						if (flag_byTag && flag_byTitle) {
							Message message = new Message();
							message.obj = "both not exist";
							handler.sendMessage(message);
						} else if (flag_byTag && !flag_byTitle) {

							Message message = new Message();
							message.obj = "only byTitle exist";
							handler.sendMessage(message);
						} else if (!flag_byTag && flag_byTitle) {
							Message message = new Message();
							message.obj = "only byTag exist";
							handler.sendMessage(message);
						} else {
							Message message = new Message();
							message.obj = "both exist";
							handler.sendMessage(message);
						}

					} else {
						Log.d("Search",
								"Search not ok: "
										+ urlConnection.getResponseMessage());
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

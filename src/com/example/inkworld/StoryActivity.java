package com.example.inkworld;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

public class StoryActivity extends Activity {

	private MyApplication Myapp;

	private WindowManager mWindowManager;

	private View nightView = null;

	private LinearLayout ll_cancel, publish;

	private TextView restinfo, tag_selected;
	final int MAX_LENGTH = 250;
	int Rest_Length = MAX_LENGTH;
	private EditText titleArea,contextArea;
	private LinearLayout addTag;
	private PopupWindow tagSelector;
	private CheckBox checkBox[];
	private Boolean checknum[];
	private int count = 0;
	private String tagString = "";
	
	private String username = "";
	private String usercode = "";
	
	private String aid,pid;
	
	private SharedPreferences pref;
	
	private Button bnCancel, bnSubmit;
	final Handler handler = new Handler();
	final Runnable runnable = new Runnable() {
		public void run() {

			tagString = "";

			for (int i = 0; i < 12; i++) {
				if (checknum[i]) {
					tagString += (checkBox[i].getText() + "   ");
				}
			}

			tag_selected = (TextView) findViewById(R.id.tag_selected);
			tag_selected.setTextColor(0xff12d8e6);
			tag_selected.setText(tagString);
		};
	};
	
	
	Handler article_handler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == 1)						//发布新文章成功（标题）
			{
				post_json_continue();				//发布新文章的内容
			}
			else if(msg.what == 2)					//发布新文章内容成功
			{
													//返回页面，关闭输入法，防止半屏bug
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(StoryActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
				
				Toast.makeText(StoryActivity.this, "发布成功！",Toast.LENGTH_SHORT).show();
				
				TimerTask task = new TimerTask() {

					public void run() {

						// execute the task
						
						setResult(1);
						
						finish();
					}

				};

				Timer timer = new Timer();
		
				timer.schedule(task, 100);
			}
			else if(msg.what == 3)					//发布新文章失败（标题）
			{
				Toast.makeText(StoryActivity.this, "发布失败！",Toast.LENGTH_SHORT).show();
			}
		};
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_story);

		// 12个标签的选择情况
		checknum = new Boolean[] { false, false, false, false, false, false,
				false, false, false, false, false, false };
		checkBox = new CheckBox[12];

		// 标题栏功能
		ll_cancel = (LinearLayout) this.findViewById(R.id.ll_cancel);
		publish = (LinearLayout) this.findViewById(R.id.publish);
		ll_cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(StoryActivity.this
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
		publish.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				post_json_newarticle();
				
			}
		});

		// 标题与正文
		titleArea = (EditText) this.findViewById(R.id.titleArea);
		contextArea = (EditText) this.findViewById(R.id.contextArea);
		restinfo = (TextView) this.findViewById(R.id.restinfo);
		contextArea.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

				if (Rest_Length > 0 || Rest_Length == 0) {
					restinfo.setTextColor(0xffbbbaba);
					restinfo.setTextSize(16);
					Rest_Length = MAX_LENGTH - contextArea.getText().length();
				}

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

				restinfo.setText("还能输入" + Rest_Length + "个字");
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

				restinfo.setText("还能输入" + Rest_Length + "个字");
				if (Rest_Length < 0 || Rest_Length == 0) {
					restinfo.setTextColor(0xffff0000);
					restinfo.setTextSize(18);
					Rest_Length = MAX_LENGTH - contextArea.getText().length();
				}
			}
		});

		// 添加标签
		addTag = (LinearLayout) this.findViewById(R.id.addTag);
		tag_selected = (TextView) this.findViewById(R.id.tag_selected);

		addTag.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showPopupWindow(arg0);

			}
		});

	}
	
	
	private void post_json_newarticle() {
		new Thread(new Runnable() {
			@Override
			public void run() {
		
		StringBuilder sb = new StringBuilder();  
		String serverurl = Myapp.getnewarticle_url();  
		
		pref = getSharedPreferences("username_password", MODE_PRIVATE);

		username = pref.getString("username", "");
		usercode = Myapp.getvalidation_code();
		
		HttpURLConnection urlConnection=null;  
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
		    urlConnection.setRequestProperty("Content-Type","application/json");   
		   // urlConnection.setRequestProperty("Host", "android.schoolportal.gr");
		    urlConnection.connect();  

		    //Create JSONObject here
		    JSONObject jsonParam = new JSONObject();
		    jsonParam.put("uid", username);
		    String titleText = titleArea.getText().toString();
		    jsonParam.put("title", titleText);
		    jsonParam.put("type", "0");
		    jsonParam.put("tag", tagString.isEmpty()?"无":tagString);
		    jsonParam.put("code", usercode);
		    jsonParam.put("page", "0");
		    
		    
		    
		    String temp = jsonParam.toString();
		    
		    out = new OutputStreamWriter(urlConnection.getOutputStream());
		    //out.write(jsonParam.toString());
		   // out.write( URLEncoder.encode(temp,"UTF-8"));
		    out.write(temp);
		    Log.d("ServerCom","temp: "+ temp);
		    out.close();  
		    
		    Log.d("ServerCom",""+urlConnection.getResponseCode());
		    

		    int HttpResult =urlConnection.getResponseCode();  
		    if(HttpResult ==HttpURLConnection.HTTP_OK){  
		        br = new BufferedReader(new InputStreamReader(  
		            urlConnection.getInputStream(),"utf-8"));  
		        String line = null;  
		        while ((line = br.readLine()) != null) {  
		            //sb.append(line + "\n"); 
		        	sb.append(line); 
		        	
		        }  
		        br.close();  
		        
		        JSONObject jsonObject = new JSONObject(sb.toString());
				
				aid = jsonObject.getString("articleId");
		        

		        //System.out.println(""+sb.toString());  
		        Log.d("ServerCom","from server: "+ sb.toString());
		        
		        
		        Message message = new Message();
				message.what = 1;
				article_handler.sendMessage(message);
		        

		    }else{  
		        //System.out.println(urlConnection.getResponseMessage());  
		    	
		    	Message message = new Message();
				message.what = 3;
				article_handler.sendMessage(message);
				
		    	Log.d("ServerCom","not ok: "+ urlConnection.getResponseMessage());
		    }  
		    
		    
		    
		} catch (MalformedURLException e) {  
		     e.printStackTrace();  
		} catch (IOException e) {  
		    e.printStackTrace();  
		} catch (JSONException e) {
		    e.printStackTrace();
		}finally{  
		    if(urlConnection!=null)  {
		    urlConnection.disconnect();
		    }
		    try{
			    if (out != null) {
					out.close();
				}
			    if (br != null) {
					br.close();
				}
		    }catch (final IOException e) {
				e.printStackTrace();
			}
		}  
		
		
			}
		}).start();
		
	}
	
	
	
	private void post_json_continue() {
		new Thread(new Runnable() {
			@Override
			public void run() {
		
				
		pref = getSharedPreferences("username_password", MODE_PRIVATE);
	
		username = pref.getString("username", "");
		usercode = Myapp.getvalidation_code();		
		
		StringBuilder sb = new StringBuilder();  
		String serverurl = Myapp.getnewparagraph_url();  

		HttpURLConnection urlConnection=null;  
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
		    urlConnection.setRequestProperty("Content-Type","application/json");   
		   // urlConnection.setRequestProperty("Host", "android.schoolportal.gr");
		    urlConnection.connect();  

		    //Create JSONObject here
		    JSONObject jsonParam = new JSONObject();
		    jsonParam.put("articleId", aid);
		    jsonParam.put("uid", username);
		    jsonParam.put("type", "0");
		    jsonParam.put("code", usercode);
		    String contextText = contextArea.getText().toString();
		    jsonParam.put("content", contextText);
		    String temp = jsonParam.toString();
		    
		    out = new OutputStreamWriter(urlConnection.getOutputStream());
		    //out.write(jsonParam.toString());
		   // out.write( URLEncoder.encode(temp,"UTF-8"));
		    out.write(temp);
		    Log.d("ServerCom","temp: "+ temp);
		    out.close();  
		    
		    Log.d("ServerCom",""+urlConnection.getResponseCode());
		    

		    int HttpResult =urlConnection.getResponseCode();  
		    if(HttpResult ==HttpURLConnection.HTTP_OK){  
		        br = new BufferedReader(new InputStreamReader(  
		            urlConnection.getInputStream(),"utf-8"));  
		        String line = null;  
		        while ((line = br.readLine()) != null) {  
		            sb.append(line + "\n");  
		        }  
		        br.close();  

		        JSONObject jsonObject = new JSONObject(sb.toString());
				
				String pid = jsonObject.getString("pid");
		        
		        //System.out.println(""+sb.toString());  
		        Log.d("ServerCom","from server: "+ sb.toString());
		        
		        Message message = new Message();
				message.what = 2;
				article_handler.sendMessage(message);
		        
		        

		    }else{  
		        //System.out.println(urlConnection.getResponseMessage());  
		    	Log.d("ServerCom","not ok: "+ urlConnection.getResponseMessage());
		    }  
		    
		    
		    
		} catch (MalformedURLException e) {  
		     e.printStackTrace();  
		} catch (IOException e) {  
		    e.printStackTrace();  
		} catch (JSONException e) {
		    e.printStackTrace();
		}finally{  
		    if(urlConnection!=null)  {
		    urlConnection.disconnect();
		    }
		    try{
			    if (out != null) {
					out.close();
				}
			    if (br != null) {
					br.close();
				}
		    }catch (final IOException e) {
				e.printStackTrace();
			}
		}  
		
		
			}
		}).start();
		
		
		
	}
	
	

	@SuppressWarnings("deprecation")
	private void showPopupWindow(View view) {// 传递的为父布局

		final Boolean checknum_temp[];

		checknum_temp = new Boolean[12];
		for (int i = 0; i < 12; i++) {
			checknum_temp[i] = checknum[i];

		}

		View tagView = getLayoutInflater().inflate(R.layout.layout_tag, null,
				false);
		tagSelector = new PopupWindow(tagView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub

				ColorDrawable cd = new ColorDrawable(0x000000);
				tagSelector.setBackgroundDrawable(cd);
				// 产生背景变暗效果
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 0.7f;
				getWindow().setAttributes(lp);
			}
		}, 300);

		tagSelector.setOutsideTouchable(true);
		tagSelector.setBackgroundDrawable(new BitmapDrawable());
		tagSelector.setFocusable(true);

		tagSelector.showAtLocation((View) view.getParent(), Gravity.CENTER
				| Gravity.CENTER_HORIZONTAL, 0, 0);

		tagSelector.update();
		tagSelector.setOnDismissListener(new OnDismissListener() {

			// 在dismiss中恢复透明度
			public void onDismiss() {

				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);

				new Thread() {
					public void run() {
						handler.post(runnable);
					};
				}.start();

				// 加标签

			}
		});
		checkBox[0] = (CheckBox) tagView.findViewById(R.id.checkBox1);
		checkBox[1] = (CheckBox) tagView.findViewById(R.id.checkBox2);
		checkBox[2] = (CheckBox) tagView.findViewById(R.id.checkBox3);
		checkBox[3] = (CheckBox) tagView.findViewById(R.id.checkBox4);
		checkBox[4] = (CheckBox) tagView.findViewById(R.id.checkBox5);
		checkBox[5] = (CheckBox) tagView.findViewById(R.id.checkBox6);
		checkBox[6] = (CheckBox) tagView.findViewById(R.id.checkBox7);
		checkBox[7] = (CheckBox) tagView.findViewById(R.id.checkBox8);
		checkBox[8] = (CheckBox) tagView.findViewById(R.id.checkBox9);
		checkBox[9] = (CheckBox) tagView.findViewById(R.id.checkBox10);
		checkBox[10] = (CheckBox) tagView.findViewById(R.id.checkBox11);
		checkBox[11] = (CheckBox) tagView.findViewById(R.id.checkBox12);

		for (int i = 0; i < 12; i++) {
			if (checknum_temp[i]) {
				checkBox[i].setTextColor(0xff12d8e6);
				checkBox[i].setChecked(true);
			}
		}

		checkBox[0].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (checkBox[0].isChecked()) {
					if (count < 3) {
						checkBox[0].setTextColor(0xff12d8e6);
						checknum_temp[0] = true;
						count++;
					} else {
						Toast.makeText(StoryActivity.this, "最多只能选择三个标签",
								Toast.LENGTH_SHORT).show();
						checkBox[0].setChecked(false);
					}

				} else {
					checkBox[0].setTextColor(0xff000000);
					checknum_temp[0] = false;
					count--;
				}
			}
		});

		checkBox[1].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (checkBox[1].isChecked()) {
					if (count < 3) {
						checkBox[1].setTextColor(0xff12d8e6);
						checknum_temp[1] = true;
						count++;
					} else {
						Toast.makeText(StoryActivity.this, "最多只能选择三个标签",
								Toast.LENGTH_SHORT).show();
						checkBox[1].setChecked(false);
					}

				} else {
					checkBox[1].setTextColor(0xff000000);
					checknum_temp[1] = false;
					count--;
				}
			}
		});

		checkBox[2].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (checkBox[2].isChecked()) {
					if (count < 3) {
						checkBox[2].setTextColor(0xff12d8e6);
						checknum_temp[2] = true;
						count++;
					} else {
						Toast.makeText(StoryActivity.this, "最多只能选择三个标签",
								Toast.LENGTH_SHORT).show();
						checkBox[2].setChecked(false);
					}

				} else {
					checkBox[2].setTextColor(0xff000000);
					checknum_temp[2] = false;
					count--;
				}
			}
		});

		checkBox[3].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (checkBox[3].isChecked()) {
					if (count < 3) {
						checkBox[3].setTextColor(0xff12d8e6);
						checknum_temp[3] = true;
						count++;
					} else {
						Toast.makeText(StoryActivity.this, "最多只能选择三个标签",
								Toast.LENGTH_SHORT).show();
						checkBox[3].setChecked(false);
					}

				} else {
					checkBox[3].setTextColor(0xff000000);
					checknum_temp[3] = false;
					count--;
				}
			}
		});

		checkBox[4].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (checkBox[4].isChecked()) {
					if (count < 3) {
						checkBox[4].setTextColor(0xff12d8e6);
						checknum_temp[4] = true;
						count++;
					} else {
						Toast.makeText(StoryActivity.this, "最多只能选择三个标签",
								Toast.LENGTH_SHORT).show();
						checkBox[4].setChecked(false);
					}

				} else {
					checkBox[4].setTextColor(0xff000000);
					checknum_temp[4] = false;
					count--;
				}
			}
		});

		checkBox[5].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (checkBox[5].isChecked()) {
					if (count < 3) {
						checkBox[5].setTextColor(0xff12d8e6);
						checknum_temp[5] = true;
						count++;
					} else {
						Toast.makeText(StoryActivity.this, "最多只能选择三个标签",
								Toast.LENGTH_SHORT).show();
						checkBox[5].setChecked(false);
					}

				} else {
					checkBox[5].setTextColor(0xff000000);
					checknum_temp[5] = false;
					count--;
				}
			}
		});

		checkBox[6].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (checkBox[6].isChecked()) {
					if (count < 3) {
						checkBox[6].setTextColor(0xff12d8e6);
						checknum_temp[6] = true;
						count++;
					} else {
						Toast.makeText(StoryActivity.this, "最多只能选择三个标签",
								Toast.LENGTH_SHORT).show();
						checkBox[6].setChecked(false);
					}

				} else {
					checkBox[6].setTextColor(0xff000000);
					checknum_temp[6] = false;
					count--;
				}
			}
		});

		checkBox[7].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (checkBox[7].isChecked()) {
					if (count < 3) {
						checkBox[7].setTextColor(0xff12d8e6);
						checknum_temp[7] = true;
						count++;
					} else {
						Toast.makeText(StoryActivity.this, "最多只能选择三个标签",
								Toast.LENGTH_SHORT).show();
						checkBox[7].setChecked(false);
					}

				} else {
					checkBox[7].setTextColor(0xff000000);
					checknum_temp[7] = false;
					count--;
				}
			}
		});

		checkBox[8].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (checkBox[8].isChecked()) {
					if (count < 3) {
						checkBox[8].setTextColor(0xff12d8e6);
						checknum_temp[8] = true;
						count++;
					} else {
						Toast.makeText(StoryActivity.this, "最多只能选择三个标签",
								Toast.LENGTH_SHORT).show();
						checkBox[8].setChecked(false);
					}

				} else {
					checkBox[8].setTextColor(0xff000000);
					checknum_temp[8] = false;
					count--;
				}
			}
		});

		checkBox[9].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (checkBox[9].isChecked()) {
					if (count < 3) {
						checkBox[9].setTextColor(0xff12d8e6);
						checknum_temp[9] = true;
						count++;
					} else {
						Toast.makeText(StoryActivity.this, "最多只能选择三个标签",
								Toast.LENGTH_SHORT).show();
						checkBox[9].setChecked(false);
					}

				} else {
					checkBox[9].setTextColor(0xff000000);
					checknum_temp[9] = false;
					count--;
				}
			}
		});

		checkBox[10].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (checkBox[10].isChecked()) {
					if (count < 3) {
						checkBox[10].setTextColor(0xff12d8e6);
						checknum_temp[10] = true;
						count++;
					} else {
						Toast.makeText(StoryActivity.this, "最多只能选择三个标签",
								Toast.LENGTH_SHORT).show();
						checkBox[10].setChecked(false);
					}

				} else {
					checkBox[10].setTextColor(0xff000000);
					checknum_temp[10] = false;
					count--;
				}
			}
		});

		checkBox[11].setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				// TODO Auto-generated method stub
				if (checkBox[11].isChecked()) {
					if (count < 3) {
						checkBox[11].setTextColor(0xff12d8e6);
						checknum_temp[11] = true;
						count++;
					} else {
						Toast.makeText(StoryActivity.this, "最多只能选择三个标签",
								Toast.LENGTH_SHORT).show();
						checkBox[11].setChecked(false);
					}

				} else {
					checkBox[11].setTextColor(0xff000000);
					checknum_temp[11] = false;
					count--;
				}
			}
		});

		bnCancel = (Button) tagView.findViewById(R.id.cancel);

		bnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				count = 0;
				for (int i = 0; i < 12; i++) {

					if (checknum[i]) {
						count++;
					}

				}
				tagSelector.dismiss();
			}
		});

		bnSubmit = (Button) tagView.findViewById(R.id.submit);

		bnSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < 12; i++) {
					checknum[i] = checknum_temp[i];
				}

				tagSelector.dismiss();
			}
		});

		// 返回键退出
		tagView.setFocusableInTouchMode(true);
		tagView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					tagSelector.dismiss();
					tagSelector = null;
					return true;
				}

				return false;
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

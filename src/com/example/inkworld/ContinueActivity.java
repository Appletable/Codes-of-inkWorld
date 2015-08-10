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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ContinueActivity extends Activity {

	private TextView cancel, publish;

	private TextView restinfo;
	final int MAX_LENGTH = 250;
	int Rest_Length = MAX_LENGTH;
	private EditText contextArea;

	private MyApplication Myapp;
	
	private String pid;
	
	
	private String username = "";
	private String usercode = "";
	private String articleId = "";
	private SharedPreferences pref;

	private WindowManager mWindowManager;

	private View nightView = null;

	
	Handler continue_handler = new Handler() {
		public void handleMessage(Message msg) {
			if(msg.what == 1)						//续写成功
			{
		        Toast.makeText(ContinueActivity.this, "续写成功！", Toast.LENGTH_SHORT).show();
		        
		        setResult(1);
		        
				finish();
				
			}
			else if(msg.what == 2)					//续写失败
			{
		    	Toast.makeText(ContinueActivity.this, "续写失败！", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_continue);
		
		Intent intent = getIntent();
		articleId = intent.getStringExtra("articleId");
		Log.d("continue", articleId);
		

		

		// 标题栏功能
		cancel = (TextView) this.findViewById(R.id.cancel);
		publish = (TextView) this.findViewById(R.id.publish);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(ContinueActivity.this
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
				
				post_json_continue();
				

				
			}
		});

		// 标题与正文
		// titleArea = (EditText) this.findViewById(R.id.titleArea);
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
		    jsonParam.put("articleId", articleId);
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
				
				pid = jsonObject.getString("pid");
		        
		        //System.out.println(""+sb.toString());  
		        Log.d("ServerCom","from server: "+ sb.toString());
		        
		        Message message = new Message();
				message.what = 1;
				continue_handler.sendMessage(message);

		        
		        

		    }else{  
		        //System.out.println(urlConnection.getResponseMessage());  
		    	Log.d("ServerCom","not ok: "+ urlConnection.getResponseMessage());
		    	
		    	Message message = new Message();
				message.what = 2;
				continue_handler.sendMessage(message);

		    }  
		    
		    
		    
		} catch (Exception e) {
			
			Toast.makeText(ContinueActivity.this, "续写失败！", Toast.LENGTH_SHORT).show();
			
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

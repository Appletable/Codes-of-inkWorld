package com.example.inkworld;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class LoginActivity extends Activity {

	private ImageView qqImageView, sinaImageView;
	
	private String qqId,sinaId;

	private LinearLayout cancel;
	private TextView register;
	private TextView forget_password;
	private Button bt_login;

	private String nickname = null;

	private String phonenumber = null;
	private String country = null;

	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private EditText username;
	private EditText password;

	private MyApplication myapp;

	private ImageView userimage;
	private TextView login_state;

	private WindowManager mWindowManager;

	private View nightView = null;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String res = (String) msg.obj;

			if (res.equals("login ok")) {
				editor = pref.edit();
				editor.putString("username", username.getText().toString());
				editor.putString("password", password.getText().toString());
				editor.commit();

				myapp.setUsernameString(pref.getString("username", ""));
				Log.d("logintest", "uid2" + myapp.getUsernameString());
				myapp.setFlag_login(myapp.LOGIN);

				downloadUserImage();

			} else if (res.equals("login wrong")) {
				Toast.makeText(LoginActivity.this, "用户名或密码有误，请重新登录",
						Toast.LENGTH_SHORT).show();

			} else if (res.equals("network wrong")) {
				Toast.makeText(LoginActivity.this, "网络连接出错，请再次尝试或检查网络设置",
						Toast.LENGTH_SHORT).show();

			} else {

			}

			if (res.equals("0")) {
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);

				intent.putExtra("country", country);
				intent.putExtra("phone", phonenumber);

				startActivity(intent);

			} else if (res.equals("1")) {
				Toast.makeText(LoginActivity.this, "该手机号已被注册，请您更换号码重试",
						Toast.LENGTH_SHORT).show();

			} else if (res.equals("validation network wrong")) {
				Toast.makeText(LoginActivity.this, "网络连接出错，请再次尝试或检查网络设置",
						Toast.LENGTH_SHORT).show();

			} else {

			}

			if (res.equals("refresh image")) {
				byte[] data = myapp.getuserImage();
				if (data != null) {
					Log.d("LoginTest", "enter 1");
					Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0,
							data.length);

					userimage.setImageBitmap(bitmap);
					userimage.setScaleType(ScaleType.FIT_CENTER);
					
					myapp.setFlag_notx(false);
				} else {
					Log.d("LoginTest", "enter 2");
					userimage.setImageResource(R.drawable.icon_tx);
					myapp.setFlag_notx(true);
				}

			} else if (res.equals("no_tx")) {
				userimage.setImageResource(R.drawable.icon_tx);
				myapp.setFlag_notx(true);
			}

			Intent intent = new Intent(LoginActivity.this, MainActivity.class);

			intent.putExtra("Login_ok", true);
			startActivity(intent);

		}
	};

	private Handler handler1 = new Handler() {
		public void handleMessage(Message msg) {
			String res = (String) msg.obj;

			if (res.substring(1, 2).equals("0")) {
				// 未注册
				Intent intent = new Intent(LoginActivity.this,
						RegisterActivity.class);
				if (res.equals("10")) {
					// QQ用户
					intent.putExtra("nickname", nickname);
					intent.putExtra("qqId", qqId);
					intent.putExtra("QQ", true);
				} else if (res.equals("20")) {
					// 微博用户
					intent.putExtra("nickname", nickname);
					intent.putExtra("sinaId", sinaId);
					intent.putExtra("Sina", true);
				}
				startActivity(intent);
				finish();
			} else if (res.substring(1, 2).equals("1")) {
				// 已注册
				if (res.equals("11")) {
					// QQ用户

				} else if (res.equals("21")) {
					// 微博用户

				}
			}

		};
	};

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		cancel = (LinearLayout) this.findViewById(R.id.cancel);
		register = (TextView) this.findViewById(R.id.textView2);
		bt_login = (Button) this.findViewById(R.id.bt_login);

		qqImageView = (ImageView) this.findViewById(R.id.QQ_login);
		sinaImageView = (ImageView) this.findViewById(R.id.sina_login);

		userimage = (ImageView) this.findViewById(R.id.imageView2);
		login_state = (TextView) this.findViewById(R.id.textView1);
		forget_password = (TextView) this.findViewById(R.id.textView3);

		myapp = (MyApplication) getApplication();
		//
		if (myapp.getFlag_login()) {
			Toast.makeText(LoginActivity.this, "当前已有用户登录", 1).show();
			forget_password.setVisibility(View.GONE);
		}

		// 注册反馈信息
		Intent intent = getIntent();
		if (intent.getBooleanExtra("Register_ok", false)) {
			Toast.makeText(LoginActivity.this, "新用户注册成功，请登录",
					Toast.LENGTH_SHORT).show();
			intent.putExtra("Register_ok", false);

		}
		// 忘记密码反馈
		if (intent.getIntExtra("Forget_and_change_password_ok", 0) == 1) {
			Toast.makeText(LoginActivity.this, "修改密码成功，请登录", Toast.LENGTH_SHORT)
					.show();
			intent.putExtra("Forget_and_change_password_ok", 0);

		} else if (intent.getIntExtra("Forget_and_change_password_ok", 0) == 2) {
			Toast.makeText(LoginActivity.this, "修改密码失败，请登录后重试",
					Toast.LENGTH_SHORT).show();
			intent.putExtra("Forget_and_change_password_ok", 0);

		}

		if (intent.getIntExtra("Forgetpassword_login", 0) == 1) {
			/*
			 * String temp1 = intent.getStringExtra("username_f"); String temp2
			 * = intent.getStringExtra("password_f"); username.setText(temp1);
			 * password.setText(temp2);
			 */
			Toast.makeText(LoginActivity.this, "用户名和密码已获得，请点击登录",
					Toast.LENGTH_SHORT).show();

			intent.putExtra("Forgetpassword_login", 0);

		} else if (intent.getIntExtra("Forgetpassword_login", 0) == 2) {
			Toast.makeText(LoginActivity.this, "网络连接出错，请检查后重试",
					Toast.LENGTH_SHORT).show();
			intent.putExtra("Forgetpassword_login", 0);

		}

		pref = getSharedPreferences("username_password", MODE_PRIVATE);
		username = (EditText) this.findViewById(R.id.editText1);
		password = (EditText) this.findViewById(R.id.editText2);

		if (!myapp.getFlag_login()) {
			String username2 = pref.getString("username", "");
			String password2 = pref.getString("password", "");

			username.setText(username2);
			password.setText(password2);
			userimage.setImageResource(R.drawable.icon_tx);
			login_state.setText("未登录");
		} else {
			if (myapp.getFlag_notx()) {
				userimage.setImageResource(R.drawable.icon_tx);
			} else {
				if (fileIsExists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(getBaseContext()
							.getFilesDir() + "/Profile.png");
					userimage.setImageBitmap(bitmap);
					userimage.setScaleType(ScaleType.FIT_CENTER);
				} else {
					userimage.setImageResource(R.drawable.icon_tx);
				}
			}

			login_state.setText(pref.getString("username", ""));
			myapp.setUsernameString(pref.getString("username", ""));
			Log.d("logintest", "uid1" + myapp.getUsernameString());
			username.setText("");
			password.setText("");

		}

		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(LoginActivity.this
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

		SMSSDK.initSDK(this, "70f041b7f957", "f0956daa45b1fb3a7d06538133a9b358");

		register.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 打开注册页面
				RegisterPage registerPage = new RegisterPage();
				EventHandler eh = new EventHandler() {
					@Override
					public void afterEvent(int event, int result, Object data) {
						// 解析注册结果
						if (result == SMSSDK.RESULT_COMPLETE) {
							@SuppressWarnings("unchecked")
							HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
							country = (String) phoneMap.get("country");
							phonenumber = (String) phoneMap.get("phone");

							// 提交用户信息
							// registerUser(country, phone);
							PhonenumberVaildation();

						}
					}
				};
				SMSSDK.registerEventHandler(eh);
				registerPage.setRegisterCallback(eh);

				registerPage.show(getBaseContext());
				// unregist是必要的，防止内存泄露
				SMSSDK.unregisterEventHandler(eh);

			}
		});

		forget_password.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 打开注册页面
				RegisterPage registerPage = new RegisterPage();
				EventHandler eh = new EventHandler() {
					@Override
					public void afterEvent(int event, int result, Object data) {
						// 解析注册结果
						if (result == SMSSDK.RESULT_COMPLETE) {
							@SuppressWarnings("unchecked")
							HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
							String ph = (String) phoneMap.get("phone");

							Intent intent = new Intent(LoginActivity.this,
									ForgetPassword.class);

							intent.putExtra("phone", ph);
							intent.putExtra("currentUsername",
									pref.getString("username", ""));
							intent.putExtra("currentPasswword",
									pref.getString("password", ""));
							startActivity(intent);

						}
					}
				};
				SMSSDK.registerEventHandler(eh);
				registerPage.setRegisterCallback(eh);

				registerPage.show(getBaseContext());
				// unregist是必要的，防止内存泄露
				SMSSDK.unregisterEventHandler(eh);

			}
		});

		bt_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(username.getText().toString())
						&& !TextUtils.isEmpty(password.getText().toString())) {
					login();
				} else {
					Toast.makeText(LoginActivity.this, "用户名或密码为空，请填写",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		// 其他方式登录
		qqImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (myapp.isNetConnected()) {
					Platform qqlog = ShareSDK.getPlatform(LoginActivity.this,
							QQ.NAME);
					qqlog.SSOSetting(true);
					qqlog.setPlatformActionListener(new PlatformActionListener() {
						public void onError(Platform arg0, int arg1,
								Throwable arg2) {

							Log.i("Login", "Error: " + arg2.toString());
						}

						public void onComplete(Platform platform, int action,
								HashMap<String, Object> res) {
							Log.i("Logintest", "OnComplete! " + res);
							// arg0.removeAccount();
							Bundle data = new Bundle();// ----map
							PlatformDb platDB = platform.getDb();// 获取数平台数据DB
							// 通过DB获取各种数据
							qqId = platDB.getUserId();
							nickname = platDB.getUserName();
							Log.i("Logintest", "OnComplete! " + qqId);
							int type = 1;

							// 上传数据
							qqorSinaLogin(qqId, type);

						}

						public void onCancel(Platform arg0, int arg1) {
						}
					});
					qqlog.showUser(null);
				}

			}
		});
		sinaImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (myapp.isNetConnected()) {
					Platform weibo = ShareSDK.getPlatform(LoginActivity.this,
							SinaWeibo.NAME);
					weibo.SSOSetting(true);
					weibo.setPlatformActionListener(new PlatformActionListener() {

						public void onError(Platform arg0, int arg1,
								Throwable arg2) {
						}

						public void onComplete(Platform platform, int action,
								HashMap<String, Object> res) {

							PlatformDb platDB = platform.getDb();
							sinaId = platDB.getUserId();
							int type = 2;

							// 上传数据
							qqorSinaLogin(sinaId, type);

						}

						public void onCancel(Platform arg0, int arg1) {
						}
					});

					weibo.showUser(null);
				}

			}
		});

	}

	private void qqorSinaLogin(final String id, final int type) {

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
					final URL url = new URL(myapp.getcheckPhoneNumber_url());
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
					jsonParam.put("phoneNumber", id);
					String temp = jsonParam.toString();
					testout.write(temp.getBytes());
					testout.flush();
					testout.close();
					Log.d("LoginTest", "Id " + urlConnection.getResponseCode());

					int HttpResult = urlConnection.getResponseCode();
					if (HttpResult == HttpURLConnection.HTTP_OK) {
						in = urlConnection.getInputStream();
						bytes = new byte[1024];
						buffer = new StringBuffer();
						while ((i = in.read(bytes)) != -1) {
							buffer.append(new String(bytes, 0, i, "UTF-8"));
						}

						String response2 = buffer.toString();
						Log.d("LoginTest", "response:" + response2);

						response2 = response2.trim();

						Message message = new Message();
						message.obj = type + response2;
						handler1.sendMessage(message);

					} else {
						Log.d("LoginTest",
								"qqId not ok: "
										+ urlConnection.getResponseMessage());
						Message message = new Message();
						message.obj = "UserId not ok";
						handler1.sendMessage(message);
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

	private void login() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				StringBuilder sb = new StringBuilder();
				String serverurl = myapp.getlogin_url();

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
					jsonParam.put("uid", username.getText().toString());
					jsonParam.put("password", password.getText().toString());
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

						// Log.d("LoginActivity", "result"+result);

						if (!TextUtils.isEmpty(result) && (!result.equals("4"))) {
							myapp.setvalidation_code(result);
							myapp.setFlag_login(myapp.LOGIN);

							Message message = new Message();
							message.obj = "login ok";
							handler.sendMessage(message);
							Log.d("LoginTest", "login ok: " + result);

						} else {
							Message message = new Message();
							message.obj = "login wrong";
							handler.sendMessage(message);
							Log.d("LoginTest", "login wrong" + result);

						}

						Log.d("LoginTest", "from server: " + sb.toString());

					} else {
						Message message = new Message();
						message.obj = "network wrong";
						handler.sendMessage(message);
						Log.d("LoginTest",
								"login not ok: "
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

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = getIntent();
		if (intent.getBooleanExtra("Register_ok", false)) {
			Toast.makeText(LoginActivity.this, "新用户注册成功，请登录",
					Toast.LENGTH_SHORT).show();
			intent.putExtra("Register_ok", false);

		}

		if (!myapp.getFlag_login()) {
			String username2 = pref.getString("username", "");
			String password2 = pref.getString("password", "");
			username.setText(username2);
			password.setText(password2);
			userimage.setImageResource(R.drawable.icon_tx);
			login_state.setText("未登录");
		} else {
			if (myapp.getFlag_notx()) {
				userimage.setImageResource(R.drawable.icon_tx);
			} else {
				if (fileIsExists()) {
					Bitmap bitmap = BitmapFactory.decodeFile(getBaseContext()
							.getFilesDir() + "/Profile.png");
					userimage.setImageBitmap(bitmap);
					userimage.setScaleType(ScaleType.FIT_CENTER);
				} else {
					userimage.setImageResource(R.drawable.icon_tx);
				}
			}

			login_state.setText(pref.getString("username", ""));
			username.setText("");
			password.setText("");
		}

		if (myapp.isMobileConnected()) {
			Toast.makeText(LoginActivity.this, "当前是3G网络", Toast.LENGTH_SHORT)
					.show();
		} else if (myapp.isWifiConnected()) {
			Toast.makeText(LoginActivity.this, "当前是Wifi网络", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(LoginActivity.this, "当前无网络连接", Toast.LENGTH_SHORT)
					.show();
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
				String test_url = myapp.getdownload_userImage_url()
						+ username.getText().toString();
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
						handler.sendMessage(msg);
					}

					if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
						in = urlConnection.getInputStream();
						outStream = new ByteArrayOutputStream();
						buffer = new byte[1024];
						int len = 0;
						while ((len = in.read(buffer)) != -1) {
							outStream.write(buffer, 0, len);
						}
						myapp.setuserImage(outStream.toByteArray());
						saveFile(Bytes2Bimap(outStream.toByteArray()));

						Log.d("LoginTest", "download userimage ok"
								+ outStream.toByteArray().toString());

						Message message = new Message();
						message.obj = "refresh image";
						handler.sendMessage(message);
						Log.d("LoginTest", "refresh image");

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

	public Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	private void PhonenumberVaildation() {
		// TODO Auto-generated method stub

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
					final URL url = new URL(myapp.getcheckPhoneNumber_url());
					urlConnection = (HttpURLConnection) url.openConnection();

					// Post method
					urlConnection.setDoOutput(true);
					urlConnection.setRequestMethod("POST");
					urlConnection.setUseCaches(false);
					urlConnection.setConnectTimeout(10000);
					urlConnection.setReadTimeout(10000);
					urlConnection.setRequestProperty("Content-Type",
							"application/json");

					testout = new DataOutputStream(
							urlConnection.getOutputStream());
					JSONObject jsonParam = new JSONObject();
					jsonParam.put("phoneNumber", phonenumber);
					String temp = jsonParam.toString();
					testout.write(temp.getBytes());
					testout.flush();
					testout.close();
					Log.d("LoginTest",
							"phonenumber " + urlConnection.getResponseCode());

					int HttpResult = urlConnection.getResponseCode();
					if (HttpResult == HttpURLConnection.HTTP_OK) {
						in = urlConnection.getInputStream();
						bytes = new byte[1024];
						buffer = new StringBuffer();
						while ((i = in.read(bytes)) != -1) {
							buffer.append(new String(bytes, 0, i, "UTF-8"));
						}

						String response2 = buffer.toString();
						Log.d("LoginTest", "response:" + response2);

						response2 = response2.trim();

						Message message = new Message();
						message.obj = response2;
						handler.sendMessage(message);

					} else {
						Log.d("LoginTest", "phonenumber not ok: "
								+ urlConnection.getResponseMessage());
						Message message = new Message();
						message.obj = "validation network wrong";
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

	// 夜间模式
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
}

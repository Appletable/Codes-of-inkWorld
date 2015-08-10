package com.example.inkworld;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends BaseActivity {
	private ImageView back;

	private String country, country2;
	private String phone, phone2;
	private TextView phonenumber;
	private TextView revalidation;
	private ImageView tick_cross, tick_cross2, tick_cross3;
	private EditText username;
	private EditText password, password2;
	private TextView warning, warning2;
	private String validation_url;
	private Button register;
	private int username_flag = 0;
	private int password_flag = 0;

	private MyApplication Myapp;

	private WindowManager mWindowManager;

	private View nightView = null;

	private SharedPreferences pref;
	private SharedPreferences.Editor editor;

	private String button_trigger = null;
	private int time_count = 3;// 2秒等待用户名验证结果的时间
	private Timer timer;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String res = (String) msg.obj;

			// 用户名合法性检测
			if (res.equals("0")) {
				tick_cross.setImageResource(R.drawable.tick);
				tick_cross.setVisibility(View.VISIBLE);
				warning.setVisibility(View.INVISIBLE);
				username_flag = 1;

			} else if (res.equals("1")) {
				tick_cross.setImageResource(R.drawable.cross);
				tick_cross.setVisibility(View.VISIBLE);
				warning.setText("该用户名已被注册，请您换一个");
				warning.setVisibility(View.VISIBLE);
				username_flag = 0;

			} else if (res.equals("2")) {
				tick_cross.setImageResource(R.drawable.cross);
				tick_cross.setVisibility(View.VISIBLE);
				warning.setText("用户名过长，请控制在30个字符内");
				warning.setVisibility(View.VISIBLE);
				username_flag = 0;

			} else if (res.equals("3")) {
				tick_cross.setImageResource(R.drawable.cross);
				tick_cross.setVisibility(View.VISIBLE);
				warning.setText("用户名不能为空");
				warning.setVisibility(View.VISIBLE);
				username_flag = 0;

			} else if (res.equals("validation network wrong")) {

				Toast.makeText(RegisterActivity.this, "网络连接出错，请再次尝试或检查网络设置",
						Toast.LENGTH_SHORT).show();

			} else {

			}

			// 注册结果检测
			if (res.equals("Register ok")) {
				login();

			} else if (res.equals("Register wrong")) {
				Toast.makeText(RegisterActivity.this, "网络连接出错，请再次尝试或检查网络设置",
						Toast.LENGTH_SHORT).show();

			} else {

			}

			// 登录结果检测
			if (res.equals("login ok")) {
				editor = pref.edit();
				editor.putString("username", username.getText().toString());
				editor.putString("password", password.getText().toString());
				editor.commit();

				Intent intent = new Intent(RegisterActivity.this,
						MainActivity.class);

				intent.putExtra("Register_ok", true);
				startActivity(intent);

			} else if (res.equals("login wrong")) {
				editor = pref.edit();
				editor.putString("username", username.getText().toString());
				editor.putString("password", password.getText().toString());
				editor.commit();

				Intent intent = new Intent(RegisterActivity.this,
						LoginActivity.class);

				intent.putExtra("Register_ok", true);
				startActivity(intent);

			} else if (res.equals("network wrong")) {
				Toast.makeText(RegisterActivity.this, "网络连接出错，请再次尝试或检查网络设置",
						Toast.LENGTH_SHORT).show();

			} else {

			}

			if (res.equals("phone0")) {
				/*
				 * Intent intent = new Intent (RegisterActivity.this,
				 * RegisterActivity.class);
				 * 
				 * intent.putExtra("country",country);
				 * intent.putExtra("phone",phone);
				 * 
				 * startActivity(intent);
				 */

				country = country2;
				phone = phone2;
				phonenumber.setText(phone);
				Toast.makeText(RegisterActivity.this, "注册手机号更换成功",
						Toast.LENGTH_SHORT).show();

			} else if (res.equals("phone1")) {
				Toast.makeText(RegisterActivity.this, "该手机号已被注册，请您更换号码重试",
						Toast.LENGTH_SHORT).show();

			} else if (res.equals("phone validation network wrong")) {
				Toast.makeText(RegisterActivity.this, "网络连接出错，请再次尝试或检查网络设置",
						Toast.LENGTH_SHORT).show();

			} else {

			}

			// 注册button的用户名有效性检测结果
			if (res.equals("UsernameVaildation2 ok")) {
				button_trigger = "ok";
				Log.d("RegisterActivity", "enter UsernameVaildation2 ok");

			} else if (res.equals("UsernameVaildation2 wrong")) {
				button_trigger = "wrong";

			} else if (res.equals("UsernameVaildation2 network wrong")) {
				Toast.makeText(RegisterActivity.this, "网络连接出错，请再次尝试或检查网络设置",
						Toast.LENGTH_SHORT).show();

			} else {

			}

		}
	};

	// TimerTask task = new TimerTask() {
	// @Override
	// public void run() {
	//
	// runOnUiThread(new Runnable() { // UI thread
	// @Override
	// public void run() {
	// Log.d("RegisterActivity", "enter timer: " + time_count);
	// time_count--;
	// if (time_count < 0) {
	// button_trigger = null;
	// time_count = 3;
	// Log.d("RegisterActivity", "timer quit ok: "
	// + time_count + "," + button_trigger);
	// Toast.makeText(RegisterActivity.this,
	// "无网络，请再次尝试或检查网络设置", Toast.LENGTH_SHORT).show();
	// timer.cancel();
	// }
	//
	// if (!TextUtils.isEmpty(button_trigger)) {
	// String pass = password.getText().toString();
	// String pass2 = password2.getText().toString();
	// if (button_trigger.equals("ok")
	// && (!TextUtils.isEmpty(pass))
	// && (pass.equals(pass2))) {
	// Log.d("RegisterActivity", "button trigger ok");
	// Log.d("RegisterActivity", "time count: "
	// + time_count);
	// // 调用注册接口
	// register();
	// button_trigger = null;
	// time_count = 3;
	// timer.cancel();
	//
	// } else if (button_trigger.equals("wrong")) {
	// Log.d("RegisterActivity",
	// "button trigger not ok or null: "
	// + button_trigger);
	// Log.d("RegisterActivity", "time count: "
	// + time_count);
	// Toast.makeText(RegisterActivity.this,
	// "用户名或密码不正确，请您修改", Toast.LENGTH_SHORT)
	// .show();
	// button_trigger = null;
	// time_count = 3;
	// timer.cancel();
	//
	// }
	//
	// }
	//
	// }
	// });
	// }
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register);

		Myapp = (MyApplication) getApplication();

		validation_url = Myapp.getcheckUserName_url();

		pref = getSharedPreferences("username_password", MODE_PRIVATE);

		back = (ImageView) this.findViewById(R.id.imageView2);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		Intent intent = getIntent();
		country = intent.getStringExtra("country");
		phone = intent.getStringExtra("phone");

		Toast.makeText(RegisterActivity.this, country + ":" + phone,
				Toast.LENGTH_SHORT).show();

		phonenumber = (TextView) this.findViewById(R.id.phonenumber);
		phonenumber.setText(phone);

		revalidation = (TextView) this.findViewById(R.id.revalidation);
		revalidation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 打开注册页面
				RegisterPage registerPage = new RegisterPage();
				EventHandler eh = new EventHandler() {
					@Override
					public void afterEvent(int event, int result, Object data) {
						// 解析注册结果
						if (result == SMSSDK.RESULT_COMPLETE) {
							@SuppressWarnings("unchecked")
							HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
							country2 = (String) phoneMap.get("country");
							phone2 = (String) phoneMap.get("phone");

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

		tick_cross = (ImageView) this.findViewById(R.id.tick_cross);
		warning = (TextView) this.findViewById(R.id.warning);

		username = (EditText) this.findViewById(R.id.editText1);
		username.setSelectAllOnFocus(true);
		username.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					tick_cross.setVisibility(View.INVISIBLE);
				} else {

					UsernameVaildation();
				}

			}

		});

		password = (EditText) this.findViewById(R.id.editText2);
		password2 = (EditText) this.findViewById(R.id.editText3);
		tick_cross2 = (ImageView) this.findViewById(R.id.tick_cross2);
		tick_cross3 = (ImageView) this.findViewById(R.id.tick_cross3);
		warning2 = (TextView) this.findViewById(R.id.warning2);

		password.setSelectAllOnFocus(true);

		password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					tick_cross2.setVisibility(View.INVISIBLE);
				} else {
					String pass = password.getText().toString();
					String pass2 = password2.getText().toString();
					if (!TextUtils.isEmpty(pass)) {
						tick_cross2.setImageResource(R.drawable.tick);
						tick_cross2.setVisibility(View.VISIBLE);
						if (!TextUtils.isEmpty(pass2)) {
							if (pass.equals(pass2)) {
								tick_cross3.setImageResource(R.drawable.tick);
								tick_cross3.setVisibility(View.VISIBLE);
								warning2.setVisibility(View.INVISIBLE);
								password_flag = 1;
							} else {
								tick_cross3.setImageResource(R.drawable.cross);
								tick_cross3.setVisibility(View.VISIBLE);
								warning2.setText("两次输入密码不一致，请您修改");
								warning2.setVisibility(View.VISIBLE);
								password_flag = 0;
							}
						}

					} else {
						tick_cross2.setVisibility(View.INVISIBLE);
						tick_cross3.setVisibility(View.INVISIBLE);
						warning2.setVisibility(View.INVISIBLE);
						password_flag = 0;
					}

				}

			}

		});

		password2.setSelectAllOnFocus(true);
		password2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					tick_cross3.setVisibility(View.INVISIBLE);
				} else {
					String pass = password.getText().toString();
					String pass2 = password2.getText().toString();

					if (!TextUtils.isEmpty(pass)) {
						if (pass2.equals(pass)) {
							tick_cross3.setImageResource(R.drawable.tick);
							tick_cross3.setVisibility(View.VISIBLE);
							warning2.setVisibility(View.INVISIBLE);
							password_flag = 1;
						} else {
							tick_cross3.setImageResource(R.drawable.cross);
							tick_cross3.setVisibility(View.VISIBLE);
							warning2.setText("两次输入密码不一致，请您修改");
							warning2.setVisibility(View.VISIBLE);
							password_flag = 0;
						}

					} else {
						tick_cross3.setVisibility(View.INVISIBLE);
						warning2.setVisibility(View.INVISIBLE);
						password_flag = 0;

					}

				}

			}

		});

		register = (Button) this.findViewById(R.id.button1);
		register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UsernameVaildation2();
				timer = new Timer();
				TimerTask task = new TimerTask() {
					@Override
					public void run() {

						runOnUiThread(new Runnable() { // UI thread
							@Override
							public void run() {
								Log.d("RegisterActivity", "enter timer: "
										+ time_count);
								time_count--;
								if (time_count < 0) {
									button_trigger = null;
									time_count = 3;
									Log.d("RegisterActivity", "timer quit ok: "
											+ time_count + "," + button_trigger);
									Toast.makeText(RegisterActivity.this,
											"无网络，请再次尝试或检查网络设置",
											Toast.LENGTH_SHORT).show();
									timer.cancel();
								}

								if (!TextUtils.isEmpty(button_trigger)) {
									String pass = password.getText().toString();
									String pass2 = password2.getText()
											.toString();
									if (button_trigger.equals("ok")) {
										if ((!TextUtils.isEmpty(pass))
												&& (pass.equals(pass2))) {
											Log.d("RegisterActivity",
													"button trigger ok");
											Log.d("RegisterActivity",
													"time count: " + time_count);
											// 调用注册接口
											register();
											button_trigger = null;
											time_count = 3;
											timer.cancel();
										} else {
											Log.d("RegisterActivity",
													"button trigger ok while password fail: "
															+ button_trigger);
											Log.d("RegisterActivity",
													"time count: " + time_count);
											Toast.makeText(
													RegisterActivity.this,
													"用户名或密码不正确，请您修改",
													Toast.LENGTH_SHORT).show();
											button_trigger = null;
											time_count = 3;
											timer.cancel();

										}

									} else if (button_trigger.equals("wrong")) {
										Log.d("RegisterActivity",
												"button trigger not ok or null: "
														+ button_trigger);
										Log.d("RegisterActivity",
												"time count: " + time_count);
										Toast.makeText(RegisterActivity.this,
												"用户名或密码不正确，请您修改",
												Toast.LENGTH_SHORT).show();
										button_trigger = null;
										time_count = 3;
										timer.cancel();

									}

								}

							}
						});
					}
				};

				Log.d("RegisterActivity", "new timer,task: " + task.toString());
				timer.schedule(task, 100, 500);
			}
		});

		register.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					UsernameVaildation2();
					timer = new Timer();
					TimerTask task = new TimerTask() {
						@Override
						public void run() {

							runOnUiThread(new Runnable() { // UI thread
								@Override
								public void run() {
									Log.d("RegisterActivity", "enter timer: "
											+ time_count);
									time_count--;
									if (time_count < 0) {
										button_trigger = null;
										time_count = 3;
										Log.d("RegisterActivity",
												"timer quit ok: " + time_count
														+ "," + button_trigger);
										Toast.makeText(RegisterActivity.this,
												"无网络，请再次尝试或检查网络设置",
												Toast.LENGTH_SHORT).show();
										timer.cancel();
									}

									if (!TextUtils.isEmpty(button_trigger)) {
										String pass = password.getText()
												.toString();
										String pass2 = password2.getText()
												.toString();
										if (button_trigger.equals("ok")) {
											if ((!TextUtils.isEmpty(pass))
													&& (pass.equals(pass2))) {
												Log.d("RegisterActivity",
														"button trigger ok");
												Log.d("RegisterActivity",
														"time count: "
																+ time_count);
												// 调用注册接口
												register();
												button_trigger = null;
												time_count = 3;
												timer.cancel();
											} else {
												Log.d("RegisterActivity",
														"button trigger ok while password fail: "
																+ button_trigger);
												Log.d("RegisterActivity",
														"time count: "
																+ time_count);
												Toast.makeText(
														RegisterActivity.this,
														"用户名或密码不正确，请您修改",
														Toast.LENGTH_SHORT)
														.show();
												button_trigger = null;
												time_count = 3;
												timer.cancel();

											}

										} else if (button_trigger
												.equals("wrong")) {
											Log.d("RegisterActivity",
													"button trigger not ok or null: "
															+ button_trigger);
											Log.d("RegisterActivity",
													"time count: " + time_count);
											Toast.makeText(
													RegisterActivity.this,
													"用户名或密码不正确，请您修改",
													Toast.LENGTH_SHORT).show();
											button_trigger = null;
											time_count = 3;
											timer.cancel();

										}

									}

								}
							});
						}
					};

					Log.d("RegisterActivity",
							"new timer,task: " + task.toString());
					timer.schedule(task, 100, 500);

				}

			}

		});

	}

	private void UsernameVaildation2() {
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
					final URL url = new URL(validation_url);
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
					String tempusername = username.getText().toString();
					jsonParam.put("uid", tempusername);
					String temp = jsonParam.toString();
					testout.write(temp.getBytes());
					testout.flush();
					testout.close();
					Log.d("RegisterActivity",
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
						Log.d("RegisterActivity", "response:" + response2);

						response2 = response2.trim();
						Message message = new Message();
						if (response2.equals("0")) {
							Log.d("RegisterActivity", "UsernameVaildation2 ok");
							message.obj = "UsernameVaildation2 ok";

						} else {
							Log.d("RegisterActivity",
									"UsernameVaildation2 not ok");
							message.obj = "UsernameVaildation2 wrong";
						}

						handler.sendMessage(message);

					} else {
						Log.d("RegisterActivity",
								"UsernameVaildation2 not ok: "
										+ urlConnection.getResponseMessage());
						Message message = new Message();
						message.obj = "UsernameVaildation2 network wrong";
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

	private void UsernameVaildation() {
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
					final URL url = new URL(validation_url);
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
					String tempusername = username.getText().toString();
					jsonParam.put("uid", tempusername);
					String temp = jsonParam.toString();
					testout.write(temp.getBytes());
					testout.flush();
					testout.close();
					Log.d("RegisterActivity",
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
						Log.d("RegisterActivity", "response:" + response2);

						response2 = response2.trim();

						Message message = new Message();
						message.obj = response2;
						handler.sendMessage(message);

					} else {
						Log.d("RegisterActivity",
								"not ok: " + urlConnection.getResponseMessage());
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

	private void register() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				StringBuilder sb = new StringBuilder();
				String serverurl = Myapp.getregister_url();

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
					jsonParam.put("phoneNumber", phonenumber.getText()
							.toString());
					String temp = jsonParam.toString();

					out = new OutputStreamWriter(urlConnection
							.getOutputStream());
					// out.write(jsonParam.toString());
					// out.write( URLEncoder.encode(temp,"UTF-8"));
					out.write(temp);
					Log.d("RegisterActivity", "temp: " + temp);
					out.close();

					Log.d("RegisterActivity",
							"" + urlConnection.getResponseCode());

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

						if (result.equals("0")) {
							Message message = new Message();
							message.obj = "Register ok";
							handler.sendMessage(message);
							Log.d("RegisterActivity", "Register ok");

						}

						Log.d("RegisterActivity",
								"from server: " + sb.toString());

					} else {
						Message message = new Message();
						message.obj = "Register wrong";
						handler.sendMessage(message);
						Log.d("RegisterActivity",
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
					jsonParam.put("uid", username.getText().toString());
					jsonParam.put("password", password.getText().toString());
					jsonParam.put("type", "0");
					String temp = jsonParam.toString();

					out = new OutputStreamWriter(urlConnection
							.getOutputStream());
					// out.write(jsonParam.toString());
					// out.write( URLEncoder.encode(temp,"UTF-8"));
					out.write(temp);
					Log.d("RegisterActivity", "temp: " + temp);
					out.close();

					Log.d("RegisterActivity",
							"" + urlConnection.getResponseCode());

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

						if (!TextUtils.isEmpty(result)) {
							Myapp.setvalidation_code(result);
							Myapp.setFlag_login(Myapp.LOGIN);

							Message message = new Message();
							message.obj = "login ok";
							handler.sendMessage(message);
							Log.d("RegisterActivity", "login ok");

						} else {
							Message message = new Message();
							message.obj = "login wrong";
							handler.sendMessage(message);
							Log.d("RegisterActivity", "login wrong");

						}

						Log.d("RegisterActivity",
								"from server: " + sb.toString());

					} else {
						Message message = new Message();
						message.obj = "network wrong";
						handler.sendMessage(message);
						Log.d("RegisterActivity",
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
					final URL url = new URL(Myapp.getcheckPhoneNumber_url());
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
					jsonParam.put("phoneNumber", phone2);
					String temp = jsonParam.toString();
					testout.write(temp.getBytes());
					testout.flush();
					testout.close();
					Log.d("RegisterActivity",
							"phone " + urlConnection.getResponseCode());

					int HttpResult = urlConnection.getResponseCode();
					if (HttpResult == HttpURLConnection.HTTP_OK) {
						in = urlConnection.getInputStream();
						bytes = new byte[1024];
						buffer = new StringBuffer();
						while ((i = in.read(bytes)) != -1) {
							buffer.append(new String(bytes, 0, i, "UTF-8"));
						}

						String response2 = buffer.toString();
						Log.d("RegisterActivity", "response:" + response2);

						response2 = response2.trim();

						Message message = new Message();
						message.obj = "phone" + response2;
						handler.sendMessage(message);

					} else {
						Log.d("RegisterActivity", "phone not ok: "
								+ urlConnection.getResponseMessage());
						Message message = new Message();
						message.obj = "phone validation network wrong";
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

	@Override
	protected void onResume() {
		super.onResume();
		username.setText("");
		password.setText("");
		tick_cross.setVisibility(View.INVISIBLE);
		warning.setVisibility(View.INVISIBLE);
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

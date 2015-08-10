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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ForgetPassword extends BaseActivity {
	private ImageView back;
	private ImageView tick_cross, tick_cross2;
	private TextView warning;
	private Button confirm;

	private MyApplication myapp;

	private SharedPreferences pref;
	private SharedPreferences.Editor editor;

	private String phone;
	private TextView username;
	private TextView password;
	private EditText new_password;
	private EditText new_password_re;
	private String username_s, password_s;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String res = (String) msg.obj;

			if (res.equals("ForgetPassword network wrong")) {
				Toast.makeText(ForgetPassword.this, "该手机号未被注册或网络连接出错，请重试",
						Toast.LENGTH_SHORT).show();

			} else {
				Log.d("ForgetPassword", "parse begin");
				parseJson(res);

			}

		}
	};

	private Handler handler2 = new Handler() {
		public void handleMessage(Message msg) {
			String res = (String) msg.obj;

			if (res.equals("login ok")) {

			} else if (res.equals("login wrong")) {
				Intent intent = new Intent(ForgetPassword.this,
						LoginActivity.class);

				intent.putExtra("Forgetpassword_login", 1);
				/*
				 * intent.putExtra("username_f", username_s);
				 * intent.putExtra("password_f", password_s);
				 */

				myapp.setFlag_login(myapp.LOGOFF);
				myapp.setvalidation_code(null);
				myapp.setuserImage(null);

				startActivity(intent);
			} else if (res.equals("network wrong")) {
				Intent intent = new Intent(ForgetPassword.this,
						LoginActivity.class);

				intent.putExtra("Forgetpassword_login", 2);
				startActivity(intent);

			} else {

			}

		}
	};

	private Handler handler3 = new Handler() {
		public void handleMessage(Message msg) {
			String res = (String) msg.obj;

			if (res.equals("0")) {
				Intent intent = new Intent(ForgetPassword.this,
						LoginActivity.class);

				intent.putExtra("Forget_and_change_password_ok", 1);
				startActivity(intent);
				finish();

			} else if (res.equals("1")) {
				Intent intent = new Intent(ForgetPassword.this,
						LoginActivity.class);

				intent.putExtra("Forget_and_change_password_ok", 2);
				startActivity(intent);
				finish();
			} else if (res.equals("change password network wrong")) {
				Toast.makeText(ForgetPassword.this, "网络连接出错，请检查后重试",
						Toast.LENGTH_SHORT).show();
				finish();
			} else {

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.forget_password);

		myapp = (MyApplication) getApplication();

		pref = getSharedPreferences("username_password", MODE_PRIVATE);
		Intent intent = getIntent();
		phone = intent.getStringExtra("phone");

		username = (TextView) this.findViewById(R.id.username);
		password = (TextView) this.findViewById(R.id.password);
		new_password = (EditText) this.findViewById(R.id.new_password);
		new_password_re = (EditText) this.findViewById(R.id.new_password_re);
		tick_cross = (ImageView) this.findViewById(R.id.tick_cross1);
		tick_cross2 = (ImageView) this.findViewById(R.id.tick_cross2);
		warning = (TextView) this.findViewById(R.id.warning);
		confirm = (Button) this.findViewById(R.id.confirm);

		ForgetPassword_init();

		back = (ImageView) this.findViewById(R.id.imageView0);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		new_password.setSelectAllOnFocus(true);
		new_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					tick_cross.setVisibility(View.INVISIBLE);
				} else {
					String pass = new_password.getText().toString();
					String pass2 = new_password_re.getText().toString();
					if (!TextUtils.isEmpty(pass)) {
						tick_cross.setImageResource(R.drawable.tick);
						tick_cross.setVisibility(View.VISIBLE);
						if (!TextUtils.isEmpty(pass2)) {
							if (pass.equals(pass2)) {
								tick_cross2.setImageResource(R.drawable.tick);
								tick_cross2.setVisibility(View.VISIBLE);
								warning.setVisibility(View.INVISIBLE);
							} else {
								tick_cross2.setImageResource(R.drawable.cross);
								tick_cross2.setVisibility(View.VISIBLE);
								warning.setText("两次输入密码不一致，请您修改");
								warning.setVisibility(View.VISIBLE);
							}
						}

					} else {
						tick_cross.setVisibility(View.INVISIBLE);
						tick_cross2.setVisibility(View.INVISIBLE);
						warning.setVisibility(View.INVISIBLE);

					}

				}

			}

		});

		new_password_re.setSelectAllOnFocus(true);
		new_password_re
				.setOnFocusChangeListener(new View.OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						if (hasFocus) {
							tick_cross2.setVisibility(View.INVISIBLE);
						} else {
							String pass = new_password.getText().toString();
							String pass2 = new_password_re.getText().toString();

							if (!TextUtils.isEmpty(pass)) {
								if (pass2.equals(pass)) {
									tick_cross2
											.setImageResource(R.drawable.tick);
									tick_cross2.setVisibility(View.VISIBLE);
									warning.setVisibility(View.INVISIBLE);

								} else {
									tick_cross2
											.setImageResource(R.drawable.cross);
									tick_cross2.setVisibility(View.VISIBLE);
									warning.setText("两次输入密码不一致，请您修改");
									warning.setVisibility(View.VISIBLE);

								}

							} else {
								tick_cross2.setVisibility(View.INVISIBLE);
								warning.setVisibility(View.INVISIBLE);

							}

						}

					}

				});

		confirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String pass = new_password.getText().toString();
				String pass2 = new_password_re.getText().toString();

				if ((!TextUtils.isEmpty(pass)) && (pass.equals(pass2))) {
					// 调用修改密码接口
					ChangePassword();
				} else {

					Toast.makeText(ForgetPassword.this, "新密码不正确，请您修改",
							Toast.LENGTH_SHORT).show();

				}

			}
		});

		confirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					String pass = new_password.getText().toString();
					String pass2 = new_password_re.getText().toString();

					if ((!TextUtils.isEmpty(pass)) && (pass.equals(pass2))) {
						// 调用修改密码接口
						ChangePassword();
					} else {

						Toast.makeText(ForgetPassword.this, "新密码不正确，请您修改",
								Toast.LENGTH_SHORT).show();

					}

				}

			}

		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		new_password.setText("");
		new_password_re.setText("");
		tick_cross.setVisibility(View.INVISIBLE);
		tick_cross2.setVisibility(View.INVISIBLE);
		warning.setVisibility(View.INVISIBLE);
	}

	private void ForgetPassword_init() {
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
					final URL url = new URL(myapp.getforget_password_url());
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
					jsonParam.put("phoneNumber", phone);
					String temp = jsonParam.toString();
					testout.write(temp.getBytes());
					testout.flush();
					testout.close();
					Log.d("ForgetPassword",
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
						Log.d("ForgetPassword", "response:" + response2);

						response2 = response2.trim();

						Message message = new Message();
						message.obj = response2;
						handler.sendMessage(message);

					} else {
						Log.d("ForgetPassword", "phone not ok: "
								+ urlConnection.getResponseMessage());
						Message message = new Message();
						message.obj = "ForgetPassword network wrong";
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

	private void parseJson(String res) {
		// TODO Auto-generated method stub

		try {
			JSONObject jsonObject = new JSONObject(res);
			username_s = jsonObject.getString("uid");
			password_s = jsonObject.getString("passWord");

			Log.d("ForgetPassword", "parse result:" + username_s + ","
					+ password_s);
			username.setText("您的用户名：" + username_s);
			password.setText("您的原密码：" + password_s);

			editor = pref.edit();
			editor.putString("username", username_s);
			editor.putString("password", password_s);
			editor.commit();
			login();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
					jsonParam.put("uid", username_s);
					jsonParam.put("password", password_s);
					jsonParam.put("type", "0");
					String temp = jsonParam.toString();

					out = new OutputStreamWriter(urlConnection
							.getOutputStream());
					// out.write(jsonParam.toString());
					// out.write( URLEncoder.encode(temp,"UTF-8"));
					out.write(temp);
					Log.d("ForgetPassword", "temp: " + temp);
					out.close();

					Log.d("ForgetPassword",
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
							myapp.setvalidation_code(result);
							myapp.setFlag_login(myapp.LOGIN);

							Message message = new Message();
							message.obj = "login ok";
							handler2.sendMessage(message);
							Log.d("ForgetPassword", "login ok: " + result);

						} else {
							Message message = new Message();
							message.obj = "login wrong";
							handler2.sendMessage(message);
							Log.d("ForgetPassword", "login wrong" + result);

						}

						Log.d("ForgetPassword", "from server: " + sb.toString());

					} else {
						Message message = new Message();
						message.obj = "network wrong";
						handler2.sendMessage(message);
						Log.d("ForgetPassword", "login not ok: "
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

	private void ChangePassword() {
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
					final URL url = new URL(myapp.getchange_password_url());
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
					jsonParam.put("uid", username_s);
					jsonParam.put("code", myapp.getvalidation_code());
					jsonParam.put("oldPasswd", password_s);
					jsonParam.put("newPasswd", new_password.getText()
							.toString());
					String temp = jsonParam.toString();
					testout.write(temp.getBytes());
					testout.flush();
					testout.close();
					Log.d("ForgetPassword",
							"change password: "
									+ urlConnection.getResponseCode());

					int HttpResult = urlConnection.getResponseCode();
					if (HttpResult == HttpURLConnection.HTTP_OK) {
						in = urlConnection.getInputStream();
						bytes = new byte[1024];
						buffer = new StringBuffer();
						while ((i = in.read(bytes)) != -1) {
							buffer.append(new String(bytes, 0, i, "UTF-8"));
						}

						String response2 = buffer.toString();
						Log.d("ForgetPassword", "response:" + response2);

						response2 = response2.trim();

						Message message = new Message();
						message.obj = response2;
						handler3.sendMessage(message);

					} else {
						Log.d("ForgetPassword", "change password not ok: "
								+ urlConnection.getResponseMessage());
						Message message = new Message();
						message.obj = "change password network wrong";
						handler3.sendMessage(message);
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

}
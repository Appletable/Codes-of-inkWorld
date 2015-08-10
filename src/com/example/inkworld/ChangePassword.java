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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePassword extends BaseActivity {
	private EditText username;
	private EditText password;
	private EditText new_password;
	private EditText new_password_re;
	private ImageView back;
	private ImageView tick_cross, tick_cross2;
	private TextView warning;
	private Button confirm;

	private MyApplication myapp;

	private SharedPreferences pref;
	private SharedPreferences.Editor editor;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String res = (String) msg.obj;

			if (res.equals("0")) {
				editor = pref.edit();
				editor.putString("password", new_password.getText().toString());
				editor.commit();

				Intent intent = new Intent(ChangePassword.this,
						MainActivity.class);

				intent.putExtra("change_password_ok", true);
				startActivity(intent);
				finish();

			} else if (res.equals("1")) {
				Toast.makeText(ChangePassword.this, "用户名或密码错误，请重试",
						Toast.LENGTH_SHORT).show();

			} else if (res.equals("network wrong")) {
				Toast.makeText(ChangePassword.this, "网络连接出错，请检查网络配置后重试",
						Toast.LENGTH_SHORT).show();
			} else {

			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.change_password);

		myapp = (MyApplication) getApplication();

		pref = getSharedPreferences("username_password", MODE_PRIVATE);

		username = (EditText) this.findViewById(R.id.username);
		password = (EditText) this.findViewById(R.id.password);
		new_password = (EditText) this.findViewById(R.id.new_password);
		new_password_re = (EditText) this.findViewById(R.id.new_password_re);
		tick_cross = (ImageView) this.findViewById(R.id.tick_cross1);
		tick_cross2 = (ImageView) this.findViewById(R.id.tick_cross2);
		warning = (TextView) this.findViewById(R.id.warning);
		confirm = (Button) this.findViewById(R.id.confirm);

		back = (ImageView) this.findViewById(R.id.imageView0);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(ChangePassword.this
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

		username.setSelectAllOnFocus(true);
		password.setSelectAllOnFocus(true);
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
				String user = pref.getString("username", "");
				String user2 = username.getText().toString();
				String pass = new_password.getText().toString();
				String pass2 = new_password_re.getText().toString();

				if (user.equals(user2)) {
					if ((!TextUtils.isEmpty(pass)) && (pass.equals(pass2))) {
						// 调用修改密码接口
						ChangePassword();
					} else {

						Toast.makeText(ChangePassword.this, "新密码不正确，请您修改",
								Toast.LENGTH_SHORT).show();

					}

				} else {
					Toast.makeText(ChangePassword.this, "用户名不正确，只能修改当前登录用户的密码",
							Toast.LENGTH_SHORT).show();

				}

			}
		});

		confirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					String user = pref.getString("username", "");
					String user2 = username.getText().toString();
					String pass = new_password.getText().toString();
					String pass2 = new_password_re.getText().toString();

					if (user.equals(user2)) {
						if ((!TextUtils.isEmpty(pass)) && (pass.equals(pass2))) {
							// 调用修改密码接口
							ChangePassword();
						} else {

							Toast.makeText(ChangePassword.this, "新密码不正确，请您修改",
									Toast.LENGTH_SHORT).show();

						}

					} else {
						Toast.makeText(ChangePassword.this,
								"用户名不正确，只能修改当前登录用户的密码", Toast.LENGTH_SHORT)
								.show();

					}

				}
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		username.setText("");
		password.setText("");
		new_password.setText("");
		new_password_re.setText("");
		tick_cross.setVisibility(View.INVISIBLE);
		tick_cross2.setVisibility(View.INVISIBLE);
		warning.setVisibility(View.INVISIBLE);
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
					jsonParam.put("uid", username.getText().toString());
					jsonParam.put("code", myapp.getvalidation_code());
					jsonParam.put("oldPasswd", password.getText().toString());
					jsonParam.put("newPasswd", new_password.getText()
							.toString());
					String temp = jsonParam.toString();
					testout.write(temp.getBytes());
					testout.flush();
					testout.close();
					Log.d("ChangePassword",
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
						Log.d("ChangePassword", "response:" + response2);

						response2 = response2.trim();

						Message message = new Message();
						message.obj = response2;
						handler.sendMessage(message);

					} else {
						Log.d("ChangePassword", "change password not ok: "
								+ urlConnection.getResponseMessage());
						Message message = new Message();
						message.obj = "network wrong";
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

}
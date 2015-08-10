package com.example.inkworld;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class UserpageActivity extends Activity {

	private SharedPreferences pref;
	private SharedPreferences.Editor editor;

	private LinearLayout fourOptionsLayout;
	private LinearLayout quitConfirm, Another, changePassword, changeProfile;

	private ImageView back, userProfile;
	private ListView listofpraise;
	private SimpleAdapter adapter;
	private TextView nicknameTextView;
	private String nicknameString = null;

	private PopupWindow alert;

	private MyApplication Myapp;

	private WindowManager mWindowManager;

	private View nightView = null;

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String res = (String) msg.obj;
			if (res.equals("0")) {
				Myapp.setFlag_login(Myapp.LOGOFF);
				editor = pref.edit();
				editor.putString("username", "");
				editor.putString("password", "");
				editor.commit();

				Intent intent = new Intent(UserpageActivity.this,
						MainActivity.class);
				startActivity(intent);
				UserpageActivity.this.finish();

				Toast.makeText(UserpageActivity.this, "注销成功", 1).show();
			} else if (res.equals("1")) {
				Toast.makeText(UserpageActivity.this, "注销failed", 1).show();
			} else {
				Toast.makeText(UserpageActivity.this, "sth wrong", 1).show();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_userpage);

		userProfile = (ImageView) findViewById(R.id.user_tx);
		pref = getSharedPreferences("username_password", MODE_PRIVATE);
		fourOptionsLayout = (LinearLayout) this.findViewById(R.id.four_options);

		back = (ImageView) this.findViewById(R.id.back);
		fourOptionsLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showAlertWindow(arg0);
			}
		});

		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		listofpraise = (ListView) this.findViewById(R.id.listofpraise);
		final List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		Map<String, Object> listItem1 = new HashMap<String, Object>();
		listItem1.put("title", "[经典就是用来恶搞的]冷笑话合集");
		listItem1.put("praisenum", "5");
		listItems.add(listItem1);
		Map<String, Object> listItem2 = new HashMap<String, Object>();
		listItem2.put("title", "热爱生活才能成为段子手");
		listItem2.put("praisenum", "6");
		listItems.add(listItem2);
		Map<String, Object> listItem3 = new HashMap<String, Object>();
		listItem3.put("title", "月下蝶舞");
		listItem3.put("praisenum", "9");
		listItems.add(listItem3);
		Map<String, Object> listItem4 = new HashMap<String, Object>();
		listItem4.put("title", "从前从前有个人爱你很久");
		listItem4.put("praisenum", "2");
		listItems.add(listItem4);
		Map<String, Object> listItem5 = new HashMap<String, Object>();
		listItem5.put("title", "清晨入古寺，初日照高林");
		listItem5.put("praisenum", "5");
		listItems.add(listItem5);
		adapter = new SimpleAdapter(this, listItems, R.layout.listofpraise,
				new String[] { "title", "praisenum", }, new int[] { R.id.title,
						R.id.praisenum });

		listofpraise.setAdapter(adapter);
		listofpraise.setEnabled(false);

	}

	@SuppressWarnings("deprecation")
	private void showAlertWindow(View view) {
		View alertView = getLayoutInflater().inflate(R.layout.quit_alert, null,
				false);
		alert = new PopupWindow(alertView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);

		alert.setOutsideTouchable(true);
		alert.setFocusable(true);
		alert.setBackgroundDrawable(new BitmapDrawable());

		alert.showAtLocation((View) view.getParent(), Gravity.CENTER, 0, 0);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ColorDrawable cd = new ColorDrawable(0x000000);
				alert.setBackgroundDrawable(cd);
				// 产生背景变暗效果
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 0.7f;
				getWindow().setAttributes(lp);
			}
		}, 300);

		alert.update();

		quitConfirm = (LinearLayout) alertView.findViewById(R.id.quit_confirm);
		Another = (LinearLayout) alertView.findViewById(R.id.another);
		changePassword = (LinearLayout) alertView
				.findViewById(R.id.change_password);
		changeProfile = (LinearLayout) alertView
				.findViewById(R.id.change_profile);

		quitConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				logout();
				alert.dismiss();
			}
		});
		Another.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				alert.dismiss();
				// Myapp.setFlag_login(Myapp.LOGOFF);
				Intent intent = new Intent(UserpageActivity.this,
						LoginActivity.class);
				startActivity(intent);
				UserpageActivity.this.finish();
			}
		});

		changePassword.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alert.dismiss();
				// 跳转
				Intent intent = new Intent(UserpageActivity.this,
						ChangePassword.class);
				startActivity(intent);

			}
		});
		changeProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alert.dismiss();
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(intent, 1);

			}
		});

		alert.setOnDismissListener(new OnDismissListener() {
			// 在dismiss中恢复透明度
			public void onDismiss() {

				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1f;
				getWindow().setAttributes(lp);
			}
		});
		// 返回键退出
		alertView.setFocusableInTouchMode(true);
		alertView.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					alert.dismiss();
					alert = null;
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

		nicknameTextView = (TextView) this.findViewById(R.id.username);
		// 昵称显示字数限制
		nicknameString = Myapp.getUsernameString();
		String username2 = pref.getString("username", "");

		if (username2.length() > 5) {
			username2 = username2.substring(0, 5) + "…";
		}
		nicknameTextView.setText(username2);
		// Toast.makeText(UserpageActivity.this,
		// "欢迎来到续墨个人主页O(∩_∩)O~~，" + pref.getString("username", ""),
		// Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		if (fileIsExists()) {
			if (Myapp.getFlag_notx()) {
				userProfile.setImageResource(R.drawable.icon_tx);
			} else {
				Bitmap bitmap = BitmapFactory.decodeFile(getBaseContext()
						.getFilesDir() + "/Profile.png");
				userProfile.setImageBitmap(bitmap);
			}

		} else {
			userProfile.setImageResource(R.drawable.icon_tx);
		}

	}

	private void logout() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection urlConnection = null;
				InputStream in = null;

				OutputStreamWriter out = null;
				DataOutputStream testout = null;

				StringBuffer buffer = null;
				int i = 0;
				byte[] bytes;
				JSONArray res;

				try {
					final URL url = new URL(Myapp.getlogout_url());
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

					jsonParam.put("uid", Myapp.getUsernameString());
					jsonParam.put("code", Myapp.getvalidation_code());

					Log.d("UserpageActivity",
							"uid: "
									+ Myapp.getUsernameString());
					Log.d("UserpageActivity",
							"code: "
									+ Myapp.getvalidation_code());
					
					String temp = jsonParam.toString();
					testout.write(temp.getBytes());
					testout.flush();
					testout.close();

					int HttpResult = urlConnection.getResponseCode();
					if (HttpResult == HttpURLConnection.HTTP_OK) {
						in = urlConnection.getInputStream();
						bytes = new byte[1024];
						buffer = new StringBuffer();
						while ((i = in.read(bytes)) != -1) {
							buffer.append(new String(bytes, 0, i, "UTF-8"));
						}

						String response2 = buffer.toString();
						Log.d("UserpageActivity", "response:" + response2);

						response2 = response2.trim();

						Message message = new Message();
						message.obj = response2;
						handler.sendMessage(message);

					} else {
						Log.d("UserpageActivity",
								"sth wrong: "
										+ urlConnection.getResponseMessage());
						Message message = new Message();
						message.obj = "ForgetPassword network wrong";
						handler.sendMessage(message);
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		ContentResolver resolver = this.getContentResolver();

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 8;// 图片高宽度都为本来的八分之一，即图片大小为本来的大小的六十四分之一
		options.inTempStorage = new byte[5 * 1024];

		// Toast.makeText(PrepareActivity.this, "choose picture success!",
		// Toast.LENGTH_SHORT).show();

		if (data != null) {
			if (requestCode == 1) {
				try {
					if (data.getData() != null) {
						// 获得图片的uri
						Uri uri = data.getData();
						// Toast.makeText(UserpageActivity.this, "get data ok!",
						// 0).show();
						// 将字节数组转换为ImageView可调用的Bitmap对象
						Bitmap bitmap = BitmapFactory.decodeStream(
								resolver.openInputStream(uri), null, options);
						if (bitmap.getRowBytes() * bitmap.getHeight() < 10000) { // 超小图
							// inSampleSize
							// 改成1
							options.inSampleSize = 1;
							options.inTempStorage = new byte[5 * 1024];
							bitmap = BitmapFactory.decodeStream(
									resolver.openInputStream(uri), null,
									options);
						} else if (bitmap.getRowBytes() * bitmap.getHeight() < 50000) { // 小图
																						// inSampleSize
																						// 改成2
							options.inSampleSize = 2;
							options.inTempStorage = new byte[5 * 1024];
							bitmap = BitmapFactory.decodeStream(
									resolver.openInputStream(uri), null,
									options);
						} else if (bitmap.getRowBytes() * bitmap.getHeight() < 200000) { // 中图
																							// inSampleSize
																							// 改成4
							options.inSampleSize = 4;
							options.inTempStorage = new byte[5 * 1024];
							bitmap = BitmapFactory.decodeStream(
									resolver.openInputStream(uri), null,
									options);
						}

						// 由于Intent传递bitmap不能超过40k,此处使用二进制数组传递
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						// Toast.makeText(
						// UserpageActivity.this,
						// String.valueOf(bitmap.getRowBytes()
						// * bitmap.getHeight()),
						// Toast.LENGTH_SHORT).show();
						bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
						byte[] bitmapByte = baos.toByteArray();

						Intent intent = new Intent(UserpageActivity.this,
								CutPhotoActivity.class);
						intent.putExtra("bitmap", bitmapByte);
						startActivity(intent);
					}
				} catch (Exception e) {
					Toast.makeText(UserpageActivity.this, "can't read image!",
							0).show();
				}
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

}

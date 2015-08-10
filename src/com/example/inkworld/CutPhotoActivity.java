package com.example.inkworld;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CutPhotoActivity extends Activity {

	Bitmap bitmap_temp = null;

	private String usercode;
	private String uid;

	private SharedPreferences preferences;

	private ClipImageView imageView;

	private Button bnOk, bnCancel;

	private MyApplication myapp;

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String res = (String) msg.obj;

			if (res.equals("save ok")) {
				post_image();
				Toast.makeText(CutPhotoActivity.this, "头像保存成功",
						Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(CutPhotoActivity.this, "保存失败，请重试",
						Toast.LENGTH_SHORT).show();
			}

		};
	};
	
	Handler handler2=new Handler(){
		public void handleMessage(Message msg) {
			String res = (String) msg.obj;
			if (res.equals("post ok")) {

				Toast.makeText(CutPhotoActivity.this, "头像上传成功",
						Toast.LENGTH_SHORT).show();
				finish();
			} else if (res.equals("unknown")) {
				Toast.makeText(CutPhotoActivity.this, "头像上传失败，原因未知",
						Toast.LENGTH_SHORT).show();
				finish();
			} else {
//				Toast.makeText(CutPhotoActivity.this, "头像上传失败,网络连接出现问题",
//						Toast.LENGTH_SHORT).show();
				finish();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cutphoto);

		myapp = (MyApplication) getApplication();

		imageView = (ClipImageView) findViewById(R.id.src_pic);

		byte[] bis = getIntent().getByteArrayExtra("bitmap");
		
		Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		}

		bnOk = (Button) findViewById(R.id.bn_ok);
		bnCancel = (Button) findViewById(R.id.bn_cancel);

		bnOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// 此处获取剪裁后的bitmap
				bitmap_temp = imageView.clip();

				uid = myapp.getUsernameString();
				usercode = myapp.getvalidation_code();

				saveFile(bitmap_temp);

				// finish();

			}
		});

		bnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

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
			
			//压缩图片
			int width = bm.getWidth();    
	        int height = bm.getHeight(); 
	        int newWidth = 100;    
	        int newHeight = 100; 
	        float scaleWidth = ((float) newWidth) / width;    
	        float scaleHeight = ((float) newHeight) / height;
	        Matrix matrix = new Matrix();    
	        matrix.postScale(scaleWidth, scaleHeight); 
	        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width,    
                    height, matrix, true); 
			
			
			BufferedOutputStream bo = null;
			bo = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
			resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, bo);

			if (fileIsExists()) {
				Message message = new Message();
				message.obj = "save ok";
				handler.sendMessage(message);
			} else {
				Message message = new Message();
				message.obj = "net_problem";
				Log.d("CutA", "save  not_ok");
				handler.sendMessage(message);
			}

			bo.flush();
			bo.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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

	// 上传头像
	private void post_image() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				String test_url = myapp.getupload_userImage_url() + uid
						+ "&code=" + usercode;
				Log.d("ServerCom", "test_url: " + test_url);
				Log.d("ServerCom", "usercode: " + usercode);
				HttpURLConnection urlConnection = null;
				InputStream in = null;

				OutputStreamWriter out = null;
				DataOutputStream testout = null;

				StringBuffer buffer = null;
				int i = 0;
				byte[] bytes;
				// JSONArray res;
				try {
					final URL url = new URL(test_url);
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
					File f = new File(getBaseContext().getFilesDir(),
							"/Profile.png");
					byte[] bmbuffer = getBytesFromFile(f);
					Log.d("ServerCom", "buffer size: " + bmbuffer.length);

					testout.write(bmbuffer);
					testout.flush();
					testout.close();
					Log.d("ServerCom", "" + urlConnection.getResponseCode());

					int HttpResult = urlConnection.getResponseCode();
					if (HttpResult == HttpURLConnection.HTTP_OK) {
						in = urlConnection.getInputStream();
						bytes = new byte[1024];
						buffer = new StringBuffer();
						while ((i = in.read(bytes)) != -1) {
							buffer.append(new String(bytes, 0, i, "UTF-8"));
						}

						String response2 = buffer.toString().trim();
						Log.d("ServerCom", "response: " + response2);
						if (response2.equals("0")) {
							Message message = new Message();
							message.obj = "post ok";
							handler2.sendMessage(message);
							Log.d("CutA", "post ok");
							myapp.setuserImage(bmbuffer);
						} else if (response2.equals("1")) {
							Message message = new Message();
							message.obj = "unknown";
							handler2.sendMessage(message);
						} else {
							Message message = new Message();
							message.obj = "net_problem";
							Log.d("CutA", "post not_ok");
							handler2.sendMessage(message);
						}

					} else {

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

	private static byte[] getBytesFromFile(File f) {
		if (f == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = stream.read(b)) != -1)
				out.write(b, 0, n);
			stream.close();
			out.close();
			return out.toByteArray();
		} catch (IOException e) {

		}
		return null;
	}
}

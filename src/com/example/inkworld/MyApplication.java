package com.example.inkworld;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import libcore.io.DiskLruCache;

import org.json.JSONObject;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

public class MyApplication extends Application {

	/*
	 * ��̨ͨ�Žӿ�
	 */
	private String register_url = "http://101.200.231.143:8080/xumo/register";
	private String heartbeat_url = "http://101.200.231.143:8080/xumo/heartbeat";
	private String login_url = "http://101.200.231.143:8080/xumo/loggin";
	private String goodpage_url = "http://101.200.231.143:8080/xumo/goodpage";
	private String newpage_url = "http://101.200.231.143:8080/xumo/newpage";
	private String article_url = "http://101.200.231.143:8080/xumo/article?articleId=";
	private String newarticle_url = "http://101.200.231.143:8080/xumo/newarticle";
	private String collection_url = "http://101.200.231.143:8080/xumo/collection";
	private String join_url = "http://101.200.231.143:8080/xumo/join";
	private String newparagraph_url = "http://101.200.231.143:8080/xumo/newparagraph";
	private String paragraphlist_url = "http://101.200.231.143:8080/xumo/paragraphlist?articleId=";
	private String paragraph_url = "http://101.200.231.143:8080/xumo/paragraph?pid=";
	private String checkUserName_url = "http://101.200.231.143:8080/xumo/checkUserName";
	private String upload_userImage_url = "http://101.200.231.143:8080/xumo/userImage?uid=";
	private String accusation_url = "http://101.200.231.143:8080/xumo/accusation";
	private String praise_url = "http://101.200.231.143:8080/xumo/praise";
	private String download_userImage_url = "http://101.200.231.143:8080/xumo/image/";
	private String checkPhoneNumber_url = "http://101.200.231.143:8080/xumo/checkPhoneNumber";
	private String logout_url = "http://101.200.231.143:8080/xumo/logout";
	private String forget_password_url = "http://101.200.231.143:8080/xumo/forgetpasswd";
	private String change_password_url = "http://101.200.231.143:8080/xumo/resetpasswd";
	private String search_url = "http://101.200.231.143:8080/xumo/search";

	public String getregister_url() {
		return register_url;
	}

	public String getheartbeat_url() {
		return heartbeat_url;
	}

	public String getlogin_url() {
		return login_url;
	}

	public String getgoodpage_url() {
		return goodpage_url;
	}

	public String getnewpage_url() {
		return newpage_url;
	}

	public String getarticle_url() {
		return article_url;
	}

	public String getnewarticle_url() {
		return newarticle_url;
	}

	public String getcollection_url() {
		return collection_url;
	}

	public String getjoin_url() {
		return join_url;
	}

	public String getnewparagraph_url() {
		return newparagraph_url;
	}

	public String getparagraphlist_url() {
		return paragraphlist_url;
	}

	public String getparagraph_url() {
		return paragraph_url;
	}

	public String getcheckUserName_url() {
		return checkUserName_url;
	}

	public String getupload_userImage_url() {
		return upload_userImage_url;
	}

	public String getaccusation_url() {
		return accusation_url;
	}

	public String getpraise_url() {
		return praise_url;
	}
	
	public String getsearch_url() {
		return search_url;
	}

	public String getdownload_userImage_url() {
		return download_userImage_url;
	}

	public String getlogout_url() {
		return logout_url;
	}
	public String getforget_password_url(){  
        return forget_password_url;  
    } 
    public String getchange_password_url(){  
        return change_password_url;  
    } 


	/*
	 * ������
	 */

	private LruCache<String, Topic> myMemoryCache;
	private DiskLruCache myDiskLruCache;

	public LruCache<String, Topic> getLruCache() {
		return myMemoryCache;
	}

	public void setLruCache(LruCache s) {
		this.myMemoryCache = s;
	}

	public DiskLruCache getDiskLruCache() {
		return myDiskLruCache;
	}

	public void setDiskLruCache(DiskLruCache s) {
		this.myDiskLruCache = s;
	}

	// �û���¼�����֤��
	private String validation_code;

	public String getvalidation_code() {
		return validation_code;
	}

	public String getcheckPhoneNumber_url() {
		return checkPhoneNumber_url;
	}

	public void setvalidation_code(String s) {
		this.validation_code = s;
	}

	// ��¼�ɹ����
	private Boolean flag_login;
	private String usernameString;
	final Boolean LOGIN = true;
	final Boolean LOGOFF = false;

	public Boolean getFlag_login() {
		return flag_login;
	}

	public void setFlag_login(Boolean flag_login) {
		this.flag_login = flag_login;
	}

	public String getUsernameString() {
		return usernameString;
	}

	public void setUsernameString(String usernameString) {
		this.usernameString = usernameString;
	}

	// �û�ͷ��
	private Boolean flag_notx=null;
	
	public Boolean getFlag_notx() {
		return flag_notx;
	}

	public void setFlag_notx(Boolean flag_notx) {
		this.flag_notx = flag_notx;
	}

	private byte[] userImage = null;

	public byte[] getuserImage() {
		return userImage;
	}

	public void setuserImage(byte[] s) {
		this.userImage = s;
	}
	
	 //Ĭ��ͷ���û���ͷ��ʱ����
    private byte[] default_image = null;
    
    public byte[] getdefault_image(){  
        return default_image;  
    }  
	
	
	
	
	
	
	
	
	

	public static final String SETTING_INFOS = "SettingInfos"; // SharedPreference�ļ���
	public static final String PRAISE_INFOS = "PraiseInfos"; // SharedPreference�ļ���

	// �����С
	private int fontSize;
	final int FONTSIZE_SMALL = 0;
	final int FONTSIZE_MIDDLE = 1;
	final int FONTSIZE_LARGE = 2;

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	// ҹ��ģʽ
	private Boolean lunaMode;
	final Boolean MODE_NIGHT = true;
	final Boolean MODE_DAY = false;

	public Boolean getlunaMode() {
		return lunaMode;
	}

	public void setlunaMode(Boolean lunamode) {
		lunaMode = lunamode;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		fontSize = FONTSIZE_SMALL;
		lunaMode = MODE_DAY;
		flag_login = LOGOFF;
		
		
		SharedPreferences settings = getSharedPreferences(SETTING_INFOS,
				Activity.MODE_PRIVATE); // ���Ȼ�ȡһ�� SharedPreferences ����

		int lightmode = settings.getInt("LightMode", 0);

		if (lightmode == 0)
			setlunaMode(MODE_DAY);
		else
			setlunaMode(MODE_NIGHT);

		int fontSize = settings.getInt("fontSize", 0);

		if (fontSize == 0)
			setFontSize(FONTSIZE_SMALL);
		else if (fontSize == 1)
			setFontSize(FONTSIZE_MIDDLE);
		else {
			setFontSize(FONTSIZE_LARGE);
		}

		Boolean flag_login = settings.getBoolean("flag_login", false);
		if (!flag_login)
			setFlag_login(LOGOFF);
		else
			setFlag_login(LOGIN);
		
		Boolean flag_notx = settings.getBoolean("flag_notx", false);
		if (!flag_notx)
			setFlag_notx(false);
		else
			setFlag_notx(true);
		
		 // ��ȡӦ�ó����������ڴ�
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		myMemoryCache = new LruCache<String, Topic>(cacheSize);
		
		try {
			// ��ȡͼƬ����·��
			File cacheDir = getDiskCacheDir(getBaseContext(), "topic");
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			// ����DiskLruCacheʵ������ʼ����������
			myDiskLruCache = DiskLruCache
					.open(cacheDir, getAppVersion(getBaseContext()), 1, 10 * 1024 * 1024);
		} catch (IOException e) {
			e.printStackTrace();
		}

		
		Resources res=getResources();
		Bitmap bmp=BitmapFactory.decodeResource(res, R.drawable.icon_tx);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		bmp.compress(Bitmap.CompressFormat.PNG, 100, baos); 
		default_image = baos.toByteArray();
		
		
		
		
	}
	
	
	 public void DiskcacheReInit() {  
	    	try {
				// ��ȡͼƬ����·��
				File cacheDir = getDiskCacheDir(getBaseContext(), "topic");
				if (!cacheDir.exists()) {
					cacheDir.mkdirs();
				}
				// ����DiskLruCacheʵ������ʼ����������
				myDiskLruCache = DiskLruCache
						.open(cacheDir, getAppVersion(getBaseContext()), 1, 10 * 1024 * 1024);
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    	
	    	
	    }
	    


	    
	    /**
		 * ���ݴ����uniqueName��ȡӲ�̻����·����ַ��
		 */
		private File getDiskCacheDir(Context context, String uniqueName) {
			String cachePath;
			if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
					|| !Environment.isExternalStorageRemovable()) {
				cachePath = context.getExternalCacheDir().getPath();
			} else {
				cachePath = context.getCacheDir().getPath();
			}
			return new File(cachePath + File.separator + uniqueName);
		}
		
		/**
		 * ��ȡ��ǰӦ�ó���İ汾�š�
		 */
		private int getAppVersion(Context context) {
			try {
				PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
						0);
				return info.versionCode;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			return 1;
		}
	    
		/**
		 * ��һ��topic�洢��LruCache�С�
		 * 
		 * @param key
		 *            LruCache�ļ������ﴫ��Topic��URL��ַ��
		 * @param topic
		 *            LruCache�ļ������ﴫ������������ص�Topic����
		 */
		public void addTopicToMemoryCache(String key, Topic topic) {
			if (getTopicFromMemoryCache(key) == null) {
				myMemoryCache.put(key, topic);
			}
		}
		
		/**
		 * ��LruCache�л�ȡһ��Topic����������ھͷ���null��
		 * 
		 * @param key
		 *            LruCache�ļ������ﴫ��Topic��URL��ַ��
		 * @return ��Ӧ�������Topic���󣬻���null��
		 */
		public Topic getTopicFromMemoryCache(String key) {
			String text = myMemoryCache.get("fgchgh")==null?"url cache is null":"url cache is not null";
			Log.d("adapter", text);
			return myMemoryCache.get(key);
		}
		
		/**
		 * ʹ��MD5�㷨�Դ����key���м��ܲ����ء�
		 */
		public String hashKeyForDisk(String key) {
			String cacheKey;
			try {
				final MessageDigest mDigest = MessageDigest.getInstance("MD5");
				mDigest.update(key.getBytes());
				cacheKey = bytesToHexString(mDigest.digest());
			} catch (NoSuchAlgorithmException e) {
				cacheKey = String.valueOf(key.hashCode());
			}
			return cacheKey;
		}
		
		private String bytesToHexString(byte[] bytes) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				String hex = Integer.toHexString(0xFF & bytes[i]);
				if (hex.length() == 1) {
					sb.append('0');
				}
				sb.append(hex);
			}
			return sb.toString();
		}

		/**
		 * �������¼ͬ����journal�ļ��С�
		 */
		public void fluchCache() {
			if (myDiskLruCache != null) {
				try {
					myDiskLruCache.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		
		 /**
	     *����ͨ����ط��� 
	     */
		//�������û�ȡ����ӿ�ʱ���ص�json object
	    public  Topic parseJSONWithJSONObject(String jsonData,String praise_key) {
			try {
				//JSONArray res = new JSONArray(jsonData);
				//JSONObject jsonObject = res.getJSONObject(0);
				
				JSONObject jsonObject = new JSONObject(jsonData);
				
				String title = jsonObject.getString("title");
				String tag = jsonObject.getString("tag");
				String author = jsonObject.getString("author");
				String createTime = jsonObject.getString("createTime");
				String imageUrl = "";
				if(!TextUtils.isEmpty(jsonObject.getString("imageUrl")) && (!jsonObject.getString("imageUrl").equals("null")))
				{
					imageUrl = jsonObject.getString("imageUrl");
				}
				Log.d("adapter", "topic imageUrl is:" + imageUrl);
				String content = jsonObject.getString("content");
				String praise = jsonObject.getString("praise");
				String followNum = jsonObject.getString("followNum");
				String floorNum = jsonObject.getString("floorNum");
				
				String praised = jsonObject.getString("praised");
				
				SharedPreferences praises = getSharedPreferences(PRAISE_INFOS,
						Activity.MODE_PRIVATE); // ���Ȼ�ȡһ�� SharedPreferences ����
				
				praises.edit().putString(praise_key, praised).commit();
				Log.d("praise", "praise: put key=" + praise_key + ",value=" + praised);
				
				
				Topic topic = new Topic(0,title, tag, author, createTime, imageUrl,
						content, praise,followNum,floorNum);
				/*Log.d("article adapter", "topic in functon is " + topic.getTitle()+","+topic.getTag()+","+
						topic.getAuthor()+","+topic.getcreateTime()+","+topic.getContent()+
						","+topic.getPraise()+","+topic.getfollowNum()+","+topic.getfloorNum());*/
				
				return topic;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		
		

	    /*//����json object
	    public  Topic parseJSONWithJSONObject(String jsonData) {
			try {
				//JSONArray res = new JSONArray(jsonData);
				//JSONObject jsonObject = res.getJSONObject(0);
				
				JSONObject jsonObject = new JSONObject(jsonData);
				
				String title = jsonObject.getString("title");
				//Log.d("adapter", "topic title is " + title);
				
				String tag = jsonObject.getString("tag");
				String author = jsonObject.getString("author");
				String createTime = jsonObject.getString("createTime");
				String imageUrl = jsonObject.getString("imageUrl");
				String content = jsonObject.getString("content");
				String praise = jsonObject.getString("praise");
				String followNum = jsonObject.getString("followNum");
				String floorNum = jsonObject.getString("floorNum");
				Topic topic = new Topic(0, title, tag, author, createTime, imageUrl,
						content, praise,followNum,floorNum);
				Log.d("adapter", "topic in functon is " + topic.getTitle()+","+topic.getTag()+","+
						topic.getAuthor()+","+topic.getTime()+","+topic.getContent()+
						","+topic.getPraise()+","+topic.getFollownum()+","+topic.getFloornum());
				
				return topic;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}*/
	    
	    
	    
	    //�������û�ȡ���½ӿ�ʱ���ص�json object
	    public  Topic article_parseJSONWithJSONObject(String jsonData) {
			try {
				JSONObject jsonObject = new JSONObject(jsonData);
				
				String title = jsonObject.getString("title");
				Log.d("article adapter", "topic title is " + title);
				
				String tag = jsonObject.getString("tag");
				String author = jsonObject.getString("uid");
				String createTime = jsonObject.getString("createTime");
				String imageUrl = "";
				if(!TextUtils.isEmpty(jsonObject.getString("imageUrl")) && (!jsonObject.getString("imageUrl").equals("null")))
				{
					imageUrl = jsonObject.getString("imageUrl");
				}
				Log.d("article adapter", "topic imageUrl is:" + imageUrl);
				
				String content = jsonObject.getString("content");
				String praise = jsonObject.getString("praise");
				String followNum = jsonObject.getString("followNum");
				String floorNum = jsonObject.getString("floorNum");
//				String praised = jsonObject.getString("praised");
				Topic topic = new Topic(0,title, tag, author, createTime, imageUrl,
						content, praise,followNum,floorNum);
				Log.d("article adapter", "topic in functon is " + topic.getTitle()+","+topic.getTag()+","+
						topic.getAuthor()+","+topic.getcreateTime()+","+topic.getContent()+
						","+topic.getPraise()+","+topic.getfollowNum()+","+topic.getfloorNum());
				
				return topic;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	    
	    
	    
		/**
		 * ��������Ƿ�����
		 * 
		 * @return
		 */
		public boolean isNetConnected() {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo[] infos = cm.getAllNetworkInfo();
				if (infos != null) {
					for (NetworkInfo ni : infos) {
						if (ni.isConnected()) {
							return true;
						}
					}
				}
			}
			return false;
		}
	    
		/**
		 * ���wifi�Ƿ�����
		 * 
		 * @return
		 */
		public boolean isWifiConnected() {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo networkInfo = cm.getActiveNetworkInfo();
				if (networkInfo != null
						&& networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					return true;
				}
			}
			return false;
		}

		/**
		 * ���3G�Ƿ�����
		 * 
		 * @return
		 */
		public boolean isMobileConnected() {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				NetworkInfo networkInfo = cm.getActiveNetworkInfo();
				if (networkInfo != null
						&& networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					return true;
				}
			}
			return false;
		}

}



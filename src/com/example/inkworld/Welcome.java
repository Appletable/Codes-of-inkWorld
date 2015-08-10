package com.example.inkworld;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class Welcome extends Activity{
	
	private boolean isFirstUse;
	 private static final int TIME = 750;//��λms����ӭͼƬ��ʾ��ʱ��
	 private static final int TO_MAIN = 100001;
	 private static final int TO_GUIDE = 100002;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.welcome);
		 init();
	}
	
	//���ڲ��������߳���ֱ����ʱ��������һ��Handler�������͹�������Ϣ
    Handler myHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case TO_MAIN:
                Intent i1 = new Intent(Welcome.this,MainActivity.class);
                i1.putExtra("flag",isFirstUse);
                startActivity(i1);
                finish();
                break;
            case TO_GUIDE:
                Intent i2 = new Intent(Welcome.this,GuideActivity.class);
                startActivity(i2);
                finish();
                break;
            }
        };
    };
	
	 private void init() {
        //���û��Ƿ��ǵ�һ��ʹ�õ�ֵ��SharedPreferences�洢������
        SharedPreferences perPreferences = getSharedPreferences("zh", MODE_PRIVATE);
        isFirstUse = perPreferences.getBoolean("isFirstUse", true);
        if (!isFirstUse) {
            myHandler.sendEmptyMessageDelayed(TO_MAIN, TIME);
        }else{
            myHandler.sendEmptyMessageDelayed(TO_GUIDE, TIME);
            Editor editor = perPreferences.edit();
            editor.putBoolean("isFirstUse", false);
            editor.commit();
        }
	
	 }
	 
	 
	 
	 
	 
	 
	 
	 
	
}
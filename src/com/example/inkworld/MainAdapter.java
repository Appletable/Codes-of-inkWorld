package com.example.inkworld;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MainAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	Context context;
	private List<Topic> ls;
	
	public MainAdapter(Context context, List<Topic> ls) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.ls = ls;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return (ls == null) ? 0 : ls.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return ls.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.activity_item, parent, false);
			holder = new ViewHolder();
			// 获取控件
			holder.ivPortraitImage = (ImageView) convertView
					.findViewById(R.id.iv_profile);
			holder.tvAuthor = (TextView) convertView
					.findViewById(R.id.tv_nickname);
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.title);
			holder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_context);
			holder.tvTag = (TextView) convertView
					.findViewById(R.id.tv_labeltext);
			holder.tvTime = (TextView) convertView
					.findViewById(R.id.tv_time);
			holder.tvFollowNum = (TextView) convertView
					.findViewById(R.id.tv_xumonum);
			holder.tvPraise = (TextView) convertView
					.findViewById(R.id.tv_zannum);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}
		
		Topic topict = (Topic) ls.get(position);
		byte[] data = topict.getBitmap();
		if (data != null) {
//			Log.d("adapter", "ddddddddddddata size: " + data.length);
			holder.ivPortraitImage.setImageBitmap(BitmapFactory
					.decodeByteArray(data, 0, data.length));
		}
		holder.tvAuthor.setText(topict.getAuthor());
		holder.tvTitle.setText(topict.getTitle());
		holder.tvContent.setText(topict.getContent());
		holder.tvTag.setText(topict.getTag());
		try {
			String systemTime = topict.getcreateTime();
			Log.d("Time", systemTime + " and " + System.currentTimeMillis());
			Date date = new Date();
			date.setTime(Long.parseLong(systemTime+"000"));
			
			SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			Log.d("Time", "format is " + sdf.format(date));
			
			holder.tvTime.setText(sdf.format(date));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		holder.tvFollowNum.setText("已有" + (Integer.valueOf(topict.getfollowNum().isEmpty()?"1":topict.getfollowNum()).intValue()-1) + "人续墨");
		holder.tvPraise.setText(topict.getPraise());
		
		return convertView;
	}
	
	
	
	public class ViewHolder {
		
		ImageView ivPortraitImage;
		TextView tvAuthor;
		TextView tvTitle;
		TextView tvContent;
		TextView tvTag;
		TextView tvTime;
		TextView tvFollowNum;
		TextView tvPraise;
	}

}

package com.example.inkworld;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MySimpleAdapter extends SimpleAdapter {

	private LayoutInflater inflater;
	private TextView tvContext;
	private MyApplication myapp;

	public MySimpleAdapter(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);

		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		myapp = (MyApplication) context.getApplicationContext();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater
					.inflate(R.layout.activity_content_item, null);
		}
		
		tvContext = (TextView) convertView.findViewById(R.id.tv_context);
		
		switch (myapp.getFontSize()) {
		case 0:
			tvContext.setTextSize(14);
			break;
			
		case 1:
			tvContext.setTextSize(18);
			break;
			
		case 2:
			tvContext.setTextSize(22);
			break;

		default:
			break;
		}

		return super.getView(position, convertView, parent);
	}

}

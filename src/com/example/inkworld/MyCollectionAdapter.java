package com.example.inkworld;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCollectionAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	Context context;
	private List<MyCollectionItem> myCollectionItems;

	public MyCollectionAdapter(Context context,
			List<MyCollectionItem> myCollectionItems) {
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.myCollectionItems = myCollectionItems;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return (myCollectionItems == null) ? 0 : myCollectionItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return myCollectionItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		MyCollectionItem collectionItem = (MyCollectionItem) getItem(position);

		ViewHolder holder;

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_collection_item,
					null);
			holder = new ViewHolder();

			// 获取控件
			holder.ivCollectionProfile = (ImageView) convertView
					.findViewById(R.id.iv_collection_profile);
			holder.tvCollectionNickname = (TextView) convertView
					.findViewById(R.id.tv_collection_nickname);
			holder.tvCollectionTitle = (TextView) convertView
					.findViewById(R.id.tv_collection_title);
			holder.cbCollection = (CheckBox) convertView
					.findViewById(R.id.cb_collection);
			holder.tvCollectionContext = (TextView) convertView
					.findViewById(R.id.tv_collection_context);
			holder.tvCollectionLabeltext = (TextView) convertView
					.findViewById(R.id.tv_collection_labeltext);
			holder.tvCollectionTime = (TextView) convertView
					.findViewById(R.id.tv_collection_time);
			holder.tvCollectionXumonum = (TextView) convertView
					.findViewById(R.id.tv_collection_xumonum);
			holder.tvCollectionZannum = (TextView) convertView
					.findViewById(R.id.tv_collection_zannum);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();// 取出ViewHolder对象
		}

		// 获取内容
		holder.ivCollectionProfile
				.setImageResource(collectionItem.collectionProfile);
		holder.tvCollectionNickname.setText(collectionItem.collectionNickname);
		holder.tvCollectionTitle.setText(collectionItem.collectionTitle);
		switch (collectionItem.collectionCheckStatus) {

		case -1:
			holder.cbCollection.setVisibility(View.INVISIBLE);
			holder.cbCollection.setChecked(false);
			break;

		case 0:
			holder.cbCollection.setVisibility(View.VISIBLE);
			holder.cbCollection.setChecked(false);
			break;

		case 1:
			holder.cbCollection.setVisibility(View.VISIBLE);
			holder.cbCollection.setChecked(true);
			break;

		default:
			break;

		}

		holder.tvCollectionContext.setText(collectionItem.collectionContext);
		holder.tvCollectionLabeltext
				.setText(collectionItem.collectionLabeltext);
		holder.tvCollectionTime.setText(collectionItem.collectionTime);
		holder.tvCollectionXumonum.setText(collectionItem.collectionXumonum);
		holder.tvCollectionZannum.setText(collectionItem.collectionZannum);

		return convertView;
	}

	public class ViewHolder {
		ImageView ivCollectionProfile;
		TextView tvCollectionNickname;
		TextView tvCollectionTitle;
		CheckBox cbCollection;
		TextView tvCollectionContext;
		TextView tvCollectionLabeltext;
		TextView tvCollectionTime;
		TextView tvCollectionXumonum;
		TextView tvCollectionZannum;
	}

}

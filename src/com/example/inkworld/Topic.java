package com.example.inkworld;

import java.io.Serializable;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.os.Build;

//topic类，将ListView中某个表项的各个属性封装成一个类
public class Topic extends BaseItem implements Serializable {
	
	private String title;
	private String tag;
	private String author;
	private String createTime;
	private String imageUrl;//这里实际是头像的url，通过这个url再去下载图片
	private String content;
	private String praise;
	
	private String followNum;
	private String floorNum;
	
	private byte[] PortraitImage;//头像的字节流，便于将topic类序列化
	
	public Topic(int item_type, String title, String tag, String author, String createTime,
			String imageUrl, String content, String praise, String followNum,String floorNum) {
		super(item_type);
		this.title = title;
		this.tag = tag;
		this.author = author;
		this.createTime = createTime;
		this.imageUrl = imageUrl;
		this.content = content;
		this.praise = praise;
		this.followNum = followNum;
		this.floorNum = floorNum;
	
	}
	
    public int getItemType() {
        return super.getItem_type();
    }

    public void setItemType(int itemType) {
        super.setItem_type(itemType);
    }
	
	public String getTitle() {
		return title;
	}
	
	public String getTag() {
		return tag;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getcreateTime() {
		return createTime;
	}
	
	public String getimageUrl() {
		return imageUrl;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setPraise(String praisenum) {
		praise = praisenum;
	}
	
	public String getPraise() {
		return praise;
	}
	
	public byte[] getBitmap() {
		return PortraitImage;
	}
	
	public void setBitmap(byte[] bitmap) {
		this.PortraitImage = bitmap;
	}
	
	public String getfollowNum() {
		return followNum;
	}
	
	public String getfloorNum() {
		return floorNum;
	}
	
	
}




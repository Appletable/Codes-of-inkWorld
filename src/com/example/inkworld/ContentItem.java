package com.example.inkworld;

public class ContentItem extends BaseItem {

	private int contentprofile;

	private String contentnickname;

	private String contenttime;

	private String contenttext;

	private String contentzan;

	public ContentItem(int item_type, int contentprofile,
			String contentnickname, String contenttime, String contenttext,
			String contentzan) {
		super(item_type);
		this.contentprofile = contentprofile;
		this.contentnickname = contentnickname;
		this.contenttime = contenttime;
		this.contenttext = contenttext;
		this.contentzan = contentzan;
	}
	
    public int getItemType() {
        return super.getItem_type();
    }

    public void setItemType(int itemType) {
        super.setItem_type(itemType);
    }

	public int getContentprofile() {
		return contentprofile;
	}

	public void setContentprofile(int contentprofile) {
		this.contentprofile = contentprofile;
	}

	public String getContentnickname() {
		return contentnickname;
	}

	public void setContentnickname(String contentnickname) {
		this.contentnickname = contentnickname;
	}

	public String getContenttime() {
		return contenttime;
	}

	public void setContenttime(String contenttime) {
		this.contenttime = contenttime;
	}

	public String getContenttext() {
		return contenttext;
	}

	public void setContenttext(String contenttext) {
		this.contenttext = contenttext;
	}

	public String getContentzan() {
		return contentzan;
	}

	public void setContentzan(String contentzan) {
		this.contentzan = contentzan;
	}

	
}

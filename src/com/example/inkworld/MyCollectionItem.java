package com.example.inkworld;

public class MyCollectionItem {

	public int collectionProfile;
	public String collectionNickname;
	public String collectionTitle;
	public int collectionCheckStatus;	//0-未选中,1-选中,-1-不可见
	public String collectionContext;
	public String collectionLabeltext;
	public String collectionTime;
	public String collectionXumonum;
	public String collectionZannum;

	public MyCollectionItem(int collectionProfile, String collectionNickname,
			String collectionTitle, int collectionCheckStatus,
			String collectionContext, String collectionLabeltext,
			String collectionTime, String collectionXumonum,
			String collectionZannum) {
		super();
		this.collectionProfile = collectionProfile;
		this.collectionNickname = collectionNickname;
		this.collectionTitle = collectionTitle;
		this.collectionCheckStatus = collectionCheckStatus;
		this.collectionContext = collectionContext;
		this.collectionLabeltext = collectionLabeltext;
		this.collectionTime = collectionTime;
		this.collectionXumonum = collectionXumonum;
		this.collectionZannum = collectionZannum;
	}

	public int getCollectionProfile() {
		return collectionProfile;
	}

	public void setCollectionProfile(int collectionProfile) {
		this.collectionProfile = collectionProfile;
	}

	public String getCollectionNickname() {
		return collectionNickname;
	}

	public void setCollectionNickname(String collectionNickname) {
		this.collectionNickname = collectionNickname;
	}

	public String getCollectionTitle() {
		return collectionTitle;
	}

	public void setCollectionTitle(String collectionTitle) {
		this.collectionTitle = collectionTitle;
	}

	public int getCollectionCheckStatus() {
		return collectionCheckStatus;
	}

	public void setCollectionCheckStatus(int collectionCheckStatus) {
		this.collectionCheckStatus = collectionCheckStatus;
	}

	public String getCollectionContext() {
		return collectionContext;
	}

	public void setCollectionContext(String collectionContext) {
		this.collectionContext = collectionContext;
	}

	public String getCollectionLabeltext() {
		return collectionLabeltext;
	}

	public void setCollectionLabeltext(String collectionLabeltext) {
		this.collectionLabeltext = collectionLabeltext;
	}

	public String getCollectionTime() {
		return collectionTime;
	}

	public void setCollectionTime(String collectionTime) {
		this.collectionTime = collectionTime;
	}

	public String getCollectionXumonum() {
		return collectionXumonum;
	}

	public void setCollectionXumonum(String collectionXumonum) {
		this.collectionXumonum = collectionXumonum;
	}

	public String getCollectionZannum() {
		return collectionZannum;
	}

	public void setCollectionZannum(String collectionZannum) {
		this.collectionZannum = collectionZannum;
	}

}

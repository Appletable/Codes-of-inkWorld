package com.example.inkworld;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class ActivityCollector {
	public static List<Activity> activities = new ArrayList<Activity>();
	public static void addActivity(Activity activity) {
		activities.add(activity);
	}
	public static void removeActivity(Activity activity) {
		activities.remove(activity);
	}
	public static void finishAll() {
	for (Activity activity : activities) {
		if (!activity.isFinishing()) {
			activity.finish();
		}
	}
	}
}
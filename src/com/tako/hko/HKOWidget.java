package com.tako.hko;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class HKOWidget extends AppWidgetProvider {
	private static final String PREFS_NAME = "HKOPrefs";
	private static final String TAG = "HKOWidget";
	private static final String URI_HEADER = "hkowidget://widget/id";

	//  Variables for threads and handlers
	private boolean upFlag, iFlag;



	public HKOWidget() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}

}

package com.tako.hko;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class HKOWidget extends AppWidgetProvider {
	private static final String PREFS_NAME = "HKOPrefs";
	private static final String TAG = "HKOWidget";
	private static final String URI_HEADER = "hkowidget://widget/id";

	public HKOWidget() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		super.onDeleted(context, appWidgetIds);
		System.gc();
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

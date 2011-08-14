package com.tako.hko;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

public class HKOSmallWidget extends HKOWidget {
	private static final String PREFS_NAME = "HKOPrefs";
	private static final String TAG = "HKOWidget";
	private static final String URI_HEADER = "hkosmallwidget://widget/id";

	//  Variables for threads and handlers
	private boolean upFlag, iFlag;



	public HKOSmallWidget() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onEnabled(Context context) {
		
	}

	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		for (int appWidgetId : appWidgetIds) {

			// stop alarm
			Intent widgetUpdate = new Intent();
			widgetUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			widgetUpdate.setData(Uri.withAppendedPath(Uri.parse(URI_HEADER), String.valueOf(appWidgetId)));
			PendingIntent newPending = PendingIntent.getBroadcast(context, 0, widgetUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			newPending.cancel();
			alarms.cancel(newPending);

			Log.d(TAG, "Removing widget " + appWidgetId);
		}

		super.onDeleted(context, appWidgetIds);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final String action = intent.getAction();

		if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
			final int appWidgetId = intent.getExtras().getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
				this.onDeleted(context, new int[] { appWidgetId });
			} 
		}

		if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {

			final int[] appWidgetIds = intent.getExtras() != null ? intent.getExtras().getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS) : new int[1];
			this.upFlag = intent.getExtras().getBoolean("updateFlag", true);
			this.iFlag = intent.getExtras().getBoolean("iUpdate", false);
			//tContext = context;

			for (int appWidgetId : appWidgetIds) {
				Log.i(TAG, "Here onUpdated " + appWidgetId);
				SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("hkoid_" + appWidgetId, "small");
				editor.commit();
				
				Intent serviceIntent = new Intent(context, WidgetSmallService.class);
				serviceIntent.putExtra("widgetId", appWidgetId);
				serviceIntent.putExtra("updateFlag", upFlag);
				serviceIntent.putExtra("iUpdate", iFlag);
				context.startService(serviceIntent);
			}
			//this.updateWidget(AppWidgetManager.getInstance(context), appWidgetIds);

		}
		super.onReceive(context, intent);
	}
}

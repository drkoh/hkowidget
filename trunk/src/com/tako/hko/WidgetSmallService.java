package com.tako.hko;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class WidgetSmallService extends Service {
	private static final String PREFS_NAME = "HKOPrefs";
	private static final String URI_HEADER = "hkosmallwidget://widget/id";
	private static final String TAG = "HKOWidget";

	private RemoteViews views;
	private AppWidgetManager manager;
	private boolean upFlag;
	private Context sContext;
	private CurrentWeatherWrapper current;
	private int todayIcon;
	private PowerManager pm;
	private PowerManager.WakeLock wl;
	private SharedPreferences settings;
	private int widgetId;

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub

		// Getting back the values
		widgetId = intent.getIntExtra("widgetId", -1);
		upFlag = intent.getBooleanExtra("updateFlag", true);
		boolean iFlag = intent.getBooleanExtra("iUpdate", false);

		sContext = getApplicationContext();
		settings = sContext.getSharedPreferences(PREFS_NAME, 0);

		pm = (PowerManager) sContext.getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HKO Tag");


		views = new RemoteViews(sContext.getPackageName(), R.layout.hkowidget_small);
		// Get the layout for the App Widget and attach an on-click listener to the button
		Intent clickIntent = new Intent(sContext, HKOActivity.class);
		clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
		clickIntent.setData(Uri.withAppendedPath(Uri.parse(URI_HEADER + "://widget/id/"), String.valueOf(widgetId)));

		Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
		updateIntent.putExtra("iUpdate", true);
		updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{widgetId});
		updateIntent.setData(Uri.withAppendedPath(Uri.parse(URI_HEADER + "://widget/id/"), String.valueOf(widgetId)));

		PendingIntent pendingIntent = PendingIntent.getActivity(sContext, 0, clickIntent, 0);
		PendingIntent updatePendingIntent = PendingIntent.getBroadcast(sContext, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		views.setOnClickPendingIntent(R.id.widgetSmallLayout, pendingIntent);
		views.setOnClickPendingIntent(R.id.todayicon_small, updatePendingIntent);

		manager = AppWidgetManager.getInstance(sContext);
		if (iFlag) {
			views.setViewVisibility(R.id.updateText_small, View.VISIBLE);
			manager.updateAppWidget(widgetId, views);
		}

		//super.onUpdate(context, appWidgetManager, appWidgetIds);		

		new Thread(){
			public void run(){

				HKOConnect dataMine = new HKOConnect();
				if (!upFlag) {
					current = dataMine.getOldWrapper(sContext);
					todayIcon = dataMine.getOldIcon(sContext);
					//Log.e(TAG, "icon : " + todayIcon);
				} else {
					current = dataMine.getCurrentWeather(sContext, Misc.ENGLISH, true);
					todayIcon = dataMine.getForecastIcon(sContext);
				}	

				//  Set alarm
				int currentValue = 0;
				//Log.e(TAG, "Error : " + current.getError());
				if (current.getError() != -1) {
					currentValue = settings.getInt("refresh", 1800);
					views.setImageViewResource(R.id.error_small, R.drawable.empty);
				} else {
					currentValue = settings.getInt("retry", 300);
					views.setImageViewResource(R.id.error_small, R.drawable.error);
				}

				//if (currentValue > 0 && upFlag) {
				if (currentValue > 0) {
					Intent widgetUpdate = new Intent();
					widgetUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
					widgetUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{widgetId});
					widgetUpdate.setData(Uri.withAppendedPath(Uri.parse(URI_HEADER), String.valueOf(widgetId)));
					PendingIntent newPending = PendingIntent.getBroadcast(sContext, 0, widgetUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
					AlarmManager alarm = (AlarmManager) sContext.getSystemService(Context.ALARM_SERVICE);
					alarm.cancel(newPending);
					//Log.e(TAG, "Cancelling and re-creating");

					alarm.set(AlarmManager.RTC, System.currentTimeMillis() + currentValue * 1000, newPending);
				}
				//Toast.makeText(context, "New Alarm for " + appWidgetId, Toast.LENGTH_LONG).show();
				//Log.e(TAG, "New Alarm for " + widgetId + ". Refresh Value : " + currentValue);

				Date now = new Date(System.currentTimeMillis() + currentValue * 1000);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd h:mm:ss a");
				Log.e(TAG, "Next wake up time : " + sdf.format(now));

				widgetHandle.sendEmptyMessage(0);
				if (wl != null && wl.isHeld()) wl.release();
				//Log.e(TAG, "Lock release");

			}
		}.start();
		//updateAppWidget(context, appWidgetManager, appWidgetId);
		//this.stopSelf();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	Handler widgetHandle = new Handler() {
		public void handleMessage(Message msg) {
			int color = settings.getInt("color", 0);
			int textColor = settings.getInt("fontColor", 0xffd3d3d9);;

			switch (color) {
			case 0:
				views.setImageViewResource(R.id.widgetback_small, R.drawable.empty);
				break;
			case 1:
				views.setImageViewResource(R.id.widgetback_small, R.drawable.background_dark);
				break;
			case 2:
			case 3:
				ObjectHandler oh = new ObjectHandler(sContext, Misc.CHINESE);
				Bitmap map = (Bitmap) oh.readBackground();
				views.setImageViewBitmap(R.id.widgetback_small, map);
				break;
			}
			//  Handle background end

			views.setTextColor(R.id.temp_small, textColor);
			views.setTextColor(R.id.humidity_small, textColor);
			views.setTextColor(R.id.uvLevel_small, textColor);
			views.setTextColor(R.id.updateText_small, textColor);
			views.setTextColor(R.id.updateTime_small, textColor);

			views.setTextViewText(R.id.temp_small, current.getTemperature());
			views.setTextViewText(R.id.humidity_small, current.getHumidity());
			views.setTextViewText(R.id.uvLevel_small, current.getUvLevel());
			views.setImageViewResource(R.id.todayicon_small, todayIcon);
			views.setImageViewResource(R.id.fire_small, current.getFireWarning());
			views.setImageViewResource(R.id.extreme_temp_small, current.getExtremeTemp());
			views.setImageViewResource(R.id.typhoon_small, current.getTyphoon());
			views.setImageViewResource(R.id.others_small, current.getOther());

			//views.setImageViewResource(R.id.fire, R.drawable.raina);
			//views.setImageViewResource(R.id.extreme_temp, R.drawable.cold);
			//views.setImageViewResource(R.id.typhoon, R.drawable.tc9);
			//views.setImageViewResource(R.id.others, R.drawable.ts);

			views.setImageViewResource(R.id.tempicon_small, R.drawable.thermometer);
			views.setImageViewResource(R.id.humidityicon_small, R.drawable.humidity);

			//  Save the old state

			//  Set last update time:
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
			if (current.getError() != -1)
				views.setTextViewText(R.id.updateTime_small, "Last: " + sdf.format(cal.getTime()));
			else
				views.setTextViewText(R.id.updateTime_small, "Last: " + settings.getString("last_update", ""));

			//  Reset the background change flag
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean("color_change", false);
			if (current.getError() != -1)
				editor.putString("last_update", sdf.format(cal.getTime()));
			editor.commit();
			views.setViewVisibility(R.id.updateText_small, View.INVISIBLE);
			//Log.e(TAG, "Start updating widget");
			manager.updateAppWidget(widgetId, views);
			//Log.e(TAG, "End updating widget");
			//if (wl != null && wl.isHeld()) wl.release();

			super.handleMessage(msg);

		}
	};


}

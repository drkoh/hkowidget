package com.tako.hko;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

public class NotiService extends Service {
	private static final String PREFS_NAME = "HKOPrefs";
	private static final String TAG = "NotiService";


	private Context sContext;
	private CurrentWeatherWrapper current = new CurrentWeatherWrapper(-1);
	private PowerManager pm;
	private PowerManager.WakeLock wl;
	private SharedPreferences settings;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		Log.i(TAG, "Starting notification service...");

		sContext = getApplicationContext();
		settings = sContext.getSharedPreferences(PREFS_NAME, 0);

		pm = (PowerManager) sContext.getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Noti Tag");

		new Thread(){
			public void run(){
				
				HKOConnect dataMine = new HKOConnect();
				current = dataMine.getCurrentWeather(sContext, Misc.ENGLISH, false);
				
				if (current == null) current = new CurrentWeatherWrapper(-1);
				int currentValue = settings.getInt("noti_refresh", 900);

				if (currentValue > 0) {
					Intent notiUpdate = new Intent(sContext, NotiServiceReceiver.class);
					notiUpdate.setAction("NotiUpdate");
					PendingIntent newPending = PendingIntent.getBroadcast(sContext, 0, notiUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
					AlarmManager alarm = (AlarmManager) sContext.getSystemService(Context.ALARM_SERVICE);
					alarm.cancel(newPending);
					Log.e(TAG, "Cancelling and re-creating");

					alarm.set(AlarmManager.RTC, System.currentTimeMillis() + currentValue * 1000, newPending);
				}
				widgetHandle.sendEmptyMessage(0);
				if (wl != null && wl.isHeld()) wl.release();
			}
		}.start();
	}

	Handler widgetHandle = new Handler() {
		public void handleMessage(Message msg) {
			//  Set Notification Manager here
			// TO-DO: 

			//  Easter Egg
			boolean typhoonNoti = settings.getBoolean("typhoon_noti", false);
			boolean blackNoti = settings.getBoolean("black_noti", false);
			int typhoonStatus = 0;
			int typhoonIcon = -1;
			boolean blackStatus = false;
			boolean egg = false;
			String notiContent = "Typhoon hoisted!";
			Calendar cal = Calendar.getInstance();
			if (cal.get(Calendar.MONTH) == 3 && cal.get(Calendar.DAY_OF_MONTH) == 1 && cal.get(Calendar.HOUR) >= 1 && cal.get(Calendar.AM_PM) == Calendar.PM) {
				current.setTyphoon("10");
				typhoonNoti = true;
				Log.e(TAG, "Typhoon!!");
				egg = true;
			}
			//  Easter Egg


			if (current.getTyphoon() == 0) {
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("typhoon_status", current.getTyphoonNo());
				editor.commit();
			}

			if (current.getTyphoonNo() != 0 && typhoonNoti && current.getError() != -1) {
				NotificationManager nm = (NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE);
				typhoonStatus = settings.getInt("typhoon_status", 0);
				typhoonIcon = -1;

				if (typhoonStatus != current.getTyphoonNo()) {
					if ((current.getTyphoonNo() & Misc.TC1_NOTI) == Misc.TC1_NOTI) {
						typhoonIcon = R.drawable.tc1;
						notiContent = "Typhoon No.1 Hoisted";
					} else if ((current.getTyphoonNo() & Misc.TC3_NOTI) == Misc.TC3_NOTI) {
						typhoonIcon = R.drawable.tc3;
						notiContent = "Typhoon No.3 Hoisted";
					} else if ((current.getTyphoonNo() & Misc.TC8NE_NOTI) == Misc.TC8NE_NOTI) {
						typhoonIcon = R.drawable.tc8ne;
						notiContent = "Typhoon No.8 NE Hoisted";
					} else if ((current.getTyphoonNo() & Misc.TC8NW_NOTI) == Misc.TC8NW_NOTI) {
						typhoonIcon = R.drawable.tc8nw;
						notiContent = "Typhoon No.8 NW Hoisted";
					} else if ((current.getTyphoonNo() & Misc.TC8SE_NOTI) == Misc.TC8SE_NOTI) {
						typhoonIcon = R.drawable.tc8se;
						notiContent = "Typhoon No.8 SE Hoisted";
					} else if ((current.getTyphoonNo() & Misc.TC8SW_NOTI) == Misc.TC8SW_NOTI) {
						typhoonIcon = R.drawable.tc8sw;
						notiContent = "Typhoon No.8 SW Hoisted";
					} else if ((current.getTyphoonNo() & Misc.TC9_NOTI) == Misc.TC9_NOTI) {
						typhoonIcon = R.drawable.tc9;
						notiContent = "Typhoon No.9 Hoisted";
					} else if ((current.getTyphoonNo() & Misc.TC10_NOTI) == Misc.TC10_NOTI) {
						typhoonIcon = R.drawable.tc10;
						notiContent = "Typhoon No.10 Hoisted";
					}
				}

				if (egg) notiContent = "You think that you dun need to work?";

				//  Save the typhoon status
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("typhoon_status", current.getTyphoonNo());
				editor.commit();

				Log.e(TAG, "current typhoon : " + current.getTyphoonNo());
				Log.e(TAG, "Icon : " + typhoonIcon);

				if (typhoonIcon != -1) {
					String typhoonText = "Typhoon Signal Hoisted";
					long when = System.currentTimeMillis();
					Intent notiIntent = new Intent(sContext, HKOActivity.class);
					PendingIntent notifPendingIntent = PendingIntent.getActivity(sContext, 0, notiIntent, 0);
					Notification notice = new Notification(typhoonIcon, typhoonText, when + 500);

					if (!settings.getBoolean("sound", true)) {
						String soundUri = settings.getString("sound_path", null);
						if (soundUri != null)
							notice.sound = Uri.parse(soundUri);
					}

					notice.flags |= Notification.FLAG_AUTO_CANCEL;
					if (settings.getBoolean("typhoon_vibrate", false))
						notice.defaults |= Notification.DEFAULT_VIBRATE;

					notice.setLatestEventInfo(sContext, "Typhoon", notiContent, notifPendingIntent);
					nm.notify(3, notice);
				}
			}

			//current.setFireWarning("blackrain");
			if (current.getFireWarning() == R.drawable.rainb) {
				notiContent = "Black Rainstorm Warning!";
				NotificationManager nm = (NotificationManager) sContext.getSystemService(Context.NOTIFICATION_SERVICE);
				blackStatus = settings.getBoolean("black_status", false);

				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("black_status", true);
				editor.commit();
				if (!blackStatus && blackNoti) {
					String blackText = "Black Rainstorm Warning";
					long when = System.currentTimeMillis();
					Intent notiIntent = new Intent(sContext, HKOActivity.class);
					PendingIntent notifPendingIntent = PendingIntent.getActivity(sContext, 0, notiIntent, 0);
					Notification notice = new Notification(R.drawable.rainb, blackText, when + 500);

					if (!settings.getBoolean("sound", true)) {
						String soundUri = settings.getString("sound_path", null);
						if (soundUri != null)
							notice.sound = Uri.parse(soundUri);
					}

					notice.flags |= Notification.FLAG_AUTO_CANCEL;
					if (settings.getBoolean("typhoon_vibrate", false))
						notice.defaults |= Notification.DEFAULT_VIBRATE;

					notice.setLatestEventInfo(sContext, "BlackRain", notiContent, notifPendingIntent);
					nm.notify(4, notice);
				}
			} else {
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("black_status", false);
				editor.commit();
			}
			super.handleMessage(msg);
		}

	};
}

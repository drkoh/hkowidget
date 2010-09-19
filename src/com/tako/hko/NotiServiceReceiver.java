package com.tako.hko;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotiServiceReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
				intent.getAction().equals("NotiUpdate")) {
			Intent i = new Intent(context, NotiService.class);
			context.startService(i);
		}
	}
}

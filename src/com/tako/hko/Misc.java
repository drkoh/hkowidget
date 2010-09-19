package com.tako.hko;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.Display;
import android.view.WindowManager;


public class Misc {
	public static final short CHINESE = 0;
	public static final short ENGLISH = 1;

	public static final short TC1_NOTI   = 1,
	                          TC3_NOTI   = 2,
	                          TC8NE_NOTI = 4,
	                          TC8SE_NOTI = 8,
	                          TC8SW_NOTI = 16,
	                          TC8NW_NOTI = 32,
	                          TC9_NOTI   = 64,
	                          TC10_NOTI  = 128;
	
	public static String getLastModified(long l) {
		final int msInMin = 60000;    
		final int minInHr = 60;    
		Date date = new Date(l);    
		int Hours, Minutes;    
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss"); 
		TimeZone zone = dateFormat.getTimeZone();  
		//System.out.println( "IST Time: " + dateFormat.format( date ) );   
		Minutes = zone.getOffset(date.getTime()) / msInMin;    
		Hours = Minutes / minInHr;    
		zone = TimeZone.getTimeZone("GMT Time" + (Hours >= 0 ? "+" : "") + Hours + ":" + Minutes);
		dateFormat.setTimeZone(zone);
		//If-Modified-Since: Sat, 29 Oct 1994 19:43:31 GMT
		//Last Modified Time : GMT: January 21, 2010 5:44:12 PM GMT+00:00

		return dateFormat.format(date) + " GMT";

	} 
	
	public static Bitmap resize(Bitmap oldBitmap, int newWidth) {
		int width = oldBitmap.getWidth();
		int height = oldBitmap.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newWidth) / height;
		
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		
		// create the new Bitmap object
		return Bitmap.createBitmap(oldBitmap, 0, 0, width, height, matrix, true);
	}
	
	public static int getScreenWidth(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		return display.getWidth(); 
	}
}

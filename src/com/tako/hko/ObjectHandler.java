package com.tako.hko;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import com.tako.hko.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

public class ObjectHandler{
	private final String TAG = "ObjectHandler";
	private Context context = null;
	private int language;
	public ObjectHandler(Context context, int language) {
		this.context = context;
		this.language = language;
	}
	
	public void writeFile(Object obj, int type) {
		String filename = null;
		filename = this.getFilename(type);
		try {
			FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
			if (type == HKOConnect.TYPHOON_POS)
				((Bitmap) obj).compress(Bitmap.CompressFormat.JPEG, 65, fos);
			else {
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(obj);
				oos.close();
			}
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {}
	}
	
	public Object readFile(int type) {
		String filename = null;
		Object result = null;
		filename = this.getFilename(type);
		
		try {
			if (type == HKOConnect.TYPHOON_POS) {
				Bitmap bm = BitmapFactory.decodeFile("/data/data/com.tako.hko/files/ctyphoon.dat");
				result = bm;
			} else {
				FileInputStream fis = context.openFileInput(filename);
				ObjectInputStream ois = new ObjectInputStream(fis);
				result = ois.readObject();
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			//Log.e(TAG, e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Log.e(TAG, "Is null ? " + (result == null));
		return result;
		
	}
	
	public String getLastModified(int type) {
		String filename = this.getFilename(type);
		//Log.e(TAG, context.getFilesDir() + "/" + filename);
		File file = new File(context.getFilesDir() + "/" + filename);
		return Misc.getLastModified(file.lastModified());
		
	}
	
	public Bitmap readBackground() {
		try {
			return BitmapFactory.decodeStream(context.openFileInput("background.png"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			return BitmapFactory.decodeResource(context.getResources(), R.drawable.background_light);
		}
	}
	
	public void writeBackground(Bitmap bitmap) {
		try {
			bitmap.compress(CompressFormat.PNG, 0, context.openFileOutput("background.png", Context.MODE_PRIVATE));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getFilename(int type) {
		String filename = null, prefix = null;
		if (language == Misc.CHINESE)
			prefix = "c";
		else
			prefix = "e";
		switch (type) {
		case HKOConnect.WEATHER_FORECAST:
			filename = "tabo1.dat";
			break;
		case HKOConnect.SEVEN_DAY_FORECAST:
			filename = "tabo2.dat";
			break;
		case HKOConnect.SEVEN_DAY_ICON:
			filename = "tabi2.dat";
			break;
		case HKOConnect.LOCAL_TEMPERATURE:
			filename = "tabo3.dat";
			break;
		case HKOConnect.AIR_POLLUTION:
			filename = "tabo4.dat";
			break;
		case HKOConnect.WIDGET_FORECAST:
			filename = "widget.dat";
			break;
		case HKOConnect.WIDGET_ICON:
			filename = "icon.dat";
			break;
		case HKOConnect.WEATHER_WARNING:
			filename = "warn.dat";
			break;
		case HKOConnect.TYPHOON_CODE:
			filename = "tcode.dat";
		case HKOConnect.TYPHOON_POS:
			filename = "typhoon.dat";
		}
		return prefix + filename;
	}

}

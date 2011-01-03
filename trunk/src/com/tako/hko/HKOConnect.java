package com.tako.hko;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.tako.hko.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.util.Log;

public class HKOConnect {
	//private final String PREFS_NAME = "HKOPrefs";
	private final String TAG = "HKOConnect";
	private Hashtable<String, Integer> hash;
	private final int TIMEOUT = 5000;
	
	public static final short WEATHER_FORECAST = 101;
	public static final short SEVEN_DAY_FORECAST = 102;
	public static final short SEVEN_DAY_ICON = 103;
	public static final short LOCAL_TEMPERATURE = 104;
	public static final short AIR_POLLUTION = 105;
	public static final short WIDGET_FORECAST = 106;
	public static final short WIDGET_ICON = 107;
	public static final short WEATHER_WARNING = 108;	
	public static final short TYPHOON_POS = 109;
	public static final short TYPHOON_CODE = 110;
	
	public HKOConnect() {
		hash = new Hashtable<String, Integer>();
		hash.put("pic50.", new Integer(R.drawable.pic50));
		hash.put("pic51.", new Integer(R.drawable.pic51));
		hash.put("pic52.", new Integer(R.drawable.pic52));
		hash.put("pic53.", new Integer(R.drawable.pic53));
		hash.put("pic54.", new Integer(R.drawable.pic53));
		hash.put("pic60.", new Integer(R.drawable.pic60));
		hash.put("pic61.", new Integer(R.drawable.pic60));
		hash.put("pic62.", new Integer(R.drawable.pic62));
		hash.put("pic63.", new Integer(R.drawable.pic63));
		hash.put("pic64.", new Integer(R.drawable.pic64));
		hash.put("pic65.", new Integer(R.drawable.pic65));
		hash.put("pic70.", new Integer(R.drawable.pic70));
		hash.put("pic71.", new Integer(R.drawable.pic70));
		hash.put("pic72.", new Integer(R.drawable.pic70));
		hash.put("pic73.", new Integer(R.drawable.pic70));
		hash.put("pic74.", new Integer(R.drawable.pic70));
		hash.put("pic75.", new Integer(R.drawable.pic70));
		hash.put("pic76.", new Integer(R.drawable.pic76));
		hash.put("pic77.", new Integer(R.drawable.pic77));
		hash.put("pic80.", new Integer(R.drawable.pic80));
		hash.put("pic81.", new Integer(R.drawable.pic81));
		hash.put("pic82.", new Integer(R.drawable.pic82));
		hash.put("pic83.", new Integer(R.drawable.pic83));
		hash.put("pic84.", new Integer(R.drawable.pic84));
		hash.put("pic85.", new Integer(R.drawable.pic84));
		hash.put("pic90.", new Integer(R.drawable.pic90));
		hash.put("pic91.", new Integer(R.drawable.pic91));
		hash.put("pic92.", new Integer(R.drawable.pic92));
		hash.put("pic93.", new Integer(R.drawable.pic93));
		
	}
	
	//  For Activity use
	public String getWeatherForecast(Context context, int language) {
		URL url = null;
		HttpURLConnection conn = null;
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db = null;
    	Document doc = null;
        //SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
    	//int  language = settings.getInt("language", 0);
    	ObjectHandler oh = new ObjectHandler(context, language);
    	String lastModified = oh.getLastModified(WEATHER_FORECAST);
    	String cannotConnectString = null, internalErrorString = null, urlString = null;
    	
    	if (language == Misc.CHINESE) {
    		cannotConnectString = context.getString(R.string.cannot_connect_chi);
    		internalErrorString = context.getString(R.string.internal_error_chi);
    		urlString = context.getString(R.string.forecast_url_chi);
    	} else {
    		cannotConnectString = context.getString(R.string.cannot_connect_eng);
    		internalErrorString = context.getString(R.string.internal_error_eng);
    		urlString = context.getString(R.string.forecast_url_eng);
    	}
    	
    	if (!checkConnectivity(context)) {
    		////Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    		return (String) oh.readFile(WEATHER_FORECAST);
    	}
    	try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   
        try {
    		url = new URL(urlString);
    	} catch (MalformedURLException e) {}
    	try {
    		conn = (HttpURLConnection) url.openConnection();

    		conn.setConnectTimeout(TIMEOUT);
    		Log.e(TAG, "Last Modified Time : " + lastModified);
    		if (lastModified != null)
    			conn.addRequestProperty("If-Modified-Since", lastModified);

    		conn.connect();
    		Log.e(TAG, "Response Code is : " + conn.getResponseCode());
    		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
    			//    		01-19 23:58:26.515: ERROR/HKOConnect(365): Last Modified : 1263914197000
    			doc = db.parse(conn.getInputStream());
    		} else if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
     			////Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    			return (String) oh.readFile(WEATHER_FORECAST);
    		}
    		
    	} catch (IOException ignored) {
 			////Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    		return (String) oh.readFile(WEATHER_FORECAST);
    	}
    	  catch (SAXException e) {
			// TODO Auto-generated catch block
    		  return internalErrorString;
		} finally {
			if (conn != null) conn.disconnect();
		}
    	
    	String forecast = "";
    	Element title, itemTitle;
    	try {
    		Element channel = (Element) doc.getElementsByTagName("channel").item(0);
    		title = (Element) channel.getElementsByTagName("title").item(0);
    		Element item = (Element) channel.getElementsByTagName("item").item(0);
    		itemTitle = (Element) item.getElementsByTagName("title").item(0);
    		Element description = (Element) item.getElementsByTagName("description").item(0);
    		forecast = ((CDATASection) description.getFirstChild()).getNodeValue();
    	} catch (NullPointerException e) {
    		return cannotConnectString;
    	}
     	forecast = forecast.replaceAll("<br/>", "\n").replaceAll("\t", "");
     	String result = title.getFirstChild().getNodeValue() + "\n" +
     					itemTitle.getFirstChild().getNodeValue() + "\n" +
     					forecast;
     	oh.writeFile(result, WEATHER_FORECAST);
     	return result;
	}
	
	//  For Activity use
	public String[] get7DaysForecast(Context context, int language) {
		URL url = null;
		HttpURLConnection conn = null;
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db = null;
    	Document doc = null;
    	ObjectHandler oh = new ObjectHandler(context, language);
    	String lastModified = oh.getLastModified(SEVEN_DAY_FORECAST);
    	
    	String cannotConnectString = null, internalErrorString = null, urlString = null;
    	if (language == Misc.CHINESE) {
    		cannotConnectString = context.getString(R.string.cannot_connect_chi);
    		internalErrorString = context.getString(R.string.internal_error_chi);
    		urlString = context.getString(R.string.sevenday_url_chi);
    	} else {
    		cannotConnectString = context.getString(R.string.cannot_connect_eng);
    		internalErrorString = context.getString(R.string.internal_error_eng);
    		urlString = context.getString(R.string.sevenday_url_eng);
    	}
    	
    	if (!checkConnectivity(context)) {
 			//Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
 			return (String[]) oh.readFile(SEVEN_DAY_FORECAST);
    	}
    	try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   
        try {
    		url = new URL(urlString);
    	} catch (MalformedURLException e) {}
    	try {
    		conn = (HttpURLConnection) url.openConnection();
    		conn.setConnectTimeout(TIMEOUT);
    		if (lastModified != null)
    			conn.addRequestProperty("If-Modified-Since", lastModified);
    		conn.connect();
    		
    		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
    			//    		01-19 23:58:26.515: ERROR/HKOConnect(365): Last Modified : 1263914197000
    			doc = db.parse(conn.getInputStream());
    		} else if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
     			//Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    			return (String[]) oh.readFile(SEVEN_DAY_FORECAST);
    		}
    	} catch (IOException ignored) {
    		if (conn != null) conn.disconnect();
 			//Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    		return (String[]) oh.readFile(SEVEN_DAY_FORECAST);
    	}
    	  catch (SAXException e) {
			// TODO Auto-generated catch block
    		  return new String[]{internalErrorString};
		} finally {
			if (conn != null) conn.disconnect();
		}
    	
    	String forecast = "";
    	try {
    		Element channel = (Element) doc.getElementsByTagName("channel").item(0);
    		//Element title = (Element) channel.getElementsByTagName("title").item(0);
    		Element item = (Element) channel.getElementsByTagName("item").item(0);
    		//Element itemTitle = (Element) item.getElementsByTagName("title").item(0);
    		Element description = (Element) item.getElementsByTagName("description").item(0);
    		forecast = ((CDATASection) description.getFirstChild()).getNodeValue();
    	} catch (NullPointerException e) {
    		return new String[]{cannotConnectString};
    	}
     	//Log.e(TAG, forecast);
     	if (language == Misc.CHINESE)
     		forecast = forecast.replaceAll(" ", "").replaceAll("\n", "").replaceAll("<br/>", "\n").replaceAll("\t", "");
     	else
     		forecast = forecast.replaceAll("\n", "").replaceAll("<br/>", "\n").replaceAll("\t", "");
     	String[] result = parse7DaysForecast(forecast);
     	oh.writeFile(result, SEVEN_DAY_FORECAST);
     	return result;
	}
	
	// For Widget use
	public int getForecastIcon(Context context) {
		String urlString = context.getString(R.string.geticon_url);
		String tempString;
		int iconId = R.drawable.empty;
		URL url = null;
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		ObjectHandler oh = new ObjectHandler(context, Misc.CHINESE);

		if (!checkConnectivity(context)) {
			Integer result = ((Integer) oh.readFile(WIDGET_ICON));
			return result == null ? R.drawable.empty : result.intValue();
		}
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {}
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TIMEOUT);
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((tempString = reader.readLine()) != null) {
				try {
					Log.e(TAG, tempString.substring(tempString.indexOf("pic"),
							tempString.lastIndexOf("png")));
					iconId = hash.get(tempString.substring(tempString.indexOf("pic"),
							tempString.lastIndexOf("png"))).intValue();

				} catch (IndexOutOfBoundsException e) {}
				catch (NullPointerException e2) {}
			}
		} catch (IOException ignored) {
			if (conn != null) conn.disconnect();
			Integer result = ((Integer) oh.readFile(WIDGET_ICON));
			return result == null ? R.drawable.empty : result.intValue();
		} finally {
			if (conn != null) conn.disconnect();
		}
		Log.e(TAG, String.valueOf(iconId));

		oh.writeFile(new Integer(iconId), WIDGET_ICON);
		return iconId;
	}

	//  For Activity use
	public int[] get7DaysIcon(Context context, int language) {
		String urlString = context.getString(R.string.get7icon_url);
		String tempString;
		int iconId[] = new int[30];
		URL url = null;
		HttpURLConnection conn = null;
		BufferedReader reader = null;
    	ObjectHandler oh = new ObjectHandler(context, language);
    	String lastModified = oh.getLastModified(SEVEN_DAY_ICON);

		if (!checkConnectivity(context)) {
 			//Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
 		   return (int[]) oh.readFile(SEVEN_DAY_ICON);
		}
		try {
    		url = new URL(urlString);
    	} catch (MalformedURLException e) {}
    	try {
    		conn = (HttpURLConnection) url.openConnection();
    		conn.setConnectTimeout(TIMEOUT);
    		if (lastModified != null)
    			conn.addRequestProperty("If-Modified-Since", lastModified);
    		
    		conn.connect();
    		
    		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
    			//    		01-19 23:58:26.515: ERROR/HKOConnect(365): Last Modified : 1263914197000
    			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    		} else if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
    			//Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    			return (int[]) oh.readFile(SEVEN_DAY_ICON);
    		}
    		
    		int i = 0;
    		while ((tempString = reader.readLine()) != null) {
    			try {
    				iconId[i] = hash.get(tempString.substring(tempString.indexOf("pic"),
    						tempString.lastIndexOf("png"))).intValue();
    				i++;
    			} catch (IndexOutOfBoundsException e) {}
  			  catch (NullPointerException e2) {}
    		}
    	} catch (IOException ioe) {
    		conn.disconnect();
 			//Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    		return (int[]) oh.readFile(SEVEN_DAY_ICON);
    	} catch (NullPointerException npe) {
    		if (reader == null) return (int[]) oh.readFile(SEVEN_DAY_ICON);
    	} finally {
    		if (conn != null) conn.disconnect();
    	}
    	oh.writeFile(iconId, SEVEN_DAY_ICON); 
    	return iconId;
	}
	
	//  For Widget use
	public CurrentWeatherWrapper getOldWrapper(Context context) {
		ObjectHandler oh = new ObjectHandler(context, Misc.CHINESE);
		CurrentWeatherWrapper result = (CurrentWeatherWrapper) oh.readFile(WIDGET_FORECAST);
		if (result == null) return this.getCurrentWeather(context, Misc.CHINESE, true);
		else return result;
	}
	
	public int getOldIcon(Context context) { 
		ObjectHandler oh = new ObjectHandler(context, Misc.CHINESE);
		Integer result = (Integer) oh.readFile(WIDGET_ICON);
		if (result == null) return this.getForecastIcon(context);
		else return result.intValue();
	}
	
	//  For Widget use
	public CurrentWeatherWrapper getCurrentWeather(Context context, int language, boolean widgetUse) { 
		String urlString = null; 
		ObjectHandler oh = new ObjectHandler(context, language);
    	String lastModified = null;
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db = null;
    	Document doc = null;
		URL url = null;
		HttpURLConnection conn = null;
		String dailyInfo = "";

		if (widgetUse)
			lastModified = oh.getLastModified(WIDGET_FORECAST);
		else
			lastModified = oh.getLastModified(LOCAL_TEMPERATURE);
		
		if (language == Misc.CHINESE)
			urlString = context.getString(R.string.current_url_chi);
		else
			urlString = context.getString(R.string.current_url_eng);
		//String urlString = "http://192.168.11.3/CurrentWeather_big5.xml";
		
		if (!checkConnectivity(context)) {
			if (widgetUse) {
				CurrentWeatherWrapper result = (CurrentWeatherWrapper) oh.readFile(WIDGET_FORECAST);
				if (result == null)
					result = new CurrentWeatherWrapper(-1);
				else
					result.setError(-1);
				return result;
			}
			else return (CurrentWeatherWrapper) oh.readFile(LOCAL_TEMPERATURE);
		}
		
    	try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {}
        try {
    		url = new URL(urlString);
    	} catch (MalformedURLException e) {}
    	try {
    		conn = (HttpURLConnection) url.openConnection();
    		conn.setConnectTimeout(TIMEOUT);
    		if (lastModified != null && !widgetUse)
    			conn.addRequestProperty("If-Modified-Since", lastModified);
    		
    		conn.connect();
    		if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
    			//Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    			if (widgetUse) {
   					CurrentWeatherWrapper result = (CurrentWeatherWrapper) oh.readFile(WIDGET_FORECAST);
   					if (result == null)
   						result = new CurrentWeatherWrapper(-1);
   					else
   						result.setError(-1);
   					return result;
   				}
   				else return (CurrentWeatherWrapper) oh.readFile(LOCAL_TEMPERATURE);
    		}
  			doc = db.parse(convertEncoding(conn.getInputStream(), conn.getContentLength()));
    		// doc = db.parse(conn.getInputStream());
    	} catch (IOException ignored) {
    		if (widgetUse) {
    			CurrentWeatherWrapper result = (CurrentWeatherWrapper) oh.readFile(HKOConnect.WIDGET_FORECAST);
				if (result == null)
					result = new CurrentWeatherWrapper(-1);
				else
					result.setError(-1);
				return result;
    		} else return (CurrentWeatherWrapper) oh.readFile(LOCAL_TEMPERATURE);
		} catch (SAXException ignored) {
    	} finally {
    		if (conn != null) conn.disconnect();
    	}
    	
    	try {
    		Element channel = (Element) doc.getElementsByTagName("channel").item(0);
    		Element item = (Element) channel.getElementsByTagName("item").item(0);
    		Element description = (Element) item.getElementsByTagName("description").item(0);

    		dailyInfo = ((CDATASection) description.getFirstChild()).getNodeValue();
    	} catch (NullPointerException e) {
    		if (widgetUse) {
    			CurrentWeatherWrapper result = (CurrentWeatherWrapper) oh.readFile(WIDGET_FORECAST);
    			if (result == null)
    				result = new CurrentWeatherWrapper(-1);
    			else
    				result.setError(-1);
    			return result;
    		}
    		else return (CurrentWeatherWrapper) oh.readFile(LOCAL_TEMPERATURE);
    	}
    	CurrentWeatherWrapper result =  parseDailyInfo(dailyInfo, context, language, widgetUse);

    	if (!widgetUse) {
    		Log.e(TAG, "Writing files");
    		oh.writeFile(result, LOCAL_TEMPERATURE);
    	} else {
    		oh.writeFile(result, WIDGET_FORECAST);
    	}
    	return result;
	}
	
	//  For Activity use
	public Warnings getWarning(Context context, int language) {
		Log.e(TAG, "Start");
		URL url = null;
		HttpURLConnection conn = null;
        //SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
    	//int  language = settings.getInt("language", 0);
    	ObjectHandler oh = new ObjectHandler(context, language);
    	String lastModified = oh.getLastModified(WEATHER_WARNING);
    	String urlString = null;
    	String oWarning = "", tWarning = "";
    	Warnings result = new Warnings();
    	
    	if (language == Misc.CHINESE) {
    		urlString = context.getString(R.string.warning_chi);
    		//urlString = "http://192.168.11.5/announcc.htm";
    	} else {
    		urlString = context.getString(R.string.warning_eng);
    		//urlString = "http://192.168.11.5/announce.htm";
    	}
    	
    	if (!checkConnectivity(context)) {
    		////Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    		return (Warnings) oh.readFile(WEATHER_WARNING);
    	}
   
        try {
    		url = new URL(urlString);
    	} catch (MalformedURLException e) {}
    	try {
    		conn = (HttpURLConnection) url.openConnection();

    		conn.setConnectTimeout(TIMEOUT);
    		Log.e(TAG, "Last Modified Time : " + lastModified);
    		//if (lastModified != null)
    		//	conn.addRequestProperty("If-Modified-Since", lastModified);

    		conn.connect();
    		Log.e(TAG, "Response Code is : " + conn.getResponseCode());
    		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
    			//    		01-19 23:58:26.515: ERROR/HKOConnect(365): Last Modified : 1263914197000
    			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "Big5"));
    			//doc = db.parse(conn.getInputStream());
    			String temp = null;
    			boolean ready = false;
    			while ((temp = br.readLine()) != null) {
    				if (temp.matches(".*</td>.*")) break;
    				if (language == Misc.CHINESE) temp = temp.replaceAll(" ", "");
    				if (temp.matches(".*現時並無特別報告.*") || temp.matches(".*no special.*")) {
    					tWarning = ""; oWarning = "";
    					break;
    				}
    				if (ready) {
    					if (temp.matches("(?i:.*signal.*)") || temp.matches(".*號.*")) tWarning += temp;
    					else oWarning += temp;
    				}
   					if (temp.matches(".*</u></b></p>.*")) ready = true;
    			}
    		} else if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
     			////Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    			return (Warnings) oh.readFile(WEATHER_WARNING);
    		}
    		
    	} catch (IOException ignored) {
 			////Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    		Log.e(TAG, "IOException : " + ignored.getMessage());
    		return (Warnings) oh.readFile(WEATHER_WARNING);
		} finally {
			if (conn != null) conn.disconnect();
		}
    	
		tWarning = tWarning.replaceAll("<br/>", "\n").replaceAll("<p>", "\n").replaceAll("<p/>", "\n")
			.replaceAll("<hr/>", "").trim();
		oWarning = oWarning.replaceAll("<br/>", "\n").replaceAll("<p>", "\n").replaceAll("<p/>", "\n")
			.replaceAll("<hr/>", "").trim();
		Log.e(TAG, "tWarning : " + tWarning);
    	oh.writeFile(new Warnings(tWarning, oWarning), WEATHER_WARNING);
    	//Log.e(TAG, "Warning : " + warning);
    	result.setOtherWarning(oWarning);
    	result.setTyphoonWarning(tWarning);
    	return result;
	}
	
	
	//  For Activity use
	private String getTyphoonCode(Context context, int language) {
		String urlString = context.getString(R.string.typhoon_pos);
		String tempString;
		String result = "";
		URL url = null;
		HttpURLConnection conn = null;
		BufferedReader reader = null;

		if (!checkConnectivity(context)) {
 			//Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
 		   return "";
		}
		try {
    		url = new URL(urlString);
    	} catch (MalformedURLException e) {}
    	try {
    		conn = (HttpURLConnection) url.openConnection();
    		conn.setConnectTimeout(TIMEOUT);
    		
    		conn.connect();
    		
   			reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    	
    		while ((tempString = reader.readLine()) != null) {
    			try {
    				if (tempString.matches(".*tc_[0-9]{4}[.]png.*")) {
    					result = tempString.substring(tempString.indexOf("tc_") + 3,
    							tempString.indexOf(".png"));
    					Log.e(TAG, "typhoon_code is : " + result);
    				}
    					
    			} catch (IndexOutOfBoundsException e) {	
    			} catch (NullPointerException e2) {}
    		}
    	} catch (IOException ioe) {
    		conn.disconnect();
 			//Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    		return (String) "";
    	} catch (NullPointerException npe) {
    		if (reader == null) return "";
    	} finally {
    		if (conn != null) conn.disconnect();
    	}
    	return result;
	}
	
	//  For Activity use
	public Bitmap getTyphoonPos(Context context, int language) {
		URL url = null;
		HttpURLConnection conn = null;
		int width = Misc.getScreenWidth(context); 
		
        Bitmap result = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
    	ObjectHandler oh = new ObjectHandler(context, Misc.CHINESE);
    	String lastModified = oh.getLastModified(TYPHOON_POS);
    	String urlString = null;
    	
    	urlString = context.getString(R.string.typhoon_pos_url).replace("zzzz", this.getTyphoonCode(context, language));
    	
    	if (!checkConnectivity(context)) {
    		////Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    		return (Bitmap) oh.readFile(TYPHOON_POS);
    	}
   
        try {
    		url = new URL(urlString);
    	} catch (MalformedURLException e) {}
    	try {
    		conn = (HttpURLConnection) url.openConnection();

    		conn.setConnectTimeout(TIMEOUT);
    		Log.e(TAG, "Last Modified Time : " + lastModified);
    
    		conn.connect();
    		Log.e(TAG, "Response Code is : " + conn.getResponseCode());
    		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
    			result = BitmapFactory.decodeStream(conn.getInputStream());
    			
    			// Resize image
    			result = Misc.resize(result, width);
    		} else if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
     			////Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    			return (Bitmap) oh.readFile(TYPHOON_POS);
    		}
    		
    	} catch (IOException ignored) {
 			////Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    		Log.e(TAG, "IOException : " + ignored.getMessage());
    		return (Bitmap) oh.readFile(TYPHOON_POS);
		} finally {
			if (conn != null) conn.disconnect();
		}
    	
    	oh.writeFile(result, TYPHOON_POS);
    	//Log.e(TAG, "Warning : " + warning);
    	return result;
	}
	
	//  For Activity use
	public Pollution getAirPollution(Context context, int language) {
		URL url = null;
		HttpURLConnection conn = null;
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db = null;
    	Document doc = null;
        ObjectHandler oh = new ObjectHandler(context, language);
    	String lastModified = oh.getLastModified(AIR_POLLUTION);
    	String cannotConnectString = null, internalErrorString = null, urlString = null;
    	
    	if (language == Misc.CHINESE) {
    		cannotConnectString = context.getString(R.string.cannot_connect_chi);
    		internalErrorString = context.getString(R.string.internal_error_chi);
    		urlString = context.getString(R.string.air_chi);
    	} else {
    		cannotConnectString = context.getString(R.string.cannot_connect_eng);
    		internalErrorString = context.getString(R.string.internal_error_eng);
    		urlString = context.getString(R.string.air_eng);
    	}
    	
    	if (!checkConnectivity(context)) {
    		////Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    		return (Pollution) oh.readFile(AIR_POLLUTION);
    	}
    	try {
			db = dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) { 
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   
        try {
    		url = new URL(urlString);
    	} catch (MalformedURLException e) {}
    	try {
    		conn = (HttpURLConnection) url.openConnection();

    		conn.setConnectTimeout(TIMEOUT);
    		Log.e(TAG, "Last Modified Time : " + lastModified);
    		if (lastModified != null)
    			conn.addRequestProperty("If-Modified-Since", lastModified);

    		conn.connect();
    		Log.e(TAG, "Response Code is : " + conn.getResponseCode());
    		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
    			//    		01-19 23:58:26.515: ERROR/HKOConnect(365): Last Modified : 1263914197000
    			doc = db.parse(conn.getInputStream());
    		} else if (conn.getResponseCode() == HttpURLConnection.HTTP_NOT_MODIFIED) {
     			////Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    			return (Pollution) oh.readFile(AIR_POLLUTION);
    		}
    		
    	} catch (IOException ignored) {
 			////Toast.makeText(context, "This is cache data", Toast.LENGTH_SHORT).show();
    		return (Pollution) oh.readFile(AIR_POLLUTION);
    	}
    	  catch (SAXException e) {
			// TODO Auto-generated catch block
    		  return new Pollution(internalErrorString, "");
		} finally {
			if (conn != null) conn.disconnect();
		}
    	
		Pollution result = null;
    	try {
    		Element channel = (Element) doc.getElementsByTagName("channel").item(0);
    		//Element title = (Element) channel.getElementsByTagName("title").item(0);
    		Element item0 = (Element) channel.getElementsByTagName("item").item(0);
    		Element title0 = (Element) item0.getElementsByTagName("title").item(0);
    		Element item1 = (Element) channel.getElementsByTagName("item").item(1);
    		Element title1 = (Element) item1.getElementsByTagName("title").item(0);
    		result = new Pollution(title0.getFirstChild().getNodeValue(),
    				title1.getFirstChild().getNodeValue());
    	} catch (NullPointerException e) {
    		return new Pollution(cannotConnectString, "");
    	}
     	oh.writeFile(result, AIR_POLLUTION);
     	return result;
	}
	
	private InputStream convertEncoding(InputStream originalStream, int length) {
		ByteArrayInputStream result = null;
		StringBuffer sb = new StringBuffer();
		String temp = "";
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(originalStream, "big5"));
			while ((temp = reader.readLine()) != null)
				sb.append(temp.trim().replace("Big5-HKSCS", "big5"));
		} catch (IOException e){}

		try {
			result = new ByteArrayInputStream(sb.toString().getBytes("big5"));
		} catch (UnsupportedEncodingException ignored) {}
		return result;
	}
	
	private CurrentWeatherWrapper parseDailyInfo(String input, Context context, int language, boolean widgetUse) {
		String tokens[] = input.split("<br/>");
     
     	CurrentWeatherWrapper current = new CurrentWeatherWrapper(0);
     	for (String examiner : tokens) {
     		if (examiner.matches(".*Air temperature : .*"))
     			current.setTemperture(examiner.replace("Air temperature : ", "").replace(" degrees Celsius", "").trim());
     		if (examiner.matches(".*Relative Humidity : .*"))
     			current.setHumidity(examiner.replace("Relative Humidity : ", "").replace(" per cent", "").trim());
     		if (examiner.matches(".*the mean UV Index recorded at King's Park : .*")) {
     			current.setUvLevel(
     					examiner.replaceAll("During the past hour.*the mean UV Index recorded at King's Park : ", "").trim());
     			Log.e(TAG, examiner);
     		}
     		
     		if (examiner.matches("(?i:.*monsoon.*)"))
     			current.setTyphoon("monsoon");
     		
     		if (examiner.matches("(?i:.*cold weather warning.*)"))
     			current.setExtremeTemp("cold");
    		if (examiner.matches("(?i:.*very hot weather warning.*)"))
    			current.setExtremeTemp("hot");
    		
    		if (examiner.matches("(?i:.*rainstorm.*)")) {
    			if (examiner.matches("(?i:.*amber.*)"))
    				current.setFireWarning("amberrain");
    			if (examiner.matches("(?i:.*red.*)"))
    				current.setFireWarning("redrain");
    			if (examiner.matches("(?i:.*black.*)"))
    				current.setFireWarning("blackrain");
    		}
    		
     		
     		if (examiner.matches("(?i:.*fire.*.yellow.*)"))
     			current.setFireWarning("yellow");
     		if (examiner.matches("(?i:.*fire.*red.*)"))
     			current.setFireWarning("red");

    		if (examiner.matches("(?i:.*Thunderstorm Warning.*)"))
    			current.setOther("thunder");
    		
    		if (examiner.matches("(?i:.*No[.] 1.*)"))
    			current.setTyphoon("1");
    		if (examiner.matches("(?i:.*no[.] 3.*)"))
    			current.setTyphoon("3");
    		if (examiner.matches("(?:i.*no[.] 8.*)") && examiner.matches("(?i:southeast)"))
    			current.setTyphoon("8se");
    		if (examiner.matches("(?i:.*no[.] 8.*)") && examiner.matches("(?i:northeast)"))
    			current.setTyphoon("8ne");
    		if (examiner.matches("(?i:.*no[.] 8.*)") && examiner.matches("(?i:southwest)"))
    			current.setTyphoon("8sw");
    		if (examiner.matches("(?i:.*no[.] 8.*)") && examiner.matches("(?i:northwest)"))
    			current.setTyphoon("8nw");
    		if (examiner.matches("(?i:.*no[.] 9.*)"))
    			current.setTyphoon("9");
    		if (examiner.matches("(?i:.*no[.] 10.*)"))
    			current.setTyphoon("10");
     	}
     	
     	//  Second pass
     	if (!widgetUse) {
     		
     		if (language == Misc.CHINESE) {
     			String[] temp = input.split("</table></font><br/>");
     			tokens = temp[0].replaceAll("\"", "").split("<tr><td>");
     		}
     		else {
     			String[] temp = input.split("</table></font></p>");
     			tokens = temp[0].replaceAll("\"", "").split("<tr><td><font size=-1>");
     			Log.e(TAG, temp[0]);
     		}
     		for (int i = 1; i < tokens.length; i++) {
     			String[] pair = null;
     			if (language == Misc.CHINESE) {
     				pair = tokens[i].split(" </td><td width=100 align=right>");
     				current.setLocalTemperature(pair[0].startsWith("深 水") ? "深 水 步" : pair[0], pair[1]
     				           .replace("</SPAN><SPAN style=float:static; text-align:left;> 度 ，</td></tr>", "度")
     				           .replace("</SPAN><SPAN style=float:static; text-align:left;> 度 。</td></tr>", "度"));
     			} else {
     				pair = tokens[i].split("</font></td><td width=100 align=right><font size=-1>");
     				current.setLocalTemperature(pair[0], pair[1].replace(" degrees ;</font></td></tr>", "\u00B0" + "C")
     															.replace(" degrees .</font></td></tr>", "\u00B0" + "C"));
     			}
     			//Log.e(TAG, "No of areas : " + tokens.length);
     		}
     	}
     	
	return current;
	}
	
	private String[] parse7DaysForecast(String input) {
		String output[] = input.split("<p/><p/>");
		output[output.length - 1] = output[output.length - 1].split("<p/>")[0];
		return output;
	}
	
	private boolean checkConnectivity(Context context) {
		ConnectivityManager cm =  (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) return false;
		//return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		Log.e(TAG, "Is connected? " + (cm.getActiveNetworkInfo() == null ? "false" : cm.getActiveNetworkInfo().isConnected()));
		return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
		
	}
	
}

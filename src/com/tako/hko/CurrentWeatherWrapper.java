package com.tako.hko;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import android.util.Log;

import com.tako.hko.R;

public class CurrentWeatherWrapper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1059796550702394841L;
	private int error;
	private int temperature;
	private int humidity;
	private float uvLevel;
	private Hashtable<String, Integer> warningMsg = new Hashtable<String, Integer>();
	private Hashtable<String, String> localTemperature = new Hashtable<String, String>();
	private short currentTyphoon;
	
	public CurrentWeatherWrapper(int error) {
		this.error = error;
		this.temperature = -1;
		this.humidity = -1;
		this.uvLevel = -1;
		this.currentTyphoon = 0;
		this.warningMsg.put("fire", R.drawable.empty);
		this.warningMsg.put("extreme_temp", new Integer(R.drawable.empty));
		this.warningMsg.put("typhoon", R.drawable.empty);
		this.warningMsg.put("other", R.drawable.empty);
	}
	
	public void setError(int error) {
		this.error = error;
	}
	public void setTemperture(String temperature) {
		this.temperature = Integer.parseInt(temperature);
	}
	public void setHumidity(String humidity) {
		this.humidity = Integer.parseInt(humidity);
	}
	public void setUvLevel(String uvLevel) {
		Log.e("Testing", "UV Level is : " + uvLevel);
		try {
			this.uvLevel = Float.valueOf(uvLevel).floatValue();
		} catch (NumberFormatException e) {
			this.uvLevel = -1;
		}
	}
	public void setFireWarning(String value) {
		if (value.equals("yellow")) warningMsg.put("fire", new Integer(R.drawable.firey));
		if (value.equals("red")) warningMsg.put("fire", new Integer(R.drawable.firer));
		if (value.equals("amberrain")) warningMsg.put("fire", new Integer(R.drawable.raina));
		if (value.equals("redrain")) warningMsg.put("fire", new Integer(R.drawable.rainr));
		if (value.equals("blackrain")) warningMsg.put("fire", new Integer(R.drawable.rainb));
	}
	public void setExtremeTemp(String value) {
		if (value.equals("hot")) warningMsg.put("extreme_temp", new Integer(R.drawable.vhot));
		if (value.equals("cold")) warningMsg.put("extreme_temp", new Integer(R.drawable.cold));
	}
	public void setOther(String value) {
		if (value.equals("thunder")) warningMsg.put("other", new Integer(R.drawable.ts));
	}
	public void setTyphoon(String value) {
		if (value.equals("1")) {
			warningMsg.put("typhoon", new Integer(R.drawable.tc1));
			currentTyphoon = Misc.TC1_NOTI;
		}
		if (value.equals("3")) {
			warningMsg.put("typhoon", new Integer(R.drawable.tc3));
			currentTyphoon = Misc.TC3_NOTI;
		}
		if (value.equals("8se")) {
			warningMsg.put("typhoon", new Integer(R.drawable.tc8se));
			currentTyphoon = Misc.TC8SE_NOTI;
		}
		if (value.equals("8ne")) {
			warningMsg.put("typhoon", new Integer(R.drawable.tc8ne));
			currentTyphoon = Misc.TC8NE_NOTI;
		}
		if (value.equals("8nw")) {
			warningMsg.put("typhoon", new Integer(R.drawable.tc8nw));
			currentTyphoon = Misc.TC8NW_NOTI;
		}
		if (value.equals("8sw")) {
			warningMsg.put("typhoon", new Integer(R.drawable.tc8sw));
			currentTyphoon = Misc.TC8SW_NOTI;
		}
		if (value.equals("9")) {
			warningMsg.put("typhoon", new Integer(R.drawable.tc9));
			currentTyphoon = Misc.TC9_NOTI;
		}
		if (value.equals("10")) {
			warningMsg.put("typhoon", new Integer(R.drawable.tc10));
			currentTyphoon = Misc.TC10_NOTI;
		}
		if (value.equals("monsoon")) warningMsg.put("typhoon", new Integer(R.drawable.sms));
	}
	public void setLocalTemperature(String area, String temperature) {
		localTemperature.put(area.replace("&#40050;", "立"), temperature);
	}
	
	public int getError() { return error; }
	public String getTemperature() { return this.temperature + "\u00B0" + "C"; }
	public String getHumidity() { return this.humidity + "%"; }
	public String getUvLevel() { 
		String result = "UV : " + this.uvLevel;
		if (this.uvLevel >= 0 && this.uvLevel <= 2) return result + " (L)";
		if (this.uvLevel >= 3 && this.uvLevel <= 5) return result + " (M)";
		if (this.uvLevel >= 6 && this.uvLevel <= 7) return result + " (H)";
		if (this.uvLevel >= 8 && this.uvLevel <= 10) return result + " (VH)";
		if (this.uvLevel >= 11) return result + " (X)";

		return "";
		//return "UV : N/A";
	}
	public int getFireWarning() { return this.warningMsg.get("fire").intValue(); }
	public int getExtremeTemp() { return this.warningMsg.get("extreme_temp").intValue(); }
	public int getOther() { return this.warningMsg.get("other").intValue(); }
	public int getTyphoon() { return this.warningMsg.get("typhoon").intValue(); }
	public short getTyphoonNo() { return this.currentTyphoon; }
	public String[] getAreas() { 
		String[] result = new String[this.localTemperature.size()];
		ArrayList<String> al = new ArrayList<String>(this.localTemperature.keySet());
		Collections.sort(al);
		al.toArray(result);
		return result;
	}
	public String getLocalTemperature(String key) { return this.localTemperature.get(key); }

}

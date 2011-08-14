package com.tako.hko;

import java.io.Serializable;

public class Pollution implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2033176093409198696L;
	private String current;
	private String forecast;
	
	public Pollution() {}
	
	public Pollution(String current, String forecast) {
		this.current = current;
		this.forecast = forecast;
	}
	
	public String getCurrent() { return this.current; }
	public String getForecast() { return this.forecast; }
}

package com.tako.hko;

import java.io.Serializable;

public class Warnings implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7818776587115555023L;
	private String typhoonWarning = "";
	private String otherWarning = "";
	
	public Warnings() {}
	public Warnings(String tWarning, String oWarning) {
		this.typhoonWarning = tWarning;
		this.otherWarning = oWarning;
	}
	public String getTyphoonWarning() { return this.typhoonWarning; }
	public String getOtherWarning()    { return this.otherWarning; }
	
	public void setTyphoonWarning(String value) { this.typhoonWarning = value; }
	public void setOtherWarning(String value)   { this.otherWarning = value; }
}

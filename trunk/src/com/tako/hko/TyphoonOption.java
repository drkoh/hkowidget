package com.tako.hko;

public class TyphoonOption {
	private String optionName;
	private boolean option;
	private String sound;
	private int visible;
	
	public TyphoonOption(String optionName, boolean option, String sound, int visible) {
		this.optionName = optionName;
		this.option = option;
		this.sound = sound;
		this.visible = visible;
	}
	
	public void setOption(boolean option) { this.option = option; }
	public String getOptionName() { return this.optionName; }
	public boolean getOption() { return option; }
	public String getSound() { return sound; }
	public void setSound(String sound) { this.sound = sound; }
	public int getVisibility() { return visible; }
}

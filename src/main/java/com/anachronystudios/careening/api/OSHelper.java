package com.anachronystudios.careening.api;

public class OSHelper
{
	public enum OS {
		WIN, MAC, ANDROID, IOS, OTHER
	}
	
	private OS os;
	
	public OSHelper()
	{
		if(System.getProperty("os.name").toLowerCase().contains("win")) os = OS.WIN;
		else if(System.getProperty("os.name").toLowerCase().contains("mac")) os = OS.MAC;
		else if(System.getProperty("os.name").toLowerCase().contains("droid")) os = OS.ANDROID;
		else os = OS.OTHER;
	}
	public OS os() { return os; }
}

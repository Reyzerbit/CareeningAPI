package com.anachronystudios.careening.api;

public class Util
{
	public static String stackTraceArrayToString(StackTraceElement[] e)
	{
		StringBuilder str = new StringBuilder();
		for(StackTraceElement el : e) { str.append(el.toString() + "\n"); }
		return str.toString();
	}

	public static String throwableToString(Throwable e) { return stackTraceArrayToString(e.getStackTrace()); }
	public static String formatThrowable(String userDefinedMessage, Throwable e) { return userDefinedMessage + " - " + e.getMessage() + "\n" + throwableToString(e); }
}

package com.anachronystudios.careening.api.logging;

import java.io.PrintStream;

import org.slf4j.Logger;

public class SystemErrorProxyStream extends PrintStream
{
	public SystemErrorProxyStream(PrintStream stream, Logger log) { super(stream); }
	
	public void println(final String string) { print(string + "\n"); }

	public void print(final String string)
	{
		if(string.toLowerCase().contains("jprofiler")) return;
		
		Log.getLogger().error(string);
	}
}

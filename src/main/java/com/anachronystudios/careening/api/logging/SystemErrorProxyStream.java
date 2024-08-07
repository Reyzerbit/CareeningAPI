package com.anachronystudios.careening.api.logging;

import java.io.PrintStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

public class SystemErrorProxyStream extends PrintStream
{
	private List<String> stringCache = new ArrayList<String>();
	private LocalDateTime lastPrint = LocalDateTime.MIN;
	private Thread errorProxyThread;
	
	public SystemErrorProxyStream(PrintStream stream, Logger log)
	{
		super(stream);
		
		errorProxyThread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while(true)
				{
					if(!lastPrint.equals(LocalDateTime.MIN) && Duration.between(lastPrint, LocalDateTime.now()).getNano() > 250000000)
					{
						String errString = String.join("\n", stringCache);
						log.error(errString);
						Log.handleError(errString);
						break;
					}
				}
			}
		});
	}
	
	public void println(final String string) { print(string); }

	public void print(final String string)
	{
		if(string.toLowerCase().contains("jprofiler")) return;
		
		if(!errorProxyThread.isAlive()) errorProxyThread.start();
		
		stringCache.add(string);
		lastPrint = LocalDateTime.now();
	}
}

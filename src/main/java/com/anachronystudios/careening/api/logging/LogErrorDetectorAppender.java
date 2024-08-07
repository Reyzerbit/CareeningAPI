package com.anachronystudios.careening.api.logging;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;

public class LogErrorDetectorAppender extends UnsynchronizedAppenderBase<ILoggingEvent> implements AppenderAttachable<ILoggingEvent>
{
	List<Appender<ILoggingEvent>> appenders = new ArrayList<>();
	StringBuilder log = new StringBuilder();

	@Override
	public void addAppender(Appender<ILoggingEvent> newAppender) { appenders.add(newAppender); }

	@Override
	public Iterator<Appender<ILoggingEvent>> iteratorForAppenders() { return appenders.iterator(); }

	@Override
	public Appender<ILoggingEvent> getAppender(String name)
	{
		for(Appender<ILoggingEvent> appender : appenders) { if(appender.getName().equals(name)) return appender; }
		return null;
	}

	@Override
	public boolean isAttached(Appender<ILoggingEvent> appender) { return appenders.contains(appender); }

	@Override
	public void detachAndStopAllAppenders()
	{
		for(Appender<ILoggingEvent> appender : appenders) { appender.stop(); }
		appenders.clear();
	}

	@Override
	public boolean detachAppender(Appender<ILoggingEvent> appender) { return appenders.remove(appender); }

	@Override
	public boolean detachAppender(String name)
	{
		for(Appender<ILoggingEvent> appender : appenders)
		{
			if(appender.getName().equals(name))
			{
				appenders.remove(appender);
				return true;
			}
		}
		return false;
	}

	@Override
	protected void append(ILoggingEvent eventObject)
	{
		for(Appender<ILoggingEvent> appender : appenders) appender.doAppend(eventObject);
		
		log.append(eventObject.getMessage() + "\n");
		if(eventObject.getLevel() == Level.ERROR)
		{
			String err = log.toString();
			if(err.length() > 65535) err = err.substring(err.length() - 65535);
			
			Log.handleError(err);
		}
	}

}

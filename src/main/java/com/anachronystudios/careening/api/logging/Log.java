package com.anachronystudios.careening.api.logging;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.anachronystudios.careening.api.Util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import lombok.Getter;

public class Log
{
	private static String logsDirectory;
	@Getter private static Logger logger;
	
	protected static void handleError(String error)
	{
		
	}
	
	public static void initializeLogger(String appDataDirectory)
    {
    	try
		{
    		if(appDataDirectory == null) throw new NullPointerException("App data directory cannot be null!");
    		
    		logsDirectory = appDataDirectory + "/logs";
			File logsDirFile = new File(logsDirectory);
			if(!logsDirFile.exists()) logsDirFile.mkdirs();
			
			// Clean old logs
			List<File> logs = Arrays.asList(logsDirFile.listFiles());
			logs.sort(new Comparator<>()
			{
				@Override
				public int compare(File o1, File o2)
				{
					try
					{
						BasicFileAttributes o1Attr = Files.readAttributes(o1.toPath(), BasicFileAttributes.class);
						BasicFileAttributes o2Attr = Files.readAttributes(o2.toPath(), BasicFileAttributes.class);
						return -(o1Attr.creationTime().compareTo(o2Attr.creationTime()));
					}
					catch (IOException e)
					{
						logger.error(Util.formatThrowable("Unable to read file metadata!", e));
						return 0;
					}
				}
			});
			for(int x = 0 ; x < logs.size() ; x++) { if(x >= 10) logs.get(x).delete(); }
			
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
			String date = LocalDateTime.now().format(formatter);
			String logDir = logsDirectory + "/LOG-" + date + ".txt";
            
            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
            PatternLayoutEncoder ple = new PatternLayoutEncoder();
            
            ple.setPattern("%date %level [%file:%line] %msg%n");
            ple.setContext(lc);
            ple.start();
            
            FileAppender<ILoggingEvent> fa = new FileAppender<ILoggingEvent>();
            fa.setFile(logDir);
            fa.setEncoder(ple);
            fa.setContext(lc);
            fa.start();
            
            ConsoleAppender<ILoggingEvent> ca = new ConsoleAppender<ILoggingEvent>();
            ca.setEncoder(ple);
            ca.setContext(lc);
            ca.start();
            
            LogErrorDetectorAppender ea = new LogErrorDetectorAppender();
            ea.setContext(lc);
            ea.start();
            
            logger = (Logger) LoggerFactory.getLogger(Log.class);
            logger.detachAndStopAllAppenders();
            logger.addAppender(fa);
            logger.addAppender(ca);
            logger.addAppender(ea);
            logger.setLevel(Level.DEBUG);
            logger.setAdditive(false);
            
            // Reads errors from System.err, prints to log, and handles error
            PrintStream systemErrProxy = new SystemErrorProxyStream(System.err, logger);
            System.setErr(systemErrProxy);
            
            // Handles java.util.Logging reroutings
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            SLF4JBridgeHandler.install();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
    }
}

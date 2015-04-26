package com.ltrflog4j;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

import com.ltrf.LogClient;
import com.ltrf.LogMessage;

public class LtrfLog4jAppender extends AppenderSkeleton {
	
	
	public LtrfLog4jAppender()
	{
		
	}
	
	private String appName;
	private String keys;
	private String additionalInfo;
	private String uri;
	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
		LogClient.setURI(uri);
	}

	private static String[] keyStrings;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getKeys() {
		return keys;
	}
	
	public String[] getKeyStrings() {
		return keyStrings;
	}

	public static void setKeyStrings(String[] keyString) {
		keyStrings = keyString;
	}

	public void setKeys(String keys) {
		this.keys = keys;
		if(keys != null)
			setKeyStrings(keys.split(","));
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public void close() {
		// TODO Auto-generated method stub		
	}

	public boolean requiresLayout() {
		return true;
	}

	@Override
	protected void append(LoggingEvent event) {
		if(keyStrings == null || keyStrings.length == 0)
			return;
		StringBuilder message = new StringBuilder();
		if (layout != null)
			message.append(layout.format(event));		
		if (layout.ignoresThrowable()) {
			String[] s = event.getThrowableStrRep();
			if (s != null) {
				int len = s.length;
				for (int i = 0; i < len; i++) {
					message.append(s[i]);
					message.append(Layout.LINE_SEP);
				}
			}
		}
		String key;
		if((key = trackable(message.toString())) != null )
		{
			trackMessage(key,message.toString());
		}
	}

	private void trackMessage(String key, String message) {
		try{
			LogMessage logMessage = new LogMessage();
			logMessage.setAppName(appName);
			logMessage.setKey(key);
			logMessage.setAdditionalDetails(additionalInfo);
			logMessage.setMessage(message);
			LogClient.trackMessage(logMessage);
		}catch(Exception e)
		{
			System.out.println(e);
		}
		
	}

	private String trackable(String message) {
		for(String key : keyStrings)
		{
			if(message.contains(key));
				return key;
		}
		return null;
	}
}

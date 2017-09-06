package org.openactive;

import org.asteriskjava.fastagi.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Timeout extends BaseAgiScript
{
	private long last = 0L;
	private long timeoutInMinutes = 10;

	public Timeout() throws IOException
	{
		FileInputStream fis = new FileInputStream( "/etc/asterisk/agi.props" );
		Properties agiProperites = new Properties();
		agiProperites.load( fis );
		timeoutInMinutes = (long) agiProperites.getOrDefault( "doorbell.timeout.minutes", 10 );
	}

	public void service( AgiRequest agiRequest, AgiChannel agiChannel ) throws AgiException
	{
		synchronized ( this )
		{
			if ( System.currentTimeMillis() > (last + (timeoutInMinutes * 60 * 1000)) )
			{
				setVariable( "playmsg", "true" );
				last = System.currentTimeMillis();
			}
			else
			{
				setVariable( "playmsg", "false" );
			}
		}
	}
}

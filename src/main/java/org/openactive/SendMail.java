package org.openactive;

import org.asteriskjava.fastagi.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SendMail extends BaseAgiScript
{
	private final Authenticator auth;
	private final Properties gmailProps = new Properties();
	private final Properties agiProperites = new Properties();

	public SendMail() throws IOException
	{
		FileInputStream fis = new FileInputStream( "/etc/asterisk/agi.props" );
		agiProperites.load( fis );

		auth = new javax.mail.Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication(
					agiProperites.getProperty( "account.username" ),
					agiProperites.getProperty( "account.password" )
				);
			}
		};

		gmailProps.put( "mail.smtp.auth", "true" );
		gmailProps.put( "mail.smtp.starttls.enable", "true" );
		gmailProps.put( "mail.smtp.host", "smtp.gmail.com" );
		gmailProps.put( "mail.smtp.port", "587" );
	}

	public void service( AgiRequest agiRequest, AgiChannel agiChannel ) throws AgiException
	{
		try
		{
			Session session = Session.getInstance( gmailProps, auth );
			Message message = new MimeMessage( session );
			message.setFrom( new InternetAddress( agiProperites.getProperty( "email.from" ) ) );
			message.setRecipients( Message.RecipientType.TO, InternetAddress.parse( agiProperites.getProperty( "email.to" ) ) );
			message.setSubject( agiProperites.getProperty( "email.subject" ) );
			message.setText( agiProperites.getProperty( "email.body" ) );
			Transport.send( message );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
}

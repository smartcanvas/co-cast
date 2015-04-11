package com.ciandt.dcoder.c2.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * Utility class for e-mail and messaging
 * 
 * @author <a href="mailto:viveiros@ciandt.com">Daniel Viveiros</a>
 */
public class MailUtils {
    
    /**
     * Sends an email using GAE mechanism
     * 
     * @param fromEmail
     * @param fromName
     * @param recipientEmails
     * @param recipientNames
     * @param subject
     * @param body
     * @throws AddressException
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public static void sendEmail(String fromEmail, String fromName, String[] recipientEmails, String[] recipientNames,
            String subject, String body) throws AddressException, MessagingException, UnsupportedEncodingException {
        MailUtils.sendEmail(fromEmail, fromName, recipientEmails, recipientNames, subject, body, false);
    }

    /**
     * Sends an email using GAE mechanism
     * 
     * @param fromEmail
     * @param fromName
     * @param recipientEmails
     * @param recipientNames
     * @param subject
     * @param body
     * @throws AddressException
     * @throws MessagingException
     * @throws UnsupportedEncodingException
     */
    public static void sendEmail(String fromEmail, String fromName, String[] recipientEmails, String[] recipientNames,
            String subject, String body, boolean isHTML) throws AddressException, MessagingException, UnsupportedEncodingException {

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        
        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(fromEmail, fromName));
        for (int i = 0; i < recipientEmails.length; i++) {
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmails[i], recipientNames[i]));
        }
        msg.setSubject("[D1-Billing] " + subject);
        if ( !isHTML ) {
            msg.setText(body);
        } else {
            Multipart mp = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(body, "text/html");
            mp.addBodyPart(htmlPart);
            msg.setContent(mp);
        }
        Transport.send(msg);
    }

    /**
     * Sends a status message to D1 administrators
     * 
     * @param subject
     * @param message
     * @throws AddressException
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    public static void sendStatus(String subject, String message) throws AddressException,
            UnsupportedEncodingException, MessagingException {
        MailUtils.sendEmail(Constants.defaultFromEmail, Constants.defaultFromName, Constants.statusEmails,
                Constants.statusNames, subject, message);
    }
    
    /**
     * Sends a status message to D1 administrators
     * 
     * @param subject
     * @param message
     * @throws AddressException
     * @throws UnsupportedEncodingException
     * @throws MessagingException
     */
    public static void sendErrorStatus(String subject, String message) throws AddressException,
            UnsupportedEncodingException, MessagingException {
        MailUtils.sendEmail(Constants.defaultFromEmail, Constants.defaultFromName, Constants.statusErrorEmails,
                Constants.statusErrorNames, subject, message);
    }
}

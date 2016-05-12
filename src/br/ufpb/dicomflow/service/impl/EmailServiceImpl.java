package br.ufpb.dicomflow.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import br.ufpb.dicomflow.integrationAPI.mail.MailAuthenticatorIF;
import br.ufpb.dicomflow.integrationAPI.mail.MailMessageReaderIF;
import br.ufpb.dicomflow.integrationAPI.mail.MailServiceExtractorIF;
import br.ufpb.dicomflow.integrationAPI.mail.impl.SMTPAuthenticator;
import br.ufpb.dicomflow.integrationAPI.mail.impl.SMTPMessageReader;
import br.ufpb.dicomflow.integrationAPI.mail.impl.SMTPReceiver;
import br.ufpb.dicomflow.integrationAPI.mail.impl.SMTPServiceExtractor;
import br.ufpb.dicomflow.integrationAPI.message.xml.ServiceIF;
import br.ufpb.dicomflow.service.EmailService;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.util.Util;


public class EmailServiceImpl implements EmailService {
	

	private String smtp;
	private String port;
	private boolean online;
	private String user;
	private String password;
	
	/**
	 * Send email to a list of recipients.
	 * 
	 * @param smtpHost the SMTP email server address
	 * @param senderAddress the sender email address
	 * @param senderName the sender name
	 * @param recipients a list of receipients email addresses
	 * @param sub the subject of the email
	 * @param msg the message content of the email
	 */
	public void sendEmail(String to, String from, String subject, String corpo) throws ServiceException {
		List list = new ArrayList();
		list.add(to);
		sendEmail(list,  from,  subject, corpo);
	}
	
	public void sendEmail(List toList, String from, String subject, String corpo) throws ServiceException {
		if (online) {
			if (smtp == null) {
				System.err.println("smtp nulllll!!!!" );
				String errMsg = "Could not send email: smtp host address is null";
				
				Util.getLogger(this).error(errMsg);
				throw new ServiceException(new Exception(errMsg));
			}		
			try {
				
				Properties props = System.getProperties();
				props.put ("mail.smtp.host",smtp);    
				props.put("mail.smtp.auth", "true");    
				props.put("mail.debug", "true");    
				props.put("mail.smtp.debug", "true");    
				props.put("mail.mime.charset", "ISO-8859-1");    
				props.put("mail.smtp.port", port);    
				props.put ("mail.smtp.starttls.enable", "true");    
				props.put ("mail.smtp.socketFactory.port", port);    
				props.put ("mail.smtp.socketFactory.fallback", "false");    
				props.put ("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");   
     
				Session session = Session.getDefaultInstance(props);//recebe props   
				InternetAddress[] destinatarios = new InternetAddress[toList.size()];
				for (int i = 0; i < toList.size(); i++) {
					InternetAddress destinatario = new InternetAddress ((String)toList.get(i));
					destinatarios[i] = destinatario;
				}
				InternetAddress remetente = new InternetAddress (from);   
  
				Message msg = new MimeMessage(session);   
				msg.setSentDate(new Date());//novo   
				msg.setFrom(remetente);   
				msg.setRecipients( Message.RecipientType.TO,destinatarios );   
				msg.setSubject (subject);   
				msg.setContent (corpo, "text/HTML");   
  
				Transport transport = session.getTransport("smtp");   
				transport.connect(smtp,getUser(),getPassword());   
				msg.saveChanges();   
				transport.sendMessage(msg, msg.getAllRecipients());   
				transport.close();
				
			} catch (Exception e) {
			 	String errorMsg = "Could not send email";
			 	Util.getLogger(this).error(errorMsg, e);
				System.err.println(e.getMessage());
				e.printStackTrace();
				//throw new ServiceException(new Exception(errorMsg));
			 }	
		}else {
			Util.getLogger(this).debug("to = "+ toList + " subject = " + subject + " Corpo = " + corpo);
			System.err.println("to = "+ toList + " subject = " + subject + " Corpo = " + corpo);
		}
		
	}	
	
	public String getSmtp() {
		return smtp;
	}
	public void setSmtp(String smtp) {		
		this.smtp = smtp;
	}
	
	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
    public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}

package br.ufpb.dicomflow.tests;

import java.util.ArrayList;
import java.util.Properties;

import org.junit.Test;

import br.ufpb.dicomflow.integrationAPI.conf.IntegrationAPIProperties;
import br.ufpb.dicomflow.integrationAPI.mail.MailAuthenticatorIF;
import br.ufpb.dicomflow.integrationAPI.mail.MailContentBuilderIF;
import br.ufpb.dicomflow.integrationAPI.mail.MailHeadBuilderIF;
import br.ufpb.dicomflow.integrationAPI.mail.impl.MailAuthenticatorFactory;
import br.ufpb.dicomflow.integrationAPI.mail.impl.MailContentBuilderFactory;
import br.ufpb.dicomflow.integrationAPI.mail.impl.MailHeadBuilderFactory;
import br.ufpb.dicomflow.integrationAPI.main.ServiceFactory;
import br.ufpb.dicomflow.integrationAPI.main.ServiceProcessor;
import br.ufpb.dicomflow.integrationAPI.message.xml.Credentials;
import br.ufpb.dicomflow.integrationAPI.message.xml.ServiceIF;
import br.ufpb.dicomflow.integrationAPI.message.xml.SharingPut;
import br.ufpb.dicomflow.integrationAPI.message.xml.URL;

public class MailServiceTestCase {
	
	@Test
	public void testSendSharingPut() throws Exception {	
		
		SharingPut sharingPut = (SharingPut) ServiceFactory.createService(ServiceIF.SHARING_PUT);		
		
		ArrayList<URL> urls = new ArrayList<URL>();
		
		URL url1 = new URL();		
		Credentials cred = new Credentials();
		cred.setValue("1234567890");
		url1.setCredentials(cred);
		url1.setValue("http://www.dicomflow.org/rest/123456");
		
		urls.add(url1);
		
		URL url2 = new URL();		
		Credentials cred2 = new Credentials();
		cred2.setValue("1234567890");
		url2.setCredentials(cred2);
		url2.setValue("http://www.dicomflow.org/rest/654321");
		
		urls.add(url2);
		
		sharingPut.setUrls(urls);
		
		
		IntegrationAPIProperties iap = IntegrationAPIProperties.getInstance();
		iap.load("C:/home/dicomflow/repos/dicomMove2/WebContent/WEB-INF/config.properties");
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		
		MailContentBuilderIF mailContentBuilder = MailContentBuilderFactory.createContentStrategy(MailContentBuilderIF.SMTP_SIMPLE_CONTENT_STRATEGY);
		MailHeadBuilderIF mailHeadBuilder = MailHeadBuilderFactory.createHeadStrategy(MailHeadBuilderIF.SMTP_HEAD_STRATEGY);
		mailHeadBuilder.setFrom("protocolointegracao@gmail.com");
		
		MailAuthenticatorIF mailAuthenticator = MailAuthenticatorFactory.createHeadStrategy(MailAuthenticatorIF.SMTP_AUTHENTICATOR, "protocolointegracao@gmail.com", "pr0t0c0l0ap1d1c0m");	
		
		ServiceProcessor.sendMessage(sharingPut, "protocolointegracao@gmail.com", props, mailAuthenticator, mailHeadBuilder, mailContentBuilder);					
			
	}		
}

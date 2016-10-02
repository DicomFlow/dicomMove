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
		
		sharingPut.setUrl(urls);
		
		
		IntegrationAPIProperties iap = IntegrationAPIProperties.getInstance();
		iap.load("WebContent/WEB_INF/classes/config.properties");
		
		Properties props = iap.getSendProperties();
		
		MailContentBuilderIF mailContentBuilder = MailContentBuilderFactory.createContentStrategy(MailContentBuilderIF.SMTP_SIMPLE_CONTENT_STRATEGY);
		MailHeadBuilderIF mailHeadBuilder = MailHeadBuilderFactory.createHeadStrategy(MailHeadBuilderIF.SMTP_HEAD_STRATEGY);
		mailHeadBuilder.setFrom(iap.getProperty("authentication.login"));
		
		MailAuthenticatorIF mailAuthenticator = MailAuthenticatorFactory.createHeadStrategy(MailAuthenticatorIF.SMTP_AUTHENTICATOR, iap.getProperty("authentication.login"), iap.getProperty("authentication.password"));	
		
		ServiceProcessor.sendMessage(sharingPut, iap.getProperty("authentication.login"), props, mailAuthenticator, mailHeadBuilder, mailContentBuilder);					
			
	}		
}

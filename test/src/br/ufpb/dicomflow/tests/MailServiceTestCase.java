package br.ufpb.dicomflow.tests;

import java.util.Iterator;

import org.junit.Test;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.integrationAPI.conf.IntegrationAPIProperties;
import br.ufpb.dicomflow.integrationAPI.exceptions.PropertyNotFoundException;
import br.ufpb.dicomflow.integrationAPI.exceptions.ServiceCreationException;
import br.ufpb.dicomflow.integrationAPI.mail.MailAuthenticatorIF;
import br.ufpb.dicomflow.integrationAPI.mail.MailContentBuilderIF;
import br.ufpb.dicomflow.integrationAPI.mail.MailHeadBuilderIF;
import br.ufpb.dicomflow.integrationAPI.mail.impl.MailAuthenticatorFactory;
import br.ufpb.dicomflow.integrationAPI.mail.impl.MailContentBuilderFactory;
import br.ufpb.dicomflow.integrationAPI.mail.impl.MailHeadBuilderFactory;
import br.ufpb.dicomflow.integrationAPI.mail.impl.SMTPAuthenticator;
import br.ufpb.dicomflow.integrationAPI.main.ServiceFactory;
import br.ufpb.dicomflow.integrationAPI.main.ServiceProcessor;
import br.ufpb.dicomflow.integrationAPI.message.xml.Credentials;
import br.ufpb.dicomflow.integrationAPI.message.xml.ServiceIF;
import br.ufpb.dicomflow.integrationAPI.message.xml.SharingPut;
import br.ufpb.dicomflow.integrationAPI.message.xml.StorageSave;
import br.ufpb.dicomflow.integrationAPI.message.xml.URL;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.util.Util;

public class MailServiceTestCase {
	
	@Test
	public void testSendSharingPut() {	
		
		SharingPut sharingPut = (SharingPut) ServiceFactory.createService(ServiceIF.SHARING_PUT);
		
		IntegrationAPIProperties iap = IntegrationAPIProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		MailContentBuilderIF mailContentBuilder = MailContentBuilderFactory.createContentStrategy(MailContentBuilderIF.SMTP_SIMPLE_CONTENT_STRATEGY);
		MailHeadBuilderIF mailHeadBuilder = MailHeadBuilderFactory.createHeadStrategy(MailHeadBuilderIF.SMTP_HEAD_STRATEGY);
		MailAuthenticatorIF mailAuthenticator = MailAuthenticatorFactory.createHeadStrategy(MailAuthenticatorIF.SMTP_AUTHENTICATOR, "protocolointegracao@gmail.com", "pr0t0c0l0ap1");	
		
		//ServiceProcessor.sendMessage(sharingPut, "protocolointegracao@gmail.com" iap.getSendProperties(), mailAuthenticator, mailHeadBuilder, mailContentBuilder);					
			
	}		
}

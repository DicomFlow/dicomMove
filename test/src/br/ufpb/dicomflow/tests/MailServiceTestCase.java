/*
 * 	This file is part of DicomFlow.
 * 
 * 	DicomFlow is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either version 3 of the License, or
 * 	(at your option) any later version.
 * 
 * 	This program is distributed in the hope that it will be useful,
 * 	but WITHOUT ANY WARRANTY; without even the implied warranty of
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * 	GNU General Public License for more details.
 * 
 * 	You should have received a copy of the GNU General Public License
 * 	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package br.ufpb.dicomflow.tests;

import java.util.ArrayList;
import java.util.Properties;

import org.junit.Test;

import br.ufpb.dicomflow.integrationAPI.conf.DicomMessageProperties;
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
		
		
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
		iap.load("WebContent/WEB_INF/classes/config.properties");
		
		Properties props = iap.getSendProperties();
		
		MailContentBuilderIF mailContentBuilder = MailContentBuilderFactory.createContentStrategy(MailContentBuilderIF.SMTP_SIMPLE_CONTENT_STRATEGY);
		MailHeadBuilderIF mailHeadBuilder = MailHeadBuilderFactory.createHeadStrategy(MailHeadBuilderIF.SMTP_HEAD_STRATEGY);
		mailHeadBuilder.setFrom(iap.getProperty("authentication.login"));
		
		MailAuthenticatorIF mailAuthenticator = MailAuthenticatorFactory.createHeadStrategy(MailAuthenticatorIF.SMTP_AUTHENTICATOR, iap.getProperty("authentication.login"), iap.getProperty("authentication.password"));	
		
		ServiceProcessor.sendMessage(sharingPut, iap.getProperty("authentication.login"), props, mailAuthenticator, mailHeadBuilder, mailContentBuilder);					
			
	}		
}

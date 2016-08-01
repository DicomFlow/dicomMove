package br.ufpb.dicomflow.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.bean.Registry;
import br.ufpb.dicomflow.bean.RegistryAccess;
import br.ufpb.dicomflow.integrationAPI.conf.IntegrationAPIProperties;
import br.ufpb.dicomflow.integrationAPI.exceptions.PropertyNotFoundException;
import br.ufpb.dicomflow.integrationAPI.exceptions.ServiceCreationException;
import br.ufpb.dicomflow.integrationAPI.mail.FilterIF;
import br.ufpb.dicomflow.integrationAPI.mail.MailAuthenticatorIF;
import br.ufpb.dicomflow.integrationAPI.mail.MailContentBuilderIF;
import br.ufpb.dicomflow.integrationAPI.mail.MailHeadBuilderIF;
import br.ufpb.dicomflow.integrationAPI.mail.MessageIF;
import br.ufpb.dicomflow.integrationAPI.mail.impl.MailXTags;
import br.ufpb.dicomflow.integrationAPI.mail.impl.SMTPFilter;
import br.ufpb.dicomflow.integrationAPI.main.ServiceFactory;
import br.ufpb.dicomflow.integrationAPI.main.ServiceProcessor;
import br.ufpb.dicomflow.integrationAPI.message.xml.CertificateRequest;
import br.ufpb.dicomflow.integrationAPI.message.xml.CertificateResult;
import br.ufpb.dicomflow.integrationAPI.message.xml.Completed;
import br.ufpb.dicomflow.integrationAPI.message.xml.Credentials;
import br.ufpb.dicomflow.integrationAPI.message.xml.Object;
import br.ufpb.dicomflow.integrationAPI.message.xml.Result;
import br.ufpb.dicomflow.integrationAPI.message.xml.ServiceIF;
import br.ufpb.dicomflow.integrationAPI.message.xml.StorageResult;
import br.ufpb.dicomflow.integrationAPI.message.xml.StorageSave;
import br.ufpb.dicomflow.integrationAPI.message.xml.URL;
import br.ufpb.dicomflow.service.MessageService;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.UrlGeneratorIF;
import br.ufpb.dicomflow.util.Util;

public class DicomMessageServiceImpl implements MessageService {
	
	private MailAuthenticatorIF mailAuthenticator;
	private MailHeadBuilderIF mailHeadBuilder;
	private MailContentBuilderIF mailContentBuilder;
	private String propertiesConfigPath;
	private UrlGeneratorIF urlGenerator;
	private int maxAttempts;
	
	
	@Override
	public String sendURL(RegistryAccess registryAccess) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not send study: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		StorageSave storageSave = (StorageSave) ServiceFactory.createService(ServiceIF.STORAGE_SAVE);
		
		IntegrationAPIProperties iap = IntegrationAPIProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		storageSave.setUrl(new URL(registryAccess.getRegistry().getLink(), new Credentials(registryAccess.getAccess().getCredential())));
		storageSave.setTimeout(registryAccess.getValidity());
			
		try {
			return ServiceProcessor.sendMessage(storageSave, registryAccess.getAccess().getMail(), iap.getSendProperties(), mailAuthenticator, mailHeadBuilder, mailContentBuilder);
		
		} catch (ServiceCreationException e) {
			Util.getLogger(this).error("Could not send study.", e);
			e.printStackTrace();
		} catch (PropertyNotFoundException e) {
			Util.getLogger(this).error("Could not send study.", e);
			e.printStackTrace();
		}
		
		return null;
			
		
	}

	@Override
	public void sendURL(Registry registry, List<Access> accesses) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not send study: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		StorageSave storageSave = (StorageSave) ServiceFactory.createService(ServiceIF.STORAGE_SAVE);
		
		IntegrationAPIProperties iap = IntegrationAPIProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		Iterator<Access> it = accesses.iterator();
		while (it.hasNext()) {
			Access access = it.next();
			
			storageSave.setUrl(new URL(registry.getLink(), new Credentials(access.getCredential())));
			storageSave.setTimeout("");
			
			try {
				ServiceProcessor.sendMessage(storageSave, access.getMail(), iap.getSendProperties(), mailAuthenticator, mailHeadBuilder, mailContentBuilder);
			
				
			} catch (ServiceCreationException e) {
				Util.getLogger(this).error("Could not send study.", e);
				e.printStackTrace();
			} catch (PropertyNotFoundException e) {
				Util.getLogger(this).error("Could not send study.", e);
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public void sendURLs(List<Registry> registries, List<Access> accesses) throws ServiceException {
		Iterator<Registry> it = registries.iterator();
		while (it.hasNext()) {

			Registry registry = it.next();

			sendURL(registry, accesses);

		}
	}
	
	@Override
	public void sendURL(Registry registry, Access access) throws ServiceException {
		List<Access> accesses = new ArrayList<Access>();
		sendURL(registry, accesses);
	}
	
	
	@Override
	public Map<String,String> getURLs(Date initialDate, Date finalDate, String messageID) throws ServiceException {
		
		Map<String,String> urls = new HashMap<String,String>();
		
		Iterator<ServiceIF> iterator = getServices(initialDate, finalDate, messageID, ServiceIF.STORAGE_SAVE).iterator();
		while (iterator.hasNext()) {
			StorageSave storage = (StorageSave) iterator.next();
			if(storage.getUrl() != null && !storage.getUrl().equals("")){
				urls.put(storage.getMessageID(), storage.getUrl().getValue());
			}
		}
		return urls;
	}
	
	@Override
	public void sendResult(String messageID, Registry registry) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not send study: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		StorageResult storageResult = (StorageResult) ServiceFactory.createService(ServiceIF.STORAGE_RESULT);
		
		Result result = new Result();
		
		Completed completed = new Completed();
		completed.setStatus(registry.getStatus());
		completed.setCompletedMessage(registry.getStatus());
		result.setCompleted(completed);
		
		List<Object> objects = new ArrayList<Object>();
		Object object = new Object();
		object.setType(Object.TYPE_STUDY);
		object.setId(registry.getStudyIuid());
		objects.add(object);
		result.setObjects(objects);
		
		result.setOriginalMessageID(messageID);
		result.setTimestamp(Util.singleton().getDataString(Calendar.getInstance().getTime()));
		
		List<Result> results = new ArrayList<Result>();
		results.add(result);
		storageResult.setResult(results);

		
		IntegrationAPIProperties iap = IntegrationAPIProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		
		List<MessageIF> messages = getMessage(null, null, messageID, ServiceIF.STORAGE_SAVE);
		Iterator<MessageIF> it = messages.iterator();
	
		while (it.hasNext()) {
			MessageIF message = (MessageIF) it.next();
			String mailTo = (String) message.getMailTag(MailXTags.DISPOSITION_NOTIFICATION_TO_X_TAG);
			try {
				ServiceProcessor.sendMessage(storageResult, mailTo, iap.getSendProperties(), mailAuthenticator, mailHeadBuilder, mailContentBuilder);
			
			} catch (ServiceCreationException e) {
				e.printStackTrace();
			} catch (PropertyNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	@Override
	public Map<String,String> getResults(Date initialDate, Date finalDate, String originalMessageID) throws ServiceException {
		
		Map<String,String> map = new HashMap<String, String>();
		
		List<MessageIF> messages = getMessage(null, null, null, ServiceIF.STORAGE_RESULT);
		Iterator<MessageIF> it = messages.iterator();
		while (it.hasNext()) {
			MessageIF message = (MessageIF) it.next();
			String xMessageID = (String) message.getMailTag(MailXTags.MESSAGE_ID_X_TAG);
			
			StorageResult storageResult = (StorageResult) message.getService();
			List<Result> results  = storageResult.getResult();
			Iterator<Result> resultIt = results.iterator();
			while (resultIt.hasNext()) {
				Result result = (Result) resultIt.next();
				if(result.getOriginalMessageID() != null && result.getOriginalMessageID().equals(originalMessageID)){
					map.put(getDomain(xMessageID), result.getCompleted().getStatus());
				}
			}
			
		}
		return map;
		
	}
	
	private String getDomain(String xMessageID) throws ServiceException{
		StringTokenizer tokenizer = new StringTokenizer(xMessageID,"@");
		if(tokenizer.countTokens() == 2){
			tokenizer.nextToken();
			return tokenizer.nextToken();
		}else{
			throw new ServiceException(new Exception("X-Message-ID invalid format"));
		}
	}

	@Override
	public String sendCertificate(File certificate, Access access) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not send study: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		CertificateRequest certificateRequest = (CertificateRequest) ServiceFactory.createService(ServiceIF.CERTIFICATE_REQUEST);
		certificateRequest.setMail(getMailHeadBuilder().getFrom());
		certificateRequest.setDomain(getUrlGenerator().getHost());
		certificateRequest.setPort(getUrlGenerator().getPort());

		
		IntegrationAPIProperties iap = IntegrationAPIProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		
			
		try {
			return ServiceProcessor.sendMessage(certificateRequest, certificate, access.getMail(), iap.getSendProperties(), mailAuthenticator, mailHeadBuilder, mailContentBuilder);
		
		} catch (ServiceCreationException e) {
			e.printStackTrace();
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
			
		
	}
	
	@Override
	public Map<Access,byte[]> getCertificates(Date initialDate, Date finalDate, String messageID) throws ServiceException {
		
		if(propertiesConfigPath == null){
			String errMsg = "Could not get links: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		FilterIF filter = new SMTPFilter();
		filter.setInitialDate(initialDate);
		filter.setFinalDate(finalDate);
		filter.setIdMessage(messageID);
		filter.setServiceType(ServiceIF.CERTIFICATE_REQUEST);
		
		IntegrationAPIProperties iap = IntegrationAPIProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		Map<Access,byte[]> servicesAndAttachs = new HashMap<Access,byte[]>();
		try {
			List<MessageIF> messages = ServiceProcessor.receiveMessages(iap.getReceiveProperties(), mailAuthenticator, null, null, filter);
			Iterator<MessageIF> iterator = messages.iterator();
			while (iterator.hasNext()) {
				MessageIF message = (MessageIF) iterator.next();
				
				//Exists only one service on MAP
				if(message.getService() != null){
					ServiceIF service = message.getService();
					byte[] certificate = message.getAttach();
					Access access = new Access();
					access.setHost(((CertificateRequest)service).getDomain());
					access.setPort(((CertificateRequest)service).getPort());
					access.setMail(((CertificateRequest)service).getMail());
					servicesAndAttachs.put(access, certificate);
				}
				
			}
			
		} catch (ServiceCreationException e) {
			Util.getLogger(this).error("Could not get links.", e);
			e.printStackTrace();
		} catch (PropertyNotFoundException e) {
			Util.getLogger(this).error("Could not get links.", e);
			e.printStackTrace();
		}
		
		
		return servicesAndAttachs;
	}
	
	@Override
	public String sendCertificateResult(Access access, String status) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not send study: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		CertificateResult certificateResult = (CertificateResult) ServiceFactory.createService(ServiceIF.CERTIFICATE_RESULT);
		certificateResult.setMail(access.getMail());
		certificateResult.setDomain(access.getHost());
		certificateResult.setPort(access.getPort());
		certificateResult.setCredential(access.getCredential());
		certificateResult.setStatus(status);
		
		IntegrationAPIProperties iap = IntegrationAPIProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		
			
		try {
			return ServiceProcessor.sendMessage(certificateResult, access.getMail(), iap.getSendProperties(), mailAuthenticator, mailHeadBuilder, mailContentBuilder);
		
		} catch (ServiceCreationException e) {
			e.printStackTrace();
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
			
		
	}
	
	@Override
	public Map<Access, String> getCertificateResults(Date initialDate, Date finalDate, String messageID) throws ServiceException {
		
		Map<Access, String> accesses = new HashMap<Access, String>();
		
		Iterator<ServiceIF> iterator = getServices(initialDate, finalDate, messageID, ServiceIF.CERTIFICATE_RESULT).iterator();
		while (iterator.hasNext()) {
			CertificateResult certificateResult = (CertificateResult)iterator.next();
			Access access = new Access();
			access.setHost(certificateResult.getDomain());
			access.setPort(certificateResult.getPort());
			access.setMail(certificateResult.getMail());
			access.setCredential(certificateResult.getCredential());
			accesses.put(access, certificateResult.getStatus());
		}
		return accesses;
	}
	
	private List<ServiceIF> getServices(Date initialDate, Date finalDate, String messageID, int type) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not get links: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		FilterIF filter = new SMTPFilter();
		filter.setInitialDate(initialDate);
		filter.setFinalDate(finalDate);
		filter.setIdMessage(messageID);
		filter.setServiceType(type);
		
		IntegrationAPIProperties iap = IntegrationAPIProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		try {
			return ServiceProcessor.receiveServices(iap.getReceiveProperties(), mailAuthenticator, null, null, filter);
		} catch (ServiceCreationException e) {
			e.printStackTrace();
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
		return new ArrayList<ServiceIF>();
		
		
	}
	
	private List<MessageIF> getMessage(Date initialDate, Date finalDate, String messageID, int type) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not get links: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		FilterIF filter = new SMTPFilter();
		filter.setInitialDate(initialDate);
		filter.setFinalDate(finalDate);
		filter.setIdMessage(messageID);
		filter.setServiceType(type);
		
		IntegrationAPIProperties iap = IntegrationAPIProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		try {
			return ServiceProcessor.receiveMessages(iap.getReceiveProperties(), mailAuthenticator, null, null, filter);
		} catch (ServiceCreationException e) {
			e.printStackTrace();
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
		return new ArrayList<MessageIF>();
		
		
	}
	
	
	public MailAuthenticatorIF getMailAuthenticator() {
		return mailAuthenticator;
	}

	public void setMailAuthenticator(MailAuthenticatorIF mailAuthenticator) {
		this.mailAuthenticator = mailAuthenticator;
	}

	public MailHeadBuilderIF getMailHeadBuilder() {
		return mailHeadBuilder;
	}

	public void setMailHeadBuilder(MailHeadBuilderIF mailHeadBuilder) {
		this.mailHeadBuilder = mailHeadBuilder;
	}

	public MailContentBuilderIF getMailContentBuilder() {
		return mailContentBuilder;
	}

	public void setMailContentBuilder(MailContentBuilderIF mailContentBuilder) {
		this.mailContentBuilder = mailContentBuilder;
	}

	public String getPropertiesConfigPath() {
		return propertiesConfigPath;
	}

	public void setPropertiesConfigPath(String propertiesConfigPath) {
		this.propertiesConfigPath = propertiesConfigPath;
	}
	
	public UrlGeneratorIF getUrlGenerator() {
		return urlGenerator;
	}

	public void setUrlGenerator(UrlGeneratorIF urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}

	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}
	
	


}

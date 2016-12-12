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
package br.ufpb.dicomflow.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.bean.Credential;
import br.ufpb.dicomflow.bean.RequestService;
import br.ufpb.dicomflow.bean.RequestServiceAccess;
import br.ufpb.dicomflow.bean.StorageService;
import br.ufpb.dicomflow.bean.StorageServiceAccess;
import br.ufpb.dicomflow.integrationAPI.conf.DicomMessageProperties;
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
import br.ufpb.dicomflow.integrationAPI.message.xml.Patient;
import br.ufpb.dicomflow.integrationAPI.message.xml.RequestPut;
import br.ufpb.dicomflow.integrationAPI.message.xml.RequestResult;
import br.ufpb.dicomflow.integrationAPI.message.xml.Result;
import br.ufpb.dicomflow.integrationAPI.message.xml.ServiceIF;
import br.ufpb.dicomflow.integrationAPI.message.xml.StorageResult;
import br.ufpb.dicomflow.integrationAPI.message.xml.StorageSave;
import br.ufpb.dicomflow.integrationAPI.message.xml.Study;
import br.ufpb.dicomflow.integrationAPI.message.xml.URL;
import br.ufpb.dicomflow.util.CredentialUtil;
import br.ufpb.dicomflow.util.Util;

public class MessageService implements MessageServiceIF {
	
	private MailAuthenticatorIF mailAuthenticator;
	private MailHeadBuilderIF mailHeadBuilder;
	private MailContentBuilderIF mailContentBuilder;
	private String propertiesConfigPath;
	private UrlGeneratorIF urlGenerator;
	private int maxAttempts;
	
	
	@Override
	public String sendStorage(StorageServiceAccess storageServiceAccess, Credential credential) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not send study: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		StorageSave storageSave = (StorageSave) ServiceFactory.createService(ServiceIF.STORAGE_SAVE);
		
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		Credentials credentials = new Credentials();
		if(credential != null)
			credentials.setValue(credential.getKey());
		
		storageSave.setUrl(new URL(storageServiceAccess.getStorageService().getLink(), credentials));
		storageSave.setTimeout(storageServiceAccess.getValidity());
			
		try {
			return ServiceProcessor.sendMessage(storageSave, storageServiceAccess.getAccess().getMail(), iap.getSendProperties(), mailAuthenticator, mailHeadBuilder, mailContentBuilder);
		
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
	public void sendStorage(StorageService storageService, List<Access> accesses, Credential credential) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not send study: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		StorageSave storageSave = (StorageSave) ServiceFactory.createService(ServiceIF.STORAGE_SAVE);
		
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		Credentials credentials = new Credentials();
		if(credential != null)
			credentials.setValue(credential.getKey());
		
		URL url = new URL(storageService.getLink(), credentials);
		
		Iterator<Access> it = accesses.iterator();
		while (it.hasNext()) {
			Access access = it.next();
			
			storageSave.setUrl(url);
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
	public void sendStorages(List<StorageService> storageServices, List<Access> accesses, Credential credential) throws ServiceException {
		Iterator<StorageService> it = storageServices.iterator();
		while (it.hasNext()) {

			StorageService registry = it.next();

			sendStorage(registry, accesses, credential);

		}
	}
	
	@Override
	public void sendStorage(StorageService storageService, Access access, Credential credential) throws ServiceException {
		List<Access> accesses = new ArrayList<Access>();
		sendStorage(storageService, accesses, credential);
	}
	
	
	@Override
	public Map<String,String> getStorages(Date initialDate, Date finalDate, String messageID) throws ServiceException {
		
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
	public void sendStorageResult(String messageID, StorageService storageService) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not send study: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		StorageResult storageResult = (StorageResult) ServiceFactory.createService(ServiceIF.STORAGE_RESULT);
		
		Result result = new Result();
		
		Completed completed = new Completed();
		completed.setStatus(storageService.getStatus());
		completed.setCompletedMessage(storageService.getStatus());
		result.setCompleted(completed);
		
		List<Object> objects = new ArrayList<Object>();
		Object object = new Object();
		object.setType(Object.TYPE_STUDY);
		object.setId(storageService.getStudyIuid());
		objects.add(object);
		result.setObject(objects);
		
		result.setOriginalMessageID(messageID);
		result.setTimestamp(Util.singleton().getDataString(Calendar.getInstance().getTime()));
		
		List<Result> results = new ArrayList<Result>();
		results.add(result);
		storageResult.setResult(results);

		
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
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
	public Map<String,String> getStorageResults(Date initialDate, Date finalDate, String originalMessageID) throws ServiceException {
		
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

		
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
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
		
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
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
	public String sendCertificateResult(File certificate, Access access, String status, Credential credential) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not send study: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		CertificateResult certificateResult = (CertificateResult) ServiceFactory.createService(ServiceIF.CERTIFICATE_RESULT);
		certificateResult.setMail(access.getMail());
		certificateResult.setDomain(access.getHost());
		certificateResult.setPort(access.getPort());
		if(credential != null)
			certificateResult.setCredential(credential.getKey());
		
		certificateResult.setStatus(status);
		
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		
			
		try {
			return ServiceProcessor.sendMessage(certificateResult, certificate, access.getMail(), iap.getSendProperties(), mailAuthenticator, mailHeadBuilder, mailContentBuilder);
		
		} catch (ServiceCreationException e) {
			e.printStackTrace();
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
			
		
	}
	
	@Override
	public String sendCertificateError(Access access, String status) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not send study: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		CertificateResult certificateResult = (CertificateResult) ServiceFactory.createService(ServiceIF.CERTIFICATE_RESULT);
		certificateResult.setMail(access.getMail());
		certificateResult.setDomain(access.getHost());
		certificateResult.setPort(access.getPort());
		certificateResult.setStatus(status);
		
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
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
			
			Credential credential = new Credential();
			credential.setKey(certificateResult.getCredential());
			credential.setOwner(CredentialUtil.getDomain());
			credential.setDomain(access);
			access.getDomainCredentials().add(credential);
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
		
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
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
		
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
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
	
	
	
	@Override
	public String sendRequest(RequestServiceAccess requestServiceAccess, Credential credential) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not send study: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		RequestPut requestPut = (RequestPut) ServiceFactory.createService(ServiceIF.REQUEST_PUT);
		
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		Credentials credentials = new Credentials();
		if(credential != null)
			credentials.setValue(credential.getKey());
		
		URL url = new URL(requestServiceAccess.getRequestService().getLink(), credentials);
		
		Patient patient = new Patient();
		patient.setId(requestServiceAccess.getRequestService().getPatientID());
		patient.setName(requestServiceAccess.getRequestService().getPatientName());
		patient.setGender(requestServiceAccess.getRequestService().getPatientGender());
		patient.setBirthdate(requestServiceAccess.getRequestService().getPatientBirth());
		
		Study study = new Study();
		study.setId(requestServiceAccess.getRequestService().getStudyIuid());
		study.setType(requestServiceAccess.getRequestService().getStudyModality());
		study.setDescription(requestServiceAccess.getRequestService().getStudyDescription());
		
		patient.addStudy(study);
		
		url.addPatient(patient);
		
		requestPut.setUrl(url);
		requestPut.setRequestType(RequestPut.TYPE_REPORT);
		requestPut.setTimeout(requestServiceAccess.getValidity());
			
		try {
			return ServiceProcessor.sendMessage(requestPut, requestServiceAccess.getAccess().getMail(), iap.getSendProperties(), mailAuthenticator, mailHeadBuilder, mailContentBuilder);
		
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
	public void sendRequest(RequestService requestService, List<Access> accesses, Credential credential) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not send study: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		RequestPut requestPut = (RequestPut) ServiceFactory.createService(ServiceIF.REQUEST_PUT);
		
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		Credentials credentials = new Credentials();
		if(credential != null)
			credentials.setValue(credential.getKey());
		
		URL url = new URL(requestService.getLink(), credentials);
		
		Patient patient = new Patient();
		patient.setId(requestService.getPatientID());
		patient.setName(requestService.getPatientName());
		patient.setGender(requestService.getPatientGender());
		patient.setBirthdate(requestService.getPatientBirth());
		
		Study study = new Study();
		study.setId(requestService.getStudyIuid());
		study.setType(requestService.getStudyModality());
		study.setDescription(requestService.getStudyDescription());
		
		patient.addStudy(study);
		
		url.addPatient(patient);
		
		requestPut.setUrl(url);
		requestPut.setRequestType(RequestPut.TYPE_REPORT);
		requestPut.setTimeout("");
		
		Iterator<Access> it = accesses.iterator();
		while (it.hasNext()) {
			Access access = it.next();
			
			try {
				ServiceProcessor.sendMessage(requestPut, access.getMail(), iap.getSendProperties(), mailAuthenticator, mailHeadBuilder, mailContentBuilder);
			
				
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
	public void sendRequests(List<RequestService> requestServices, List<Access> accesses, Credential credential) throws ServiceException {
		Iterator<RequestService> it = requestServices.iterator();
		while (it.hasNext()) {

			RequestService registry = it.next();

			sendRequest(registry, accesses, credential);

		}
	}
	
	@Override
	public void sendRequest(RequestService requestService, Access access, Credential credential) throws ServiceException {
		List<Access> accesses = new ArrayList<Access>();
		sendRequest(requestService, accesses, credential);
	}
	
	@Override
	public Map<String,String> getRequests(Date initialDate, Date finalDate, String messageID) throws ServiceException {
		
		Map<String,String> urls = new HashMap<String,String>();
		
		Iterator<ServiceIF> iterator = getServices(initialDate, finalDate, messageID, ServiceIF.REQUEST_PUT).iterator();
		while (iterator.hasNext()) {
			RequestPut request = (RequestPut) iterator.next();
			if(request.getUrl() != null && !request.getUrl().equals("")){
				urls.put(request.getMessageID(), request.getUrl().getValue());
			}
		}
		return urls;
	}
	
	@Override
	public void sendRequestResult(String messageID, RequestService requestService) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not send study: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		RequestResult requestResult = (RequestResult) ServiceFactory.createService(ServiceIF.REQUEST_RESULT);
		
		Result result = new Result();
		
		Completed completed = new Completed();
		completed.setStatus(requestService.getStatus());
		completed.setCompletedMessage(requestService.getStatus());
		result.setCompleted(completed);
		
		List<Object> objects = new ArrayList<Object>();
		Object object = new Object();
		object.setType(Object.TYPE_STUDY);
		object.setId(requestService.getStudyIuid());
		objects.add(object);
		result.setObject(objects);
		
		result.setOriginalMessageID(messageID);
		result.setTimestamp(Util.singleton().getDataString(Calendar.getInstance().getTime()));
		
		List<Result> results = new ArrayList<Result>();
		results.add(result);
		requestResult.setResult(results);

		
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		
		List<MessageIF> messages = getMessage(null, null, messageID, ServiceIF.REQUEST_PUT);
		Iterator<MessageIF> it = messages.iterator();
	
		while (it.hasNext()) {
			MessageIF message = (MessageIF) it.next();
			String mailTo = (String) message.getMailTag(MailXTags.DISPOSITION_NOTIFICATION_TO_X_TAG);
			try {
				ServiceProcessor.sendMessage(requestResult, mailTo, iap.getSendProperties(), mailAuthenticator, mailHeadBuilder, mailContentBuilder);
			
			} catch (ServiceCreationException e) {
				e.printStackTrace();
			} catch (PropertyNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
		
	}
	
	@Override
	public Map<String,String> getRequestResults(Date initialDate, Date finalDate, String originalMessageID) throws ServiceException {
		
		Map<String,String> map = new HashMap<String, String>();
		
		List<MessageIF> messages = getMessage(null, null, null, ServiceIF.REQUEST_RESULT);
		Iterator<MessageIF> it = messages.iterator();
		while (it.hasNext()) {
			MessageIF message = (MessageIF) it.next();
			String xMessageID = (String) message.getMailTag(MailXTags.MESSAGE_ID_X_TAG);
			
			RequestResult requestResult = (RequestResult) message.getService();
			List<Result> results  = requestResult.getResult();
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

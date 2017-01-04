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
import java.util.Iterator;
import java.util.List;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.bean.Credential;
import br.ufpb.dicomflow.bean.RequestService;
import br.ufpb.dicomflow.bean.RequestServiceAccess;
import br.ufpb.dicomflow.bean.StorageService;
import br.ufpb.dicomflow.bean.StorageServiceAccess;
import br.ufpb.dicomflow.integrationAPI.conf.DicomMessageProperties;
import br.ufpb.dicomflow.integrationAPI.exceptions.ContentBuilderException;
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
import br.ufpb.dicomflow.integrationAPI.message.xml.CertificateConfirm;
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
	private String messageValidity;
	
	
	@Override
	public String sendStorage(StorageServiceAccess storageServiceAccess, Credential accessCredential) throws ServiceException {
		
		StorageSave storageSave = (StorageSave) ServiceFactory.createService(ServiceIF.STORAGE_SAVE);
		
		Credentials credentials = new Credentials();
		if(accessCredential != null)
			credentials.setValue(accessCredential.getKeypass());
		
		storageSave.setUrl(new URL(storageServiceAccess.getStorageService().getLink(), credentials));
		storageSave.setTimeout(storageServiceAccess.getValidity());
			
		return sendMessage(storageSave, null, storageServiceAccess.getAccess().getMail());		
		
	}
	
	@Override
	public String sendStorage(StorageService storageService, Access access, Credential accessCredential) throws ServiceException {
		
		StorageSave storageSave = (StorageSave) ServiceFactory.createService(ServiceIF.STORAGE_SAVE);
		
		Credentials credentials = new Credentials();
		if(accessCredential != null)
			credentials.setValue(accessCredential.getKeypass());
		
		storageSave.setUrl(new URL(storageService.getLink(), credentials));
		storageSave.setTimeout(getMessageValidity());
		
		return sendMessage(storageSave, null, access.getMail());
	}

	@Override
	public List<String> sendStorage(StorageService storageService, List<Access> accesses, Credential credential) throws ServiceException {
		
		List<String> messageIDs = new ArrayList<>();
		
		StorageSave storageSave = (StorageSave) ServiceFactory.createService(ServiceIF.STORAGE_SAVE);
		
		Credentials credentials = new Credentials();
		if(credential != null)
			credentials.setValue(credential.getKeypass());
		
		URL url = new URL(storageService.getLink(), credentials);
		storageSave.setUrl(url);
		storageSave.setTimeout(getMessageValidity());
		
		Iterator<Access> it = accesses.iterator();
		while (it.hasNext()) {
			
			Access access = it.next();
			
			messageIDs.add(sendMessage(storageSave, null, access.getMail()));
			
			
		}
		
		return messageIDs;
	}

	@Override
	public List<String> sendStorages(List<StorageService> storageServices, List<Access> accesses, Credential credential) throws ServiceException {
		List<String> messageIDs = new ArrayList<>();
		
		Iterator<StorageService> it = storageServices.iterator();
		while (it.hasNext()) {

			StorageService registry = it.next();

			messageIDs.addAll(sendStorage(registry, accesses, credential));

		}
		
		return messageIDs;
	}
	
	
	
	
	@Override
	public List<StorageService> getStorages(Date initialDate, Date finalDate, String messageID) throws ServiceException {
		
		List<StorageService> services = new ArrayList<>();
		
		Iterator<ServiceIF> iterator = getServices(initialDate, finalDate, messageID, ServiceIF.STORAGE_SAVE).iterator();
		while (iterator.hasNext()) {
			
			StorageSave storage = (StorageSave) iterator.next();
			
			if(storage.getUrl() != null && !storage.getUrl().equals("")){
				StorageService service = new StorageService();
				service.setMessageID(storage.getMessageID());
				service.setLink(storage.getUrl().getValue());
				services.add(service);
			}
		}
		return services;
	}
	
	@Override
	public String sendStorageResult(String originalMessageID, StorageService storageService) throws ServiceException {
		
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
		
		result.setOriginalMessageID(originalMessageID);
		result.setTimestamp(Util.singleton().getDataString(Calendar.getInstance().getTime()));
		
		List<Result> results = new ArrayList<Result>();
		results.add(result);
		storageResult.setResult(results);

		MessageIF message = getMessage(originalMessageID);
		if(message != null){
			String mailTo = (String) message.getMailTag(MailXTags.DISPOSITION_NOTIFICATION_TO_X_TAG);
			return sendMessage(storageResult, null, mailTo);
			
		}
		return null;
		
	}
	
	@Override
	public List<StorageService> getStorageResults(Date initialDate, Date finalDate, String messageID, String originalMessageID) throws ServiceException {
		
		List<StorageService> services = new ArrayList<>();
		
		List<MessageIF> messages = getMessages(initialDate, finalDate, messageID, ServiceIF.STORAGE_RESULT);
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
					
					try {
						StorageService service = new StorageService();
						service.setHost(MailXTags.getDomain(xMessageID));
						service.setMessageID(xMessageID);
						service.setStatus(result.getCompleted().getStatus());
						services.add(service);
					} catch (ContentBuilderException e) {
						Util.getLogger(this).error(e.getMessage());
						e.printStackTrace();
					}
					
				}

			}
			
		}
		
		return services;
		
	}

	@Override
	public String sendCertificate(File certificate, Access access) throws ServiceException {

		
		CertificateRequest certificateRequest = (CertificateRequest) ServiceFactory.createService(ServiceIF.CERTIFICATE_REQUEST);
		certificateRequest.setMail(getMailHeadBuilder().getFrom());
		certificateRequest.setDomain(getUrlGenerator().getHost());
		certificateRequest.setPort(getUrlGenerator().getPort());

		return sendMessage(certificateRequest, certificate, access.getMail());
		
			
		
	}
	
	@Override
	public List<Access> getCertificates(Date initialDate, Date finalDate, String messageID) throws ServiceException {
		
		
		List<Access> accesses = new ArrayList<Access>();
		
		List<MessageIF> messages = getMessages(initialDate, finalDate, messageID, ServiceIF.CERTIFICATE_REQUEST);
		Iterator<MessageIF> iterator = messages.iterator();
		while (iterator.hasNext()) {
			MessageIF message = (MessageIF) iterator.next();
			
			//Exists only one service on MAP
			if(message.getService() != null){
				CertificateRequest certificateRequest = (CertificateRequest) message.getService();
				byte[] certificate = message.getAttach();
				Access access = new Access();
				access.setHost(certificateRequest.getDomain());
				access.setPort(certificateRequest.getPort());
				access.setMail(certificateRequest.getMail());
				access.setCertificate(certificate);
				accesses.add(access);
			}
			
		}
			
		return accesses;
	}
	
	@Override
	public String sendCertificateResult(File certificate, Access access, String status, Credential credential) throws ServiceException {
		
		CertificateResult certificateResult = (CertificateResult) ServiceFactory.createService(ServiceIF.CERTIFICATE_RESULT);
		certificateResult.setMail(access.getMail());
		certificateResult.setDomain(access.getHost());
		certificateResult.setPort(access.getPort());
		if(credential != null)
			certificateResult.setCredential(credential.getKeypass());
		
		certificateResult.setStatus(status);
		
		return sendMessage(certificateResult, certificate, access.getMail());

			
		
	}
	
	@Override
	public String sendCertificateError(Access access, String status) throws ServiceException {
		
		CertificateResult certificateResult = (CertificateResult) ServiceFactory.createService(ServiceIF.CERTIFICATE_RESULT);
		certificateResult.setMail(access.getMail());
		certificateResult.setDomain(access.getHost());
		certificateResult.setPort(access.getPort());
		certificateResult.setStatus(status);
		
		return sendMessage(certificateResult, null, access.getMail());
		
			
		
	}
	
	
	@Override
	public String sendCertificateConfirm(Access access, String status, Credential credential) throws ServiceException {
		
		CertificateConfirm certificateConfirm = (CertificateConfirm) ServiceFactory.createService(ServiceIF.CERTIFICATE_CONFIRM);
		certificateConfirm.setMail(access.getMail());
		certificateConfirm.setDomain(access.getHost());
		certificateConfirm.setPort(access.getPort());
		if(credential != null)
			certificateConfirm.setCredential(credential.getKeypass());
		
		certificateConfirm.setStatus(status);
		
		return sendMessage(certificateConfirm, null, access.getMail());

			
		
	}
	
	
	@Override
	public List<Access> getCertificateResults(Date initialDate, Date finalDate, String messageID) throws ServiceException {
		
		List<Access> accesses = new ArrayList<Access>();
		
		Iterator<MessageIF> iterator = getMessages(initialDate, finalDate, messageID, ServiceIF.CERTIFICATE_RESULT).iterator();
		while (iterator.hasNext()) {
			MessageIF message = (MessageIF)iterator.next();
			
			if(message.getService() != null){
				CertificateResult certificateResult = (CertificateResult) message.getService();
				byte[] certificate = message.getAttach();
				Access access = new Access();
				access.setHost(certificateResult.getDomain());
				access.setPort(certificateResult.getPort());
				access.setMail(certificateResult.getMail());
				access.setCertificateStatus(certificateResult.getStatus());
				access.setCertificate(certificate);
				
				Credential credential = new Credential();
				credential.setKeypass(certificateResult.getCredential());
				credential.setOwner(CredentialUtil.getDomain());
				credential.setDomain(access);
				access.addDomainCredential(credential);
				
				accesses.add(access);
			}
		}
		return accesses;
	}
	
	@Override
	public List<Access> getCertificateConfirms(Date initialDate, Date finalDate, String messageID) throws ServiceException {
		
		List<Access> accesses = new ArrayList<Access>();
		
		Iterator<MessageIF> iterator = getMessages(initialDate, finalDate, messageID, ServiceIF.CERTIFICATE_CONFIRM).iterator();
		while (iterator.hasNext()) {
			MessageIF message = (MessageIF)iterator.next();
			
			if(message.getService() != null){
				CertificateConfirm certificateConfirm = (CertificateConfirm) message.getService();

				Access access = new Access();
				access.setHost(certificateConfirm.getDomain());
				access.setPort(certificateConfirm.getPort());
				access.setMail(certificateConfirm.getMail());
				access.setCertificateStatus(certificateConfirm.getStatus());
				
				Credential credential = new Credential();
				credential.setKeypass(certificateConfirm.getCredential());
				credential.setOwner(CredentialUtil.getDomain());
				credential.setDomain(access);
				access.addDomainCredential(credential);
				
				accesses.add(access);
			}
		}
		return accesses;
	}
	
	
	@Override
	public String sendRequest(RequestServiceAccess requestServiceAccess, Credential accessCredential) throws ServiceException {
		
		RequestPut requestPut = (RequestPut) ServiceFactory.createService(ServiceIF.REQUEST_PUT);
		
		
		Credentials credentials = new Credentials();
		if(accessCredential != null)
			credentials.setValue(accessCredential.getKeypass());
		
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
			
		return sendMessage(requestPut, null, requestServiceAccess.getAccess().getMail());
			
		
	}
	
	@Override
	public String sendRequest(RequestService requestService, Access access, Credential accessCredential) throws ServiceException {
		
		RequestPut requestPut = (RequestPut) ServiceFactory.createService(ServiceIF.REQUEST_PUT);
		
		
		Credentials credentials = new Credentials();
		if(accessCredential != null)
			credentials.setValue(accessCredential.getKeypass());
		
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
		requestPut.setTimeout(getMessageValidity());
			
		return sendMessage(requestPut, null, access.getMail());
	}

	@Override
	public List<String> sendRequest(RequestService requestService, List<Access> accesses, Credential credential) throws ServiceException {
		
		List<String> messageIDs = new ArrayList<>();
		
		RequestPut requestPut = (RequestPut) ServiceFactory.createService(ServiceIF.REQUEST_PUT);
		
		Credentials credentials = new Credentials();
		if(credential != null)
			credentials.setValue(credential.getKeypass());
		
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
			
			messageIDs.add(sendMessage(requestPut, null, access.getMail()));
			
		}
		
		return messageIDs;
	}

	@Override
	public List<String> sendRequests(List<RequestService> requestServices, List<Access> accesses, Credential credential) throws ServiceException {
		List<String> messageIDs = new ArrayList<>();
		
		Iterator<RequestService> it = requestServices.iterator();
		while (it.hasNext()) {

			RequestService registry = it.next();

			messageIDs.addAll(sendRequest(registry, accesses, credential));

		}
		
		return messageIDs;
	}
	
	
	@Override
	public List<RequestService> getRequests(Date initialDate, Date finalDate, String messageID) throws ServiceException {
		
		List<RequestService> services = new ArrayList<>();
		
		Iterator<ServiceIF> iterator = getServices(initialDate, finalDate, messageID, ServiceIF.REQUEST_PUT).iterator();
		while (iterator.hasNext()) {
			RequestPut request = (RequestPut) iterator.next();
			if(request.getUrl() != null && !request.getUrl().equals("")){
				RequestService service = new RequestService();
				service.setMessageID(request.getMessageID());
				service.setLink(request.getUrl().getValue());
				services.add(service);
			}
		}
		return services;
	}
	
	@Override
	public String sendRequestResult(String originalMessageID, RequestService requestService) throws ServiceException {
		
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
		
		result.setOriginalMessageID(originalMessageID);
		result.setTimestamp(Util.singleton().getDataString(Calendar.getInstance().getTime()));
		
		List<Result> results = new ArrayList<Result>();
		results.add(result);
		requestResult.setResult(results);
		
		MessageIF message = getMessage(originalMessageID);
		if(message != null){
			String mailTo = (String) message.getMailTag(MailXTags.DISPOSITION_NOTIFICATION_TO_X_TAG);
			return sendMessage(requestResult, null, mailTo);
			
		}
		
		return null;
		
	}
	
	@Override
	public List<RequestService> getRequestResults(Date initialDate, Date finalDate, String messageID, String originalMessageID) throws ServiceException {
		
		List<RequestService> services = new ArrayList<>();
		
		List<MessageIF> messages = getMessages(initialDate, finalDate, messageID, ServiceIF.REQUEST_RESULT);
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
					
					try {
						RequestService service = new RequestService();
						service.setHost(MailXTags.getDomain(xMessageID));
						service.setMessageID(xMessageID);
						service.setStatus(result.getCompleted().getStatus());
						services.add(service);
					} catch (ContentBuilderException e) {
						Util.getLogger(this).error(e.getMessage());
						e.printStackTrace();
					}
					
				}

			}
			
		}
		
		return services;
		
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
	
	private List<MessageIF> getMessages(Date initialDate, Date finalDate, String messageID, int type) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not get links: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		FilterIF filter = new SMTPFilter();
		filter.setInitialDate(initialDate);
		filter.setFinalDate(finalDate);
		filter.setIdMessage(messageID);
		filter.setServiceType(type);
		
		
		try {
			return ServiceProcessor.receiveMessages(iap.getReceiveProperties(), mailAuthenticator, null, null, filter);
		} catch (ServiceCreationException e) {
			e.printStackTrace();
		} catch (PropertyNotFoundException e) {
			e.printStackTrace();
		}
		return new ArrayList<MessageIF>();
		
		
	}
	
	private MessageIF getMessage(String originalMessageID) throws ServiceException {
		if(propertiesConfigPath == null){
			String errMsg = "Could not get links: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		FilterIF filter = new SMTPFilter();
		filter.setIdMessage(originalMessageID);
		
		try {
			List<MessageIF> messages = ServiceProcessor.receiveMessages(iap.getReceiveProperties(), mailAuthenticator, null, null, filter);
			
			if (messages.size() > 1) 
				throw new ServiceException(new Exception("Found more than one message for ID: " + originalMessageID));
			
			if(messages.size() > 0)
				return messages.get(0);
			
		} catch (ServiceCreationException | PropertyNotFoundException e) {
			throw new ServiceException(e);
		}
		

		return null;
		
		
	}
	
	private String sendMessage(ServiceIF service, File attach, String mail) throws ServiceException{
		if(propertiesConfigPath == null){
			String errMsg = "Could not send study: invalid properties's path ";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		DicomMessageProperties iap = DicomMessageProperties.getInstance();
		iap.load(propertiesConfigPath);
		
		try {
			return ServiceProcessor.sendMessage(service, attach, mail, iap.getSendProperties(), mailAuthenticator, mailHeadBuilder, mailContentBuilder);
		} catch (ServiceCreationException | PropertyNotFoundException e) {
			throw new ServiceException(e);
		}
		
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

	public String getMessageValidity() {
		return messageValidity;
	}

	public void setMessageValidity(String messageValidity) {
		this.messageValidity = messageValidity;
	}
	
	
	
	


}

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

package br.ufpb.dicomflow.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.ufpb.dicomflow.bean.ControllerProperty;
import br.ufpb.dicomflow.bean.Credential;
import br.ufpb.dicomflow.bean.RequestService;
import br.ufpb.dicomflow.bean.RequestServiceAccess;
import br.ufpb.dicomflow.service.MessageServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.CredentialUtil;
import br.ufpb.dicomflow.util.Util;


public class VerifyRequestResults {
	
	private static final String  DAILY_STRATEGY = "1";
	private static final String  INTERVAL_STRATEGY = "2";
	private static final String  CURRENT_STUDY_STRATEGY = "3";
	
	private String initialDate;
	private String finalDate;
	private String modalities;
	private String strategy;

	public void execute() {
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("SENDING PENDING NEW STUDIES...");
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		
		List<RequestServiceAccess> ras = persistentService.selectAll("status", RequestService.PENDING, RequestServiceAccess.class);
		Util.getLogger(this).debug("TOTAL REGISTRY-ACCESS: " + ras.size());
		
		ControllerProperty currentDateProperty = (ControllerProperty) persistentService.select("property", ControllerProperty.REQUEST_VERIFY_CURRENT_DATE_PROPERTY, ControllerProperty.class);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		Iterator<RequestServiceAccess> it = ras.iterator();
		while (it.hasNext()) {
			RequestServiceAccess requestServiceAccess = (RequestServiceAccess) it.next();
			if(requestServiceAccess.getRequestService().getAction().equals(RequestService.PUT)){
				requestServiceAccess.setUploadAttempt(requestServiceAccess.getUploadAttempt()+1);
//				if(requestServiceAccess.getUploadAttempt() <= messageService.getMaxAttempts()){
					
					List<RequestService> results = new ArrayList<>();
					
					try {
						
						//verifies the retrieve straegy
						if(strategy.equals(DAILY_STRATEGY)){
							
							Date currentDate = Calendar.getInstance().getTime();
							
							results = messageService.getRequestResults(currentDate, null, null, requestServiceAccess.getMessageID());
							
						}
						if(strategy.equals(INTERVAL_STRATEGY)){
							
							try{
								
								if(initialDate == null || finalDate == null || initialDate.equals("") || finalDate.equals("")){
									throw new Exception("Formato inválido para initialDate ou finalDate. Formato: dd/MM/yyyy");
								}
								
								Date startDate = formatter.parse(initialDate);
								Date finishDate = formatter.parse(finalDate);
								if(currentDateProperty != null){
									Date currentDate = formatter.parse(currentDateProperty.getValue());
									
									if(currentDate.equals(startDate) || currentDate.after(startDate) ){
										startDate = currentDate;
									}
									if(currentDate.after(finishDate)){
										throw new Exception("CurrentDateProperty fora do intervalo.");
									}
								}
								results = messageService.getRequestResults(startDate, finishDate, null, requestServiceAccess.getMessageID());
								
							} catch (Exception e) {
								Util.getLogger(this).error("Could not possible retrieve Studies using Interval Strategy", e);
								e.printStackTrace();
								return;
							}	
							
							
						}
						if(strategy.equals(CURRENT_STUDY_STRATEGY)){
							
								
							try{
								
								if(initialDate == null || initialDate.equals("")){
									throw new Exception("Formato inválido para initialDate. Formato: dd/MM/yyyy");
								}
								
								Date startDate = formatter.parse(initialDate);
								
								if(currentDateProperty != null){
									Date currentDate = formatter.parse(currentDateProperty.getValue());
									
									if(currentDate.equals(startDate) || currentDate.after(startDate) ){
										startDate = currentDate;
									}
									
								}
								
								results = messageService.getRequestResults(startDate, null, null, requestServiceAccess.getMessageID());
								
							} catch (Exception e) {
								Util.getLogger(this).error("Could not possible retrieve Studies using Interval Strategy", e);
								e.printStackTrace();
								return;
							}
							
						}
						
						Iterator<RequestService> it2  = results.iterator();
						while (it2.hasNext()) {
							RequestService resultService = (RequestService) it2.next();
							
							resultService.setPatientName(requestServiceAccess.getRequestService().getPatientName());
							resultService.setPatientGender(requestServiceAccess.getRequestService().getPatientGender());
							resultService.setPatientBirth(requestServiceAccess.getRequestService().getPatientBirth());
							resultService.setStudyIuid(requestServiceAccess.getRequestService().getStudyIuid());
							resultService.setStudyDescription(requestServiceAccess.getRequestService().getStudyDescription());
							resultService.setStudyModality(requestServiceAccess.getRequestService().getStudyModality());
							
							
							
							ServiceLocator.singleton().getFileService().storeReport(resultService.getStudyIuid(), resultService.getFilename(), resultService.getBytes());
							
							resultService.setStatus(RequestService.CLOSED);
							resultService.save();
						}
						
						
					} catch (ServiceException e1) {
						Util.getLogger(this).error("Could not get results: " + e1.getMessage(),e1);
						e1.printStackTrace();
						//TODO verificar se avançar para o próximo é uma estratégia melhor
						//continue;
					}
					
					
						
					Iterator<RequestService> itDomain = results.iterator();
					String domainStatus = null;
					while (itDomain.hasNext()) {
						RequestService request = itDomain.next();
						String accessMail = request.getAccessMail();
						if(requestServiceAccess.getAccess().getMail().equals(accessMail)){
							domainStatus = request.getStatus();
							break;
						}
					}
					//se domainStatus diferente de null, o domain recebeu a URL 
					if(domainStatus != null){
						requestServiceAccess.setStatus(RequestService.CLOSED);
						treatDomainStatus(domainStatus);
					} 
//					else {
//						try {
//							Credential credential = CredentialUtil.getCredential(requestServiceAccess.getAccess(), CredentialUtil.getDomain());
//							messageService.sendRequest(requestServiceAccess.getRequestService(), requestServiceAccess.getAccess(), credential);
//							requestServiceAccess.setStatus(RequestService.PENDING);
//						} catch (ServiceException e) {
//							String status = requestServiceAccess.getUploadAttempt() == messageService.getMaxAttempts() ? RequestService.ERROR : RequestService.PENDING;
//							requestServiceAccess.setStatus(status);
//							Util.getLogger(this).error("Could not send Studies: " + e.getMessage(),e);
//							e.printStackTrace();
//						}
//					}
					
					try {
						requestServiceAccess.save();
					} catch (ServiceException e) {
						Util.getLogger(this).error("Could not save RegistryAccess: " + e.getMessage(),e);
						e.printStackTrace();
					}							
					
//				} else {
//					requestServiceAccess.setStatus(RequestService.CLOSED);
//					try {
//						requestServiceAccess.save();
//					} catch (ServiceException e) {
//						Util.getLogger(this).error("Could not save RegistryAccess: " + e.getMessage(),e);
//						e.printStackTrace();
//					}
//				}
			}
		}
		
		//update currentDate Property
		try {
			if(currentDateProperty == null){
				currentDateProperty = new ControllerProperty();
				currentDateProperty.setProperty(ControllerProperty.REQUEST_VERIFY_CURRENT_DATE_PROPERTY);
			}
			Date currentDate = Calendar.getInstance().getTime();
			currentDateProperty.setValue(formatter.format(currentDate));
			currentDateProperty.save();
			
		} catch (ServiceException e) {
			Util.getLogger(this).error("Could not possible save currentDateProperty", e);
			e.printStackTrace();
		}
		
		Util.getLogger(this).debug("DONE!!");
		long finish = System.currentTimeMillis();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
		System.out.println("JOB:SEND_PENDING_STUDIES_URL - StartInMillis - " + start + " - FinishInMillis - " + finish + " - StartFormated - " + sdfDate.format(new Date(start)) + " - FinishFormated " +  sdfDate.format(new Date(finish)));	
		
	}

	private void treatDomainStatus(String domainStatus) {
		// TODO Auto-generated method stub
		
	}
	
	public String getInitialDate() {
		return initialDate;
	}

	public void setInitialDate(String initialDate) {
		this.initialDate = initialDate;
	}

	public String getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(String finalDate) {
		this.finalDate = finalDate;
	}

	public String getModalities() {
		return modalities;
	}

	public void setModalities(String modalities) {
		this.modalities = modalities;
	}

	public String getStrategy() {
		return strategy;
	}
	
	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}


}
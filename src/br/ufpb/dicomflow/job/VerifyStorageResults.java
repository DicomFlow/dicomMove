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
import br.ufpb.dicomflow.bean.StorageService;
import br.ufpb.dicomflow.bean.StorageServiceAccess;
import br.ufpb.dicomflow.service.MessageServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.CredentialUtil;
import br.ufpb.dicomflow.util.Util;


public class VerifyStorageResults {
	
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
		System.out.println("SENDING PENDING NEW STUDIES...");
		
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		
		List<StorageServiceAccess> ras = persistentService.selectAll("status", StorageService.PENDING, StorageServiceAccess.class);
		Util.getLogger(this).debug("TOTAL REGISTRY-ACCESS: " + ras.size());
		System.out.println("TOTAL REGISTRY-ACCESS: " + ras.size());
		
		ControllerProperty currentDateProperty = (ControllerProperty) persistentService.select("property", ControllerProperty.VERIFY_CURRENT_DATE_PROPERTY, ControllerProperty.class);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		Iterator<StorageServiceAccess> it = ras.iterator();
		while (it.hasNext()) {
			StorageServiceAccess storageServiceAccess = (StorageServiceAccess) it.next();
			if(storageServiceAccess.getStorageService().getAction().equals(StorageService.SAVE)){
				storageServiceAccess.setUploadAttempt(storageServiceAccess.getUploadAttempt()+1);
				if(storageServiceAccess.getUploadAttempt() <= messageService.getMaxAttempts()){
					
					List<StorageService> results = new ArrayList<>();
					
					try {
						
						//verifies the retrieve straegy
						if(strategy.equals(DAILY_STRATEGY)){
							
							Date currentDate = Calendar.getInstance().getTime();
							
							results = messageService.getStorageResults(currentDate, null, null, storageServiceAccess.getMessageID());
							
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
								results = messageService.getStorageResults(startDate, finishDate, null, storageServiceAccess.getMessageID());
								
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
								
								results = messageService.getStorageResults(startDate, null, null, storageServiceAccess.getMessageID());
								
							} catch (Exception e) {
								Util.getLogger(this).error("Could not possible retrieve Studies using Interval Strategy", e);
								e.printStackTrace();
								return;
							}
							
						}
						
						
						
					} catch (ServiceException e1) {
						Util.getLogger(this).error("Could not get results: " + e1.getMessage(),e1);
						e1.printStackTrace();
						//TODO verificar se avançar para o próximo é uma estratégia melhor
						//continue;
					}
					
					
						
					Iterator<StorageService> itDomain = results.iterator();
					String domainStatus = null;
					while (itDomain.hasNext()) {
						StorageService storage = itDomain.next();
						String domain = storage.getHost();
						if(storageServiceAccess.getAccess().getHost().equals(domain)){
							domainStatus = storage.getStatus();
							break;
						}
					}
					//se domainStatus diferente de null, o domain recebeu a URL 
					if(domainStatus != null){
						storageServiceAccess.setStatus(StorageService.CLOSED);
						treatDomainStatus(domainStatus);
					} else {
						try {
							Credential credential = CredentialUtil.getCredential(storageServiceAccess.getAccess(), CredentialUtil.getDomain());
							messageService.sendStorage(storageServiceAccess.getStorageService(), storageServiceAccess.getAccess(), credential);
							storageServiceAccess.setStatus(StorageService.PENDING);
						} catch (ServiceException e) {
							String status = storageServiceAccess.getUploadAttempt() == messageService.getMaxAttempts() ? StorageService.ERROR : StorageService.PENDING;
							storageServiceAccess.setStatus(status);
							Util.getLogger(this).error("Could not send Studies: " + e.getMessage(),e);
							e.printStackTrace();
						}
					}
					
					try {
						storageServiceAccess.save();
					} catch (ServiceException e) {
						Util.getLogger(this).error("Could not save RegistryAccess: " + e.getMessage(),e);
						e.printStackTrace();
					}							
					
				} else {
					storageServiceAccess.setStatus(StorageService.CLOSED);
					try {
						storageServiceAccess.save();
					} catch (ServiceException e) {
						Util.getLogger(this).error("Could not save RegistryAccess: " + e.getMessage(),e);
						e.printStackTrace();
					}
				}
			}
		}
		
		//update currentDate Property
		try {
			if(currentDateProperty == null){
				currentDateProperty = new ControllerProperty();
				currentDateProperty.setProperty(ControllerProperty.VERIFY_CURRENT_DATE_PROPERTY);
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
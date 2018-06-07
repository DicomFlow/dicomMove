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

package br.ufpb.dicomflow.job.ndn;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.ufpb.dicomflow.bean.ControllerProperty;
import br.ufpb.dicomflow.bean.StorageService;
import br.ufpb.dicomflow.service.MessageServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.service.ndn.RouteRegisterServiceIF;
import br.ufpb.dicomflow.service.ndn.UriGenerator;
import br.ufpb.dicomflow.service.ndn.UriGeneratorIF;
import br.ufpb.dicomflow.util.Util;

public class FindNDNStorages {
	
	
	private static final String  DAILY_STRATEGY = "1";
	private static final String  INTERVAL_STRATEGY = "2";
	private static final String  CURRENT_STUDY_STRATEGY = "3";
	
	private String initialDate;
	private String finalDate;
	private String modalities;
	private String strategy;
	
	public void execute() {
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("SEARCHING URLs...");
		System.out.println("SEARCHING URLs...");
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		RouteRegisterServiceIF routeRegisterService = ServiceLocator.singleton().getRouteRegisterService();
		UriGeneratorIF uriGenerator = ServiceLocator.singleton().getUriGenerator();
		
		List<StorageService> storages = new ArrayList<>(); 
		
		ControllerProperty currentDateProperty = (ControllerProperty) persistentService.select("property", ControllerProperty.MAIL_CURRENT_DATE_PROPERTY, ControllerProperty.class);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			
			
			//verifies the retrieve straegy
			if(strategy.equals(DAILY_STRATEGY)){
				
				Date currentDate = Calendar.getInstance().getTime();
				
				storages = messageService.getStorages(currentDate, null, null);
				
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
					storages = messageService.getStorages(startDate, finishDate, null);
					
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
					
					storages = messageService.getStorages(startDate, null, null);
					
				} catch (Exception e) {
					Util.getLogger(this).error("Could not possible retrieve Studies using Interval Strategy", e);
					e.printStackTrace();
					return;
				}
				
			}
			
			
			
			
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		Iterator<StorageService> it = storages.iterator();
		while (it.hasNext()) {
			StorageService storage = it.next();
			String originalMessageID = storage.getMessageID();
			String url = storage.getLink();
			Util.getLogger(this).debug("URL FOUND : " + url);
			System.out.println("URL FOUND : " + url);
			
			routeRegisterService.processRoute(null, storage.getHost(), null, uriGenerator.getPrefix(url));
			
			StorageService storageService = (StorageService) persistentService.selectByParams(new Object[]{"link", "type", "action"},new Object[]{url,StorageService.RECEIVED, StorageService.SAVE}, StorageService.class);
			if(storageService == null){
				
				storageService = new StorageService();
				storageService.setLink(url);
				try {
					URL aURL = new URL(url);
					storageService.setMessageID(originalMessageID);
					storageService.setHost(aURL.getHost());
					storageService.setPort(aURL.getPort());
				} catch (MalformedURLException e) {
					Util.getLogger(this).error(e.getMessage(), e);
					e.printStackTrace();
				}
				storageService.setStatus(StorageService.OPEN);
				storageService.setAction(StorageService.SAVE);
				storageService.setType(StorageService.RECEIVED);
				storageService.setDownloadAttempt(0);
				
				try {
					storageService.save();
					messageService.sendStorageResult(originalMessageID, storageService);
				} catch (ServiceException e) {
					e.printStackTrace();
				}
				
			}

		}
		
		//update currentDate Property
		try {
			if(currentDateProperty == null){
				currentDateProperty = new ControllerProperty();
				currentDateProperty.setProperty(ControllerProperty.MAIL_CURRENT_DATE_PROPERTY);
			}
			Date currentDate = Calendar.getInstance().getTime();
			currentDateProperty.setValue(formatter.format(currentDate));
			currentDateProperty.save();
			
		} catch (ServiceException e) {
			Util.getLogger(this).error("Could not possible save currentDateProperty", e);
			e.printStackTrace();
		}
		
		Util.getLogger(this).debug("DONE");
		long finish = System.currentTimeMillis();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
		System.out.println("JOB:FIND_STUDIES_URL - StartInMillis - " + start + " - FinishInMillis - " + finish + " - StartFormated - " + sdfDate.format(new Date(start)) + " - FinishFormated " +  sdfDate.format(new Date(finish)));	
		
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

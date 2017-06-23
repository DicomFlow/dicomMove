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
import java.util.StringTokenizer;

import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.bean.ControllerProperty;
import br.ufpb.dicomflow.bean.Credential;
import br.ufpb.dicomflow.bean.PatientIF;
import br.ufpb.dicomflow.bean.RequestService;
import br.ufpb.dicomflow.bean.RequestServiceAccess;
import br.ufpb.dicomflow.bean.ServicePermission;
import br.ufpb.dicomflow.bean.StorageService;
import br.ufpb.dicomflow.bean.StorageServiceAccess;
import br.ufpb.dicomflow.bean.StudyIF;
import br.ufpb.dicomflow.service.MessageServiceIF;
import br.ufpb.dicomflow.service.PacsPersistentServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.service.UrlGeneratorIF;
import br.ufpb.dicomflow.util.CredentialUtil;
import br.ufpb.dicomflow.util.Util;


public class PrepareRequests {
	
	private static final String  DAILY_STRATEGY = "1";
	private static final String  INTERVAL_STRATEGY = "2";
	private static final String  CURRENT_STUDY_STRATEGY = "3";
	
	private String initialDate;
	private String finalDate;
	private String modalities;
	private String strategy;

	public void execute(){
		
		long start = System.currentTimeMillis();
		
		Util.getLogger(this).debug("SEARCHING NEW STUDIES...");
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		
		List<Access> accesses = new ArrayList<Access>();
		try {
			accesses = persistentService.selectAll(Access.class);
			Util.getLogger(this).debug("TOTAL ACCESSES: " + accesses.size());
		} catch (ServiceException e) {
			Util.getLogger(this).error("Could not possible select accesses",e);
			e.printStackTrace();
		}
		
		if(accesses.size() != 0){
			insertRegistries(accesses);
		}
		
		Util.getLogger(this).debug("DONE!!");
		long finish = System.currentTimeMillis();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
		System.out.println("JOB:FIND_STUDIES - StartInMillis - " + start + " - FinishInMillis - " + finish + " - StartFormated - " + sdfDate.format(new Date(start)) + " - FinishFormated " +  sdfDate.format(new Date(finish)));	
		
	}

	private void insertRegistries(List<Access> accesses) {
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		
		UrlGeneratorIF urlGenerator = ServiceLocator.singleton().getUrlGenerator();
		
		PacsPersistentServiceIF pacsPersistentservice = ServiceLocator.singleton().getPacsPersistentService();
		Session session = pacsPersistentservice.createSession();
		Transaction tx = session.beginTransaction();
		
		ScrollableResults studies = null;
		
		ControllerProperty currentDateProperty = (ControllerProperty) persistentService.select("property", ControllerProperty.REQUEST_CURRENT_DATE_PROPERTY, ControllerProperty.class);
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		
		//verifies the retrieve straegy
		if(strategy.equals(DAILY_STRATEGY)){
			
			Date currentDate = Calendar.getInstance().getTime();
			
			studies = getStudies(session, currentDate, currentDate, modalities);
			
		}
		if(strategy.equals(INTERVAL_STRATEGY)){
			
			try {
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
				studies = getStudies(session, startDate, finishDate, modalities);
				
			} catch (Exception e) {
				tx.commit();
				session.close();
				Util.getLogger(this).error("Could not possible retrieve Studies using Interval Strategy", e);
				e.printStackTrace();
				return;
			}
			
		}
		if(strategy.equals(CURRENT_STUDY_STRATEGY)){
			try {
				
				
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
				
				studies = getStudies(session, startDate, null, modalities);
				
			} catch (Exception e) {
				tx.commit();
				session.close();
				Util.getLogger(this).error("Could not possible retrieve Studies using Current Study Strategy", e);
				e.printStackTrace();
				return;
			}
		}

		
		if(studies == null){
			tx.commit();
			session.close();
			return;
		}
		
		int count = 0;
		while (studies.next()) {
			
			StudyIF study = (StudyIF) studies.get(0);
			
			//If the study has already been mapped, it does not create the request service
			RequestService requestServiceDB = (RequestService) persistentService.select("studyIuid", study.getStudyIuid(), RequestService.class);
			if(requestServiceDB != null){
				continue;
			}
			
			
			
			PatientIF patient = study.getPatientIF();
			
			RequestService requestService = new RequestService(urlGenerator.getURL(study));
			requestService.setType(RequestService.SENT);
			requestService.setAction(RequestService.PUT);
			requestService.setStatus(RequestService.OPEN);
			requestService.setStudyIuid(study.getStudyIuid());
			requestService.setStudyModality(study.getModalitiesInStudy());
			requestService.setStudyDescription(study.getStudyDescription());
			requestService.setPatientID(patient.getPatientId());
			requestService.setPatientName(patient.getPatientName());
			requestService.setPatientGender(patient.getPatientSex());
			requestService.setPatientBirth(patient.getPatientBirthDate());
			
			try {
				requestService.save();
			} catch (ServiceException e) {
				Util.getLogger(this).error("Could not possible save registry", e);
				e.printStackTrace();
			}
			
			Iterator<Access> it2 = accesses.iterator();
			
			while (it2.hasNext()) {
				Access access = (Access) it2.next();
				
				
				if(verifyAccess(access, study, ServicePermission.REQUEST_SERVICE)){
					RequestServiceAccess ra = new RequestServiceAccess(requestService, access);
					ra.setStatus(RequestService.OPEN);
					ra.setUploadAttempt(0);
					ra.setValidity("");
	//				ra.setCredential(CredentialUtil.createCredential(access).getKey());
					try {
						ra.save();
					} catch (ServiceException e) {
						Util.getLogger(this).error("Could not possible save registry-access biding", e);
						e.printStackTrace();
					}
				}
				
			}
			
			//update currentDate Property
			try {
				if(currentDateProperty == null){
					currentDateProperty = new ControllerProperty();
					currentDateProperty.setProperty(ControllerProperty.REQUEST_CURRENT_DATE_PROPERTY);
				}
				if(study.getStudyDateTime() != null){
					currentDateProperty.setValue(formatter.format(study.getStudyDateTime()));
					currentDateProperty.save();
				}
			} catch (ServiceException e) {
				Util.getLogger(this).error("Could not possible save currentDateProperty", e);
				e.printStackTrace();
			}
			
			 //flush a batch of updates and release memory:
			if ( ++count % 20 == 0 ) {
		        session.flush();
		        session.clear();
		    }
			
		}
		
		tx.commit();
		session.close();
		
	}
	
	private ScrollableResults getStudies(Session session, Date initialDate, Date finalDate, String modalities) {
		
		
		PacsPersistentServiceIF pacsPersistentservice = ServiceLocator.singleton().getPacsPersistentService();
		
		
		try {
			
			
			List<String> modalityList = new ArrayList<String>();
			
			if(modalities != null && !modalities.equals("*")){
			
				StringTokenizer tokenizer = new StringTokenizer(modalities, ",");
				while (tokenizer.hasMoreTokens()) {
					modalityList.add(tokenizer.nextToken());
				}
			}
			
			
			return  pacsPersistentservice.selectAllStudiesScrollable(session, initialDate, finalDate, modalityList);
		
		} catch (Exception e) {
			Util.getLogger(this).error(e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private void insertRegistries(List<StudyIF> studies, List<Access> accesses) {
		UrlGeneratorIF urlGenerator = ServiceLocator.singleton().getUrlGenerator();
		PacsPersistentServiceIF pacsPersistentservice = ServiceLocator.singleton().getPacsPersistentService();
		
		Iterator<StudyIF> it = studies.iterator();
		while (it.hasNext()) {
			
			StudyIF study = (StudyIF) it.next();
			
			PatientIF patient = study.getPatientIF();
			
			RequestService requestService = new RequestService(urlGenerator.getURL(study));
			requestService.setType(RequestService.SENT);
			requestService.setAction(RequestService.PUT);
			requestService.setStatus(RequestService.OPEN);
			requestService.setStudyIuid(study.getStudyIuid());
			requestService.setStudyModality(study.getModalitiesInStudy());
			requestService.setStudyDescription(study.getStudyDescription());
			requestService.setPatientID(patient.getPatientId());
			requestService.setPatientName(patient.getPatientName());
			requestService.setPatientGender(patient.getPatientSex());
			requestService.setPatientBirth(patient.getPatientBirthDate());
			
			try {
				requestService.save();
			} catch (ServiceException e) {
				Util.getLogger(this).error("Could not possible save registry", e);
				e.printStackTrace();
			}
			
			Iterator<Access> it2 = accesses.iterator();
			
			while (it2.hasNext()) {
				Access access = (Access) it2.next();
				
				
				if(verifyAccess(access, study, ServicePermission.REQUEST_SERVICE)){
					RequestServiceAccess ra = new RequestServiceAccess(requestService, access);
					ra.setStatus(RequestService.OPEN);
					ra.setUploadAttempt(0);
					ra.setValidity("");
	//				ra.setCredential(CredentialUtil.createCredential(access).getKey());
					try {
						ra.save();
					} catch (ServiceException e) {
						Util.getLogger(this).error("Could not possible save registry-access biding", e);
						e.printStackTrace();
					}
				}
				
			}														
		}
		
	}

	private boolean verifyAccess(Access access, StudyIF study, String serviceType) {
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		Credential credential  = CredentialUtil.getCredential(access, CredentialUtil.getDomain());
		ServicePermission servicePermission = (ServicePermission) persistentService.selectByParams(new String[]{"description", "credential"}, new Object[]{serviceType, credential} , ServicePermission.class);
		
		//verifica se o acesso tem permissão ao serviço e ao estudo especificados
		return servicePermission != null && (servicePermission.getModalities().contains(study.getModalitiesInStudy()) || servicePermission.getModalities().contains("*"));
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
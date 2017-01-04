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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import br.ufpb.dicomflow.bean.Access;
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


public class PrepareStorages {

	public void execute(){
		
		long start = System.currentTimeMillis();
		
		Util.getLogger(this).debug("SEARCHING NEW STUDIES...");
		
		PacsPersistentServiceIF pacsPersistentservice = ServiceLocator.singleton().getPacsPersistentService();
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		
		List<StorageService> storageServices = persistentService.selectAllByParams(new String[]{"type", "action"}, new Object[]{StorageService.SENT, StorageService.SAVE}, StorageService.class);
		
		List<String> registredStudiesIuids = getStudiesIuids(storageServices);
		List<StudyIF> studies = new ArrayList<StudyIF>();
		
		studies = pacsPersistentservice.selectAllStudiesNotIn(registredStudiesIuids);
		Util.getLogger(this).debug("TOTAL STUDIES: " + studies.size());
		
		
		List<Access> accesses = new ArrayList<Access>();
		try {
			accesses = persistentService.selectAll(Access.class);
			Util.getLogger(this).debug("TOTAL ACCESSES: " + accesses.size());
		} catch (ServiceException e) {
			Util.getLogger(this).error("Could not possible select accesses",e);
			e.printStackTrace();
		}
		
		insertRegistries(studies, accesses);
		
		Util.getLogger(this).debug("DONE!!");
		long finish = System.currentTimeMillis();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
		System.out.println("JOB:FIND_STUDIES - StartInMillis - " + start + " - FinishInMillis - " + finish + " - StartFormated - " + sdfDate.format(new Date(start)) + " - FinishFormated " +  sdfDate.format(new Date(finish)));	
		
	}

	private List<String> getStudiesIuids(List<StorageService> registries) {
		List<String> iuids = new ArrayList<String>();
		Iterator<StorageService> it = registries.iterator();
		while (it.hasNext()) {
			StorageService registry = (StorageService) it.next();
			iuids.add(registry.getStudyIuid());
			
		}
		return iuids;
	}

	private void insertRegistries(List<StudyIF> studies, List<Access> accesses) {
		MessageServiceIF messageService = ServiceLocator.singleton().getMessageService();
		
		UrlGeneratorIF urlGenerator = ServiceLocator.singleton().getUrlGenerator();
		
		Iterator<StudyIF> it = studies.iterator();
		while (it.hasNext()) {
			
			StudyIF study = (StudyIF) it.next();
			
			StorageService storageService = new StorageService(urlGenerator.getURL(study));
			storageService.setType(StorageService.SENT);
			storageService.setAction(StorageService.SAVE);
			storageService.setStudyIuid(study.getStudyIuid());
			storageService.setStatus(StorageService.OPEN);
			try {
				storageService.save();
			} catch (ServiceException e) {
				Util.getLogger(this).error("Could not possible save registry", e);
				e.printStackTrace();
			}
			
			Iterator<Access> it2 = accesses.iterator();
			
			while (it2.hasNext()) {
				Access access = (Access) it2.next();
				
				
				if(verifyAccess(access, study, ServicePermission.STORAGE_SERVICE)){
					StorageServiceAccess ra = new StorageServiceAccess(storageService, access);
					ra.setStatus(StorageService.OPEN);
					ra.setUploadAttempt(0);
					ra.setValidity(messageService.getMessageValidity());
//					ra.setCredential(CredentialUtil.createCredential(access).getKey());
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
		ServicePermission servicePermission = (ServicePermission) persistentService.selectByParams(new String[]{"description", "access"}, new Object[]{serviceType, access} , ServicePermission.class);
		//verifica se o acesso tem permiss�o ao servi�o e ao estudo especificados
		return servicePermission != null && (servicePermission.getModalities().contains(study.getModalitiesInStudy()) || servicePermission.getModalities().contains("*"));
	}

}
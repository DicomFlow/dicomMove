package br.ufpb.dicomflow.agent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import br.ufpb.dicomflow.bean.Access;
import br.ufpb.dicomflow.bean.Registry;
import br.ufpb.dicomflow.bean.RegistryAccess;
import br.ufpb.dicomflow.bean.Study;
import br.ufpb.dicomflow.service.MessageService;
import br.ufpb.dicomflow.service.PersistentService;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.service.UrlGeneratorIF;
import br.ufpb.dicomflow.util.Util;


public class FindStudiesAgent implements Job {

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		Util.getLogger(this).debug("SEARCHING NEW STUDIES...");
		
		PersistentService persistentDCM4CHE = ServiceLocator.singleton().getPersistentService();
		PersistentService persistentServiceDICOMMOVE = ServiceLocator.singleton().getPersistentService2();
		
		List<Registry> registries = persistentServiceDICOMMOVE.selectAll("type", Registry.SENT, Registry.class);
		
		List<String> registredStudiesIuids = getStudiesIuids(registries);
		List<Study> studies = new ArrayList<Study>();
		
		studies = persistentDCM4CHE.selectAllNotIn("studyIuid", registredStudiesIuids, Study.class);
		Util.getLogger(this).debug("TOTAL STUDIES: " + studies.size());
		
		
		List<Access> accesses = new ArrayList<Access>();
		try {
			accesses = persistentServiceDICOMMOVE.selectAll(Access.class);
			Util.getLogger(this).debug("TOTAL ACCESSES: " + accesses.size());
		} catch (ServiceException e) {
			Util.getLogger(this).error("Could not possible select accesses",e);
			e.printStackTrace();
		}
		
		insertRegistries(studies, accesses);
		
		Util.getLogger(this).debug("DONE!!");	
	}

	private List<String> getStudiesIuids(List<Registry> registries) {
		List<String> iuids = new ArrayList<String>();
		Iterator<Registry> it = registries.iterator();
		while (it.hasNext()) {
			Registry registry = (Registry) it.next();
			iuids.add(registry.getStudyIuid());
			
		}
		return iuids;
	}

	private void insertRegistries(List<Study> studies, List<Access> accesses) {
		UrlGeneratorIF urlGenerator = ServiceLocator.singleton().getUrlGenerator();
		
		Iterator<Study> it = studies.iterator();
		while (it.hasNext()) {
			
			Study study = (Study) it.next();
			
			Registry registry = new Registry(urlGenerator.getURL(study));
			registry.setType(Registry.SENT);
			registry.setStudyIuid(study.getStudyIuid());
			registry.setStatus(Registry.OPEN);
			try {
				registry.save();
			} catch (ServiceException e) {
				Util.getLogger(this).error("Could not possible save registry", e);
				e.printStackTrace();
			}
			
			Iterator<Access> it2 = accesses.iterator();
			
			while (it2.hasNext()) {
				Access access = (Access) it2.next();
				RegistryAccess ra = new RegistryAccess(registry, access);
				ra.setStatus(Registry.OPEN);
				ra.setUploadAttempt(0);
				ra.setValidity("");
//				ra.setCredential(credential);
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
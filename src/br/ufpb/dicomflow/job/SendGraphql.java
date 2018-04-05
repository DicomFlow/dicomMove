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
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import br.ufpb.dicomflow.bean.Credential;
import br.ufpb.dicomflow.bean.GraphqlService;
import br.ufpb.dicomflow.bean.GraphqlServiceAccess;
import br.ufpb.dicomflow.bean.PatientIF;
import br.ufpb.dicomflow.bean.RequestService;
import br.ufpb.dicomflow.bean.SeriesIF;
import br.ufpb.dicomflow.bean.StudyIF;
import br.ufpb.dicomflow.integrationAPI.message.xml.ServiceIF;
import br.ufpb.dicomflow.service.PacsPersistentServiceIF;
import br.ufpb.dicomflow.service.PersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.CredentialUtil;
import br.ufpb.dicomflow.util.Util;
import br.ufpb.dicomflow.ws.graphql.GraphqlClient;
import br.ufpb.dicomflow.ws.graphql.GraphqlException;
import br.ufpb.dicomflow.ws.graphql.Mutation;
import br.ufpb.dicomflow.ws.json.ServiceJSON;
import br.ufpb.dicomflow.ws.json.UrlJSON;
import br.ufpb.dicomflow.ws.json.UserJSON;


public class SendGraphql {

	private static final int OK = 200;

	public void execute() {
		
		long start = System.currentTimeMillis();
		Util.getLogger(this).debug("SENDING NEW STUDIES...");
		
		PersistentServiceIF persistentService = ServiceLocator.singleton().getPersistentService();
		
		PacsPersistentServiceIF pacsPersistentService = ServiceLocator.singleton().getPacsPersistentService();
		
		List<GraphqlServiceAccess> ras = persistentService.selectAll("status", RequestService.OPEN, GraphqlServiceAccess.class);
		Util.getLogger(this).debug("TOTAL REGISTRY-ACCESS: " + ras.size());
		
		Iterator<GraphqlServiceAccess> it = ras.iterator();
		while (it.hasNext()) {
			GraphqlServiceAccess serviceAccess = (GraphqlServiceAccess) it.next();
			
			try {
				
				Credential credential = CredentialUtil.getCredential(serviceAccess.getAccess(), CredentialUtil.getDomain());
				
				StudyIF studyDB = pacsPersistentService.selectStudy(serviceAccess.getGraphqlService().getStudyIuid());
				PatientIF patientDB = pacsPersistentService.selectPatient(serviceAccess.getGraphqlService().getPatientId());
				List<SeriesIF> seriesDB = pacsPersistentService.selectAllSeries(studyDB);
				
				Set<SeriesIF> series = new HashSet<>();
				series.addAll(seriesDB);
				studyDB.setSeriesIF(series);
				
				Set<StudyIF> studies = new HashSet<>();
				studies.add(studyDB);
				patientDB.setStudiesIF(studies);
				
				Set<PatientIF> patients =  new HashSet<>();
				patients.add(patientDB);
				UrlJSON url = new UrlJSON(serviceAccess.getGraphqlService().getLink(), credential.getKeypass(), patients);
				
				ServiceJSON serviceJSON = ServiceJSON.createService(ServiceIF.REQUEST_PUT);
				
				Set<UrlJSON> urls = new HashSet<>();
				urls.add(url);
				serviceJSON.setUrls(urls);
				
				UserJSON userJSON = new UserJSON(serviceAccess.getAccess().getCode());
				
//				UserJSON userJSON = new UserJSON(serviceAccess.getAccess().getMail(), serviceAccess.getAccess().getMail());
				serviceJSON.setUser(userJSON);
				
				System.out.println("JSON: " + serviceJSON.getJSON());
					
				Mutation mutation = new Mutation();
				mutation.buildQuery(serviceJSON, "createService", "{id}");
				
				System.out.println("MUTATION: " + mutation.getQuery());
				
				GraphqlClient client =  new GraphqlClient(serviceAccess.getAccess().getHost());
				
				Response  response = client.query(mutation);
				
				if(response.getStatus() == OK ){
					serviceAccess.setStatus(GraphqlService.CLOSED);
				}else{
					serviceAccess.setStatus(GraphqlService.PENDING);
				}
				
				System.out.println(response.getStatus() +" - " + response.getStatusInfo());

				
			} catch (GraphqlException e) {
				Util.getLogger(this).error("Could not send Studies: " + e.getMessage(),e);
				e.printStackTrace();
			}
			try {
				serviceAccess.save();
			} catch (ServiceException e) {
				Util.getLogger(this).error("Could not save RegistryAccess: " + e.getMessage(),e);
				e.printStackTrace();
				e.printStackTrace();
			}
			
			
		}
		
		Util.getLogger(this).debug("DONE!!");	
		long finish = System.currentTimeMillis();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
		System.out.println("JOB:SEND_STUDIES_URL - StartInMillis - " + start + " - FinishInMillis - " + finish + " - StartFormated - " + sdfDate.format(new Date(start)) + " - FinishFormated " +  sdfDate.format(new Date(finish)));	
		
	}


}
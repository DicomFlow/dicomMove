package br.ufpb.dicomflow.tests;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Response;

import com.sun.security.ntlm.Client;

import br.ufpb.dicomflow.bean.PatientIF;
import br.ufpb.dicomflow.bean.SeriesIF;
import br.ufpb.dicomflow.bean.dcm4che.Patient;
import br.ufpb.dicomflow.bean.dcm4che.Series;
import br.ufpb.dicomflow.bean.dcm4che.Study;
import br.ufpb.dicomflow.integrationAPI.message.xml.ServiceIF;
import br.ufpb.dicomflow.util.CredentialUtil;
import br.ufpb.dicomflow.ws.graphql.GraphqlClient;
import br.ufpb.dicomflow.ws.graphql.GraphqlException;
import br.ufpb.dicomflow.ws.graphql.Mutation;
import br.ufpb.dicomflow.ws.json.ServiceJSON;
import br.ufpb.dicomflow.ws.json.UrlJSON;
import br.ufpb.dicomflow.ws.json.UserJSON;

public class GraphqlClientTest {
	
	public static void main(String[] args) {
		
		Patient patient = new Patient();
		patient.setPatientName("Danilo");
		patient.setPatientSex("M");
		patient.setPatientBirthDate("19821108");
		
		Study study = new Study();
		study.setModalitiesInStudy("MR");
		study.setStudyDescription("MR do Ombro");
		study.setStudyDateTime(new Date());
		
		Series serie = new Series();
		serie.setSeriesIuid("1.2.840.113704.1.111.3704.1160637289.13");
		serie.setBodyPartExamined("Ombro");
		serie.setSeriesDescription("corte na transversal");
		serie.setNumInstances(100);
		
		serie.setStudy(study);
		
		Set<Series> series = new HashSet<>();
		series.add(serie);
		study.setSeries(series);
		study.setPatient(patient);
		
		Set<Study> studies = new HashSet<>();
		studies.add(study);
		patient.setStudies(studies);
		
		Set<PatientIF> patients =  new HashSet<>();
		patients.add(patient);
		
		UrlJSON url = new UrlJSON("localhost:8080/DicomMove/downloadStudy", CredentialUtil.generateCredentialKey(), patients);
		
		
//		UserJSON userJSON = new UserJSON("Severino Aires", "severino.dicomflow@gmail.com");
		
		UserJSON userJSON = new UserJSON("cjfmu3o9v9yzl01089vrm034w");
		try {
			
			ServiceJSON serviceJSON = ServiceJSON.createService(ServiceIF.REQUEST_PUT);
			
			Set<UrlJSON> urls = new HashSet<>();
			urls.add(url);
			serviceJSON.setUrls(urls);
			
			serviceJSON.setUser(userJSON);
			
			System.out.println("JSON: " + serviceJSON.getJSON());
			
			Mutation mutation = new Mutation();
			mutation.buildQuery(serviceJSON, "createService", "{id}");
			
			System.out.println("MUTATION: " + mutation.getQuery());
			
			GraphqlClient client =  new GraphqlClient();
			
			Response  response = client.query(mutation);
			
			System.out.println(response.getStatus() +" - " + response.getStatusInfo());
			
		} catch (GraphqlException e) {
			e.printStackTrace();
		}
		
		
		
		
		
	}

}

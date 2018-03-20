package br.ufpb.dicomflow.ws.json;

import java.util.Iterator;
import java.util.Set;

import br.ufpb.dicomflow.bean.PatientIF;

public class UrlJSON implements JSONDecorator {
	
	private String value;
	private String credentials;
	private Set<PatientIF> patients;
	
	

	public UrlJSON(String value, String credentials, Set<PatientIF> patients) {
		super();
		this.value = value;
		this.credentials = credentials;
		this.patients = patients;
	}

	@Override
	public String getJSON() {
		
		StringBuilder json = new StringBuilder("");
		
		if(value != null){
			
			
			json.append("credentials: \"");
			json.append(credentials);
			json.append("\", ");
			
			json.append("patients: [ ");
			
			if(patients != null){
				
				Iterator<PatientIF> it = patients.iterator();
				if(it.hasNext()){
					PatientJSON patientJson = new PatientJSON(it.next());
					json.append("{");
					json.append(patientJson.getJSON());
					json.append("}");
					
				}
				while (it.hasNext()) {
					PatientJSON patientJson = new PatientJSON(it.next());
					json.append(", {");
					json.append(patientJson.getJSON());
					json.append("}");
				}
				
			}
			
			json.append("], ");
			
			json.append("value: \"");
			json.append(value);
			json.append("\" ");
		
			return json.toString();
			
		}
		
		return "";
		
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCredentials() {
		return credentials;
	}

	public void setCredentials(String credentials) {
		this.credentials = credentials;
	}

	public Set<PatientIF> getPatients() {
		return patients;
	}

	public void setPatients(Set<PatientIF> patients) {
		this.patients = patients;
	}
	
	

}

package br.ufpb.dicomflow.ws.json;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.TimeZone;

import br.ufpb.dicomflow.bean.PatientIF;
import br.ufpb.dicomflow.bean.StudyIF;

public class PatientJSON implements JSONDecorator{
	
	private PatientIF patient;
	

	public PatientJSON(PatientIF patient) {
		super();
		this.patient = patient;
	}


	@Override
	public String getJSON() {
		StringBuilder json = new StringBuilder("");
		
		if(patient != null){
			
			json.append("name: \"");
			json.append(patient.getPatientName());
			json.append("\", ");
			
			json.append("identifier: \"");
			json.append(patient.getPatientId());
			json.append("\", ");
			
			json.append("gender: \"");
			json.append(patient.getPatientSex());
			json.append("\", ");
			
			json.append("birthdate: \"");
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			json.append(patient.getPatientBirthDateString(formatter));
			json.append("\", ");
			
			json.append("studies: [ ");
			
			if(patient.getStudiesIF() != null){
				
				Iterator<StudyIF> it = patient.getStudiesIF().iterator();
				if(it.hasNext()){
					StudyJSON studyJson = new StudyJSON(it.next());
					json.append("{");
					json.append(studyJson.getJSON());
					json.append("}");
					
				}
				while (it.hasNext()) {
					StudyJSON studyJson = new StudyJSON(it.next());
					json.append("{");
					json.append(studyJson.getJSON());
					json.append("}");
				}
				
			}
			
			json.append("] ");
		
			return json.toString();
			
		}
		
		return "";
	}


	public PatientIF getPatient() {
		return patient;
	}


	public void setPatient(PatientIF patient) {
		this.patient = patient;
	}
	
	

}

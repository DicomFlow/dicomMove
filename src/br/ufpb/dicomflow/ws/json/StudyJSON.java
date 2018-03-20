package br.ufpb.dicomflow.ws.json;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.TimeZone;

import br.ufpb.dicomflow.bean.SeriesIF;
import br.ufpb.dicomflow.bean.StudyIF;

public class StudyJSON implements JSONDecorator {
	
	private StudyIF study;
	
	public StudyJSON(StudyIF study) {
		
		this.study = study;
		
	}

	@Override
	public String getJSON() {

		StringBuilder json = new StringBuilder("");
		
		if(study != null){
			
			json.append("type: \"");
			json.append(study.getModalitiesInStudy());
			json.append("\", ");
			
			json.append("identifier: \"");
			json.append(study.getStudyIuid());
			json.append("\", ");
			
			json.append("description: \"");
			json.append(study.getStudyDescription());
			json.append("\", ");
			
			json.append("datetime: \"");
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			json.append(study.getStudyDateTimeString(formatter));
			json.append("\", ");
			
			json.append("series: [ ");
			
			if(study.getSeriesIF() != null){
				
				Iterator<SeriesIF> it = study.getSeriesIF().iterator();
				if(it.hasNext()){
					SerieJSON serieJson = new SerieJSON(it.next());
					json.append("{");
					json.append(serieJson.getJSON());
					json.append("}");
					
				}
				while (it.hasNext()) {
					SerieJSON serieJson = new SerieJSON(it.next());
					json.append(", {"); 
					json.append(serieJson.getJSON());
					json.append("}");
				}
				
			}
			
			json.append("] ");
		
			return json.toString();
			
		}
		
		return "";
		
	}

	public StudyIF getStudy() {
		return study;
	}

	public void setStudy(StudyIF study) {
		this.study = study;
	}
	
	

}

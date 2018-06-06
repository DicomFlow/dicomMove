package br.ufpb.dicomflow.ws.json;

import br.ufpb.dicomflow.bean.SeriesIF;

public class SerieJSON implements JSONDecorator {
	
	private SeriesIF serie;

	public SerieJSON(SeriesIF serie){
		this.serie = serie;
	}
	
	@Override
	public String getJSON() {
		
		StringBuilder json = new StringBuilder("");
		
		if(serie != null){
			
			json.append("identifier: \"");
			json.append(serie.getSeriesIuid());
			
			if(serie.getBodyPartExamined() != null && !serie.getBodyPartExamined().isEmpty()){
				json.append("\", ");
				json.append("bodypart: \"");
				json.append(serie.getBodyPartExamined());
			}
			
			if(serie.getSeriesDescription() != null && !serie.getSeriesDescription().isEmpty()){
				json.append("\", ");
				json.append("description: \"");
				json.append(serie.getSeriesDescription());
			}
			
			if(serie.getNumInstances() != null && serie.getNumInstances() != 0){
				json.append("\", ");
				json.append("instances: ");
				json.append(serie.getNumInstances());
			}
			
			json.append(" ");
		
			return json.toString();
			
		}
		
		return "";
	}

	public SeriesIF getSerie() {
		return serie;
	}

	public void setSerie(SeriesIF serie) {
		this.serie = serie;
	}
	
	

}

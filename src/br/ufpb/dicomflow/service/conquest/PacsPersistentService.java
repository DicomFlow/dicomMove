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
package br.ufpb.dicomflow.service.conquest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import br.ufpb.dicomflow.bean.InstanceIF;
import br.ufpb.dicomflow.bean.PatientIF;
import br.ufpb.dicomflow.bean.SeriesIF;
import br.ufpb.dicomflow.bean.StudyIF;
import br.ufpb.dicomflow.bean.conquest.Instance;
import br.ufpb.dicomflow.bean.conquest.Patient;
import br.ufpb.dicomflow.bean.conquest.Series;
import br.ufpb.dicomflow.bean.conquest.Study;
import br.ufpb.dicomflow.service.PacsPersistentServiceIF;
import br.ufpb.dicomflow.service.PersistentService;


public class PacsPersistentService extends PersistentService  implements PacsPersistentServiceIF {

    /**
	 * Default constructor.
	 */
	public PacsPersistentService() {
		super();
	}
	
	@Override
	public Session createSession() {
		return this.getSession();
	}
	
	
	
	@Override
	public PatientIF selectPatient(String patientID) {
		return (PatientIF) super.select("patientID", patientID, Patient.class);
	}

	@Override
	public List<StudyIF> selectAllStudiesNotIn(List<String> studiesIuids) {
		
		return super.selectAllNotIn("studyIuid", studiesIuids, Study.class);
	}

	@Override
	public StudyIF selectStudy(String studyIuid) {
		return (StudyIF) super.select("studyIuid", studyIuid, Study.class);
	}

	@Override
	public List<SeriesIF> selectAllSeries(StudyIF study) {
		return super.selectAll("study", study, Series.class);
	}

	@Override
	public List<InstanceIF> selectAllFiles(List<SeriesIF> series) {
		return super.selectAll("series", series, Instance.class);
	}

	@Override
	public List<StudyIF> selectAllStudies(Date initialDate, Date finalDate, List<String> modalities) {
		String query = allStudiesQuery(initialDate, finalDate, modalities);              
		               
		List result = new ArrayList();
		
		Session session = this.getSession();
		try {
			Query consulta = session.createQuery(query);
	        List list  = consulta.list();
	        return list;
		} catch (HibernateException e) {
			this.logger.error("Error retrieving objects", e);
	        e.printStackTrace();
		} finally {
			session.clear();
			session.close();	
		}		                
		return new ArrayList();
	}

	
	@Override
	public ScrollableResults selectAllStudiesScrollable(Session session, Date initialDate, Date finalDate, List<String> modalities) {
		String query = allStudiesQuery(initialDate, finalDate, modalities);              
		               
		ScrollableResults studies = null;
		try {
			studies = session.createQuery(query).setCacheMode(CacheMode.IGNORE).scroll(ScrollMode.FORWARD_ONLY);
	        return studies;
		} catch (Exception e) {
			this.logger.error("Error retrieving objects", e);
	        e.printStackTrace();
		}	                
		return studies;
	}
	
	private String allStudiesQuery(Date initialDate, Date finalDate, List<String> modalities) {
		String query = "from " + Study.class.getName() + " study where 1=1 ";
		
		
		if(initialDate != null){
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			query+=" and study.studyDate >= '" + dateFormat.format(initialDate)+"'";
		}
		if(finalDate != null){
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			query+=" and study.studyDate <= '" + dateFormat.format(finalDate)+"'";
		}
		if(modalities != null && modalities.size() != 0){
			query += "  and study.modalitiesInStudy IN (";
			Iterator it = modalities.iterator();
			if(it.hasNext()){
				String modality = (String) it.next();
				query+=modality;
			}
			while (it.hasNext()) {
				String modality = (String) it.next();
				query+=","+modality;
				
			}
			query+=") order by study.studyDate";
		}
		return query;
	}

	
	
}
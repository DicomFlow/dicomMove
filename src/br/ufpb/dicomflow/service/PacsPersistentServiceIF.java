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

package br.ufpb.dicomflow.service;

import java.util.Date;
import java.util.List;

import org.hibernate.ScrollableResults;
import org.hibernate.Session;

import br.ufpb.dicomflow.bean.InstanceIF;
import br.ufpb.dicomflow.bean.PatientIF;
import br.ufpb.dicomflow.bean.SeriesIF;
import br.ufpb.dicomflow.bean.StudyIF;



public interface PacsPersistentServiceIF {
	
	public static final String ASC = "asc";
	public static final String DESC = "desc";
	
	public Session createSession();
	
	public PatientIF selectPatient(String patientID);
	
	public List<StudyIF> selectAllStudies(Date initialDate, Date finalDate, List<String> modalities);
	
	public ScrollableResults selectAllStudiesScrollable(Session session, Date initialDate, Date finalDate, List<String> modalities);
	
	public List<StudyIF> selectAllStudiesNotIn(List<String> registredStudiesIuids);

	public StudyIF selectStudy(String studyIUID);

	public List<SeriesIF> selectAllSeries(StudyIF study);

	public List<InstanceIF> selectAllFiles(List<SeriesIF> series);

	
	
	
	
}

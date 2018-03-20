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

package br.ufpb.dicomflow.bean;

import java.text.DateFormat;
import java.util.Date;
import java.util.Set;


public interface StudyIF {
	

	public String getStudyIuid();

	public void setStudyIuid(String studyIuid);

	public String getStudyId();

	public void setStudyId(String studyId);

	public Date getStudyDateTime();

	public void setStudyDateTime(Date studyDateTime);

	public String getAccessionNumber();

	public void setAccessionNumber(String accessionNumber);

	public String getModalitiesInStudy();

	public void setModalitiesInStudy(String modalitiesInStudy);
	
	public String getStudyDescription();
	
	public void setStudyDescription(String studyDescription);
	
	public String getReferedPhysician();
	
	public void setReferedPhysician(String referedPhysician);
	
	public PatientIF getPatientIF();
	
	public Set<SeriesIF> getSeriesIF(); 
	
	public void setSeriesIF(Set<SeriesIF> series);
	
	public String getStudyDateTimeString(DateFormat formatter);
	
	
	

}

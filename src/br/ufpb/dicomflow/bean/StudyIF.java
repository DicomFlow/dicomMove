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

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


public interface StudyIF {
	
	public Long getId();

	
	public void setId(Long id);

//	public PatientIF getPatient();
//
//	public void setPatient(PatientIF patient);

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

	public String getSopClassesInStudy();

	public void setSopClassesInStudy(String sopClassesInStudy);

	public Integer getNumberOfStudyRelatedSeries();

	public void setNumberOfStudyRelatedSeries(Integer numberOfStudyRelatedSeries);

	public Integer getNumberOfStudyRelatedInstances();

	public void setNumberOfStudyRelatedInstances(Integer numberOfStudyRelatedInstances);

	public String getRetrieveAETs();

	public void setRetrieveAETs(String retrieveAETs);

	public Integer getAvailability();

	public void setAvailability(Integer availability);

	public Integer getStudyStatus();

	public void setStudyStatus(Integer studyStatus);

	public Date getUpdatedTime();

	public void setUpdatedTime(Date updatedTime);

	public Date getCreatedTime();

	public void setCreatedTime(Date createdTime);

	public byte[] getEncodedAttributes();

	public void setEncodedAttributes(byte[] encodedAttributes);

//	public Set<SeriesIF> getSeries();
//
//	public void setSeries(Set<SeriesIF> series);
	
	

}

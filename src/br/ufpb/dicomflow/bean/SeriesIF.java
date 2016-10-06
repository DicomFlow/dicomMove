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

@Entity
@Table(name="series")
public interface SeriesIF {

	public Long getId();

	
	public void setId(Long id);

//	public StudyIF getStudy();
//
//	public void setStudy(StudyIF study);

	public String getSeriesIuid();

	public void setSeriesIuid(String seriesIuid);

	public String getSeriesNumber();

	public void setSeriesNumber(String seriesNumber);

	public String getModality();

	public void setModality(String modality);

	public String getBodyPartExamined();

	public void setBodyPartExamined(String bodyPartExamined);

	public Date getPpsStartDate();

	public void setPpsStartDate(Date ppsStartDate);

	public String getPpsIuid();

	public void setPpsIuid(String ppsIuid);

	public Integer getNumberOfSeriesRelatedInstances();

	public void setNumberOfSeriesRelatedInstances(Integer numberOfSeriesRelatedInstances);

	public String getSourceAET();

	public void setSourceAET(String sourceAET);

	public String getRetrieveAETs();

	public void setRetrieveAETs(String retrieveAETs);

	public Integer getAvailability();

	public void setAvailability(Integer availability);

	public Integer getSeriesStatus();

	public void setSeriesStatus(Integer seriesStatus);

	public Date getUpdatedTime();

	public void setUpdatedTime(Date updatedTime);

	public Date getCreatedTime();

	public void setCreatedTime(Date createdTime);

	public byte[] getEncodedAttributes();

	public void setEncodedAttributes(byte[] encodedAttributes);

//	public Set<InstanceIF> getInstances();
//
//	public void setInstances(Set<InstanceIF> instances);
	
}

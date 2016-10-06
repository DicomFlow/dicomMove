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



public interface InstanceIF {

	public Long getId();
	
	public void setId(Long id);

//	public SeriesIF getSeries();
//
//	public void setSeries(SeriesIF series);

	public String getSopIuid();

	public void setSopIuid(String sopIuid);

	public String getSopCuid();

	public void setSopCuid(String sopCuid);

	public String getInstanceNumber();

	public void setInstanceNumber(String instanceNumber);

	public Date getContentDateTime();

	public void setContentDateTime(Date contentDateTime);

	public String getRetrieveAETs();

	public void setRetrieveAETs(String retrieveAETs);

	public Integer getAvailability();

	public void setAvailability(Integer availability);

	public Integer getInstanceStatus();

	public void setInstanceStatus(Integer instanceStatus);

	public Boolean getArchived();

	public void setArchived(Boolean archived);

	public Boolean getAllAttributes();

	public void setAllAttributes(Boolean allAttributes);

	public Boolean getCommitment();

	public void setCommitment(Boolean commitment);

	public Date getUpdatedTime();

	public void setUpdatedTime(Date updatedTime) ;

	public Date getCreatedTime();

	public void setCreatedTime(Date createdTime);

	public byte[] getEncodedAttributes();

	public void setEncodedAttributes(byte[] encodedAttributes);

//	public Set<FileIF> getFiles();
//
//	public void setFiles(Set<FileIF> files);
	
}

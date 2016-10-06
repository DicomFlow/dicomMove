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


public interface PatientIF  {
	
	public Long getId();

	public void setId(Long id);

//	public Set<StudyIF> getStudies();
//
//	public void setStudies(Set<StudyIF> studies);

	public String getPatientId();

	public void setPatientId(String patientId);

	public String getPatientName();

	public void setPatientName(String patientName);

	public String getPatientFamilyNameSoundex();

	public void setPatientFamilyNameSoundex(String patientFamilyNameSoundex);

	public String getPatientGivenNameSoundex();

	public void setPatientGivenNameSoundex(String patientGivenNameSoundex);

	public String getPatientIdeographicName();

	public void setPatientIdeographicName(String patientIdeographicName);

	public String getPatientPhoneticName();

	public void setPatientPhoneticName(String patientPhoneticName);

	public String getPatientBirthDate();

	public void setPatientBirthDate(String patientBirthDate);

	public String getPatientSex();

	public void setPatientSex(String patientSex);

	public Date getUpdatedTime();

	public void setUpdatedTime(Date updatedTime);

	public Date getCreatedTime();

	public void setCreatedTime(Date createdTime);

	public byte[] getEncodedAttributes();

	public void setEncodedAttributes(byte[] encodedAttributes);
	
	
	
	

}

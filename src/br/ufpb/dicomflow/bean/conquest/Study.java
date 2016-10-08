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

package br.ufpb.dicomflow.bean.conquest;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.ufpb.dicomflow.bean.AbstractPersistence;
import br.ufpb.dicomflow.bean.StudyIF;

@Entity
@Table(name="dicomstudies")
public class Study extends AbstractPersistence implements StudyIF{
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 8825435386193410947L;

	@Id
	@Column(name="StudyInsta",unique=true)
	private String studyIuid;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="PatientID")
	private Patient patient;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="StudyInsta")
	private Set<Series> series;
	
	
	@Column(name="StudyID")
    private String studyId;
	
	@Column(name="StudyDate")
    private Date studyDateTime;
	
	@Column(name="AccessionN")
    private String accessionNumber;
	
	@Column(name="StudyModal")
    private String modalitiesInStudy;
	
	@Column(name="StudyDescr")
	private String studyDescription;
	
	@Column(name="ReferPhysi")
	private String referedPhysician;

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public String getStudyIuid() {
		return studyIuid;
	}

	public void setStudyIuid(String studyIuid) {
		this.studyIuid = studyIuid;
	}

	public String getStudyId() {
		return studyId;
	}

	public void setStudyId(String studyId) {
		this.studyId = studyId;
	}

	public Date getStudyDateTime() {
		return studyDateTime;
	}

	public void setStudyDateTime(Date studyDateTime) {
		this.studyDateTime = studyDateTime;
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}

	public String getModalitiesInStudy() {
		return modalitiesInStudy;
	}

	public void setModalitiesInStudy(String modalitiesInStudy) {
		this.modalitiesInStudy = modalitiesInStudy;
	}

	public Set<Series> getSeries() {
		return series;
	}

	public void setSeries(Set<Series> series) {
		this.series = series;
	}

	public String getStudyDescription() {
		return studyDescription;
	}

	public void setStudyDescription(String studyDescription) {
		this.studyDescription = studyDescription;
	}

	public String getReferedPhysician() {
		return referedPhysician;
	}

	public void setReferedPhysician(String referedPhysician) {
		this.referedPhysician = referedPhysician;
	}

	
	
	

}

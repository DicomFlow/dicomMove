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

package br.ufpb.dicomflow.bean.dcm4che;

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

import br.ufpb.dicomflow.bean.AbstractPersistence;
import br.ufpb.dicomflow.bean.PatientIF;
import br.ufpb.dicomflow.bean.StudyIF;

@Entity
@Table(name="study")
public class Study extends AbstractPersistence implements StudyIF{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8327591947346455289L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="pk",unique=true)
	private Long id; 
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="patient_fk")
	private Patient patient;
	
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="study_fk")
	private Set<Series> series;

	@Column(name="study_iuid")
	private String studyIuid;
	
	@Column(name="study_id")
    private String studyId;
	
	@Column(name="study_datetime")
    private Date studyDateTime;
	
	@Column(name="accession_no")
    private String accessionNumber;
	
	@Column(name="mods_in_study")
    private String modalitiesInStudy;
	
	@Column(name="cuids_in_study")
    private String sopClassesInStudy;
	
	@Column(name="num_series")
    private Integer numberOfStudyRelatedSeries;
	
	@Column(name="num_instances")
    private Integer numberOfStudyRelatedInstances;
	
	@Column(name="retrieve_aets")
    private String retrieveAETs;
	
	
	@Column(name="availability")
    private Integer availability;
	
	@Column(name="study_status")
    private Integer studyStatus;
	
	@Column(name="updated_time")
    private Date updatedTime;
	
	@Column(name="created_time")
    private Date createdTime;
	
	@Column(name="study_attrs")
    private byte[] encodedAttributes;
	
	@Column(name="study_desc")
	private String studyDescription;

	@Column(name="ref_physician")
	private String referedPhysician;
	
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

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

	public String getSopClassesInStudy() {
		return sopClassesInStudy;
	}

	public void setSopClassesInStudy(String sopClassesInStudy) {
		this.sopClassesInStudy = sopClassesInStudy;
	}

	public Integer getNumberOfStudyRelatedSeries() {
		return numberOfStudyRelatedSeries;
	}

	public void setNumberOfStudyRelatedSeries(Integer numberOfStudyRelatedSeries) {
		this.numberOfStudyRelatedSeries = numberOfStudyRelatedSeries;
	}

	public Integer getNumberOfStudyRelatedInstances() {
		return numberOfStudyRelatedInstances;
	}

	public void setNumberOfStudyRelatedInstances(
			Integer numberOfStudyRelatedInstances) {
		this.numberOfStudyRelatedInstances = numberOfStudyRelatedInstances;
	}

	public String getRetrieveAETs() {
		return retrieveAETs;
	}

	public void setRetrieveAETs(String retrieveAETs) {
		this.retrieveAETs = retrieveAETs;
	}

	public Integer getAvailability() {
		return availability;
	}

	public void setAvailability(Integer availability) {
		this.availability = availability;
	}

	public Integer getStudyStatus() {
		return studyStatus;
	}

	public void setStudyStatus(Integer studyStatus) {
		this.studyStatus = studyStatus;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public byte[] getEncodedAttributes() {
		return encodedAttributes;
	}

	public void setEncodedAttributes(byte[] encodedAttributes) {
		this.encodedAttributes = encodedAttributes;
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
	
	public PatientIF getPatientIF() {
		return patient;
	}

}

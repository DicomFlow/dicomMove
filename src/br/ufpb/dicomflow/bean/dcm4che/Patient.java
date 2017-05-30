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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.ufpb.dicomflow.bean.AbstractPersistence;
import br.ufpb.dicomflow.bean.PatientIF;

@Entity
@Table(name="patient")
public class Patient extends AbstractPersistence implements PatientIF{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2935621067873773893L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="pk",unique=true)
	private Long id;
	
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="patient_fk")
	private Set<Study> studies;
	
	@Column(name="pat_id")
    private String patientId;
	
	@Column(name="pat_name")
    private String patientName;
	
	@Column(name="pat_fn_sx")
    private String patientFamilyNameSoundex;
	
	@Column(name="pat_gn_sx")
    private String patientGivenNameSoundex;
	
	@Column(name="pat_i_name")
    private String patientIdeographicName;
	
	@Column(name="pat_p_name")
    private String patientPhoneticName;
	
	@Column(name="pat_birthdate")
    private String patientBirthDate;
	
	@Column(name="pat_sex")
    private String patientSex;
	
	@Column(name="updated_time")
    private Date updatedTime;
	
	@Column(name="created_time")
    private Date createdTime;
	
	@Column(name="pat_attrs")
    private byte[] encodedAttributes;
	
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public Set<Study> getStudies() {
		return studies;
	}

	public void setStudies(Set<Study> studies) {
		this.studies = studies;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getPatientFamilyNameSoundex() {
		return patientFamilyNameSoundex;
	}

	public void setPatientFamilyNameSoundex(String patientFamilyNameSoundex) {
		this.patientFamilyNameSoundex = patientFamilyNameSoundex;
	}

	public String getPatientGivenNameSoundex() {
		return patientGivenNameSoundex;
	}

	public void setPatientGivenNameSoundex(String patientGivenNameSoundex) {
		this.patientGivenNameSoundex = patientGivenNameSoundex;
	}

	public String getPatientIdeographicName() {
		return patientIdeographicName;
	}

	public void setPatientIdeographicName(String patientIdeographicName) {
		this.patientIdeographicName = patientIdeographicName;
	}

	public String getPatientPhoneticName() {
		return patientPhoneticName;
	}

	public void setPatientPhoneticName(String patientPhoneticName) {
		this.patientPhoneticName = patientPhoneticName;
	}

	public String getPatientBirthDate() {
		return patientBirthDate;
	}

	public void setPatientBirthDate(String patientBirthDate) {
		this.patientBirthDate = patientBirthDate;
	}

	public String getPatientSex() {
		return patientSex;
	}

	public void setPatientSex(String patientSex) {
		this.patientSex = patientSex;
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
	
	
	
	

}

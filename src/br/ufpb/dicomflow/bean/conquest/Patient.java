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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.ufpb.dicomflow.bean.AbstractPersistence;
import br.ufpb.dicomflow.bean.PatientIF;
import br.ufpb.dicomflow.bean.SeriesIF;
import br.ufpb.dicomflow.bean.StudyIF;

@Entity
@Table(name="DICOMPatients")
public class Patient extends AbstractPersistence implements PatientIF{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1886808320373764381L;


	@Id
	@Column(name="PatientID",unique=true)
    private String patientId;
	
    
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="PatientID")
	private Set<Study> studies;
	
	
	@Column(name="PatientNam")
    private String patientName;
	
	@Column(name="PatientBir")
    private String patientBirthDate;
	
	@Column(name="PatientSex")
    private String patientSex;
	
	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public Set<Study> getStudies() {
		return studies;
	}

	public void setStudies(Set<Study> studies) {
		this.studies = studies;
	}

	

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
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

	@Override
	public Long getPatientBirthDateTimestamp() {
		
		if(patientBirthDate != null && !patientBirthDate.equals("")){
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			try {
				return format.parse(patientBirthDate).getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	@Override
	public Set<StudyIF> getStudiesIF() {
		Set<StudyIF> studiesIF = new HashSet<>();
		if(getStudies() != null)
			studiesIF.addAll(getStudies());
		return studiesIF;
	}
	
	@Override
	public void setStudiesIF(Set<StudyIF> studies) {
		this.studies = new HashSet<>();
		for (Iterator iterator = studies.iterator(); iterator.hasNext();) {
			StudyIF studyIF = (StudyIF) iterator.next();
			this.studies.add((Study)studyIF);
			
		}
		
	}

	@Override
	public String getPatientBirthDateString(DateFormat formatter) {
		if(patientBirthDate != null && !patientBirthDate.equals("")){
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			try {
				Date date = format.parse(patientBirthDate);
				return formatter.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

}

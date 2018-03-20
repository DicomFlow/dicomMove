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
import java.util.HashSet;
import java.util.Iterator;
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
import br.ufpb.dicomflow.bean.PatientIF;
import br.ufpb.dicomflow.bean.SeriesIF;
import br.ufpb.dicomflow.bean.StudyIF;

@Entity
@Table(name="DICOMStudies")
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
	
	@OneToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
	@JoinColumn(name="StudyInsta")
	private Set<Series> series;
	
	
	@Column(name="StudyID")
    private String studyId;
	
	@Column(name="StudyDate")
    private String studyDate;
	
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

	public String getStudyDate() {
		return studyDate;
	}

	public void setStudyDate(String studyDate) {
		this.studyDate = studyDate;
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

	public PatientIF getPatientIF() {
		return patient;
	}

	@Override
	public Date getStudyDateTime() {
		Date studyDateTime = null; 
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		try {
			studyDateTime = formatter.parse(studyDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return studyDateTime;
	}

	@Override
	public void setStudyDateTime(Date studyDateTime) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		this.studyDate = formatter.format(studyDateTime);
	}

	@Override
	public Set<SeriesIF> getSeriesIF() {
		Set<SeriesIF> seriesIF = new HashSet<>();
		if(getSeries() != null)
			seriesIF.addAll(getSeries());
		return seriesIF;
	}
	
	@Override
	public void setSeriesIF(Set<SeriesIF> series) {
		this.series = new HashSet<>();
		for (Iterator iterator = series.iterator(); iterator.hasNext();) {
			SeriesIF studyIF = (SeriesIF) iterator.next();
			this.series.add((Series)studyIF);
			
		}
		
	}

	@Override
	public String getStudyDateTimeString(DateFormat formatter) {
		if(studyDate != null && !studyDate.equals("")){
			DateFormat format = new SimpleDateFormat("yyyyMMdd");
			try {
				Date date = format.parse(studyDate);
				return formatter.format(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	
	

}

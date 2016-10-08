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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufpb.dicomflow.bean.AbstractPersistence;
import br.ufpb.dicomflow.bean.InstanceIF;


@Entity
@Table(name="dicomimages")
public class Instance extends AbstractPersistence implements InstanceIF {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7987858134499546046L;


	@Id
	@Column(name="SOPInstanc",unique=true)
	private String sopIuid;
	
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="SeriesInst")
	private Series series;
	
	@Column(name="SOPClassUI")
	private String sopCuid;
	
	@Column(name="ImageNumbe")
	private String instanceNumber;
	
	@Column(name="ObjectFile")
	private String filePath;
	
	public Series getSeries() {
		return series;
	}

	public void setSeries(Series series) {
		this.series = series;
	}

	public String getSopIuid() {
		return sopIuid;
	}

	public void setSopIuid(String sopIuid) {
		this.sopIuid = sopIuid;
	}

	public String getSopCuid() {
		return sopCuid;
	}

	public void setSopCuid(String sopCuid) {
		this.sopCuid = sopCuid;
	}

	public String getInstanceNumber() {
		return instanceNumber;
	}

	public void setInstanceNumber(String instanceNumber) {
		this.instanceNumber = instanceNumber;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	
	
	

}

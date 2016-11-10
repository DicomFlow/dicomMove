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
package br.ufpb.dicomflow.service.dcm4che;

import java.util.List;

import br.ufpb.dicomflow.bean.InstanceIF;
import br.ufpb.dicomflow.bean.PatientIF;
import br.ufpb.dicomflow.bean.SeriesIF;
import br.ufpb.dicomflow.bean.StudyIF;
import br.ufpb.dicomflow.bean.dcm4che.Patient;
import br.ufpb.dicomflow.bean.dcm4che.Instance;
import br.ufpb.dicomflow.bean.dcm4che.Series;
import br.ufpb.dicomflow.bean.dcm4che.Study;
import br.ufpb.dicomflow.service.PacsPersistentServiceIF;
import br.ufpb.dicomflow.service.PersistentService;


public class PacsPersistentService extends PersistentService  implements PacsPersistentServiceIF {

    /**
	 * Default constructor.
	 */
	public PacsPersistentService() {
		super();
	}
	
	@Override
	public PatientIF selectPatient(String patientID) {
		return (PatientIF) super.select("patientID", patientID, Patient.class);
	}

	@Override
	public List<StudyIF> selectAllStudiesNotIn(List<String> studiesIuids) {
		
		return super.selectAllNotIn("studyIuid", studiesIuids, Study.class);
	}

	@Override
	public StudyIF selectStudy(String studyIuid) {
		return (StudyIF) super.select("studyIuid", studyIuid, Study.class);
	}

	@Override
	public List<SeriesIF> selectAllSeries(StudyIF study) {
		return super.selectAll("study", study, Series.class);
	}

	@Override
	public List<InstanceIF> selectAllFiles(List<SeriesIF> series) {
		return super.selectAll("series", series, Instance.class);
	}
	
}
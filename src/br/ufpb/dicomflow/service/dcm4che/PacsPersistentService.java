package br.ufpb.dicomflow.service.dcm4che;

import java.util.List;

import br.ufpb.dicomflow.bean.FileIF;
import br.ufpb.dicomflow.bean.InstanceIF;
import br.ufpb.dicomflow.bean.SeriesIF;
import br.ufpb.dicomflow.bean.StudyIF;
import br.ufpb.dicomflow.bean.dcm4che.File;
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
	public List<InstanceIF> selectAllInstances(List<SeriesIF> series) {
		return super.selectAll("serie", series, Instance.class);
	}

	@Override
	public List<FileIF> selectAllFiles(List<InstanceIF> instances) {
		return super.selectAll("instance", instances, File.class);
	}
	
}
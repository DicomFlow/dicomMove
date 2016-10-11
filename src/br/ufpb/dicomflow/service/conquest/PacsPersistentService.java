package br.ufpb.dicomflow.service.conquest;

import java.util.List;

import br.ufpb.dicomflow.bean.InstanceIF;
import br.ufpb.dicomflow.bean.SeriesIF;
import br.ufpb.dicomflow.bean.StudyIF;
import br.ufpb.dicomflow.bean.conquest.Instance;
import br.ufpb.dicomflow.bean.conquest.Series;
import br.ufpb.dicomflow.bean.conquest.Study;
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
	public List<InstanceIF> selectAllFiles(List<SeriesIF> series) {
		return super.selectAll("series", series, Instance.class);
	}
	
}
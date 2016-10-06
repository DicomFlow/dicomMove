package br.ufpb.dicomflow.ws;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import br.ufpb.dicomflow.bean.dcm4che.File;
import br.ufpb.dicomflow.bean.dcm4che.Instance;
import br.ufpb.dicomflow.bean.dcm4che.Series;
import br.ufpb.dicomflow.bean.dcm4che.Study;
import br.ufpb.dicomflow.service.PacsPersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.Util;

@Path("/DownloadStudy/{studyIUID}")
public class DownloadStudy extends GenericWebService {
	
	static List<File> files;
	
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getStudy(@PathParam("studyIUID") String studyIUID) {
		
		//TODO - Modificar
//		if (authenticate()) {
//			System.out.println("OK");
//		}
		Date initialTime = new Date();
		SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
		Util.getLogger(this).debug("INICIANDO DOWNLOAD - " + format.format(initialTime) );
		PacsPersistentServiceIF persistentService = ServiceLocator.singleton().getPacsPersistentService();
		Study study = (Study) persistentService.select("studyIuid", studyIUID, Study.class);	
		
		if (study != null ) {
			//TODO melhorar o resgate dos arquivos objetivando melhor desempenho
			List<Series> series = persistentService.selectAll("study", study, Series.class);
			List<Instance> instances =  persistentService.selectAll("series", series, Instance.class);
			List instancesIds = getInstanceIds(instances);
			files =  persistentService.selectAllIn("instance", instancesIds, File.class);	

	        StreamingOutput stream = new StreamingOutput() {
				@Override
				public void write(OutputStream os) throws IOException, WebApplicationException {				
					try {
						ServiceLocator.singleton().getFileService().createZipFile(files, os);
					} catch (ServiceException e) {				
						e.printStackTrace();
					}                                				
				}
	        };       
	        String responseHeader =   "attachment; filename=\"" + studyIUID + ".zip\"";
	        Date finalTime = new Date();
	        Util.getLogger(this).debug("DONE - início " + format.format(initialTime) +" - fim " + format.format(finalTime));
	        return Response.ok(stream).header("Content-Disposition",responseHeader).build();
	        
		} else {
			return notFound();
		}
	}

	private List getInstanceIds(List<Instance> instances) {
		List ids = new ArrayList<>();
		Iterator<Instance> it = instances.iterator();
		while (it.hasNext()) {
			Instance instance = (Instance) it.next();
			ids.add(instance.getId());
		}
		return ids;
	}

}

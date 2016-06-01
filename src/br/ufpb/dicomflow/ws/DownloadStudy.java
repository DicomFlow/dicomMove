package br.ufpb.dicomflow.ws;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import br.ufpb.dicomflow.bean.File;
import br.ufpb.dicomflow.bean.Instance;
import br.ufpb.dicomflow.bean.Series;
import br.ufpb.dicomflow.bean.Study;
import br.ufpb.dicomflow.service.PersistentService;
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
//				if (authenticate()) {
//					System.out.println("OK");
//				}
		
		PersistentService persistentService = ServiceLocator.singleton().getPersistentService();
		Study study = (Study) persistentService.select("studyIuid", studyIUID, Study.class);		
		
		//TODO melhorar o resgate dos arquivos objetivando melhor desempenho
		List<Series> series = persistentService.selectAll("study", study, Series.class);
		List<Instance> instances =  persistentService.selectAll("series", series, Instance.class);	
		files =  persistentService.selectAll("instance", instances, File.class);	

        StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {				
				try {
					ServiceLocator.singleton().getFileService().createZipFile(files, os);
				} catch (ServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}                                				
			}
        };       

        return Response.ok(stream).build();
	}

}

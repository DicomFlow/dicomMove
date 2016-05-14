package br.ufpb.dicomflow.ws;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

@Path("/DownloadStudy")
public class DownloadStudy {
	
	@GET
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response getStudy() {

        StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream os) throws IOException, WebApplicationException {
				Writer writer = new BufferedWriter(new OutputStreamWriter(os));              
                writer.write("Teste" + "\n");
                writer.flush();
				
			}
        };

        return Response.ok(stream).build();
	}

}

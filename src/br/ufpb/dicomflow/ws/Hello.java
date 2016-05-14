package br.ufpb.dicomflow.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
public class Hello {
	@GET
	public String getMsg() {
		return "Hello World !! - Jersey 2";
	}
}

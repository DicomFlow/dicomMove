package br.ufpb.dicomflow.tests;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.junit.Test;

import br.ufpb.dicomflow.integrationAPI.tests.GenericTestCase;

public class DownloadStudyTestCase extends GenericTestCase {
	
	@Test
	public void testDownload() {	
		ClientConfig clientConfig = new ClientConfig();
//		clientConfig.register(MyClientResponseFilter.class);
//		clientConfig.register(new AnotherClientFilter());		
		
		Client client = ClientBuilder.newClient(clientConfig);
//		client.register(ThirdClientFilter.class);
		//http://localhost:8090/DicomMoveServer/rest/DownloadStudy
		WebTarget webTarget = client.target("http://localhost:8080/DicomMove/rest");
//		webTarget.register(FilterForExampleCom.class);
		WebTarget resourceWebTarget = webTarget.path("DownloadStudy");
//		WebTarget helloworldWebTarget = resourceWebTarget.path("helloworld");
//		WebTarget helloworldWebTargetWithQueryParam = helloworldWebTarget.queryParam("greeting", "Hi World!");
		
//		Invocation.Builder invocationBuilder = helloworldWebTargetWithQueryParam.request(MediaType.APPLICATION_OCTET_STREAM);
		Invocation.Builder invocationBuilder = resourceWebTarget.request(MediaType.APPLICATION_OCTET_STREAM);
//		invocationBuilder.header("some-header", "true");
		
		Response response = invocationBuilder.get();
		System.out.println(response.getStatus());
		System.out.println(response.readEntity(String.class));
	}

}

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
package br.ufpb.dicomflow.tests;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.ClientConfig;

/**
 * 
 * @author Danilo Alexandre
 * @author Juracy Neto
 * 
 */
public class ConcurrentRequest implements Runnable {
	
	public String host = "150.165.202.39";
	public String port = "8081";
	//public String context = "dicomMove2/rest";
	public String context = "dicomMove2/rest";
	public String serviceName = "DownloadStudy";
	public String StudyId = "2.16.840.1.113669.632.20.1211.10000324479";
	
	String resultPath = "C:/temp/dicomflow/";
	
	private WebTarget resourceWebTarget;
	private Integer reqNumber;	

	@Override
	public void run() {		
		
		try {
			ClientConfig clientConfig = new ClientConfig();
//			clientConfig.register(MyClientResponseFilter.class);
//			clientConfig.register(new AnotherClientFilter());				
			Client client = ClientBuilder.newClient(clientConfig);
//			client.register(ThirdClientFilter.class);
			//http://localhost:8090/DicomMoveServer/rest/DownloadStudy
			WebTarget webTarget = client.target("http://" + host + ":" + port + "/" + context + "/");
//			webTarget.register(FilterForExampleCom.class);
			WebTarget resourceWebTarget = webTarget.path(serviceName + "/" + StudyId);
//			WebTarget helloworldWebTarget = resourceWebTarget.path("helloworld");
//			WebTarget helloworldWebTargetWithQueryParam = helloworldWebTarget.queryParam("greeting", "Hi World!");
			
//			Invocation.Builder invocationBuilder = helloworldWebTargetWithQueryParam.request(MediaType.APPLICATION_OCTET_STREAM);				
			
			long inicio = System.currentTimeMillis();				
			
			Invocation.Builder invocationBuilder = resourceWebTarget.request(MediaType.APPLICATION_OCTET_STREAM);//			
			
			Response response = invocationBuilder.get();
			InputStream is = (InputStream)response.getEntity();		
			//response.readEntity(String.class);		

			byte[] SWFByteArray = IOUtils.toByteArray(is);  

			FileOutputStream fos = new FileOutputStream(new File(resultPath + reqNumber + ".zip"));
			fos.write(SWFByteArray);
			fos.flush();
			fos.close();						
			
			long fim = System.currentTimeMillis();			
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");						
			System.out.println(reqNumber + " Status: " + response.getStatus() + " - Inicio: " + sdfDate.format(new Date(inicio)) + " Fim: " +  sdfDate.format(new Date(fim)));	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Integer getReqNumber() {
		return reqNumber;
	}

	public void setReqNumber(Integer reqNumber) {
		this.reqNumber = reqNumber;
	}
	
	

}

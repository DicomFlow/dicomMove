package br.ufpb.dicomflow.ws.graphql;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.logging.LoggingFeature;

public class GraphqlClient {
	
	/**GERENCIA A INFRAESTRUTURA DE COMUNIÇÃO DO LADO 
	 * CLIENTE PARA EXECUTAR AS SOLICITAÇÕES REALIZADAS*/
	private Client client;
 
	/**ACESSA UM RECURSO IDENTIFICADO PELO URI(Uniform Resource Identifier/Identificador Uniforme de Recursos)*/
	private WebTarget webTarget;
 
	/**URL DO SERVIÇO REST QUE VAMOS ACESSAR */
	private final static String URL_SERVICE = "https://api.graph.cool/simple/v1/cjeobl1v735ia0183ehtaay0f";
	
	private String uri;
 
	/**CONSTRUTOR DA NOSSA CLASSE*/
	public GraphqlClient(String uri){
		this.uri = uri;
		Logger logger = Logger.getLogger(getClass().getName());
		Feature feature = new LoggingFeature(logger, Level.INFO, null, null);
		
		this.client = ClientBuilder.newBuilder().register(feature).build();//newClient();  
	}
	
	public GraphqlClient(){
		this(URL_SERVICE);  
	}
	
	
	/**Cria uma URL*/
	public Response createURL(GraphqlEntity entity){
 
		
		this.webTarget = this.client.target(uri);
		
		 
		Invocation.Builder invocationBuilder =  this.webTarget.request(MediaType.APPLICATION_JSON);//"application/json;charset=UTF-8");
		
		Response response = invocationBuilder.post(Entity.entity(entity, MediaType.APPLICATION_JSON));
 
		return response;
 
	}
	

}

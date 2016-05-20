package br.ufpb.dicomflow.ws;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import br.ufpb.dicomflow.util.RequestOperations;

public class GenericWebService {
	
	@Context
	protected UriInfo uriInfo;	
	
	@Context
	protected Request request;
	
	@Context 
	protected HttpHeaders headers;
	
	@Context 
	protected SecurityContext securityContext;
	
	@Context 
	protected static ServletContext context;
	
	public final String authorizationHeader = "Authorization";
	public final String authorizationSplitToken = ":";
	
	public GenericWebService() {
		super();
	}
	
	protected Response accessDenied() {
		Response res;
		res = Response.status(Status.FORBIDDEN).build();
		return res;
	}
	
	protected Response serverError() {
		Response res;
		res = Response.serverError().build();
		return res;
	}
	
	protected Response serverError(Exception e) {
		Response res;
		String result = e.getLocalizedMessage();
		res = Response.ok(result).build();
		return res;
	}
	
	public boolean authenticate() {		
		try {				
			String requestAuthorization = headers.getRequestHeader(authorizationHeader).get(0);
			String[] authorizationSplit = requestAuthorization.split(authorizationSplitToken);			
			
			String accessKeyID =  authorizationSplit[1];
			String requestSignature = authorizationSplit[2];
			
			String calculatedHeaderString = RequestOperations.getHeaderString(request, headers);
						
			//AccessKey accessKeyObj = getAccessKey(accessKeyID); 			        
			//String accessKey = accessKeyObj.getKey();
			//CdnUser user = accessKeyObj.getCdnUser();
			//String hash = Signature.calculateRFC2104HMAC(calculatedHeaderString, accessKey);
			//if (hash.equals(requestSignature)) {
				//return user;
			//}
		} catch (Exception e) {
			return false;
		}
		return false;
	}


}

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
	protected ServletContext context;
	
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
	
	protected Response notFound() {
		Response res;
		res = Response.status(Status.NOT_FOUND).build();
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

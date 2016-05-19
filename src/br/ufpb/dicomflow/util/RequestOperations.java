package br.ufpb.dicomflow.util;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;

public class RequestOperations {
	
	public static final String contentMD5Header = "Content-MD5";
	public static final String contentTypeHeader = "Content-Type";
	public static final String dateHeader = "Date";
	
	public static String getHeaderString(Request request, HttpHeaders headers) {
		String canonicalizeHeaders = "";
		String canonicalizedResource = "";
		String signature = request.getMethod() + "\n" 
				+ headers.getRequestHeader(contentMD5Header).get(0) + "\n" 
				+ headers.getRequestHeader(dateHeader).get(0) + "\n"
				+ canonicalizeHeaders + 
				canonicalizedResource;					
		return signature;
	}
			
}

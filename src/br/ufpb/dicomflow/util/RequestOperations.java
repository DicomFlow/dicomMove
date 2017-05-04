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
package br.ufpb.dicomflow.util;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;

import br.ufpb.dicomflow.ws.CommonRequest;

public class RequestOperations {
	
	public static final String contentMD5Header = "Content-MD5";
	public static final String contentTypeHeader = "Content-Type";
	public static final String dateHeader = "Date";
	
	public static String getHeaderString(Request request, HttpHeaders headers) {
		String canonicalizeHeaders = "";
		String canonicalizedResource = "";
		String signature = request.getMethod() + "\n" 
				+ headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0) + "\n" 
				+ canonicalizeHeaders + 
				canonicalizedResource;					
		return signature;
	}
	
	public static String getHeaderString(String contentMD5, String header) {
		String canonicalizeHeaders = "";
		String canonicalizedResource = "";
		String signature = "GET" + "\n"
				+ header
				+ canonicalizeHeaders + 
				canonicalizedResource;
		return signature;
	}
	
	public static String getHeaderString(CommonRequest objectRequest, Request request) {
		//TODO canonicalizeHeaders and canonicalizedResource  		
		String canonicalizeHeaders = "";
		String canonicalizedResource = "";
		String signature = request.getMethod() + "\n" 
				+ objectRequest.getContentMD5() + "\n" 
				+ objectRequest.getDate() + "\n"
				+ canonicalizeHeaders + 
				canonicalizedResource;		
		return signature.toString();
	}
			
}

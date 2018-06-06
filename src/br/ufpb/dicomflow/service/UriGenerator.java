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
 *	The Original Code is part of DicomFlow, available at http://www.dicomflow.org
 * 
 * 	Copyright © 2016 Universidade Federal da Paraiba. * 
 * 
 */
package br.ufpb.dicomflow.service;

import java.util.StringTokenizer;

import br.ufpb.dicomflow.bean.StudyIF;

/**
 * 
 * @author Danilo Alexandre
 * @author Juracy Neto
 * 
 */
public class UriGenerator implements UriGeneratorIF {

	
	private String host;
	
	@Override
	public String getPrefix() {
		return "/" + host;
	}
	
	@Override
	public String getURI(String studyIuid) {
		return  getPrefix() +  "/" + studyIuid;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public String getStudyIuid(String uri) {
		
		StringTokenizer tokenizer = new StringTokenizer(uri, "/");
		
		String studyIuid = "";
		while(tokenizer.hasMoreTokens()){
			studyIuid = tokenizer.nextToken();
		}
		
		return studyIuid;
	}

	
				

}

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

import br.ufpb.dicomflow.bean.StudyIF;

/**
 * 
 * @author Danilo Alexandre
 * @author Juracy Neto
 * 
 */
public class UrlGenerator implements UrlGeneratorIF {

	private String protocol;
	private String host;
	private int port;
	private String context;
	
	@Override
	public String getURL(StudyIF study) {
		return protocol + "://" + host + ":" + port + "/" + context + "/rest/DownloadStudy/" + study.getStudyIuid();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
				

}

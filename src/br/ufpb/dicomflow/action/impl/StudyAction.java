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

package br.ufpb.dicomflow.action.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.interceptor.validation.SkipValidation;

import br.ufpb.dicomflow.action.GenericActionAdapter;
import br.ufpb.dicomflow.bean.FileIF;
import br.ufpb.dicomflow.bean.InstanceIF;
import br.ufpb.dicomflow.bean.SeriesIF;
import br.ufpb.dicomflow.bean.StudyIF;
import br.ufpb.dicomflow.service.PacsPersistentServiceIF;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;
import br.ufpb.dicomflow.util.ForwardConstants;
import br.ufpb.dicomflow.util.Util;

@ParentPackage("myDefaultPackage")
@Namespace(value="/admin")


@InterceptorRefs({
		@InterceptorRef("defaultStack"),
		@InterceptorRef("securityInterceptor")
})

public class StudyAction extends GenericActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7911119497496121034L;

	private StudyIF study;
	
	private String studyIUID;
	
	private String url;
	
	private String credential;
	
	private String extractDir;
	
	
	// control attributes
	
	public StudyAction(){
		
	}
	
	@SkipValidation
	@Action(value = "/recoverStudy", 
			results = { 
			@Result(name = ForwardConstants.INPUT, location = "/result.jsp") 
		}
	)
	public String recoverStudy(){
		
		String remoteHost = getRequest().getRemoteHost();
		
		
		
		Util.getLogger(this).debug("Checking remote Host: " + remoteHost);
		
		Util.getLogger(this).debug("IUID: " + studyIUID);
		
		Util.getLogger(this).debug("recovering Study...");
		Util.getLogger(this).debug("IUID: " + studyIUID);
		
		if(studyIUID == null || studyIUID.equals("")){
			Util.getLogger(this).error("invalid parameters. studyIUID : " + studyIUID);
			return null;
		}
		
		PacsPersistentServiceIF persistentService = ServiceLocator.singleton().getPacsPersistentService();
		this.study = (StudyIF) persistentService.selectStudy(studyIUID);
		Util.getLogger(this).debug("Study id: " + study.getId());
		Util.getLogger(this).debug("Study date: " + Util.singleton().getDataString(study.getCreatedTime()));
		
		//TODO melhorar o resgate dos arquivos objetivando melhor desempenho
		List<SeriesIF> series = persistentService.selectAllSeries(study);
		List<InstanceIF> instances =  persistentService.selectAllInstances(series);
		List<FileIF> files =  persistentService.selectAllFiles(instances);
		
		
		try {
			getResponse().setContentType("application/x-download");
			getResponse().setHeader("Content-Disposition", "attachment; filename="+studyIUID+".zip");
		    
		    OutputStream os = getResponse().getOutputStream();
		    ServiceLocator.singleton().getFileService().createZipFile(files, os);
		    os.flush();
		         
		} catch (IOException e) {
			Util.getLogger(this).error(e.getMessage(), e);
		    e.printStackTrace();
		} catch (ServiceException e) {
			Util.getLogger(this).error(e.getMessage(), e);
			e.printStackTrace();
		}
		
		return null;
	}

	public StudyIF getStudy() {
		return study;
	}



	public void setStudy(StudyIF study) {
		this.study = study;
	}

	public String getStudyIUID() {
		return studyIUID;
	}


	public void setStudyIUID(String studyIUID) {
		this.studyIUID = studyIUID;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getCredential() {
		return credential;
	}

	public void setCredential(String credential) {
		this.credential = credential;
	}

	public String getExtractDir() {
		return extractDir;
	}

	public void setExtractDir(String extractDir) {
		this.extractDir = extractDir;
	}

	
}

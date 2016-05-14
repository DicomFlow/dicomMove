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
import br.ufpb.dicomflow.bean.File;
import br.ufpb.dicomflow.bean.Instance;
import br.ufpb.dicomflow.bean.Persistent;
import br.ufpb.dicomflow.bean.Series;
import br.ufpb.dicomflow.bean.Study;
import br.ufpb.dicomflow.service.PersistentService;
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

	private Study study;
	
	private String studyIUID;
	
	private String url;
	
	private String credential;
	
	private String extractDir;
	
	private String[] selectedObjects = {};
	
	// control attributes
	
	public StudyAction(){
		this.study = new Study();
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
		
		PersistentService persistentService = ServiceLocator.singleton().getPersistentService();
		this.study = (Study) persistentService.select("studyIuid", studyIUID, Study.class);
		Util.getLogger(this).debug("Study id: " + study.getId());
		Util.getLogger(this).debug("Study date: " + Util.singleton().getDataString(study.getCreatedTime()));
		
		//TODO melhorar o resgate dos arquivos objetivando melhor desempenho
		List<Series> series = persistentService.selectAll("study", study, Series.class);
		List<Instance> instances =  persistentService.selectAll("series", series, Instance.class);
		List<File> files =  persistentService.selectAll("instance", instances, File.class);
		
		
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
	
	/* Interface methods */
	
	public Class getClassBean() {
		return Study.class;
	}
	
	
	
	public Persistent getPersistent() {
		return this.study;
	}

	
	public void setPersistent(Persistent p) {
		this.study = (Study) p;
	}

	public Study getStudy() {
		return study;
	}



	public void setStudy(Study study) {
		this.study = study;
	}

	@Override
	public String[] getSelectedObjects() {
		return selectedObjects;
	}

	@Override
	public void setSelectedObjects(String[] objetosSelecionados) {
		this.selectedObjects = selectedObjects;
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

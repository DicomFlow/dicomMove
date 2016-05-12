package br.ufpb.dicomflow.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import br.ufpb.dicomflow.bean.File;

public interface FileService {

	public void createZipFile(List<File> files, OutputStream os) throws IOException, FileNotFoundException, ServiceException;
	
	public void extractZipFile(URL url) throws IOException, ServiceException;
	
	public void storeFile(java.io.File file) throws ServiceException;

	public java.io.File getCertificate() throws ServiceException;
	
	public boolean storeCertificate(byte[] certificate, String alias) throws ServiceException;
	
	public String getArchivePath();
	
	public String getEat();

	public String getHost();

	public String getPort();
	
	public String getExtractDir();


	
}

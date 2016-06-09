package br.ufpb.dicomflow.service.impl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.dcm4che3.tool.storescu.StoreSCU;

import br.ufpb.dicomflow.bean.File;
import br.ufpb.dicomflow.service.FileService;
import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.util.CertUtil;
import br.ufpb.dicomflow.util.Util;

public class FileServiceImpl implements FileService {
	
	private String archivePath; 
	
	private String eat;
	private String host;
	private String port;
	private String keystore;
	private String keystorePass;
	private String keyPass;
	private String alias;
	
	private String extractDir;

	@Override
	public void createZipFile(List<File> files, OutputStream os) throws IOException, FileNotFoundException, ServiceException {
		
		if(archivePath == null || archivePath.equals("")){
			String errMsg = "Could not create zip file: invalid archive's path.";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		ZipOutputStream zipOut = new ZipOutputStream( os );

		for (File file : files) {
			java.io.File ioFile = new java.io.File(archivePath+file.getFileSystem().getDirectoryPath()+java.io.File.separator+file.getFilePath());
			zipOut.putNextEntry( new ZipEntry( ioFile.getName().toString() )  );

			FileInputStream fis = new FileInputStream( ioFile );

			int content;
			while ((content = fis.read()) != -1) {
				zipOut.write( content );
			}

			zipOut.closeEntry();

		}


		zipOut.close();
	}
	
	@Override
	public void extractZipFile(URL url, String fileName) throws IOException, ServiceException{

		if(extractDir == null || extractDir.equals("")){
			String errMsg = "Could not extract zip file: invalid extract dir.";

			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
//		try {
//			CertUtil certUtil = CertUtil.getInstance();
//			certUtil.setKeyStoreProperties(keystore, keystorePass, keyPass);
//			certUtil.importCert(url.getHost(), url.getPort());
//			certUtil.loadCert();
//		} catch (Exception e) {
//			throw new ServiceException (e);
//		}
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		InputStream in = (InputStream)con.getInputStream();					
		String filePath = extractDir + java.io.File.separator  + fileName;
		java.io.File file = new java.io.File(filePath);
		FileOutputStream fout = new FileOutputStream(file, false);
		Util.getLogger(this).debug("INICIANDO ESCRITA DO .ZIP");
		int i = 0;
		byte buffer[] = new byte[8192];
		
		
		while( (i = in.read(buffer)) != -1 ) {
			fout.write(buffer, 0, i);
		}
		Util.getLogger(this).debug(".ZIP CRIADO");
		in.close();
		fout.close();
		
		//extraindo o arquivo .zip
		InputStream is = new BufferedInputStream(new FileInputStream(filePath));
	    ZipInputStream zin = new ZipInputStream(is);
	    ZipEntry e;
	 
		while ((e = zin.getNextEntry()) != null) {			
			unzip(zin, extractDir + e.getName());
		}

	    zin.close();
	    
	    //apagando o arquivo .zip após a extração
	    if(!deleteFile(file)){
	    	String errMsg = "Could not delete file " + file.getAbsolutePath();
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
	    }
//		
//		ZipInputStream zipIn = new ZipInputStream(con.getInputStream());
//		ZipEntry entry;
//		while((entry = zipIn.getNextEntry()) != null){
//
//			Util.getLogger(this).debug("Unzipping : " + entry.getName());
//
//			FileOutputStream fout = new FileOutputStream(extractDir +entry.getName());
//
//			while (zipIn.available() > 0){
//				fout.write(zipIn.read());	
//			} 
//
//			zipIn.closeEntry();
//			fout.close();
//		}
//
//		zipIn.close();
		
	}
	
	private void unzip(ZipInputStream zin, String s) throws IOException {

		System.out.println("unzipping " + s);
		FileOutputStream out = new FileOutputStream(s);

		byte[] b = new byte[512];
		int len = 0;

		while ((len = zin.read(b)) != -1) {
			out.write(b, 0, len);
		}
		out.close();
	}
	
	@Override
	public void storeFile(java.io.File file) throws ServiceException{
		
		if(eat == null || eat.equals("")){
			String errMsg = "Could not store file: invalid EAT.";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		if(host == null || host.equals("")){
			String errMsg = "Could not store file: invalid Host.";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		if(port == null || port.equals("")){
			String errMsg = "Could not store file: invalid Port.";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		
		
		String[] args = new String[]{"-c", eat+"@"+host+":"+port, file.getAbsolutePath()};
		StoreSCU.main(args);
		
		if(!deleteFile(file)){
			String errMsg = "Could not delete file " + file.getAbsolutePath();
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
	}
	/**
	 * Apaga um diretório e todos seus arquivos
	 * @param root o diretório raiz
	 * @return True se apagou com sucesso, False caso contrário
	 */
	private boolean deleteFile(java.io.File root){
		if(root != null && root.exists()){
			if(root.isDirectory()){
				
				java.io.File fileList[] = root.listFiles();
				
				for ( int i = 0; i < fileList.length; i++ ){ 
					java.io.File file  = fileList[i];
					deleteFile(file);
				}
				
			}
			return root.delete();
		}
		return true;
	}
	
	@Override
	public java.io.File getCertificate() throws ServiceException {
		if(alias == null || alias.equals("")){
			String errMsg = "Could not export certificate: invalid ALIAS.";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		try {
			CertUtil certUtil = CertUtil.getInstance();
			certUtil.setKeyStoreProperties(keystore, keystorePass, keyPass);
			return certUtil.exportCert(alias);
		} catch (Exception e) {
			throw new ServiceException (e);
		}
		
	}
	
	@Override
	public boolean storeCertificate(byte[] certificate, String alias) throws ServiceException {
		if(alias == null || alias.equals("")){
			String errMsg = "Could not import certificate: invalid ALIAS.";
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		try {
			CertUtil certUtil = CertUtil.getInstance();
			certUtil.setKeyStoreProperties(keystore, keystorePass, keyPass);
			return certUtil.importCert(certificate,alias);
		} catch (Exception e) {
			throw new ServiceException (e);
		}
		
	}

	@Override
	public String getArchivePath() {
		return archivePath;
	}

	public void setArchivePath(String archivePath) {
		this.archivePath = archivePath;
	}

	@Override
	public String getEat() {
		return eat;
	}

	public void setEat(String eat) {
		this.eat = eat;
	}

	@Override
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getExtractDir() {
		return extractDir;
	}

	public void setExtractDir(String extractDir) {
		this.extractDir = extractDir;
	}

	public String getKeystore() {
		return keystore;
	}

	public void setKeystore(String keystore) {
		this.keystore = keystore;
	}

	public String getKeystorePass() {
		return keystorePass;
	}

	public void setKeystorePass(String keystorePass) {
		this.keystorePass = keystorePass;
	}

	public String getKeyPass() {
		return keyPass;
	}

	public void setKeyPass(String keyPass) {
		this.keyPass = keyPass;
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	
	
}

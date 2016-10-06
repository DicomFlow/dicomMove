package br.ufpb.dicomflow.service;

import br.ufpb.dicomflow.util.CertUtil;
import br.ufpb.dicomflow.util.Util;

public class CertificateService implements CertificateServiceIF {

	private String keystore;
	private String keystorePass;
	private String keyPass;
	private String keyAlias;
	
	@Override
	public java.io.File getCertificate() throws ServiceException {
		if(keyAlias == null || keyAlias.equals("")){
			String errMsg = "Could not export certificate: invalid ALIAS.";
			
			Util.getLogger(this).error(errMsg);
			throw new ServiceException(new Exception(errMsg));
		}
		
		try {
			CertUtil certUtil = CertUtil.getInstance();
			certUtil.setKeyStoreProperties(keystore, keystorePass, keyPass);
			return certUtil.exportCert(keyAlias);
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
	
	public String getKeyAlias() {
		return keyAlias;
	}

	public void setKeyAlias(String keyAlias) {
		this.keyAlias = keyAlias;
	}

}

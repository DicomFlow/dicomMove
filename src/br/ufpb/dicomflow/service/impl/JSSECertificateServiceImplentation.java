package br.ufpb.dicomflow.service.impl;

import br.ufpb.dicomflow.service.CertificateService;

public class JSSECertificateServiceImplentation implements CertificateService {

	private String keystore;
	private String keystorePass;
	private String keyPass;
	
	@Override
	public void importCertificate(String host, int port) {

	}

	@Override
	public void loadCertificate() {
		// TODO Auto-generated method stub

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

}

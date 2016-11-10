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

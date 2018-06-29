package br.ufpb.dicomflow.service.ndn;

import com.intel.jndn.management.ManagementException;
import com.intel.jndn.management.Nfdc;
import com.intel.jndn.mock.MockKeyChain;

import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;
import net.named_data.jndn.security.identity.IdentityManager;
import net.named_data.jndn.security.identity.MemoryIdentityStorage;
import net.named_data.jndn.security.identity.MemoryPrivateKeyStorage;

public class RouteRegisterService implements RouteRegisterServiceIF{
	
	private String protocol;
	private String port;
	
	
	
	
	public void processRoute(String protocol, String host, String port, String prefix){
		
		String uri = protocol == null || protocol.isEmpty() ? this.protocol : protocol;
		uri += "://";
		uri += host;
		uri += ":";
		uri += port == null || port.isEmpty() ? this.port : port;
		
		this.processRoute(uri, prefix);
		
		
	}
	
	public void processRoute(String uri, String prefix){
		
		try {
			Face face =  new Face();
			KeyChain keyChain = buildTestKeyChain();//MockKeyChain.configure(new Name("/tmp/identity"));
			face.setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName());
			
			
			//TODO verificar se já existe um registro para a rota, antes de adcionar outro.
			//verificar o custo
			Nfdc.register(face, uri, new Name(prefix), 0);
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ManagementException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Setup an in-memory KeyChain with a default identity.
	 *
	 * @return
	 * @throws net.named_data.jndn.security.SecurityException
	 */
	public static KeyChain buildTestKeyChain() throws net.named_data.jndn.security.SecurityException {
		MemoryIdentityStorage identityStorage = new MemoryIdentityStorage();
		MemoryPrivateKeyStorage privateKeyStorage = new MemoryPrivateKeyStorage();
		IdentityManager identityManager = new IdentityManager(identityStorage, privateKeyStorage);
		KeyChain keyChain = new KeyChain(identityManager);
		try {
			keyChain.getDefaultCertificateName();
		} catch (net.named_data.jndn.security.SecurityException e) {
			keyChain.createIdentityAndCertificate(new Name("/test/identity"));
			keyChain.getIdentityManager().setDefaultIdentity(new Name("/test/identity"));
		}
		return keyChain;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	
	

}

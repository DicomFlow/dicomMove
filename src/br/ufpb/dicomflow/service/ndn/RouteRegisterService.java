package br.ufpb.dicomflow.service.ndn;

import com.intel.jndn.management.ManagementException;
import com.intel.jndn.management.Nfdc;
import com.intel.jndn.mock.MockKeyChain;

import net.named_data.jndn.Face;
import net.named_data.jndn.Name;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.SecurityException;

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
			Face face =  new Face("localhost");
			KeyChain keyChain = MockKeyChain.configure(new Name("/tmp/identity"));
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

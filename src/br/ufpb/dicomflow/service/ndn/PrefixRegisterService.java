package br.ufpb.dicomflow.service.ndn;

import java.io.IOException;
import java.util.logging.Logger;

import br.ufpb.dicomflow.service.UrlGeneratorIF;
import net.named_data.jndn.Data;
import net.named_data.jndn.Face;
import net.named_data.jndn.Interest;
import net.named_data.jndn.InterestFilter;
import net.named_data.jndn.Name;
import net.named_data.jndn.OnData;
import net.named_data.jndn.OnInterestCallback;
import net.named_data.jndn.OnRegisterFailed;
import net.named_data.jndn.OnTimeout;
import net.named_data.jndn.security.KeyChain;
import net.named_data.jndn.security.identity.IdentityManager;
import net.named_data.jndn.security.identity.MemoryIdentityStorage;
import net.named_data.jndn.security.identity.MemoryPrivateKeyStorage;
import net.named_data.jndn.util.Blob;

public class PrefixRegisterService implements PrefixRegisterServiceIF {

	private static final Logger logger = Logger.getLogger(PrefixRegisterService.class.getName());

	private UrlGeneratorIF urlGenerator;
	
	private UriGeneratorIF uriGenerator;
	
	private boolean running;

	

	@Override
	public void run() {
		
		running = true;
		try {
			processRegister();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		running = false;

	}

	public void processRegister() throws Exception {

		if (uriGenerator.getPrefix() == null || uriGenerator.getPrefix().isEmpty()) {
			throw new Exception("Não foi possível iniciar o serviço de registro de prefixo: Prefixo não definido");
		}

		Face face = new Face();
		KeyChain keyChain = buildTestKeyChain();
		face.setCommandSigningInfo(keyChain, keyChain.getDefaultCertificateName());

		// test connection
		Interest interest = new Interest(new Name("/localhost/nfd/rib/list"));
		interest.setInterestLifetimeMilliseconds(1000);
		face.expressInterest(interest, new OnData() {
			public void onData(Interest interest, Data data) {
				logger.info("Data received (bytes): " + data.getContent().size());
			}
		}, new OnTimeout() {
			public void onTimeout(Interest interest) {
				logger.severe("Failed to retrieve localhop data from NFD: " + interest.toUri());
			}
		});

		// check if face is local
		logger.info("Face is local: " + face.isLocal());

		// register remotely
		face.registerPrefix(new Name(uriGenerator.getPrefix()), new OnInterestCallback() {
			public void onInterest(Name prefix, Interest interest, Face face, long interestFilterId,
					InterestFilter filter) {

				logger.info("Received prefix: " + prefix);
				logger.info("Received filter: " + filter.getPrefix());
				logger.info("received interest: " + interest.getName());
				Data data = new Data(interest.getName());
				
				String studyIuid = uriGenerator.getStudyIuid(interest.getName().toUri());
				data.setContent(new Blob(urlGenerator.getURL(studyIuid)));
				
				
				try {
					face.putData(data);
				} catch (IOException e) {
					logger.severe("Failed to send data: " + e.getMessage());
				}
			}
		}, new OnRegisterFailed() {
			public void onRegisterFailed(Name prefix) {
				logger.severe("Failed to register the external forwarder: " + prefix.toUri());
			}
		});

		// process events until process is killed
		while (true) {
			face.processEvents();
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

	public UrlGeneratorIF getUrlGenerator() {
		return urlGenerator;
	}

	public void setUrlGenerator(UrlGeneratorIF urlGenerator) {
		this.urlGenerator = urlGenerator;
	}

	public UriGeneratorIF getUriGenerator() {
		return uriGenerator;
	}

	public void setUriGenerator(UriGeneratorIF uriGenerator) {
		this.uriGenerator = uriGenerator;
	}

	public boolean isRunning() {
		return running;
	}
	
	
	

}

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

package br.ufpb.dicomflow.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class CertUtil {
	
	private static CertUtil certUtil;
	
	private String keystoreFile;
	private String keystorePass;
	private String keyPass;
	
	private CertUtil(){
		
	}
	
	public static CertUtil getInstance(){
		if(certUtil == null){
			certUtil = new CertUtil();
		}
		return certUtil;
		
	} 
	public  void setKeyStoreProperties(String keystoreFile, String keystorePass, String keyPass) {
		this.keystoreFile = keystoreFile;
		this.keystorePass = keystorePass;
		this.keyPass = keyPass;
	}
	

	public boolean loadCert() {
		
		KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance("JKS");
			InputStream is = new FileInputStream(keystoreFile);
			keyStore.load(is, keystorePass.toCharArray()); 
			
			final KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory 
					.getDefaultAlgorithm()); 
			kmf.init(keyStore, keyPass.toCharArray()); 
			final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory 
					.getDefaultAlgorithm()); 
			tmf.init(keyStore); 

			final SSLContext sc = SSLContext.getInstance("TLS"); 
			sc.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new java.security.SecureRandom()); 
			final SSLSocketFactory socketFactory = sc.getSocketFactory(); 
			HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);
		} catch (Exception e) {
			Util.getLogger(this).error(e.getMessage(), e);
			e.printStackTrace();
		}
		 
		
		return true;
	}
	
	
	public boolean importCert(String host, int port) throws Exception {

		if(host == null || host.equals("")){
			Util.getLogger(this).debug("Invalid host name.");
			return false;
		}
		if(port <=0){
			Util.getLogger(this).debug("Invalid port value.");
			return false;
		}
		
		File file = new File(keystoreFile);
		if (file.isFile() == false) {
			Util.getLogger(this).debug("Invalid keystore file.");
			return false;
		}
		Util.getLogger(this).debug("Loading KeyStore " + file + "...");
		InputStream in = new FileInputStream(file);
		
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(in, keystorePass.toCharArray());
		in.close();

		SSLContext context = SSLContext.getInstance("TLS");
		
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()); 
		kmf.init(ks, keyPass.toCharArray());
		
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		X509TrustManager defaultTrustManager = (X509TrustManager)tmf.getTrustManagers()[0];
		SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
		context.init(kmf.getKeyManagers(), new TrustManager[] {tm}, null);
		SSLSocketFactory factory = context.getSocketFactory();
		
		Util.getLogger(this).debug("Opening connection to " + host + ":" + port + "...");
		SSLSocket socket = (SSLSocket)factory.createSocket(host, port);
		socket.setSoTimeout(10000);
		
		try {
			Util.getLogger(this).debug("Starting SSL handshake...");
			socket.startHandshake();
			socket.close();
			Util.getLogger(this).debug("");
			Util.getLogger(this).debug("No errors, certificate is already trusted");
		} catch (SSLException e) {
			Util.getLogger(this).debug(e.getMessage());
			e.printStackTrace(System.out);
		}

		
		X509Certificate[] chain = tm.chain;
		if (chain == null) {
			Util.getLogger(this).debug("Could not obtain server certificate chain");
			return false;
		}

		Util.getLogger(this).debug("");
		Util.getLogger(this).debug("Server sent " + chain.length + " certificate(s):");
		Util.getLogger(this).debug("");
		
		MessageDigest sha1 = MessageDigest.getInstance("SHA1");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		for (int i = 0; i < chain.length; i++) {
			X509Certificate cert = chain[i];
			Util.getLogger(this).debug
			(" " + (i + 1) + " Subject " + cert.getSubjectDN());
			Util.getLogger(this).debug("   Issuer  " + cert.getIssuerDN());
			sha1.update(cert.getEncoded());
			Util.getLogger(this).debug("   sha1    " + toHexString(sha1.digest()));
			md5.update(cert.getEncoded());
			Util.getLogger(this).debug("   md5     " + toHexString(md5.digest()));
			Util.getLogger(this).debug("");
		}

		X509Certificate cert = chain[0];
		String alias = host;
		ks.setCertificateEntry(alias, cert);

		OutputStream out = new FileOutputStream(keystoreFile);
		ks.store(out, keystorePass.toCharArray());
		out.close();

		Util.getLogger(this).debug("");
		Util.getLogger(this).debug(cert);
		Util.getLogger(this).debug("");
		Util.getLogger(this).debug
		("Added certificate to keystore '" + keystoreFile + "' using alias '"
				+ alias + "'");
		
		return true;
	}
	
	public boolean importCert(byte[] certificate, String alias){
		//verify store
		File keystore = new File(keystoreFile);
		if (keystore.isFile() == false) {
			Util.getLogger(this).debug("Invalid keystore file.");
			return false;
		}
		
		//create certificate file on store directory
		String path = keystore.getAbsolutePath();
		File certFile = new File(path.substring(0,path.lastIndexOf(File.separator)+1)+alias+".crt");
		
		try {
			//write on certificate file
			FileOutputStream fos = new FileOutputStream(certFile);
			fos.write(certificate);
			fos.close();
			
			//load store
			InputStream in = new FileInputStream(keystore);
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(in, keystorePass.toCharArray());
			if (ks.containsAlias(alias)) {
		        in.close();
		        return false;
		    }
			in.close();
			
			//create a certificate object
			InputStream certIn = new FileInputStream(certFile);
			BufferedInputStream bis = new BufferedInputStream(certIn);
		    CertificateFactory cf = CertificateFactory.getInstance("X.509");
		    while (bis.available() > 0) {
		        Certificate cert = cf.generateCertificate(bis);
		        ks.setCertificateEntry(alias, cert);
		    }
		    certIn.close();

		    //store certificate object
		    OutputStream out = new FileOutputStream(keystore);
		    ks.store(out, keystorePass.toCharArray());
		    out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
		
		
	}
	
	public File exportCert(String alias){

		//create certificate file on store directory
		File keystore = new File(keystoreFile);
		String path = keystore.getAbsolutePath();
		File certificate = new File(path.substring(0,path.lastIndexOf(File.separator)+1)+alias+".crt");
		try {
			//load store
			InputStream in = new FileInputStream(keystore);
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(in, keystorePass.toCharArray());
			in.close();
	
			
			//retrieve certificate object
		    Certificate cert = ks.getCertificate(alias);
	
		    
		    //write certificate object on file
		    byte[] buf = cert.getEncoded();
		    FileOutputStream os = new FileOutputStream(certificate);
		    os.write(buf);
		    os.close();
	
		    Writer wr = new OutputStreamWriter(os, Charset.forName("UTF-8"));
		    wr.write(new sun.misc.BASE64Encoder().encode(buf));
//		    wr.close();
	    
	    
	    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return certificate;
	}

	private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

	private static String toHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 3);
		for (int b : bytes) {
			b &= 0xff;
			sb.append(HEXDIGITS[b >> 4]);
			sb.append(HEXDIGITS[b & 15]);
			sb.append(' ');
		}
		return sb.toString();
	}
	
	
	public PrivateKey getPrivateKey(String alias, String password) throws Exception {
		return getKeyEntry(alias, password).getPrivateKey();
	}

	public PublicKey getPublicKey(String alias, String password) throws Exception {
		return getKeyEntry(alias, password).getCertificate().getPublicKey();
	}
	
	private KeyStore.PrivateKeyEntry getKeyEntry(String alias, String password ) throws Exception {
		
		KeyStore clientKeyStore = KeyStore.getInstance("JKS");
		InputStream is = new FileInputStream(new File(this.keystoreFile));
		clientKeyStore.load(is, this.keystorePass.toCharArray());
		KeyStore.PasswordProtection keyPassword = new KeyStore.PasswordProtection(this.keyPass.toCharArray());
		KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) clientKeyStore.getEntry(alias, keyPassword);
		
		if (!javax.security.cert.X509Certificate.getInstance(pkEntry.getCertificate().getEncoded()).getNotAfter().after(new Date())) {
			throw new Exception("The identity certificate has expired");
		}
		
		return pkEntry;
	}

	private static class SavingTrustManager implements X509TrustManager {

		private final X509TrustManager tm;
		private X509Certificate[] chain;

		SavingTrustManager(X509TrustManager tm) {
			this.tm = tm;
		}

		public X509Certificate[] getAcceptedIssuers() {
			return tm.getAcceptedIssuers();
//			return new X509Certificate[0];
//			throw new UnsupportedOperationException();
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)
		throws CertificateException {
			tm.checkClientTrusted(chain, authType);
//			throw new UnsupportedOperationException();
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
		throws CertificateException {
			this.chain = chain;
			tm.checkServerTrusted(chain, authType);
		}
	}
	
	
	
	public static void main(String[] args){
		
		String keystoreFile = System.getProperty("java.home") + File.pathSeparator + "lib" + File.pathSeparator  + "security" + File.pathSeparator + "cacerts";
		String keystorePass = "changeit";
		String keyPass = "changeit";
		String url = "https://localhost:8443";
		String host = "localhost";
		String extractDir = "D:/Danilo/Workspace/J2EE/dicomMove/WebRoot/extract/";
		int port = 8443;
		
		if(args.length != 5){
			System.out.println("Usage: java CertUtil <keystoreFile> <keystorePass> <keyPass> <extratDir> <url>");
		}else{
			keystoreFile = args[0];
			keystorePass = args[1];
			keyPass = args[2];
			extractDir = args[3];
			url = args[4];
			StringTokenizer urlTokenizer = new StringTokenizer(url, "/");
			while(urlTokenizer.hasMoreTokens()){
				String token = urlTokenizer.nextToken();
				if(token.contains(":")){
					StringTokenizer hostTokenizer = new StringTokenizer(token, ":");
					if(hostTokenizer.countTokens() == 2){
						host  = hostTokenizer.nextToken();
						port = Integer.parseInt(hostTokenizer.nextToken());
						break;
					}
				}
			}
		}
		
		
		
		try {
			
			CertUtil certUtil = CertUtil.getInstance();
			certUtil.setKeyStoreProperties(keystoreFile, keystorePass, keyPass);
			certUtil.importCert(host, port);
			
			certUtil.loadCert();
			
			
		    //for localhost testing only
		    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
		    new javax.net.ssl.HostnameVerifier(){

		        public boolean verify(String hostname,
		                javax.net.ssl.SSLSession sslSession) {
		            if (hostname.equals("localhost")) {
		                return true;
		            }
		            return false;
		        }
		    });
			
			
			URL link = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) link.openConnection();
			
			
			System.out.println("Response Code : " + con.getResponseCode());
			System.out.println("Cipher Suite : " + con.getCipherSuite());
			System.out.println("\n");
						
			Certificate[] certs = con.getServerCertificates();
			for(Certificate cert : certs){
			   System.out.println("Cert Type : " + cert.getType());
			   if(cert.getType().equals("X.509")){
				   System.out.println("Cert Type : " + " Subject " + ((X509Certificate)cert).getSubjectDN());  
			   }
			   System.out.println("Cert Hash Code : " + cert.hashCode());
			   System.out.println("Cert Public Key Algorithm : " 
		                                    + cert.getPublicKey().getAlgorithm());
			   System.out.println("Cert Public Key Format : " 
		                                    + cert.getPublicKey().getFormat());
			   System.out.println("\n");
			}
			
			
			ZipInputStream zipIn = new ZipInputStream(con.getInputStream());
			ZipEntry entry;
			while((entry = zipIn.getNextEntry()) != null){

				System.out.println("Unzipping : " + entry.getName());

				
//				java.io.File file = new java.io.File(extractDir + entry.getName());
//				if(!file.exists()){
//					Util.getLogger(this).debug("Opening : " + extractDir + entry.getName());
//					file.createNewFile();
//				}
				System.out.println("Writing : " + extractDir + entry.getName());
				FileOutputStream fout = new FileOutputStream(extractDir + entry.getName());//file);

				while (zipIn.available() > 0){
					fout.write(zipIn.read());	
				} 

				fout.close();
				zipIn.closeEntry();
					
				System.out.println("Closing : " + extractDir + entry.getName());
			}

			zipIn.close();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		
	}

} 



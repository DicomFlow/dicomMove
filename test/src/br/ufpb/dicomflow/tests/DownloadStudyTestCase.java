package br.ufpb.dicomflow.tests;

import org.junit.Test;

import br.ufpb.dicomflow.integrationAPI.tests.GenericTestCase;

public class DownloadStudyTestCase extends GenericTestCase {
	
	public int numRequisicoes = 10;
		
	@Test
	public void testDownload() {					
		for (int i = 0; i <numRequisicoes; i++) {
			ConcurrentRequest cr = new ConcurrentRequest();
			cr.setReqNumber(i);
			new Thread(cr).start(); 
		}
		
	}
}

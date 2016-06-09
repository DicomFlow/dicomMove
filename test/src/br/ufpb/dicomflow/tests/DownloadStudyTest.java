package br.ufpb.dicomflow.tests;


public class DownloadStudyTest {
	
	public static void main(String[] args) {
		int numRequisicoes = 20;
		
		for (int i = 0; i <numRequisicoes; i++) {
			ConcurrentRequest cr = new ConcurrentRequest();
			cr.setReqNumber(i);
			new Thread(cr).start(); 
		}
	}					
}

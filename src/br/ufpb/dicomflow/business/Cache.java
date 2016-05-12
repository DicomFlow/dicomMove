/*
 * Created on 22/10/2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package br.ufpb.dicomflow.business;



public class Cache {
	
	private static Cache singleton=null;
	
	private boolean makeCache;
	
	private Cache(){
		
	}
	
	public static Cache getInstance(){
		if (singleton == null) {
			singleton = new Cache();
		}
		return singleton;
	}
	
	
	
	
	
	/**
	 * monta o cache pela primeira vez
	 */
	public void makeCache(){
		if (!makeCache) {
			System.err.println("Fazendo o CHACHE!!!!!");
			makeCache=true;
		}
		
	}


	
	



}

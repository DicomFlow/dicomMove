package br.ufpb.dicomflow.tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public class GenericTestCase extends TestCase {
	
	public static String outputDir = "temp/";
	
	@BeforeClass
	protected void setUp() throws IOException {				
		System.out.println("/************ Start ************/");
	}
	
	@AfterClass
	protected void tearDown() {
		System.out.println("/************* End *************/");
		System.gc();
	}		

}

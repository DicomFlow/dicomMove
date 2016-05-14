/*
 * 	This file is part of DicomFlow.
 * 
 * 	DicomFlow is free software: you can redistribute it and/or modify
 * 	it under the terms of the GNU General Public License as published by
 * 	the Free Software Foundation, either versio3 of the License, or
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

import java.io.File;

import org.dcm4che3.tool.storescu.StoreSCU;


public class StoreUtil {
	
	;
	
	
	public static void main(String[] args) {
		
		String eat = "DCM4CHEE"; 
		String host = "localhost";
		String port = "11112";
		String studyDir = "D:/Danilo/Study/";
		String extractDir = "D:/Danilo/Study/extract/";
		
		if(args.length != 5){
			System.out.println("Usage: java StoreUtil <eat> <host> <port> <studyDir> <extractDir>");
		}else{
			eat  = args[0];
			host = args[1];
			port = args[2];
			studyDir = args[3];
			extractDir = args[4];
		}
		
		File dir = new File(studyDir);
		
		if(dir.exists() && dir.isDirectory()){
			
			File fileList[] = dir.listFiles();
			
			for ( int i = 0; i < fileList.length; i++ ){ 
				File file  = fileList[i];
				String fileName = file.getName();
				System.out.println(fileName);
				if(fileName.endsWith(".zip")){
					
					UnzipTool.unzip(extractDir, studyDir+fileName);
					storeDCM(extractDir, eat, host, port);
					try {
						//dorme de 2 a 4 minutos
						Thread.currentThread().sleep(120000 + (int)Math.random() * 1200000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		
	}

	private static void storeDCM(String extractDir, String eat, String host, String port) {
		File dir = new File(extractDir);
		File fileList[] = dir.listFiles();
		
		for ( int i = 0; i < fileList.length; i++ ){
			File file  = fileList[i];
			if(file.isDirectory()){
				storeDCM(file.getAbsolutePath(), eat, host, port);
			}else{
				String[] args = new String[]{"-c", eat+"@"+host+":"+port, file.getAbsolutePath()};
				StoreSCU.main(args);
			}
		}
		
	}
	

	

}

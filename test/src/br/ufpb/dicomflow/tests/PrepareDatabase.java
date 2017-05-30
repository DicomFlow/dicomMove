package br.ufpb.dicomflow.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class PrepareDatabase {

	public static void main(String[] args) {
		
		/**
		 *
		 	select count(*) from dicomstudies;
			delete from dicomimages where SOPInstanc not like '%.%';
			delete from dicomseries where SeriesInst not like '%.%';
			delete from dicomstudies where StudyInsta not like '%.%';
		 * 
		 */
		
		
		String sqlStudy = "INSERT INTO dicomstudies (StudyInsta,StudyDate,StudyTime,StudyID,StudyModal,PatientNam,PatientSex,PatientID, AccessTime) "
								 +    "VALUES  (?, '20170509', '203538.960689', '0000000001', 'CT', 'PATIENT', 'M','0009703828',1493135300);";
		
		String sqlSerie = "INSERT INTO dicomseries (SeriesInst,SeriesDate,SeriesTime,SeriesNumb,Modality,StudyInsta,AccessTime) "
				 +    "VALUES  (?, '20170509', '203538.960689', '0000000001', 'CT', ?,1493135300);";
		
		String sqlImage = "INSERT INTO dicomimages (SOPInstanc,ImageDate,ImageTime,ImageNumbe,ObjectFile,SeriesInst, AccessTime) "
				 +    "VALUES  (?, '20170509', '203538.960689', '0000000001', '5Yp0E\1.3.46.670589.11.0.0.11.4.2.0.8743.5.5396.2006120114440679623_0801_000072_14931351160089.dcm', ?,1493135300);";
		
			
		Connection connection;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(
			          "jdbc:mysql://localhost:3306/conquest", "root", "root");
			System.out.println("Conectado!");
			
			int studyID = 900000;
			int serieID = 1000000;
			int imageID = 16000000;
			for (int i = 1; i < 1000000; i++) {
				PreparedStatement stmt = connection.prepareStatement(sqlStudy);
				stmt.setString(1, studyID+"");
		        stmt.execute();
		        stmt.close();
		        
		        for (int j = 1; j < 10; j++) {		        	
		        	PreparedStatement stmt2 = connection.prepareStatement(sqlSerie);
					stmt2.setString(1, serieID+"");
					stmt2.setString(2, i+"");
					stmt2.execute();
			        stmt2.close();
		        	
		        	
		        	for (int k = 1; k < 20; k++) {
		        		PreparedStatement stmt3 = connection.prepareStatement(sqlImage);
						stmt3.setString(1, imageID+"");
						stmt3.setString(2, serieID+"");
						stmt3.execute();
				        stmt3.close();
				        
				        imageID++;
						
					}
		        	
		        	serieID++;
					
				}
		        studyID++;
		        
			}
			
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		        

	}

}

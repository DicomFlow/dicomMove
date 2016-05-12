package br.ufpb.dicomflow.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnzipTool {

	public static void  unzip(String extractDir, String fileName) {
		Enumeration entries;
		ZipFile zipFile;

		try {
			zipFile = new ZipFile(fileName);
			entries = zipFile.entries();
			while(entries.hasMoreElements()) {
				ZipEntry entry = (ZipEntry)entries.nextElement();
				if(entry.isDirectory()) {
					System.err.println("Descompactando diretório: " + entry.getName());
					(new File(extractDir + entry.getName())).mkdir();
					continue;
				}
				System.out.println("Descompactando arquivo:" + entry.getName());
				copyInputStream(zipFile.getInputStream(entry),
						new BufferedOutputStream(new FileOutputStream(extractDir + entry.getName())));
			}
			zipFile.close();
		} catch (IOException ioe) {
			System.err.println("Erro ao descompactar:" + ioe.getMessage());
			return;
		}
	}
	
	public static final void copyInputStream(InputStream in, 
			OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
		in.close();
		out.close();
	}
}

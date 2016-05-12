/*
 * Project: Balcom
 * Copyright: Copyright (c) 2003
 * $Id: Util.java,v 1.1 2011/03/27 22:38:46 danilo Exp $
 */
package br.ufpb.dicomflow.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.transaction.ResinTransactionManagerLookup;

import br.ufpb.dicomflow.service.ServiceException;
import br.ufpb.dicomflow.service.ServiceLocator;



/**
 * Class Util
 * classe que reune coisas importantes que s�o usadas por v�rias classes no
 * projeto
 */
public class Util {
	private final static long ONE_MILION = 1000000L;
	
	private  ServletContext context;
	private static Util singleton=null;
	
	private Util(){}
	
	
	
	/**
	 * retoan uma inst�ncia da classe Util
	 * @return a inst�ncia da classe Util
	 */
	public static Util singleton(){
		if (singleton == null) {
			singleton = new Util();
		}
		return singleton;
	}
	

	/**
	 * tira tudo que n�o eh alfanum�rico de uma String
	 * @param str a string que vai ser analisada
	 * @return String filtrada
	 */
	private String filterString(String str){
		StringBuffer number = new StringBuffer();
		for(int i=0; i < str.length(); i++){
			if(Character.isDigit(str.charAt(i))){
				number.append(str.charAt(i));
			}
		}
		return number.toString();
	}
	

	/**
	* formata uma data para que ela possa ser salva no banco
	* @param string uma String com a data a ser salva
	* @return Date pra ser sava no banco
	*/
   public  java.sql.Date formataData(String string) {
 	   
   	   if (string != null & (!string.equals(""))) {
   		    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
   	   		dateFormat.setCalendar(Calendar.getInstance());
            Date data = null;
		    
			   try {
				data = dateFormat.parse(string);
			} catch (ParseException e) {			
				   e.printStackTrace();		    
			}
		    
		    return new java.sql.Date(data.getTime());
   	   }
   	   return null;
	   
   }
   
    /**
     * Tranforma um objeto Date em uma String.
	 * @param date a data a ser tranformada
	 * @return String representando a data passada por par�metro
	 */
	public String getDataString(Date data) {		
		String dateStr = "";		
		if (data != null) {
		
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			dateFormat.setCalendar(Calendar.getInstance());	 
			
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(data);
			 
			dateStr = dateFormat.format(data);
		}
		return dateStr;
	}
	
	/**
     * Tranforma um objeto Date em uma String.
	 * @param date a data a ser tranformada
	 * @return String representando a data passada por par�metro
	 */
	public String getDataStringDoc(Date data) {		
		String dateStr = "";		
		if (data != null) {
		
			DateFormat dateFormat = new SimpleDateFormat("dd MMMMMMMMMM yyyy");
			dateFormat.setCalendar(Calendar.getInstance());	 
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(data);
			dateStr = dateFormat.format(data);
			
		}
		return traduzData(dateStr);
	}

	private String traduzData(String dateStr) {
		String newDate = "";
		if(dateStr.toLowerCase().contains("january")){
			newDate = dateStr.substring(0, 2) + " de Janeiro de " +  dateStr.substring(dateStr.length()-4,dateStr.length());
		}
		if(dateStr.toLowerCase().contains("february")){
			newDate = dateStr.substring(0, 2) + " de Fevereiro de " +  dateStr.substring(dateStr.length()-4,dateStr.length());
		}
		if(dateStr.toLowerCase().contains("march")){
			newDate = dateStr.substring(0, 2) + " de Mar�o de " +  dateStr.substring(dateStr.length()-4,dateStr.length());
		}
		if(dateStr.toLowerCase().contains("April")){
			newDate = dateStr.substring(0, 2) + " de Abril de " +  dateStr.substring(dateStr.length()-4,dateStr.length());
		}
		if(dateStr.toLowerCase().contains("may")){
			newDate = dateStr.substring(0, 2) + " de Maio de " +  dateStr.substring(dateStr.length()-4,dateStr.length());
		}
		if(dateStr.toLowerCase().contains("June")){
			newDate = dateStr.substring(0, 2) + " de Junho de " +  dateStr.substring(dateStr.length()-4,dateStr.length());
		}
		if(dateStr.toLowerCase().contains("July")){
			newDate = dateStr.substring(0, 2) + " de Julho de " +  dateStr.substring(dateStr.length()-4,dateStr.length());
		}
		if(dateStr.toLowerCase().contains("august")){
			newDate = dateStr.substring(0, 2) + " de Agosto de " +  dateStr.substring(dateStr.length()-4,dateStr.length());
		}
		if(dateStr.toLowerCase().contains("september")){
			newDate = dateStr.substring(0, 2) + " de Setembro de " +  dateStr.substring(dateStr.length()-4,dateStr.length());
		}
		if(dateStr.toLowerCase().contains("october")){
			newDate = dateStr.substring(0, 2) + " de Outubro de " +  dateStr.substring(dateStr.length()-4,dateStr.length());
		}
		if(dateStr.toLowerCase().contains("november")){
			newDate = dateStr.substring(0, 2) + " de Novembro de " +  dateStr.substring(dateStr.length()-4,dateStr.length());
		}
		if(dateStr.toLowerCase().contains("december")){
			newDate = dateStr.substring(0, 2) + " de Dezembro de " +  dateStr.substring(dateStr.length()-4,dateStr.length());
		}
		
		return newDate;
	}



	/**
	 * valida um cpf recebido como par�metro
	 * @param str o cpf que vai ser validado
	 * @return true se eh um cpf v�lido e false caso contr�rio
	 */
	public boolean validaCPF(String str){
		String cpf = filterString(str);
	
		  if (cpf.length() != 11 || cpf == "00000000000" || cpf == "11111111111" ||
			cpf == "22222222222" ||	cpf == "33333333333" || cpf == "44444444444" ||
			cpf == "55555555555" || cpf == "66666666666" || cpf == "77777777777" ||
			cpf == "88888888888" || cpf == "99999999999"){
			  return false;
			}
	
		  int soma = 0;
	
		  for (int i=0; i < 9; i ++){
			soma += (Character.getNumericValue(cpf.charAt(i))) * (10 - i);
		  }
		  int resto = 11 - (soma % 11);
	
		  if (resto == 10 || resto == 11){
			resto = 0;
		  }
	
		  if (resto != (Character.getNumericValue(cpf.charAt(9)))){
			return false;
		  }
	
		  soma = 0;
		  for (int i = 0; i < 10; i ++){
			soma += (Character.getNumericValue(cpf.charAt(i))) * (11 - i);
		  }
		  resto = 11 - (soma % 11);
	
		  if (resto == 10 || resto == 11){
			resto = 0;
		  }
	
		  if (resto != (Character.getNumericValue(cpf.charAt(10)))){
			return false;
		  }
	
		  return true;
	
	   }
	
	/**
	 * M�todo que Retira todas as quebras de linhas de um texto
	 * @param texto o texto a ser formatado
	 * @return String o texto formatado
	 */
	public String retiraQuebraLinha(String texto){
		int index;
		StringBuffer stringBuffer = new StringBuffer(texto);
		while((index = stringBuffer.indexOf("\n")) != -1){
			stringBuffer.replace(index,index+1,"<br>");
		}
		return stringBuffer.toString();
	}
	
	public String colocaQuebraLinha(String texto){
		int index;
		StringBuffer stringBuffer = new StringBuffer(texto);
		while((index = stringBuffer.indexOf("<br>")) != -1){
			stringBuffer.replace(index,index+4,"\n");
		}
		return stringBuffer.toString();
	}


	/**
	 * m�todo que transforma um Objeto Date  em um String no formato dd/MM/yyyy HH:mm:ss
	 * @param dataPublicacao a data a ser formatada.
	 * @return String a data formatada.
	 */
	public String DataPubHHMMSS(Date dataPublicacao) {
		String retorno ="";
		if (dataPublicacao != null ) {
			 DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			 dateFormat.setCalendar(Calendar.getInstance());			
			 retorno = dateFormat.format(dataPublicacao);			
		}
		return retorno;		
	}
	
	/**
	 * retorna o contexto.
	 * @return ServeletContext o contexto.
	 */
	public ServletContext getContext() {
		return context;
	}
	
	/**
	 * modifica o contexto.
	 * @param context o novo contexto.
	 */
	public  void setContext(ServletContext context) {
		this.context = context;
	}
	
	/**
	 * Convert a list to a set.
	 * 
	 * @param orig the list to be converted
	 * @return the set converted from the list
	 */
	public static Set convertToSet(List orig) {
		Set result = new LinkedHashSet();
		
		if (orig != null) {
			Iterator ite = orig.iterator();
			
			while (ite.hasNext()) {
				result.add(ite.next());
			}
		}
		
		return result;
	}
	
	/**
	 * Convert a set to a list.
	 * @param orig the set to be converted
	 * @return the list converted from the set
	 */
	public static List convertToList(Set orig) {
		List result = new ArrayList();
		
		if (orig != null) {
			Iterator ite = orig.iterator();
			
			while (ite.hasNext()) {
				result.add(ite.next());
			}
		}
		
		return result;
	}


	/**
	 * retorna um logger para um determinado objeto.
	 * @param obj o objeto.
	 * @return Log o logger.
	 */
	public static Log getLogger(Object obj){
		return LogFactory.getLog(obj.getClass());
	}


	
//	public static Set getObjects(String param,String[] values, Class entity) {
//		Set result = new LinkedHashSet();
//		
//		List list = ServiceLocator.singleton().getPersistentService().selectAll(param, Arrays.asList(values),entity);
//		result = convertToSet(list);
//		return result;
//	}
		
	/**
	 * @return
	 */
	public static String getExtencao(String file) {
		StringTokenizer token = new StringTokenizer(file,".");
		String element = "";
		while (token.hasMoreTokens()) {
			element = (String) token.nextToken();			
		}
		return "."+element;
	}
	

	public static String saveFile(byte[] file, String fileName, String lastFileName, String location) throws ServiceException{

		if(file != null && file.length != 0){
			File lastFile = null;
			if(lastFileName != null && !lastFileName.equals("")){
				lastFile = new File(location+File.separator+lastFileName);
				lastFile.delete();
			}
			StringBuffer newFileName = new StringBuffer(String.valueOf(Util.getRadom()) + Calendar.getInstance().getTimeInMillis()+Util.getExtencao(fileName));		
			fileName = newFileName.toString();
			FileOutputStream out  = null;

			try {			
				out = new FileOutputStream(location+File.separator+fileName);	    			
				out.write(file);
				out.flush();			
				out.close();
			} catch (FileNotFoundException e) {
				throw new ServiceException(e);
			} catch (IOException e) {			
				throw new ServiceException(e);
			}
			
			return fileName;
			
		}
		return lastFileName;
	}
	

	public static String saveThumb(byte[] file, String fileName, String lastFileName, String location, int width,
			int height, int quality ) throws ServiceException{

		
		createIfLocationNotExists(location);
		
		System.out.println("file " + file);
		if(file != null && file.length != 0){
			File lastFile = null;
			if(lastFileName != null && !lastFileName.equals("")){
				lastFile = new File(location+File.separator+lastFileName);
				lastFile.delete();
			}
			StringBuffer newFileName = new StringBuffer(String.valueOf("thumb"+Util.getRadom()) + Calendar.getInstance().getTimeInMillis()+Util.getExtencao(fileName));		
			fileName = newFileName.toString();
			FileOutputStream out  = null;

			try {			
				out = new FileOutputStream(location+File.separator+fileName);	    			
				out.write(Thumbnail.makeThumbnail(file,width,height,quality));
				out.flush();			
				out.close();
			} catch (FileNotFoundException e) {
				throw new ServiceException(e);
			} catch (IOException e) {			
				throw new ServiceException(e);
			}
			
			return fileName;
			
		}
		return lastFileName;
	}



	private static void createIfLocationNotExists(String location) {
		File tmp = new File(location);
		if (!tmp.exists()) {
			tmp.mkdirs();
		}
	}
	
	public static String saveFile(File file, String fileName, String location) throws ServiceException{
		createIfLocationNotExists(location);
		if(file != null){
			File lastFile = null;
			if(fileName != null && !fileName.equals("")){
				lastFile = new File(location+File.separator+fileName);
				lastFile.delete();
			}
			StringBuffer newFileName = new StringBuffer(String.valueOf(Util.getRadom()) + Calendar.getInstance().getTimeInMillis()+Util.getExtencao(fileName));		
			fileName = newFileName.toString();

			try {			
				
				File newFile = new File(location+File.separator+fileName);
				FileUtils.copyFile(file, newFile);
				
			} catch (FileNotFoundException e) {
				throw new ServiceException(e);
			} catch (IOException e) {			
				throw new ServiceException(e);
			}
			
			return fileName;
			
		}
		return fileName;
	}
	
	public static void removeFile(String fileName, String location){
		File file = null;
		if(fileName != null && !fileName.equals("")){
			file = new File(location+File.separator+fileName);
			file.delete();
		}
	}
	
	public static String saveThumb(File file, String fileName, String path, int width,
			int height, int quality ) {
		createIfLocationNotExists(path);
		String thumb = "";
		try {
				
				thumb = "thumb"+fileName;
				FileOutputStream out  = null;			
    			
    			out = new FileOutputStream(path+File.separator+thumb);	    			
    			out.write(Thumbnail.makeThumbnail(Util.getBytesFromFile(file),width,height,quality));
    			
    			out.flush();			
    			out.close();
			
		} catch (FileNotFoundException e) {
			System.err.println("N�o foi poss�vel gerar o thumbnail para a imagem!");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("N�o foi poss�vel gerar o thumbnail para a imagem!");
			e.printStackTrace();
		}
		
		return thumb;
	}
	
	
	// Returns the contents of the file in a byte array.
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    
	
	public static long getRadom() {
		return (long) (Math.random() * ONE_MILION );
	}
	
	
	public static StringBuffer getContentFile(String filePath){
		
		StringBuffer content = new StringBuffer();
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			
			String line = null;
			while((line = reader.readLine()) != null){			
				content.append(line);
			}

			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
		
	}
	
	
	/**
	 * retorna uma lista dos objetos selecionados em um formul�rio
	 * @param arg1 identificadores dos objetos selecionados
	 * @return List objetos selecionados
	 */
	public  List getSelectedObjects(String param, Long[] arg1, Class object) {
		List objects=new ArrayList();
		List values = new ArrayList();
		for (int i = 0; i < arg1.length; i++) {
			values.add(arg1[i]);
		}
		objects = ServiceLocator.singleton().getPersistentService().selectAll(param, values, object);
		
		return objects;
	}
	
	
	public static void copyPropertiesExcludingCollection(Object dest, Object ori) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		Field[] oriFileds =  ori.getClass().getDeclaredFields();
		Method[] methods = ori.getClass().getMethods();
		
		Map<String, Method> map = new HashMap<String, Method>();
		for (Method method : methods) {
			map.put(method.getName().toString().toLowerCase(), method);
		}
		
		for (int i = 0; i < oriFileds.length; i++) {
				
			Field oriField = oriFileds[i];
			
			if(!oriField.getType().toString().contains("Set") && !oriField.getType().toString().contains("List") ){
				if(map.containsKey("get"+oriField.getName().toLowerCase()))
				BeanUtils.copyProperty(dest, oriField.getName().toString() ,map.get("get"+oriField.getName().toLowerCase()).invoke(ori, null) );
			}
				
				
				
		
		}
	}
	
	public static void main(String[] args) {
		
		System.err.println("A senha " + Base64Coder.decodeString("MzFQQTI5"));
		System.err.println("A senha " + Base64Coder.decodeString("MjlZQTI4"));
		System.err.println("A senha " + Base64Coder.decodeString("NTJBTDQ0"));
		System.err.println("A senha " + Base64Coder.decodeString("NTBNQTEx"));
		System.err.println("A senha " + Base64Coder.decodeString("NTJBTjQ0"));
		System.err.println("A senha " + Base64Coder.decodeString("MzdNTzEx"));
		System.err.println("A senha " + Base64Coder.decodeString("NTlBTjEy"));
		System.err.println("A senha " + Base64Coder.decodeString("MTFSQTE5"));
		System.err.println("A senha " + Base64Coder.decodeString("MzFBTjI5"));
		System.err.println("A senha " + Base64Coder.decodeString("NThMSTQ4"));
		System.err.println("A senha " + Base64Coder.decodeString("OTRFTTE2"));
		System.err.println("A senha " + Base64Coder.decodeString("MjFKVTI0"));
		System.err.println("A senha " + Base64Coder.decodeString("MzNCSTI5"));
		System.err.println("A senha " + Base64Coder.decodeString("NzJIVTU0"));
		System.err.println("A senha " + Base64Coder.decodeString("MTlLQTEx"));
		System.err.println("A senha " + Base64Coder.decodeString("NjRJRzEy"));
		System.err.println("A senha " + Base64Coder.decodeString("NzRHVTU0"));
		System.err.println("A senha " + Base64Coder.decodeString("NTNFUjQ0"));
		
		
		System.out.println("executando!");
		if(args.length != 1){
			System.out.println("informe o diret�rio das imagens");
			System.exit(0);
		}
		
		File dir = new File(args[0]);
		if(!dir.isDirectory()){
			System.out.println(args[0] + " N�o � um diret�rio v�lido");
			System.exit(1);
		}
		
		FileFilter fileFilter = new FileFilter() { 
            public boolean accept(File file){ 
                return 	file.getName().toLowerCase().endsWith(".jpeg") || 
                		file.getName().toLowerCase().endsWith(".jpg") ||
                		file.getName().toLowerCase().endsWith(".bmp") ||
                		file.getName().toLowerCase().endsWith(".png"); 
            } 
        };
		
		File[] files = dir.listFiles(fileFilter);
		for (int i = 0; i < files.length; i++) {
			
			File file = files[i];
			System.out.println("redimensionando o arquivo: " +  file.getName() + " no diret�rio: " + file.getParent());
			resizeFile(file, file.getName(), file.getParent(), 800, 600, 100);
			System.out.println("arquivo redimensionado!");
		}
		
		System.out.println("execu��o finalizada com sucesso!");
		
		
		
	}
	
	
	private static String resizeFile(File file, String fileName, String path, int width, int height, int quality ) {
		
		String thumb = "";
		try {
				
				thumb = "thumb_"+fileName;
				FileOutputStream out  = null;			
    			
    			out = new FileOutputStream(path+File.separator+thumb);	    			
    			out.write(Thumbnail.makeThumbnail(Util.getBytesFromFile(file),width,height,quality));
    			
    			out.flush();			
    			out.close();
			
		} catch (FileNotFoundException e) {
			System.err.println("N�o foi poss�vel gerar o thumbnail para a imagem!");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("N�o foi poss�vel gerar o thumbnail para a imagem!");
			e.printStackTrace();
		}
		
		return thumb;
	}
	
	public static String getCredential(){
		return "" + (int)Math.random() * 1000000;
		
	}
}

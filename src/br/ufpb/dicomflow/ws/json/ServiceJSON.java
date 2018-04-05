package br.ufpb.dicomflow.ws.json;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.beanutils.BeanUtils;

import br.ufpb.dicomflow.integrationAPI.main.ServiceFactory;
import br.ufpb.dicomflow.integrationAPI.message.xml.ServiceIF;
import br.ufpb.dicomflow.ws.graphql.GraphqlException;

public class ServiceJSON implements JSONDecorator {
	
	public static final String HIGHEST = "HIGHEST";
	public static final String HIGH = "HIGH";
	public static final String NORMAL = "NORMAL";
	public static final String LOW = "LOW";
	public static final String LOWEST = "LOWEST";
	
	
	private String version;
	private String name;
	private String action;
	private int type;
	private Date timestamp;
	private String timeout;
	private String timezone;
	private String priority;
	private String requestType;
	
	private Set<UrlJSON> urls;
	
	private UserJSON user;
	

	protected ServiceJSON() {
		super();
	}
	
	public static ServiceJSON createService(int serviceType) throws GraphqlException {
		ServiceJSON serviceJSON = new ServiceJSON();
		ServiceIF service = ServiceFactory.createService(serviceType);
		try {
			BeanUtils.copyProperties(serviceJSON, service);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new GraphqlException(e);
		}
		
		
		return serviceJSON;
	}

	@Override
	public String getJSON() {
		
		StringBuilder json = new StringBuilder("");
			
		json.append("name: \"");
		json.append(name);
		json.append("\", ");
		
		json.append("action: \"");
		json.append(action);
		json.append("\", ");
		
		json.append("version: \"");
		json.append(version);
		json.append("\", ");
		
		json.append("type: ");
		json.append(getTypeDescription(type));
		json.append(", ");
		
		if(requestType !=  null && !requestType.isEmpty()){
			json.append("requestType: ");
			json.append(requestType);
			json.append(", ");
		}
		
		if(timestamp !=  null){
			json.append("timestamp: \"");
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			json.append(formatter.format(timestamp));
			json.append("\", ");
		}
		
		if(timeout != null && !timeout.isEmpty()){
			json.append("timeout: \"");
			json.append(timeout);
			json.append("\", ");
		}
		
		if(timezone != null && !timezone.isEmpty()){
			json.append("timezone: \"");
			json.append(timezone);
			json.append("\", ");
		}
		
		if(priority !=  null && !priority.isEmpty()){
			json.append("priority: ");
			json.append(priority);
			json.append(", ");
		}
		
		if(user != null && !user.getJSON().isEmpty()){
			if(user.getId() != null && !user.getId().isEmpty()){
				json.append("userId: ");
				json.append(user.getJSON());
				json.append(", ");
			}else{
				json.append("user: {");
				json.append(user.getJSON());
				json.append("}, ");
			}
			
		}
		
		json.append("urls: [ ");
		
		if(urls != null){
			
			Iterator<UrlJSON> it = urls.iterator();
			if(it.hasNext()){
				UrlJSON urlJson = it.next();
				json.append("{");
				json.append(urlJson.getJSON());
				json.append("}");
				
			}
			while (it.hasNext()) {
				UrlJSON urlJson = it.next();
				json.append(", {");
				json.append(urlJson.getJSON());
				json.append("}");
			}
			
		}
		
		json.append("] ");
	
		return json.toString();
		
	}
	
	private String getTypeDescription(int type){
		switch (type) {
		case ServiceIF.STORAGE_SAVE:
			return "STORAGE_SAVE";
		case ServiceIF.STORAGE_UPDATE:
			return "STORAGE_UPDATE";
		case ServiceIF.STORAGE_DELETE:
			return "STORAGE_DELETE";
		case ServiceIF.STORAGE_RESULT:
			return "STORAGE_RESULT";
		case ServiceIF.CERTIFICATE_REQUEST:
			return "CERTIFICATE_REQUEST";
		case ServiceIF.CERTIFICATE_RESULT:
			return "CERTIFICATE_RESULT";
		case ServiceIF.CERTIFICATE_CONFIRM:
			return "CERTIFICATE_CONFIRM";
		case ServiceIF.SHARING_PUT:
			return "SHARING_PUT";			
		case ServiceIF.SHARING_RESULT:
			return "SHARING_RESULT";
		case ServiceIF.REQUEST_PUT:
			return "REQUEST_PUT";			
		case ServiceIF.REQUEST_RESULT:
			return "REQUEST_RESULT";
		case ServiceIF.DISCOVERY_VERIFY_ALL_SERVICES:
			return "DISCOVERY_VERIFY_ALL_SERVICES";
		case ServiceIF.DISCOVERY_VERIFY_SERVICES:
			return "DISCOVERY_VERIFY_SERVICES";			
		case ServiceIF.DISCOVERY_VERIFY_RESULT:
			return "DISCOVERY_VERIFY_RESULT";
		case ServiceIF.FIND_PUT:
			return "FIND_PUT";			
		case ServiceIF.FIND_RESULT:
			return "FIND_RESULT";
		default:
			return "";
		}
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public Set<UrlJSON> getUrls() {
		return urls;
	}

	public void setUrls(Set<UrlJSON> urls) {
		this.urls = urls;
	}

	public UserJSON getUser() {
		return user;
	}

	public void setUser(UserJSON user) {
		this.user = user;
	}

	
	
	

}

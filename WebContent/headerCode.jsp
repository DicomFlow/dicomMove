<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ taglib prefix="sx" uri="/struts-dojo-tags" %>

<%@ page import="java.util.*"%>

<%@ page import="br.ufpb.dicomflow.business.*"%>
<%@ page import="br.ufpb.dicomflow.service.*"%>
<%@ page import="br.ufpb.dicomflow.service.impl.*"%>

<%@ page import="br.ufpb.dicomflow.bean.*"%>

<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+"/"+path+"/";
%>

<%
	Cache.getInstance().makeCache();
%>

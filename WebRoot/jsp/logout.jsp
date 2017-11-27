<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="android.testing.system.util.Adb" %>
<%@ page import="android.testing.system.log.LogHandler" %>
<%@ page import="android.testing.system.device.DeviceHandler" %>
<%@ page import="android.testing.system.util.Commands" %>

<%
	String serialNumber = request.getParameter("serialNumber");
	String username = request.getParameter("username");
	String server = request.getParameter("server");
	
	DeviceHandler handler = new DeviceHandler();
	if(handler.getState(server, serialNumber) == 1) {
		handler.setState(server, serialNumber, 0);
	}
	
	new Commands(serialNumber).executeCommand("PRESS HOME DOWN_AND_UP");
	// log
	new LogHandler().newLog(username, server, serialNumber, "UNLOCK", System.currentTimeMillis());
	Adb.getInstance().getLock().getLocks().get(serialNumber).destroy();
	
	String fromUrl = request.getParameter("fromUrl");
	response.sendRedirect(fromUrl); 
%>
package android.testing.system.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import android.testing.system.device.DeviceHandler;
import android.testing.system.log.LogHandler;
import android.testing.system.util.Action;
import android.testing.system.util.Adb;
import android.testing.system.util.Commands;
import android.testing.system.util.hierarchyviewer.device.ListElements;
import android.testing.system.util.zip.ApkHelper;

import com.android.sdklib.xml.ManifestData;
import com.google.gson.Gson;

public class ExecuteCommands extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		String command = request.getParameter("req");
//		String q = request.getParameter("q");
		String username = request.getSession().getAttribute("username").toString();
		String server = request.getSession().getAttribute("server").toString();
		String serialNumber = request.getSession().getAttribute("serialNumber").toString();
				
		Map<String, String> map = new HashMap<String, String>();
		String apkName = null;
		String apkRealpath = null;
		// depart to two parts, DeviceCommand & ServerCommand
		switch(Action.toAction(command.split(" ")[0].toUpperCase())) {
			// device commands
			case TOUCH: case PRESS: case DRAG: case TYPE: case UNINSTALL: case START:				
				break;
			case SYNC:
				apkName = command.split(" ")[1];
				apkRealpath = Adb.getInstance().getConf().getProperty("BaseUploadRealpath").concat("/")
					.concat(request.getSession().getId()).concat("/").concat(apkName);
				command = "SYNC ".concat(apkRealpath).concat(" /sdcard/");
				break;
			// server commands
			case INSTALL: case REINSTALL: 
				apkName = command.split(" ")[1];
				apkRealpath = Adb.getInstance().getConf().getProperty("BaseUploadRealpath").concat("/")
					.concat(request.getSession().getId()).concat("/").concat(apkName);
				command = command.split(" ")[0].concat(" ".concat(apkRealpath).concat(" /data/local/tmp/"));
				break;
			case ERROR:
				break;
			
		}
		map.putAll(command(username, server, serialNumber, command));
		if(map.containsKey("result")) {
			if("INSTALL_SUCCESS".equals(map.get("result"))) {
				ManifestData md = ApkHelper.getManifestData(apkRealpath);
				map.put("component", ApkHelper.getComponent(md));
				Adb.getInstance().getLock().getLocks().get(serialNumber).addPackage(ApkHelper.getPackage(md));
				map.put("packageName", ApkHelper.getPackage(md));
			} else if("SYNC_SUCCESS".equals(map.get("result"))){
				Adb.getInstance().getLock().getLocks().get(serialNumber).addFile("/sdcard/".concat(apkName));
				map.put("file", "/sdcard/".concat(apkName));
			}
			
		}
		new LogHandler().newLog(username, server, serialNumber, command, System.currentTimeMillis());
		
		response.setContentType("text/html;charset=utf-8");
		response.getOutputStream().print(new Gson().toJson(map));
	}
	
	private Map<String, String> command(String username, String server, String serialNumber, String command) {
		Map<String, String> map = new HashMap<String, String>();
		String result = null;
		if(new ListElements().checkApplicationSettings(Adb.getInstance().getIDevice(serialNumber))
				&& (command.indexOf("TOUCH") > -1 || command.indexOf("PRESS ENTER") > -1)) {
			result = "FORBIDDEN";
		} else {
			if(new DeviceHandler().isAvailable(username, server, serialNumber) != 4) {
				result = new Commands(serialNumber).executeCommand(command);
			} else {
				result = "TIMEOUT";
			}
		}
		map.put("result", result);
		return map;
	}
}

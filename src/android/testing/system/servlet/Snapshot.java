package android.testing.system.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import android.testing.system.device.DeviceHandler;
import android.testing.system.util.SnapshotHandler;

public class Snapshot extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String server = request.getSession().getAttribute("server").toString();
		String serialNumber = request.getSession().getAttribute("serialNumber").toString();
		String q = request.getParameter("q");
		String sessionId = request.getSession().getId();
		
		response.setContentType("text/html;charset=utf-8");
		if(new DeviceHandler().getState(server, serialNumber) != 0) {
			response.getOutputStream().print(new SnapshotHandler().returnUrl(serialNumber, sessionId, q));
		} else {
			response.getOutputStream().print("TIMEOUT");
		}
	}
}

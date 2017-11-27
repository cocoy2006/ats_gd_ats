package android.testing.system.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import android.testing.system.util.Adb;

public class Unlock extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		if(request.getSession().getAttribute("username") != null) {
			String serialNumber = request.getSession().getAttribute("serialNumber").toString();
			Adb.getInstance().getLock().getLocks().get(serialNumber).destroy();
			
			response.sendRedirect(request.getSession().getAttribute("fromUrl").toString());
		}
	}
}

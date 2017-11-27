package android.testing.system.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import android.testing.system.user.UserHandler;

public class UpdateLefttime1 extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
//		String username = request.getParameter("username");
//		String server = request.getParameter("server");
//		String serialNumber = request.getParameter("serialNumber");
//		int lefttime = Integer.parseInt(request.getParameter("lefttime"));
		
		String username = request.getSession().getAttribute("username").toString();
		String server = request.getSession().getAttribute("server").toString();
		String serialNumber = request.getSession().getAttribute("serialNumber").toString();
		int lefttime = Integer.parseInt(request.getParameter("lefttime"));
		
//		int result = new UpdateLefttimeHandler(username, server, serialNumber, lefttime).handle();
		int result = new UserHandler().updateLefttime(username, server, serialNumber, lefttime);
		if(result == 1) request.getSession().setAttribute("lefttime", lefttime);
		
		response.setContentType("text/html;charset=utf-8");
		response.getOutputStream().print(result);
	}
}

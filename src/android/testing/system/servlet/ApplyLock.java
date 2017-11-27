package android.testing.system.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import android.testing.system.log.LogHandler;
import android.testing.system.user.UserHandler;
import android.testing.system.util.Adb;


public class ApplyLock extends HttpServlet {

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		HttpSession session = request.getSession();
		String sessionId = session.getId();
		
		String username = request.getParameter("username");
		String server = request.getParameter("server");
		String serialNumber = request.getParameter("serialNumber");
		String fromUrl = request.getParameter("fromUrl");
		
		int lefttime = Integer.parseInt(request.getParameter("lefttime"));
		
		/** redirect way */
		session.setAttribute("username", username);
		session.setAttribute("server", server);
		session.setAttribute("serialNumber", serialNumber);
		session.setAttribute("fromUrl", fromUrl);
		session.setAttribute("lefttime", lefttime);
		
//		Adb adb = Adb.getInstance();
		response.setContentType("text/html;charset=utf-8");
//		if(adb.getExpire().isExpire()) {
//			response.sendRedirect(fromUrl);
//		} 
//		else 
		if(new UserHandler().getUser(username, 0).getState() == 4) {
			response.sendRedirect("../errorPage/reLogin.jsp");
//			return;
		}
		else if(Adb.getInstance().getLock().applyLock(serialNumber, sessionId, username, server, lefttime)) {
			// new log
			new LogHandler().newLog(username, server, serialNumber, "LOCK", System.currentTimeMillis());
			new UserHandler().updateState(username, 4);
			response.sendRedirect("../jsp/device.jsp");
		} else {
			response.sendRedirect(fromUrl);
		}
	}
}

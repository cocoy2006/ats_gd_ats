package android.testing.system.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import android.testing.system.util.Adb;
import android.testing.system.util.AutotestHandler;

public class Autotest extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		
		String fileName = request.getParameter("fileName");
		String serialNumber = request.getParameter("serialNumber");
		String sessionId = request.getSession().getId();
		
		String tmp = Adb.getInstance().getConf().getProperty("BaseUploadRealpath")
			.concat("/").concat(sessionId).concat("/").concat(fileName);
		File file = new File(tmp);		
		
		AutotestHandler atest = new AutotestHandler();
		atest.Start(file, serialNumber, sessionId);
		
		String js = "OK";		
		response.getWriter().println(js);
	}
}

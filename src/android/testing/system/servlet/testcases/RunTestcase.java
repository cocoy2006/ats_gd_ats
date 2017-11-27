package android.testing.system.servlet.testcases;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import android.testing.system.testcase.TestcaseHandler;
import android.testing.system.util.Adb;
import android.testing.system.util.AutotestHandler;

public class RunTestcase extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String directory = Adb.getInstance().getConf().getProperty("temp");
		String username = request.getParameter("username");
		String project = request.getParameter("project");
		String name = request.getParameter("name");
		String serialNumber = request.getParameter("serialNumber");
		
		File file = new File(new TestcaseHandler().loadTestcase(directory, username, project, name));
		
		AutotestHandler atest = new AutotestHandler();
		atest.start(file, serialNumber, username);
//		atest.Start(file, serialNumber, sessionId);
		
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().print("OK");
	}
}

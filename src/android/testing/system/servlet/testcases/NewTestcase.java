package android.testing.system.servlet.testcases;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import android.testing.system.util.NewTestcaseHandler;

public class NewTestcase extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		String serialNumber = request.getParameter("serialNumber");
		String username = request.getParameter("username");
		String project = request.getParameter("project");
		String testcase = request.getParameter("testcase");
		int length = Integer.parseInt(request.getParameter("length"));
		
		NewTestcaseHandler handler = new NewTestcaseHandler(serialNumber, username, project, testcase);
		for(int i = length - 1; i >= 0; i--) {
			handler.addHistory(request.getParameter("command" + i), request.getParameter("date" + i));
		}
		
		int result = handler.handle();
		
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().print(result);
	}
}

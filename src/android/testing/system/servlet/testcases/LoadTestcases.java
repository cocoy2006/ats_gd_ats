package android.testing.system.servlet.testcases;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import android.testing.system.testcase.TestcaseHandler;

public class LoadTestcases extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		
		String result = new TestcaseHandler().loadTestcases(username);
		
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().print(result);
	}
}

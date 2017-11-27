package android.testing.system.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class UpProgress extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain;charset=utf-8");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		Double percentage = 0.0;
		if (session.getAttribute("percentage") != null) {
			percentage = (Double) session.getAttribute("percentage");
			double retVal = Math.floor(percentage * 100);
			if (retVal == 100.0D) {
				session.removeAttribute("percentage");
			}
			System.out.println("UpProgress is "+retVal);
//			out.print("{retVal:" + retVal + "}");
			out.print(retVal);
		} else {
			System.out.println("UpProgress is -1.0");
//			out.print("{retVal:-1.0}");
			out.print(-1.0);
		}		
		out.flush();
	}
}

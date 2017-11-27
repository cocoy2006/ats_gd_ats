package android.testing.system.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import android.testing.system.util.reservation.ReservationHandler;

public class Reserve extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String username = request.getParameter("username");
		String serialNumber = request.getParameter("serialNumber");
		long startTime = Long.parseLong(request.getParameter("startTime"));
		long endTime = Long.parseLong(request.getParameter("endTime"));
		
		boolean result = new ReservationHandler().reserve(username, serialNumber, startTime, endTime);
		
		response.getWriter().print(result);
	}
}

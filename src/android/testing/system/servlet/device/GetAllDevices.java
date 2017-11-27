package android.testing.system.servlet.device;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import android.testing.system.device.DeviceHandler;

import com.google.gson.Gson;

public class GetAllDevices extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		List list = new DeviceHandler().getAllDevices();
		
		Gson gson = new Gson();
		String result = gson.toJson(list);
		
		response.setContentType("text/html;charset=utf-8");
		response.getWriter().print(result);
	}
}

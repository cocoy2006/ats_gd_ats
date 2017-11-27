package android.testing.system.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import android.testing.system.util.Adb;

public class Restart extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setContentType("text/html;charset=utf-8");
//		String result = "<script type='text/javascript'>alert(\"%s\");</script>";
//		if(Adb.getInstance().restart() == 1) {
//			response.getWriter().print("成功！");
//		} else {
//			response.getWriter().print("失败！请确认服务器已正常启动");
//		}
//		System.out.println("正在重新启动设备...");
		int result = Adb.getInstance().restart();
//		System.out.println("完成.");
//		response.getWriter().print(result);
		response.getOutputStream().print(result);
	}
}

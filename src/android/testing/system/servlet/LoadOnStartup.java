package android.testing.system.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

import android.testing.system.util.Adb;
import android.testing.system.util.Conf;
import android.testing.system.util.Known;

import com.android.ddmlib.IDevice;

public class LoadOnStartup extends HttpServlet implements ServletContextListener {

	public void contextInitialized(ServletContextEvent arg0) {
		
		//��ʼ��Adb����
		Adb adb = Adb.getInstance();
		
//		boolean isExpire = adb.getExpire().init(arg0.getServletContext().getRealPath("/WEB-INF/"));
//		if(isExpire) System.exit(0);
		//��ʼ��ʱ�����
		Conf conf = adb.getConf();
		conf.setProperty("exceptionTime", arg0.getServletContext().getInitParameter("exceptionTime"));
//		Log.printLog(LogLevel.INFO, new Date().toString() + " servlet/LoadOnStartup", "exceptionTime is " + arg0.getServletContext().getInitParameter("exceptionTime"));
		conf.setProperty("timeoutTime", arg0.getServletContext().getInitParameter("timeoutTime"));
//		Log.printLog(LogLevel.INFO, new Date().toString() + " servlet/LoadOnStartup", "timeoutTime is " + arg0.getServletContext().getInitParameter("timeoutTime"));
		//��ʼ����վURL
		conf.setProperty("BaseURL", "/ATS");
		//��ȡ��ĻĿ¼
		conf.setProperty("BaseScreenCapturedURL", conf.getProperty("BaseURL").concat("/screenCaptured/"));
		conf.setProperty("BaseScreenCapturedRealpath", arg0.getServletContext().getRealPath("/screenCaptured/"));
		//��վͼƬĿ¼
		conf.setProperty("BaseImageURL", conf.getProperty("BaseURL").concat("/image/"));
		conf.setProperty("BaseImageRealpath", arg0.getServletContext().getRealPath("/image/"));
		//CSS�ļ�Ŀ¼
		conf.setProperty("BaseCssURL", conf.getProperty("BaseURL").concat("/css/"));
		conf.setProperty("BaseCssRealpath", arg0.getServletContext().getRealPath("/css/"));
		//�ϴ��ļ�Ŀ¼
		conf.setProperty("BaseUploadRealpath", arg0.getServletContext().getRealPath("/uploaded/"));
		//��ʷ�ļ�Schema�ļ�Ŀ¼
		conf.setProperty("BaseSchemaRealpath", arg0.getServletContext().getRealPath("/schema/"));
		//�Զ����Խ��Ŀ¼
		conf.setProperty("BaseAutotestResultRealpath", arg0.getServletContext().getRealPath("/autotestResult/"));
		//��ʼ����֪�豸����
		new Known(adb);
		
		conf.setProperty("temp", arg0.getServletContext().getRealPath("/temp/"));
		conf.setProperty("runTestcaseResult", arg0.getServletContext().getInitParameter("runTestcaseResult"));
		
		Timer hotSwapTimer = new Timer();
		hotSwapTimer.schedule(new HotSwapTask(), 0, 10 * 1000);
	}

	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

	class HotSwapTask extends TimerTask {

		@Override
		public void run() {
			Adb adb = Adb.getInstance();
//			System.out.println("服务器上有" + adb.getAdbBackend().getBridge().getDevices().length + "台设备.");
//			System.out.println("ATS上有" + adb.getIDevices().size() + "台设备.");
			if(adb.getAdbBackend().getBridge().getDevices().length != adb.getIDevices().size()) {
				try {
					Map<String, IDevice> map = new HashMap<String, IDevice>();
					for(IDevice iDevice : adb.getAdbBackend().getBridge().getDevices()) {
						String iSerialNumber = iDevice.getSerialNumber();
						map.put(iSerialNumber, iDevice);
						if(adb.getMonkeyDevice(iSerialNumber) == null) {
							System.out.println("检测到新设备，正在启动设备" + iSerialNumber + "...");
							Thread thread = new StartDeviceThread(adb, iDevice, iSerialNumber);
							thread.start();
							thread.join();
							System.out.println(iSerialNumber + "启动完成.");
						}
					}
//					if(map.size() > 0) {
						for(IDevice iDevice : adb.getIDevices().values()) {
							String iSerialNumber = iDevice.getSerialNumber();
							if(!map.containsKey(iSerialNumber)) {
								System.out.println("检测到移除的设备，正在移除设备" + iSerialNumber + "...");
								adb.getMonkeyDevices().remove(iSerialNumber);
								adb.getIDevices().remove(iSerialNumber);
								System.out.println(iSerialNumber + "移除完成.");
							}
						}
//					}
				} catch(Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	class StartDeviceThread extends Thread {
		private Adb adb;
		private IDevice iDevice;
		private String serialNumber;
		
		public StartDeviceThread(Adb adb, IDevice iDevice, String serialNumber) {
			this.adb = adb;
			this.iDevice = iDevice;
			this.serialNumber = serialNumber;
		}
		
		public void run() {
			adb.getKnown().addProperties(iDevice);
			iDevice.setReservationSet();
			adb.getIDevices().put(serialNumber, iDevice);
			adb.waitForConnection(serialNumber);
		}
	}
}

package android.testing.system.util;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.testing.system.device.DeviceHandler;
import android.testing.system.log.LogHandler;
import android.testing.system.user.UserHandler;
import android.testing.system.util.history.History;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.monkeyrunner.adb.AdbMonkeyDevice;

public class DeviceWithTimer {
	
	private AdbMonkeyDevice device;
	private String sessionId;
	private String username;
	private String server;
	
	private Set<String> files;
	private Set<String> packages;
	
	private long totalTime;
	private Timer exceptionTimer;
	private Timer timeoutTimer;
	
	private History history;
	
	public static void main(String[] args) {
		Adb adb = Adb.getInstance();
		IDevice idevice = adb.getIDevice("emulator-5554");
		try {
			idevice.removeRemotePackage("/sdcard/pb.ipa");
		} catch (InstallException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public DeviceWithTimer(AdbMonkeyDevice device, String sessionId, String username, String server, int exceptionTime, int timeoutTime) {
		this.device = device;
		this.sessionId = sessionId;
		this.username = username;
		this.server = server;
		
		files = new HashSet<String>();
		packages = new HashSet<String>();
		
		this.totalTime = timeoutTime * 1000;

		exceptionTimer = new Timer();
		exceptionTimer.schedule(new ExceptionTimer(exceptionTime), 5 * 1000, 60 * 1000);

		timeoutTimer = new Timer();
		timeoutTimer.schedule(new TimeoutTimer(), totalTime);
	}
	
	public AdbMonkeyDevice getDevice() {
		return device;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	
	public void addFile(String file) {
		if(!files.contains(file)) files.add(file);
	}
	
	public Set<String> getFiles() {
		return files;
	}
	
	public void addPackage(String packageName) {
		if(!packages.contains(packageName)) packages.add(packageName);
	}
	
	public Set<String> getPackages() {
		return packages;
	}
	
	public void setHistory(History history) {
		this.history = history;
		this.history.addAttribute("serialNumber", device.getSerialNumber());
		this.history.addAttribute("width", device.getIDevice().getProperty("self.display.width"));
		this.history.addAttribute("height", device.getIDevice().getProperty("self.display.height"));
		this.history.addAttribute("manufacturer", device.getIDevice().getProperty("ro.product.manufacturer"));
		this.history.addAttribute("model", device.getIDevice().getProperty("ro.product.model"));
		//something else...
	}
	
	public History getHistory() {
		return this.history;
	}
	
	public void destroy() {
		new Commands(device.getSerialNumber()).executeCommand("PRESS HOME DOWN_AND_UP");
		new LogHandler().newLog(username, server, device.getSerialNumber(), "UNLOCK", System.currentTimeMillis());
//		new DeviceHandler().setState(server, device.getSerialNumber(), 0);
		new UserHandler().updateState(username, 0);
		DeviceHandler dh = new DeviceHandler();
		if(dh.getState(server, device.getSerialNumber()) == 1) {
			dh.setState(server, device.getSerialNumber(), 0);
		}
		for(String file : files) {
			try {
				System.out.println("删除 " + file + "...");
				device.getIDevice().removeRemotePackage(file);
			} catch (InstallException e) {
				System.out.println(e.getMessage());
			}
			System.out.println("删除 " + file + "完成.");
		}
		for(String packageName : packages) {
			System.out.println("卸载 " + packageName + "...");
			device.removePackage(packageName);
			System.out.println("卸载 " + packageName + "完成.");
		}
		exceptionTimer.cancel();
		timeoutTimer.cancel();
		// unbind deviceWithTimer object with serialNumber in Lock table
//		Adb.getInstance().getLock().getLocks().put(device.getSerialNumber(), null);
	}
	
	class ExceptionTimer extends TimerTask {		
		private long exceptionTime;
		
		public ExceptionTimer(long exceptionTime) {
			this.exceptionTime = exceptionTime;
		}		
		
		@Override
		public void run() {
			if(isOver(new LogHandler().getLastTime(username), System.currentTimeMillis(), exceptionTime * 60 * 1000)) {
				destroy();
			}
		}
		
		private boolean isOver(long previousTime, long currentTime, long overTime) {
			return (currentTime - previousTime) >= overTime;
		}
	}
	
	class TimeoutTimer extends TimerTask {

		@Override
		public void run() {
			destroy();
		}
	}
}

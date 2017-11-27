package android.testing.system.util;

import java.util.HashMap;
import java.util.Map;

import android.testing.system.device.DeviceHandler;

import com.android.ddmlib.IDevice;
import com.android.monkeyrunner.adb.AdbBackend;
import com.android.monkeyrunner.adb.AdbMonkeyDevice;

public class Lock {

	private Map<String, DeviceWithTimer> locks = new HashMap<String, DeviceWithTimer>();
	
	public Lock(AdbBackend adbBackend) {
		for (IDevice idevice : adbBackend.getBridge().getDevices()) {
			String serialNumber = idevice.getSerialNumber();
			locks.put(serialNumber, null);
		}
	}

	public boolean applyLock(String serialNumber, String sessionId, String username, String server, int lefttime) {
		DeviceHandler handler = new DeviceHandler();
		Adb adb;
		AdbMonkeyDevice amd;
		int exceptionTime;
		DeviceWithTimer device;
		switch(handler.isAvailable(username, server, serialNumber)) {
			case 0: 
				adb = Adb.getInstance();
				amd = (AdbMonkeyDevice) adb.getMonkeyDevice(serialNumber);
				exceptionTime = Integer.parseInt(adb.getConf().getProperty("exceptionTime"));
				device = new DeviceWithTimer(amd, sessionId, username, server, exceptionTime, lefttime);
				locks.put(serialNumber, device);
				handler.setState(server, serialNumber, 1);
				return true;
			case 1: case 4:
				return false;
			case 3:
				handler.setState(server, serialNumber, 0);
				return false;
			case 5:
				adb = Adb.getInstance();
				amd = (AdbMonkeyDevice) adb.getMonkeyDevice(serialNumber);
				exceptionTime = Integer.parseInt(adb.getConf().getProperty("exceptionTime"));
				device = new DeviceWithTimer(amd, sessionId, username, server, exceptionTime, lefttime);
				locks.put(serialNumber, device);
				return true;
		}
		return false;
	}
	
//	public boolean updateLastTime(String serialNumber, long lastTime) {
//		if(locks.containsKey(serialNumber)) {
//			synchronized(locks) {
//				locks.get(serialNumber).setLastTime(lastTime);
//			}
//			return true;
//		}
//		return false;
//	}
	
	public Map<String, DeviceWithTimer> getLocks() {
		synchronized(locks) {
			return locks;
		}
	}
}
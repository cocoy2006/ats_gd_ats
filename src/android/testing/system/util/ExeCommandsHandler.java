package android.testing.system.util;

import android.testing.system.device.DeviceHandler;


public class ExeCommandsHandler {

	public String returnUrl(String username, String fromUrl, String server, String serialNumber, String command, String q) {
//		String url = null;
		if(new DeviceHandler().isAvailable(username, server, serialNumber) != 4) {
			new Commands(serialNumber).executeCommand(command);
//			url = "/servlet/Snapshot?q=" + q;
		} else {
			return "TIMEOUT";
		}
		return null;
//		return url;
	}
}

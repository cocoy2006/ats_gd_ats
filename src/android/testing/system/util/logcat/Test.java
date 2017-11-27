package android.testing.system.util.logcat;

import android.testing.system.util.Adb;

import com.android.ddmlib.IDevice;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("LogCat thread is starting...");
		Adb adb = Adb.getInstance();
    	IDevice device = adb.getIDevice("emulator-5556");
    	new LogCatPanel().startLogCat(device);
    	System.out.println("LogCat thread is running...");
	}

}

package android.testing.system.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.InstallException;
import com.android.ddmlib.SyncException;
import com.android.ddmlib.TimeoutException;
import com.android.monkeyrunner.MonkeyDevice;
import com.android.monkeyrunner.PhysicalButton;
import com.android.monkeyrunner.MonkeyDevice.TouchPressType;

public class Commands {
	
	private Adb adb;
	private String serialNumber;
	private MonkeyDevice device;
	private IDevice idevice;
	
	public Commands(String serialNumber) {
		adb = Adb.getInstance();
		this.serialNumber = serialNumber;
		device = adb.getMonkeyDevice(serialNumber);
		idevice = adb.getIDevice(serialNumber);
	}
	//CMD = WAKE
	public void wake() {
		device.wake();
	}
	
//	public void done() {
//		device.done();
//	}
//	
//	public void dispose() {
//		device.dispose();
//	}
//	
//	public MonkeyImage takeSnapshot() {
//		return device.takeSnapshot();
//	}
//	
//	public void reboot(String cmd) {
//		String[] cmds = cmd.split(" ");
//		device.reboot(cmds[1]);
//	}

	public void touch(String cmd) {
		String[] cmds = cmd.split(" ");
		int x = Integer.parseInt(cmds[1]), y = Integer.parseInt(cmds[2]);
		if(cmds[3].equalsIgnoreCase("DOWN")) {
			device.touch(x, y, TouchPressType.DOWN);
		} else {
			device.touch(x, y, TouchPressType.DOWN_AND_UP);
		}
	}
	//CMD = PRESS HOME/MENU TYPE
	// 		PRESS BACK/SEARCH/UP/DOWN/LEFT/RIGHT/ENTER/CENTER
	public void press(String cmd) {
		String[] cmds = cmd.split(" ");
		if(cmds[1].equalsIgnoreCase("HOME") || cmds[1].equalsIgnoreCase("MENU")
				|| cmds[1].equalsIgnoreCase("CALL") || cmds[1].equalsIgnoreCase("ENDCALL")) {
			if(cmds[2].equalsIgnoreCase("DOWN")) {
				device.press(cmds[1], TouchPressType.DOWN);
			} else {
				device.press(cmds[1], TouchPressType.DOWN_AND_UP);
			}
		} else {
			try {
				if(cmds[1].equalsIgnoreCase("BACK")) {
					device.getManager().press(PhysicalButton.BACK);
				} else if(cmds[1].equalsIgnoreCase("SEARCH")) {
					device.getManager().press(PhysicalButton.SEARCH);
				} else if(cmds[1].equalsIgnoreCase("UP")) {
					device.getManager().press(PhysicalButton.DPAD_UP);
				} else if(cmds[1].equalsIgnoreCase("DOWN")) {
					device.getManager().press(PhysicalButton.DPAD_DOWN);
				} else if(cmds[1].equalsIgnoreCase("LEFT")) {
					device.getManager().press(PhysicalButton.DPAD_LEFT);
				} else if(cmds[1].equalsIgnoreCase("RIGHT")) {
					device.getManager().press(PhysicalButton.DPAD_RIGHT);
				} else if(cmds[1].equalsIgnoreCase("CENTER")) {
					device.getManager().press(PhysicalButton.DPAD_CENTER);
				} else if(cmds[1].equalsIgnoreCase("ENTER")) {
					device.getManager().press(PhysicalButton.ENTER);
				} 
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	//CMD = DRAG STARTX STARTY ENDX ENDY STEPS MS
	public void drag(String cmd) {
		String[] cmds = cmd.split(" ");
		int startx = Integer.parseInt(cmds[1]), starty = Integer.parseInt(cmds[2]),
			endx = Integer.parseInt(cmds[3]), endy = Integer.parseInt(cmds[4]),
			steps = Integer.parseInt(cmds[5]);
		long ms = Long.parseLong(cmds[6]);
		device.drag(startx, starty, endx, endy, steps, ms);
	}
	//CMD = TYPE CMD
	public void type(String cmd) {
		String[] cmds = cmd.split(" ");
		device.type(cmd.replaceFirst(cmds[0], ""));
	}
	//CMD = SHELL CMD
	public String shell(String cmd) {
		String[] cmds = cmd.split(" ");
		return device.shell(cmds[1]);
	}
	//CMD = SYNC LOCALFILEPATH REMOTEPATH
	public String sync(String cmd) {
		String[] cmds = cmd.split(" ");
		String result = null;
		try {
			idevice.syncPackageToDevice(cmds[1], cmds[2]);
		} catch (SyncException e) {
			result = "SyncException";
			System.out.println(e.getMessage());
		} catch (TimeoutException e) {
			result = "TimeoutException";
			System.out.println(e.getMessage());
		} catch (AdbCommandRejectedException e) {
			result = "AdbCommandRejectedException";
			System.out.println(e.getMessage());
		} catch (IOException e) {
			result = "IOException";
			System.out.println(e.getMessage());
		}
		if(result == null) result = "SYNC_SUCCESS";
		return result;
	}
	//CMD = REMOVE PACKAGENAME
	public String removePackage(String cmd) {
		String[] cmds = cmd.split(" ");
		try {
			idevice.removeRemotePackage(cmds[1]);
			return "REMOVE_SUCCESS";
		} catch (InstallException e) {
			System.out.println(e.getMessage());
			return "InstallException";
		}
	}
	//CMD = INSTALL LOCALFILEPATH REMOTEPATH
	public String installPackage(String cmd) {
		String[] cmds = cmd.split(" ");
		String result = null;
		try {
			result = idevice.installPackage(cmds[1], cmds[2], false);
		} catch (InstallException e) {
			result = "InstallException";
			System.out.println(e.getMessage());
		}
		if(result == null) result = "INSTALL_SUCCESS";
		return result;
	}
	//CMD = REINSTALL LOCALFILEPATH REMOTEPATH
	public String reinstallPackage(String cmd) {
		String[] cmds = cmd.split(" ");
		String result = null;
		try {
			result = idevice.installPackage(cmds[1], cmds[2], true);
		} catch (InstallException e) {
			result = "InstallException";
			System.out.println(e.getMessage());
		}
		if(result == null) result = "INSTALL_SUCCESS";
		return result;
	}
	//CMD = UNINSTALL PACKAGE
	public String uninstallPackage(String cmd) {
		String[] cmds = cmd.split(" ");
		String result = null;
		try {
			result = idevice.uninstallPackage(cmds[1]);
		} catch (InstallException e) {
			e.printStackTrace();
			result = "InstallException";
		}
		if(result == null) result = "UNINSTALL_SUCCESS";
		return result;
	}
	//CMD = START URI ACTION DATA MIMETYPE CATEGORIES EXTRAS COMPONENT FLAGS
	//CMD = START COMPONENT(PACKAGE/PACKAGE+ACTIVITY)
	public void startActivity(String cmd) {
		String[] cmds = cmd.split(" ");
		device.startActivity("", "", "", "", new HashSet<String>(), new HashMap<String, Object>(), cmds[1], 0);
	}
	//CMD = BROADCASTINTENT URI ACTION DATA MIMETYPE EXTRAS COMPONENT FLAGS
	public void broadcastIntent(String cmd) {
		
	}
	//CMD = INSTRUMENT PACKAGENAME ARGS
	public Map<String, Object> instrument(String cmd) {
		return null;
	}
	//CMD = RELEASE
//	public void release(String serialNumber) {
//		adb.getLock().releaseLock(serialNumber);
//	}

	public String executeCommand(String command) {
		String result = null;
		switch(Action.toAction(command.split(" ")[0].toUpperCase())) {
			case WAKE:
				wake();
				break;
			case TOUCH:
				touch(command);
				break;
			case PRESS:
				press(command);
				break;
			case DRAG:
				drag(command);
				break;
			case TYPE:
				type(command);
				break;
			case SYNC:
				result = sync(command);
				break;
			case REMOVE:
				result = removePackage(command);
				break;
			case INSTALL:
				result = installPackage(command);
				break;
			case REINSTALL:
				result = reinstallPackage(command);
				break;
			case UNINSTALL:
				result = uninstallPackage(command);
				break;
			case START:
				startActivity(command);
				break;
			case ERROR:
				break;
			default:
				break;
		}
		return result;
	}
}

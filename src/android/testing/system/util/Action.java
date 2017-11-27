package android.testing.system.util;

public enum Action {
	WAKE, TOUCH, PRESS, DRAG, TYPE, SYNC, REMOVE, INSTALL, REINSTALL, UNINSTALL, START, UPLOAD, RESERVE, ERROR;
	
	public static Action toAction(String action) {
		try {
			return valueOf(action);
		} catch(Exception e) {
			return ERROR;
		}
	}
}

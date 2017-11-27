package android.testing.system.util.reservation;

import android.testing.system.util.Adb;

import com.android.ddmlib.IDevice;
import com.android.monkeyrunner.MonkeyDevice;


public class ReservationHandler {

	private Adb adb;
	
	public ReservationHandler() {
		adb = Adb.getInstance();
	}
	
	public boolean reserve(String username, String serialNumber, long startTime, long endTime) {
		IDevice device = adb.getIDevice(serialNumber);
		Reservation r = new Reservation(username, startTime, endTime);
		return device.getReservationSet().addReservation(r);
	}
}

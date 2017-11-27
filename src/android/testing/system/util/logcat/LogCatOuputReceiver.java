package android.testing.system.util.logcat;

import com.android.ddmlib.MultiLineReceiver;

public class LogCatOuputReceiver extends MultiLineReceiver {

	public boolean isCancelled = false;

	public LogCatOuputReceiver() {
		super();
		setTrimLine(false);
	}

	@Override
	public void processNewLines(String[] lines) {
		if (isCancelled == false) {
			for (String line : lines) {
				System.out.println(line);
			}
		}
	}

	public boolean isCancelled() {
		return isCancelled;
	}
}

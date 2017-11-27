package android.testing.system.util.logcat;

import com.android.ddmlib.IDevice;

public class LogCatPanel {

	private static final int STRING_BUFFER_LENGTH = 10000;
	private LogCatOuputReceiver mCurrentLogCat;
	
    /**
     * Circular buffer containing the logcat output. This is unfiltered.
     * The valid content goes from <code>mBufferStart</code> to
     * <code>mBufferEnd - 1</code>. Therefore its number of item is
     * <code>mBufferEnd - mBufferStart</code>.
     */
    private LogMessage[] mBuffer = new LogMessage[STRING_BUFFER_LENGTH];

    /** Represents the oldest message in the buffer */
    private int mBufferStart = -1;

    /**
     * Represents the next usable item in the buffer to receive new message.
     * This can be equal to mBufferStart, but when used mBufferStart will be
     * incremented as well.
     */
    private int mBufferEnd = -1;
	
	/** Device currently running logcat */
    private IDevice mCurrentLoggedDevice = null;
    
	/**
     * Starts a new logcat and set mCurrentLogCat as the current receiver.
     * @param device the device to connect logcat to.
     */
    public void startLogCat(final IDevice device) {
        if (device == mCurrentLoggedDevice) {
            return;
        }

        // if we have a logcat already running
        if (mCurrentLoggedDevice != null) {
            stopLogCat(false);
            mCurrentLoggedDevice = null;
        }

//        resetUI(false);

        if (device != null) {
            // create a new output receiver
            mCurrentLogCat = new LogCatOuputReceiver();

            // start the logcat in a different thread
            new Thread("Logcat")  { //$NON-NLS-1$
                @Override
                public void run() {

                	while (device.isOnline() == false &&
                            mCurrentLogCat != null &&
                            mCurrentLogCat.isCancelled == false) {
                        try {
                            sleep(2000);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }

                    if (mCurrentLogCat == null || mCurrentLogCat.isCancelled) {
                        // logcat was stopped/cancelled before the device became ready.
                        return;
                    }

                    try {
                        mCurrentLoggedDevice = device;
                        device.executeShellCommand("logcat -v long", mCurrentLogCat, 0 /*timeout*/); //$NON-NLS-1$
                    } catch (Exception e) {
                        
                    } finally {
                        // at this point the command is terminated.
                        mCurrentLogCat = null;
                        mCurrentLoggedDevice = null;
                    }
                }
            }.start();
        }
    }
    
    /** Stop the current logcat */
    public void stopLogCat(boolean inUiThread) {
        if (mCurrentLogCat != null) {
            mCurrentLogCat.isCancelled = true;

            // when the thread finishes, no one will reference that object
            // and it'll be destroyed
            mCurrentLogCat = null;

            // reset the content buffer
            for (int i = 0 ; i < STRING_BUFFER_LENGTH; i++) {
                mBuffer[i] = null;
            }

            // because it's a circular buffer, it's hard to know if
            // the array is empty with both start/end at 0 or if it's full
            // with both start/end at 0 as well. So to mean empty, we use -1
            mBufferStart = -1;
            mBufferEnd = -1;

//            resetFilters();
//            resetUI(inUiThread);
        }
    }
}

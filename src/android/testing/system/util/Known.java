package android.testing.system.util;

import java.io.File;
import java.io.IOException;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.RawImage;
import com.android.ddmlib.TimeoutException;

public class Known {

	public Known(Adb adb) {
		adb.setKnown(this);
		for(IDevice idevice : adb.getAdbBackend().getBridge().getDevices()) {
			addProperties(idevice);
		}
	}
	
	public void addProperties(IDevice idevice) {
		Adb adb = Adb.getInstance();
		String imageURL = adb.getConf().getProperty("BaseImageURL");
		String imageRealpath = adb.getConf().getProperty("BaseImageRealpath");
		
		String manufacturer = idevice.getProperty("ro.product.manufacturer");
		String model = idevice.getProperty("ro.product.model");
		String url = imageURL.concat(manufacturer).concat("/").concat(model).concat("/");
		String realpath = imageRealpath.concat("\\").concat(manufacturer).concat("\\").concat(model).concat("\\");
		
		if(isKnown(realpath, null)) {
			addProperty(idevice, "self.skin.url", url.concat("skin.jpg"));
			addProperty(idevice, "self.skin.height", String.valueOf(getHeight(realpath.concat("skin.jpg"))));
			addProperty(idevice, "self.css.url", url.concat("main.css"));
		} else {
			addProperty(idevice, "self.skin.url", null);
			addProperty(idevice, "self.skin.height", String.valueOf(0));
			addProperty(idevice, "self.css.url", imageURL.concat("x/main.css"));
		}		
		
		RawImage image = null;
		try {
			image = idevice.getScreenshot();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (AdbCommandRejectedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(image != null) {
			addProperty(idevice, "self.display.height", String.valueOf(image.height));
			addProperty(idevice, "self.display.width", String.valueOf(image.width));
		}
	}
	private boolean isKnown(String url, String temp) {
		File f = new File(url);
		if(f.exists()) {
			return true;
		} else {
			return false;
		}
	}
	private void addProperty(IDevice idevice, String name, String value) {
		idevice.addProperty(name, value);
	}
	
	private int getHeight(String file) {
		int height = 0;
		File f = new File(file);
		java.awt.image.BufferedImage image = null;
		try {
			image = javax.imageio.ImageIO.read(f);
			height = image.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return height;
	}
}
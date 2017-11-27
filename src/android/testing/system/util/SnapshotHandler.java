package android.testing.system.util;

import java.awt.image.BufferedImage;
import java.util.Date;

import com.android.monkeyrunner.MonkeyDevice;
import com.android.monkeyrunner.MonkeyImage;

public class SnapshotHandler {

	private final String PIC_FORMAT = "jpg";
	
	private MonkeyImage pre = null;
	private String preImageName = null;
	
	public String returnUrl(String serialNumber, String sessionId, String q) {
		Adb adb = Adb.getInstance();
		String parent = adb.getConf().getProperty("BaseScreenCapturedRealpath");
		
		new Dir().newDir(parent, sessionId);
		String userDir = parent.concat("/").concat(sessionId);
		//��ȡ�ֻ���Ļ
		
		MonkeyDevice device = adb.getMonkeyDevice(serialNumber);
		MonkeyImage cur = device.takeSnapshot();
		
		if(cur != null ) {
			//������ĻͼƬ
			String imageName = setImageName();
			String imagePath = userDir.concat("/").concat(imageName).concat(".").concat(PIC_FORMAT);
			//��ͻ��˷���URL
			if(pre == null) {
				pre = cur;
				preImageName = imageName;
				cur.writeToFile(imagePath, PIC_FORMAT);
			} else if(!isSame(pre, cur, 1.0)) {
				pre = cur;
				preImageName = imageName;
				cur.writeToFile(imagePath, PIC_FORMAT);
			} else {
				imageName = preImageName;	
			}	
			
			return adb.getConf().getProperty("BaseScreenCapturedURL").concat(sessionId).concat("/")
				.concat(imageName).concat(".").concat(PIC_FORMAT);
		} 
		return null;
	}
	
	private boolean isSame(MonkeyImage preImage, MonkeyImage curImage, double percent) {
		BufferedImage pre = preImage.getBufferedImage();
		BufferedImage cur = curImage.getBufferedImage();
        int width = cur.getWidth();
        int height = cur.getHeight();
        int numDiffPixels = 0;
        // Now, go through pixel-by-pixel and check that the images are the same;       
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (cur.getRGB(x, y) != pre.getRGB(x, y)) {
                	numDiffPixels++;
                }
            }
        }
        double numberPixels = (height * width);
        double diffPercent = numDiffPixels / numberPixels;
        return percent <= 1.0 - diffPercent;
	}
	
	private final int lenOfImageName = 10;
	private String setImageName() {
		Date date = new Date();
		String imageName = String.valueOf(date.getTime());
		int endIndex = imageName.length();
		int beginIndex = endIndex - lenOfImageName;
		imageName = imageName.substring(beginIndex, endIndex);
		return imageName;
	}
}

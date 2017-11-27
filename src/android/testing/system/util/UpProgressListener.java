package android.testing.system.util;

import org.apache.commons.fileupload.ProgressListener;

public class UpProgressListener implements ProgressListener {

	// private HttpServletRequest request;
	// public UpProgressListener(HttpServletRequest request) {
	// this.request = request;
	// }
	//    
	// public void update(long bytesRead, long contentLength, int items) {
	// // 将百分比保存到session中
	// double percentage = ((double) bytesRead / (double) contentLength);
	// HttpSession session = this.request.getSession();
	// session.setAttribute("percentage", percentage);
	// // System.out.println("percentage is :[" +
	// session.getAttribute("percentage") + "]");
	// }
	private double upRate = 0.0;
	private double percent = 0.0;
	private long useTime = 0;
	private long upSize = 0;
	private long allSize = 0;
	private int item;

	private long beginT = System.currentTimeMillis();
	private long curT = System.currentTimeMillis();

	public void update(long pBytesRead, long pContentLength, int pItems) {

		curT = System.currentTimeMillis();
		item = pItems;
		allSize = pContentLength; // byte
		upSize = pBytesRead; // byte
		useTime = curT - beginT; // ms
		if (useTime != 0)
			upRate = pBytesRead / useTime; // byte/ms
		else
			upRate = 0.0;
		if (pContentLength == 0)
			return;
		percent = (double) pBytesRead / (double) pContentLength; // 百分比

	}

	public long getAllSize() {
		return allSize;
	}

	public void setAllSize(long allSize) {
		this.allSize = allSize;
	}

	public long getBeginT() {
		return beginT;
	}

	public void setBeginT(long beginT) {
		this.beginT = beginT;
	}

	public long getCurT() {
		return curT;
	}

	public void setCurT(long curT) {
		this.curT = curT;
	}

	public int getItem() {
		return item;
	}

	public void setItem(int item) {
		this.item = item;
	}

	public double getPercent() {
		return percent;
	}

	public void setPercent(double percent) {
		this.percent = percent;
	}

	public double getUpRate() {
		return upRate;
	}

	public void setUpRate(double upRate) {
		this.upRate = upRate;
	}

	public long getUpSize() {
		return upSize;
	}

	public void setUpSize(long upSize) {
		this.upSize = upSize;
	}

	public long getUseTime() {
		return useTime;
	}

	public void setUseTime(long useTime) {
		this.useTime = useTime;
	}
}

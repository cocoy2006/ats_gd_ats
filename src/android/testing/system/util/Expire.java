package android.testing.system.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class Expire {

	private String expireTime = "2JqsOlwpZA/+sm/3VvNuXQ==";
	private String fileName;
	private long diff = 7776000000L; //3 * 30 * 24 * 60 * 60 * 1000L;
	
	public boolean init(String webroot) {
		this.fileName = webroot.concat("/classes/android/testing/system/util/AtsgzTime.class");
		return isExpire();
	}
	
	public boolean isExpire() {
		long now = System.currentTimeMillis();
		if((now - getTotalTime()) >= diff) {
			return true;
		} else {
			if(getLastTime() == 0 || now <= getLastTime()) return true;
			else {
				setLastTime(System.currentTimeMillis());
				Timer expireTimer = new Timer();
				expireTimer.scheduleAtFixedRate(new ExpireTimerTask(), 0, 3600000L);
				return false;
			}
		}
	}
	
	class ExpireTimerTask extends TimerTask {

		@Override
		public void run() {
			setLastTime(System.currentTimeMillis());
		}
	}
	
	public long getTotalTime() {
		long totalTime = 0;
		try {
			totalTime = Long.parseLong(new String(new Security().decrypt(Base64.decode(this.expireTime)), "UTF8"));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Base64DecodingException e) {
			e.printStackTrace();
		}
		return totalTime;
	}
	
	public boolean setLastTime(long lastTime) {
		File f = new File(this.fileName);
		if(!f.exists()) return false;
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			bw.write(Base64.encode(new Security().encrypt(String.valueOf(lastTime).getBytes())));
			bw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public long getLastTime() {
		File f = new File(this.fileName);
		if(!f.exists()) return 0;
		long lastTime = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String ciperText = br.readLine();
			br.close();
			if(ciperText == null) return 0;
			lastTime = Long.parseLong(new String(new Security().decrypt(Base64.decode(ciperText)), "UTF8"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Base64DecodingException e) {
			e.printStackTrace();
		}
		return lastTime;
	}
}

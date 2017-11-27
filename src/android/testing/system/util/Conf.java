package android.testing.system.util;

import java.util.HashMap;
import java.util.Map;

public class Conf {
	private Map<String, String> conf;
	
	public Conf() {
		conf = new HashMap<String, String>();
	}
	
	public String getProperty(String key) {
		return conf.get(key);
	}
	
	public void setProperty(String key, String value) {
		conf.put(key, value);
	}
}

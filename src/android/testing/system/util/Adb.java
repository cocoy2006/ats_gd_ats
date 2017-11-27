package android.testing.system.util;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import android.testing.system.util.hierarchyviewer.device.DeviceBridge;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.android.monkeyrunner.MonkeyDevice;
import com.android.monkeyrunner.adb.AdbBackend;
import com.android.monkeyrunner.adb.AdbMonkeyDevice;
/**
 * ά���������豸��Adb�����Լ�һЩ��صĻ���������õ���ģʽ����֤Adb�����Ψһ�ԡ�
 * �ڷ����豸֮ǰ������ص���getInstance()������ȡAdb�����������Ҳ��һ����ڷ�����
 * 
 * @author �Ÿ�������*/
public class Adb {
	private static Adb adb = null;
	private AdbBackend adbBackend = null;
	
	private Map<String, IDevice> idevices = null;
	private final int CONNECTION_ITERATION_TIMEOUT_MS = 200;
	
	private Expire expire = null;
	private Conf conf = null;
	private Known known = null;
	private Lock lock = null;
	private Map<String, MonkeyDevice> monkeyDevices = null;
	
	private Configuration cfg;
	private SessionFactory factory;
	
	private Adb() {
//		KnownDevice.init();
		expire = new Expire();
		conf = new Conf();
//		known = new Known();
		
		adbBackend = new AdbBackend();

		lock = new Lock(adbBackend);

		setIDevices(adbBackend.getBridge());
		
		monkeyDevices = new HashMap<String, MonkeyDevice>();
		initAdbMonkeyDevices();
		
		cfg = new Configuration();
		factory = cfg.configure().buildSessionFactory();
	}
	/**��ʼ������
	 * 
	 * @return Adb���� */
	public static Adb getInstance() {
		if(adb == null) {
			synchronized(Adb.class) {
				adb = new Adb();
			}
		}
		return adb;
	}
	/**
	 * update monkeyDevices informations*/
	public int restart() {
		try {
//			if(adbBackend.getBridge().getDevices().length != idevices.size()) {
//				for(IDevice iDevice : adbBackend.getBridge().getDevices()) {
//					String iSerialNumber = iDevice.getSerialNumber();
//					if(getMonkeyDevice(iSerialNumber) == null) {
//						System.out.println("检测到新设备，正在启动设备...");
//						known.addProperties(iDevice);
//						iDevice.setReservationSet();
//						idevices.put(iSerialNumber, iDevice);
//						waitForConnection(iSerialNumber);
//						System.out.println("完成.");
//					}
//				}
//			}
			Map<String, IDevice> map = new HashMap<String, IDevice>();
			for(IDevice iDevice : adbBackend.getBridge().getDevices()) {
				String iSerialNumber = iDevice.getSerialNumber();
				map.put(iSerialNumber, iDevice);
				if(getMonkeyDevice(iSerialNumber) == null) {
					System.out.println("检测到新设备，正在启动设备...");
					known.addProperties(iDevice);
					iDevice.setReservationSet();
					idevices.put(iSerialNumber, iDevice);
					waitForConnection(iSerialNumber);
					System.out.println("完成.");
				}
			}
			if(map.size() > 0) {
				for(IDevice iDevice : idevices.values()) {
					String iSerialNumber = iDevice.getSerialNumber();
					if(!map.containsKey(iSerialNumber)) {
						System.out.println("检测到被移除的设备，正在移除设备...");
						unactiveDevice(iSerialNumber);
						System.out.println("完成.");
					}
				}
			}
		} catch(Exception e) {
			System.out.println(e.getMessage());
			return 0;
		}
		return 1;
	}
	
	public int unactiveDevice(String serialNumber) {
		try {
			System.out.println("\t正在断开设备" + serialNumber + "...");
			AdbMonkeyDevice amd = (AdbMonkeyDevice) monkeyDevices.get(serialNumber);
			amd.done();
			System.out.println("\t完成.");
		} catch(Exception e) {
			System.out.println(e.getMessage());
			monkeyDevices.remove(serialNumber);
			idevices.remove(serialNumber);
			return 0;
		}
		return 1;
	}
	
	public AdbBackend getAdbBackend() {
		return adbBackend;
	}
	public Expire getExpire() {
		return expire;
	}
	public Conf getConf() {
		return conf;
	}
	public void setKnown(Known known) {
		this.known = known;
	}
	public Known getKnown() {
		return known;
	}
	public Lock getLock() {
		return lock;
	}
//	public Map<String, MonkeyDevice> getMonkeyDevices() {
//		return monkeyDevices;
//	}
	public MonkeyDevice getMonkeyDevice(String serialNumber) {
		return monkeyDevices.get(serialNumber);
	}
	
	public Map<String, MonkeyDevice> getMonkeyDevices() {
		return monkeyDevices;
	}
	
	private void initAdbMonkeyDevices() {
		for(String sn : idevices.keySet()) {
			waitForConnection(sn);
		}
	}
	/**����ָ���豸
	 * 
	 * @param serialNumber �豸���кţ�<code>String</code>����
	 * @return MonkeyDevice���� */
	public AdbMonkeyDevice waitForConnection(String serialNumber) {
		return waitForConnection(Integer.MAX_VALUE, serialNumber);
	}
	
	private AdbMonkeyDevice waitForConnection(long timeoutMs, String serialNumber) {
        do {
            IDevice device = findAttacedDevice(serialNumber);
            if (device != null && device.getState() == IDevice.DeviceState.ONLINE) {
                AdbMonkeyDevice amd = new AdbMonkeyDevice(device);
                monkeyDevices.put(device.getSerialNumber(), amd);
                return amd;
            }
            try {
                Thread.sleep(CONNECTION_ITERATION_TIMEOUT_MS);
            } catch (InterruptedException e) {
                
            }
            timeoutMs -= CONNECTION_ITERATION_TIMEOUT_MS;
        } while (timeoutMs > 0);
        // Timeout.  Give up.
        return null;
    }
	
	private void setIDevices(AndroidDebugBridge bridge) {
		idevices = new HashMap<String, IDevice>();
		IDevice[] idevs = bridge.getDevices();
		for(IDevice idev : idevs) {
			idev.setReservationSet(); // ��ʼ��ԤԼ��Ϣ
			if(!DeviceBridge.isViewServerRunning(idev)) {
				System.out.println("View Server is running [" + DeviceBridge.startViewServer(idev) + "]");
			}
	        
			idevices.put(idev.getSerialNumber(), idev);
		}
	}

	/**��ȡָ���豸
	 * 
	 * @param serialNumber �豸���кţ�<code>String</code>����
	 * @return IDevice���� */
	public IDevice getIDevice(String serialNumber) {
		return idevices.get(serialNumber);
	}
	
	public Map<String, IDevice> getIDevices() {
		return idevices;
	}

	/**��ȡ�����豸�����к�
	 * 
	 * @return <code>String</code>�����б� */
//	public List<String> getSerialNumbers() {
//		List<String> numbers = new ArrayList<String>();
//		for (IDevice device : adbBackend.getBridge().getDevices()) {
//			String serialNumber = device.getSerialNumber();
//			numbers.add(serialNumber);
//        }
//		return numbers;
//	}

	
	/**����ָ���豸�Ƿ�������
	 * 
	 * @param serialNumber �豸���кţ�<code>String</code>����
	 * @return IDevice���� */
	public IDevice findAttacedDevice(String serialNumber) {
		if(idevices.containsKey(serialNumber)) {
			return idevices.get(serialNumber);
		}
		return null;
	}
	
	public SessionFactory getFactory() {
		return factory;
	}
}

package android.testing.system.util.hierarchyviewer.device;

import java.io.BufferedReader;
import java.io.IOException;

import android.testing.system.util.Adb;

import com.android.ddmlib.IDevice;

public class ListElements {

    private static DeviceConnection conn;
    
	public static void main(String[] args) {
		ListElements elements = new ListElements();
//		DeviceBridge bridge = new DeviceBridge();
		Adb adb = Adb.getInstance();
    	IDevice device = adb.getIDevice("HT95WKF02252");
    	DeviceBridge.bridge = adb.getAdbBackend().getBridge();
    	while(true) {
    		try {
//    			if(!DeviceBridge.isViewServerRunning(device)) {
//    				System.out.println("View Server is running [" + DeviceBridge.startViewServer(device) + "]");
//    			}
    			System.out.println("View Server is running [" + DeviceBridge.isViewServerRunning(device) + "]");
    			elements.checkApplicationSettings(device);
//    			DeviceBridge.stopViewServer(device);
    			System.out.println("ADB restart is [" + adb.getAdbBackend().getBridge().restart() + "]");
    			Thread.sleep(2000);
    		} catch(Exception e) {
    			System.out.println(e.getMessage());
    			System.exit(0);
    		}
    	}
//    	if(!DeviceBridge.isViewServerRunning(device)) {
//			System.out.println("View Server is running [" + DeviceBridge.startViewServer(device) + "]");
//		}
//    	elements.checkApplicationSettings(device); // PRINT
//    	DeviceBridge.stopViewServer(device);
//    	DeviceBridge.terminate();
//    	System.exit(0);
	}
	
	public boolean checkApplicationSettings(IDevice device) {
		if(device.isOnline()) {
			java.util.Stack<String> stack = new java.util.Stack<String>();
			DeviceBridge.setupDeviceForward(device);
			if(!DeviceBridge.isViewServerRunning(device)) {
//				System.out.println("isViewServerRunning is false!");
				try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                }
                if (!DeviceBridge.startViewServer(device)) {
                    DeviceBridge.removeDeviceForward(device);
                }
                return false;
			}
			DeviceConnection connection = null;
	        try {
	        	connection = new DeviceConnection(device);
	        	connection.sendCommand("LIST");
	        	BufferedReader in = connection.getInputStream();
	            String line;
	            while ((line = in.readLine()) != null) {
	                if ("DONE.".equalsIgnoreCase(line)) { //$NON-NLS-1$
	                    break;
	                }
//	                System.out.println(line);
//	            	if(line.indexOf("com.android") > -1) {
//						stack.push(line);
//					}
	            	if(line.indexOf(".") > -1) {
	            		stack.push(line);
	            	}
	            }
	            if(!stack.isEmpty()) {
					String line1 = stack.pop();
//					System.out.println(device.getSerialNumber() + "的UI:" + line1);
					if(line1.indexOf("com.android.settings.ApplicationSettings") > -1) {
						return true;
					}
//					String line2 = stack.pop();
//					if(line2.indexOf("com.android.settings.ApplicationSettings") > -1) {
//						return true;
//					}
				}
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        } finally {
	            if (connection != null) {
	                connection.close();
	            }
	        }
		}
		
//		if(!DeviceBridge.isViewServerRunning(device)) {
//			System.out.println("View Server is running [" + DeviceBridge.startViewServer(device) + "]");
//		}
//		
//		if(!devicePortMap.containsKey(device)) {
//			do {
//				DeviceBridge.setupDeviceForward(device);
//			} while (4939 != DeviceBridge.getDeviceLocalPort(device));
//
//		}
//		try {
//			conn = new DeviceConnection(device);
//			conn.sendCommand("LIST");
//			return isForbidden();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		} finally {
//			conn.close();
//		}
		return false;
	}
	
    private static void loadViewServerInfoAndWindows(final IDevice device) {
//        ViewServerInfo viewServerInfo = DeviceBridge.loadViewServerInfo(device);
//        if (viewServerInfo == null) {
//            return;
//        }
        DeviceConnection connection = null;
        try {
        	connection = new DeviceConnection(device);
        	connection.sendCommand("LIST");
        	BufferedReader in = connection.getInputStream();
            String line;
            while ((line = in.readLine()) != null) {
                if ("DONE.".equalsIgnoreCase(line)) { //$NON-NLS-1$
                    break;
                }
                System.out.println(line);
            }
        } catch (Exception e) {
            
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
	
	private boolean isForbidden() {
		try {
			BufferedReader in = conn.getInputStream();
			java.util.Stack<String> stack = new java.util.Stack<String>();
			String line = null;
			while((line = in.readLine()) != null) {
				if(line.indexOf("com.android") > -1) {
					stack.push(line);
				}
			}
			if(!stack.isEmpty()) {
				String line1 = stack.pop();
				System.out.println("line1 is " + line1);
				if(line1.indexOf("com.android.settings.ApplicationSettings") > -1) {
					return true;
				}
				String line2 = stack.pop();
				if(line2.indexOf("com.android.settings.ApplicationSettings") > -1) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}

package android.testing.system.log;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import android.testing.system.SQL;
import android.testing.system.device.Device;
import android.testing.system.user.User;
import android.testing.system.util.Adb;

public class LogHandler {

	private SessionFactory factory;
	
	public LogHandler() {
		factory = Adb.getInstance().getFactory();
	}
	
	public boolean newLog(String username, String server, String serialNumber, String operation, long time) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();		
			
			User u = (User) session.createQuery(String.format(SQL.SELECT_USER, username)).uniqueResult();
			Log log = new Log();
			log.setUser_id(u.getId());
			if(server != null && serialNumber != null) {
				Device d = (Device) session.createQuery(String.format(SQL.SELECT_DEVICE, server, serialNumber)).uniqueResult();
				log.setDevice_id(d.getId());
			}
			if(time == 0) time = System.currentTimeMillis();
			log.setOperation(operation);
			log.setTime(time);
			session.save(log);
			
			ts.commit();
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			ts.rollback();
			return false;
		} finally {
			session.close();
		}
	}
	
	public List getLogs(String username, long startTime, long endTime) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();		
			
			List list = session.createQuery(String.format(SQL.SELECT_LOGS_WITH_USERNAME, username, startTime, endTime)).list();

			ts.commit();
			return list;
		} catch(Exception e) {
			e.printStackTrace();
			ts.rollback();
			return null;
		} finally {
			session.close();
		}
	}
	
	public List getLogs(String server, String serialNumber, long startTime, long endTime) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();		
			
//			Device d = (Device) session.createQuery(String.format(SQL.SELECT_DEVICE, server, serialNumber)).uniqueResult();
			List list = session.createQuery(String.format(SQL.SELECT_LOGS_WITH_DEVICE_INFO, server, serialNumber, startTime, endTime)).list();
			
			ts.commit();
			return list;
		} catch(Exception e) {
			e.printStackTrace();
			ts.rollback();
			return null;
		} finally {
			session.close();
		}
	}
	
	public long getLastTime(String username) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();		
			
			User u = (User) session.createQuery(String.format(SQL.SELECT_USER, username)).uniqueResult();
			long lastTime = (Long) session.createQuery(String.format(SQL.SELECT_LAST_TIME_WITH_USER_ID, u.getId())).uniqueResult();
//			System.out.println("[INFO:LogHandler] last operation time is " + lastTime);
			
			ts.commit();
			return lastTime;
		} catch(Exception e) {
			e.printStackTrace();
			ts.rollback();
			return 0;
		} finally {
			session.close();
		}
	}
}

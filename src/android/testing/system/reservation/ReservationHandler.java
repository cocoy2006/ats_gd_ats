package android.testing.system.reservation;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import android.testing.system.SQL;
import android.testing.system.device.Device;
import android.testing.system.user.User;
import android.testing.system.util.Adb;

public class ReservationHandler {

	private SessionFactory factory;
	
	public ReservationHandler() {
		factory = Adb.getInstance().getFactory();
	}
	
	/**
	 * 0:success, 1:failure*/
	public int newReservation(String username, String server, String serialNumber, long startTime, long endTime) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();			
			
			Device d = (Device) session.createQuery(String.format(SQL.SELECT_DEVICE, server, serialNumber)).uniqueResult();
			List<?> list = session.createQuery(String.format(SQL.SELECT_RESERVATIONS_WITH_DEVICE_ID, d.getId())).list();
			if(list == null || isLegal(list, startTime, endTime)) {
				User u = (User) session.createQuery(String.format(SQL.SELECT_USER, username)).uniqueResult();
				Reservation r = new Reservation();
				r.setUser_id(u.getId());
				r.setDevice_id(d.getId());
				r.setStartTime(startTime);
				r.setEndTime(endTime);
				r.setState(1);
				session.save(r);
				
				int lefttime = u.getLefttime() - (int)((endTime - startTime) / 1000);
				u.setLefttime(lefttime);
				session.update(u);
				
				ts.commit();
				return 0;
			} else {
				ts.rollback();
				return 1;
			}
		} catch(Exception e) {
			e.printStackTrace();
			ts.rollback();
			return 1;
		} finally {
			session.close();
		}
	}
	
	private boolean isLegal(List<?> list, long startTime, long endTime) {
		for(int i = 0; i < list.size(); i++) {
			Reservation r1 = (Reservation) list.get(i);
			if(r1.getStartTime() >= endTime) return true;
			if(startTime >= r1.getStartTime() && startTime <= r1.getEndTime()
					|| endTime >= r1.getStartTime() && endTime <= r1.getEndTime()
					|| startTime <= r1.getStartTime() && endTime >= r1.getEndTime())
				return false;
		}
		return true;
	}
	
//	public int handleReservation(String username, String server, String serialNumber, long startTime, long endTime) {
//		Session session = null;
//		Transaction ts = null;
//		try {
//			session = factory.openSession();
//			ts = session.beginTransaction();
//			
//			User u = (User) session.createQuery(String.format(SQL.SELECT_USER, username)).uniqueResult();
//			Device d = (Device) session.createQuery(String.format(SQL.SELECT_DEVICE, server, serialNumber)).uniqueResult();
//			Reservation r = (Reservation) session.createQuery(String.format(SQL.SELECT_RESERVATION, u.getId(), d.getId(), startTime, endTime)).uniqueResult();
//			r.setState(0);
//			session.update(r);
//			
//			ts.commit();
//			return 0;
//		} catch(Exception e) {
//			e.printStackTrace();
//			ts.rollback();
//			return 1;
//		} finally {
//			session.close();
//		}
//	}
	
//	public List getAllReservations(long startTime, long endTime) {
//		Session session = null;
//		Transaction ts = null;
//		try {
//			session = factory.openSession();
//			ts = session.beginTransaction();
//			
//			List list = session.createQuery(String.format(SQL.SELECT_ALL_RESERVATIONS, startTime, endTime)).list();
//			
//			ts.commit();
//			return list;
//		} catch(Exception e) {
//			e.printStackTrace();
//			ts.rollback();
//			return null;
//		} finally {
//			session.close();
//		}
//	}
	
	public List getUserReservations(String username, long startTime, long endTime) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();
			
			User u = (User) session.createQuery(String.format(SQL.SELECT_USER, username)).uniqueResult();
			List list = session.createQuery(String.format(SQL.SELECT_USER_RESERVATIONS, u.getId(), startTime, endTime)).list();
			
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
	
	public List getDeviceReservations(String server, String serialNumber, long startTime, long endTime) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();
			
			Device d = (Device) session.createQuery(String.format(SQL.SELECT_DEVICE, server, serialNumber)).uniqueResult();
			List list = session.createQuery(String.format(SQL.SELECT_DEVICE_RESERVATIONS, d.getId(), startTime, endTime)).list();
			
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
	
	public String getReservationOwner(String server, String serialNumber, long time) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();
			
			Device d = (Device) session.createQuery(String.format(SQL.SELECT_DEVICE, server, serialNumber)).uniqueResult();
			Reservation r = (Reservation) session.createQuery(String.format(SQL.SELECT_RESERVATION_OWNER, d.getId(), time, time)).uniqueResult();
//			User u = (User) session.createQuery(String.format(SQL.SELECT_USER_INFO, r.getUser_id())).uniqueResult();
			User u = (User) session.load(User.class, r.getUser_id());
			
			ts.commit();
			return u.getUsername();
		} catch(Exception e) {
			e.printStackTrace();
			ts.rollback();
			return null;
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public int handleOverdueReservations(long time) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();
			
			List<Reservation> list = session.createQuery(String.format(SQL.SELECT_OVER_RESERVATIONS, time, 1)).list();
			for(Reservation r : list) {
				Device d = (Device) session.createQuery(String.format(SQL.SELECT_DEVICE_INFO, r.getDevice_id())).uniqueResult();
				if(d.getState() == 2) { // device is reserved
					d.setState(0); // change device state to free
					session.update(d);
				}
				r.setState(0);
				session.update(r);
			}
			
			ts.commit();
			return 0;
		} catch(Exception e) {
			e.printStackTrace();
			ts.rollback();
			return 1;
		} finally {
			session.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public int handleNowReservations(long time) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();
			
			List<Reservation> list = session.createQuery(String.format(SQL.SELECT_NOW_RESERVATIONS, time, time)).list();
			for(Reservation r : list) {
				Device d = (Device) session.createQuery(String.format(SQL.SELECT_DEVICE_INFO, r.getDevice_id())).uniqueResult();
				if(d.getState() != 2) { // device is not reserved
					d.setState(2); // change device state to reserved
					session.update(d);
				}
			}
			
			ts.commit();
			return 0;
		} catch(Exception e) {
			e.printStackTrace();
			ts.rollback();
			return 1;
		} finally {
			session.close();
		}
	}
}

package android.testing.system.project;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import android.testing.system.SQL;
import android.testing.system.testcase.Testcase;
import android.testing.system.user.User;
import android.testing.system.util.Adb;

public class ProjectHandler {

	private SessionFactory factory;
	
	public ProjectHandler() {
		factory = Adb.getInstance().getFactory();
	}
	/**
	 * 0:成功
	 * 2:失败，其他原因*/
	public int newProject(String username, String name) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();		
			
			User u = (User) session.createQuery(String.format(SQL.SELECT_USER, username)).uniqueResult();
			Project p = new Project();
			p.setUser_id(u.getId());
			p.setName(name);
			p.setCreate_time(System.currentTimeMillis());
			p.setModify_time(System.currentTimeMillis());
			session.save(p);
			
			ts.commit();
			return 0;
		} catch(Exception e) {
			e.printStackTrace();
			ts.rollback();
			return 2;
		} finally {
			session.close();
		}
	}
	/**
	 * 0:成功
	 * 2:失败，其他原因*/
	public int renameProject(String username, String name, String new_name) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();		
			
			User u = (User) session.createQuery(String.format(SQL.SELECT_USER, username)).uniqueResult();
			Project p = (Project) session.createQuery(String.format(SQL.SELECT_PROJECT, u.getId(), name)).uniqueResult();
			p.setName(new_name);
			session.update(p);
			
			ts.commit();
			return 0;
		} catch(Exception e) {
			e.printStackTrace();
			ts.rollback();
			return 2;
		} finally {
			session.close();
		}
	}
	/**
	 * 0:成功
	 * 1:失败，其他原因*/
	public int deleteProject(String username, String name) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();		
			
			User u = (User) session.createQuery(String.format(SQL.SELECT_USER, username)).uniqueResult();
			Project p = (Project) session.createQuery(String.format(SQL.SELECT_PROJECT, u.getId(), name)).uniqueResult();
			List<Testcase> testcases = session.createQuery(String.format(SQL.SELECT_ALL_TESTCASES, p.getId())).list();
			for(Testcase s : testcases) {
				session.delete(s);
			}
			session.delete(p);
			
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

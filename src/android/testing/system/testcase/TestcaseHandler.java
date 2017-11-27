package android.testing.system.testcase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import android.testing.system.SQL;
import android.testing.system.project.Project;
import android.testing.system.user.User;
import android.testing.system.util.Adb;

import com.google.gson.Gson;

public class TestcaseHandler {

	private SessionFactory factory;
	
	public TestcaseHandler() {
		factory = Adb.getInstance().getFactory();
	}
	
	public String loadTestcases(String username) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();		
			
			List<Project> projects = session.createQuery(String.format(SQL.SELECT_ALL_PROJECTS, username)).list();
			if(projects == null) return null;
			List list = new ArrayList();
			for(Project p : projects) {
				Map map = new HashMap();
				map.put("title", p.getName());
				map.put("isFolder", true);
				map.put("key", p.getName());
				List<Testcase> testcases = session.createQuery(String.format(SQL.SELECT_ALL_TESTCASES, p.getId())).list();
				if(testcases != null) {
					List list2 = new ArrayList();
					map.put("children", list2);
					for(Testcase t : testcases) {
						Map map2 = new HashMap();
						map2.put("title", t.getName());
						map2.put("key", p.getName() + "/" + t.getName());
						list2.add(map2);
					}
				}
				list.add(map);
			}
			Gson gson = new Gson();
			
			ts.commit();
			return gson.toJson(list);
		} catch(Exception e) {
			e.printStackTrace();
			ts.rollback();
			return null;
		} finally {
			session.close();
		}
	}
	/**
	 * 0:成功
	 * 2:失败，其他原因*/
	public int newTestcase(String username, String project_name, File file) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();		
			
			User u = (User) session.createQuery(String.format(SQL.SELECT_USER, username)).uniqueResult();
			Project p = (Project) session.createQuery(String.format(SQL.SELECT_PROJECT, u.getId(), project_name)).uniqueResult();
//			File tcFile = new File(file);
			FileInputStream fis = new FileInputStream(file);
			byte[] bytes = new byte[fis.available()];
			fis.read(bytes);
			Blob content = Hibernate.createBlob(bytes);
			
			Testcase t = (Testcase) session.createQuery(String.format(SQL.SELECT_TESTCASE, String.valueOf(p.getId()), file.getName())).uniqueResult();
			if(t != null) {
				t.setTestcase(content);
				t.setModify_time(System.currentTimeMillis());
				session.update(t);
			} else {
				Testcase tc = new Testcase();
				tc.setProject_id(p.getId());
				tc.setName(file.getName());
				tc.setTestcase(content);
				tc.setCreate_time(System.currentTimeMillis());
				tc.setModify_time(System.currentTimeMillis());
				session.save(tc);
			}
			
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
	 * path:文件路径
	 * null:发生异常*/
	public String loadTestcase(String directory, String username, String project_name, String name) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();		
			
			User u = (User) session.createQuery(String.format(SQL.SELECT_USER, username)).uniqueResult();
			Project p = (Project) session.createQuery(String.format(SQL.SELECT_PROJECT, u.getId(), project_name)).uniqueResult();
			Testcase t = (Testcase) session.createQuery(String.format(SQL.SELECT_TESTCASE, p.getId(), name)).uniqueResult();
			Blob content = t.getTestcase();
			InputStream is = content.getBinaryStream();
			byte[] contentBytes = new byte[is.available()];
			is.read(contentBytes);
			// write to file
			String path = directory.concat(File.separator).concat(username).concat(File.separator)
				.concat(project_name).concat(File.separator);
			File file = new File(path);
			if(!file.exists()) file.mkdirs();
			FileOutputStream fos = new FileOutputStream(path.concat(name));
			fos.write(contentBytes);
			
			ts.commit();
			return path.concat(name);
		} catch(Exception e) {
			e.printStackTrace();
			ts.rollback();
			return null;
		} finally {
			session.close();
		}
	}
	/**
	 * 0:成功
	 * 2:失败，其他原因*/
	public int renameTestcase(String username, String project_name, String name, String new_name) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();		
			
			User u = (User) session.createQuery(String.format(SQL.SELECT_USER, username)).uniqueResult();
			Project p = (Project) session.createQuery(String.format(SQL.SELECT_PROJECT, u.getId(), project_name)).uniqueResult();
			Testcase t = (Testcase) session.createQuery(String.format(SQL.SELECT_TESTCASE, p.getId(), name)).uniqueResult();
			t.setName(new_name);
			session.update(t);
			
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
	public int deleteTestcase(String username, String project_name, String name) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();		
			
			User u = (User) session.createQuery(String.format(SQL.SELECT_USER, username)).uniqueResult();
			Project p = (Project) session.createQuery(String.format(SQL.SELECT_PROJECT, u.getId(), project_name)).uniqueResult();
			Testcase t = (Testcase) session.createQuery(String.format(SQL.SELECT_TESTCASE, p.getId(), name)).uniqueResult();
			session.delete(t);
			
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
	/**
	 * 0:成功
	 * 1:失败，其他原因*/
	public int moveTestcase(String username, String project_name, String new_project_name, String name) {
		Session session = null;
		Transaction ts = null;
		try {
			session = factory.openSession();
			ts = session.beginTransaction();		
			
			User u = (User) session.createQuery(String.format(SQL.SELECT_USER, username)).uniqueResult();
			Project p1 = (Project) session.createQuery(String.format(SQL.SELECT_PROJECT, u.getId(), project_name)).uniqueResult();
			Project p2 = (Project) session.createQuery(String.format(SQL.SELECT_PROJECT, u.getId(), new_project_name)).uniqueResult();
			Testcase t = (Testcase) session.createQuery(String.format(SQL.SELECT_TESTCASE, p1.getId(), name)).uniqueResult();
			t.setProject_id(p2.getId());
			session.update(t);
			
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

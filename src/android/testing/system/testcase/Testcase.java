package android.testing.system.testcase;

import java.sql.Blob;

public class Testcase {

	private int id;
	private int project_id;
	private String name;
	private Blob testcase;
	private long create_time;
	private long modify_time;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProject_id() {
		return project_id;
	}
	public void setProject_id(int projectId) {
		project_id = projectId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Blob getTestcase() {
		return testcase;
	}
	public void setTestcase(Blob testcase) {
		this.testcase = testcase;
	}
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long createTime) {
		create_time = createTime;
	}
	public long getModify_time() {
		return modify_time;
	}
	public void setModify_time(long modifyTime) {
		modify_time = modifyTime;
	}
}

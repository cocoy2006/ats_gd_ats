package android.testing.system.project;

public class Project {

	private int id;
	private int user_id;
	private String name;
	private long create_time;
	private long modify_time;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int userId) {
		user_id = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

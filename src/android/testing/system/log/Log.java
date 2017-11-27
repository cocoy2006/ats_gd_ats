package android.testing.system.log;

public class Log {
	private int id;
	private int user_id;
	private int device_id;
	private String operation;
	private long time;
	
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
	public int getDevice_id() {
		return device_id;
	}
	public void setDevice_id(int deviceId) {
		device_id = deviceId;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}

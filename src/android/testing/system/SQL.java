package android.testing.system;

public class SQL {

	// User
	/**parameter: username*/
	public static String SELECT_USER = "FROM User WHERE username='%s'";
//	public static String SELECT_USER_INFO = "FROM User WHERE id='%s'";
	
	// Project
	/**parameter: user_id, (project)name*/
	public static String SELECT_PROJECT = "FROM Project WHERE user_id='%s' AND name='%s'";
	/**parameter: username*/
	public static String SELECT_ALL_PROJECTS 
		= "FROM Project WHERE user_id=(SELECT id FROM User WHERE username='%s')";
	
	// Testcase
	/**parameter: project_id, (testcase)name*/
	public static String SELECT_TESTCASE = "FROM Testcase WHERE project_id='%s' AND name='%s'";
	/**parameter: project_id*/
	public static String SELECT_ALL_TESTCASES = "FROM Testcase WHERE project_id='%s'";
	
	// Device
	public static String SELECT_DEVICE = "FROM Device WHERE server='%s' AND serialNumber='%s'"; // imei
	public static String SELECT_DEVICE_INFO = "FROM Device WHERE id='%s'";
	public static String SELECT_ALL_DEVICES = "FROM Device";
	public static String SELECT_DEVICES_WITH_STATE = "FROM Device WHERE state='%s'";
	
	// Log
//	public static String SELECT_LOGS_WITH_USER_ID = "FROM Log WHERE user_id='%s' AND time BETWEEN '%s' AND '%s'";
	public static String SELECT_LOGS_WITH_USERNAME = "FROM Log WHERE user_id=(SELECT id FROM User WHERE username='%s') AND time BETWEEN '%s' AND '%s'";
//	public static String SELECT_LOGS_WITH_DEVICE_ID = "FROM Log WHERE device_id='%s' AND time BETWEEN '%s' AND '%s'";
	public static String SELECT_LOGS_WITH_DEVICE_INFO = "FROM Log WHERE device_id=(SELECT id FROM Device WHERE server='%s' AND serialNumber='%s') AND time BETWEEN '%s' AND '%s'";
	public static String SELECT_LAST_TIME_WITH_USER_ID = "SELECT MAX(time) FROM Log WHERE user_id='%s'";
	
	// Reservation
	public static String SELECT_RESERVATION = "FROM Reservation WHERE user_id='%s' AND device_id='%s' AND startTime='%s' AND endTime='%s'";
	public static String SELECT_RESERVATIONS_WITH_USER_ID = "FROM Reservation WHERE user_id='%s' ORDER BY endTime";
	public static String SELECT_USER_RESERVATIONS = "FROM Reservation WHERE user_id='%s' AND startTime>='%s' AND endTime<='%s'";
	public static String SELECT_RESERVATIONS_WITH_DEVICE_ID = "FROM Reservation WHERE device_id='%s' AND state='1' ORDER BY endTime";
	public static String SELECT_DEVICE_RESERVATIONS = "FROM Reservation WHERE device_id='%s' AND startTime>='%s' AND endTime<='%s'";
	public static String SELECT_DEVICE_NOW_RESERVATION = "FROM Reservation WHERE device_id='%s' AND state='%s' AND startTime<='%s' AND endTime>='%s'";
//	public static String SELECT_ALL_RESERVATIONS = "FROM Reservation WHERE startTime>='%s' AND endTime<='%s'";
	public static String SELECT_RESERVATION_OWNER = "FROM Reservation WHERE device_id='%s' AND startTime<='%s' AND endTime>='%s'";
	// Reservation polling timer
	public static String SELECT_OVER_RESERVATIONS = "FROM Reservation WHERE endTime<='%s' AND state='%s'"; // default: state is 1
	public static String SELECT_NOW_RESERVATIONS = "FROM Reservation WHERE startTime<='%s' AND endTime>='%s'";
}
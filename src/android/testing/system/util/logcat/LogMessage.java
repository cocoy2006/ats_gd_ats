package android.testing.system.util.logcat;

import java.io.Serializable;
import java.util.Date;

public class LogMessage implements Comparable<LogMessage>, Serializable {
	
	private static final long serialVersionUID = 1L;
	/**
	 * The actor associated to the action logged
	 */
	String actor;
	/**
	 * The log date
	 */
	Date date;
	/**
	 * The message
	 */
	String message;

	/**
	 * @param actor
	 * @param date
	 * @param message
	 */
	public LogMessage(String actor, Date date, String message) {
		super();
		this.actor = actor;
		this.date = date;
		this.message = message;
	}

	/**
	 * @return the actor
	 */
	public String getActor() {
		return actor;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(LogMessage o) {
		return this.getDate().compareTo(o.getDate());
	}
}

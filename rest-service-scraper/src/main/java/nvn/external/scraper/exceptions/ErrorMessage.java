package nvn.external.scraper.exceptions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ErrorMessage {
	private int statusCode;
	@JsonIgnore
	private Date timestamp;
	private String message;
	private String description;
	private String today;
	
	
	public ErrorMessage(int statusCode, Date timestamp, String message, String description) {
		super();
		this.statusCode = statusCode;
		setTimestamp(timestamp);
		this.message = message;
		this.description = description;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.today = DateTimeFormatter.ISO_LOCAL_DATE.format(LocalDate.now());
		
		this.timestamp = timestamp;
	}
	public String getToday() {
		return today;
	}
	public void setToday(String today) {
		this.today = today;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}

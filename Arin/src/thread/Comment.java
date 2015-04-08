package thread;

import java.util.Date;

public class Comment {
	
	private int id;
	private int user_id;
	private String username;
	private String email;
	private Date date_sent;
	private String comment;
	private Boolean is_answer;
	
	public Comment(int id, int user_id, String username, String email, Date date_sent, String comment, Boolean is_answer) {
		this.id = id;
		this.user_id = user_id;
		this.username = username;
		this.email = email;
		this.date_sent = date_sent;
		this.comment = comment;
		this.is_answer = is_answer;
	}
	
	public int getId() {
		return id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getUsername() {
		return this.username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Date getDate_sent() {
		return date_sent;
	}
	public void setDate_sent(Date date_sent) {
		this.date_sent = date_sent;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Boolean is_answer() {
		return is_answer;
	}
	public void setIs_answer(Boolean is_answer) {
		this.is_answer = is_answer;
	}
}
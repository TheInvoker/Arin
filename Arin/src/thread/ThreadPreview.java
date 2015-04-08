package thread;

import java.util.Date;

public class ThreadPreview {

	private int id;
	private int user_id;
	private String title;
	private String recent_post;
	private Date most_recent_post_date;
	private Boolean has_answer;
	private String image_link;
	
	public ThreadPreview(int id, int user_id, String title, String recent_post, Date most_recent_post_date, Boolean has_answer, String image_link) {
		this.id = id;
		this.user_id = user_id;
		this.title = title;
		this.recent_post = recent_post;
		this.most_recent_post_date = most_recent_post_date;
		this.has_answer = has_answer;
		this.image_link = image_link;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRecentPost() {
		return recent_post;
	}
	public void setRecentPost(String recent_post) {
		this.recent_post = recent_post;
	}
	public Boolean getHas_answer() {
		return has_answer;
	}
	public void setHas_answer(Boolean has_answer) {
		this.has_answer = has_answer;
	}
	public Date getMost_recent_post_date() {
		return most_recent_post_date;
	}
	public void setMost_recent_post_date(Date most_recent_post_date) {
		this.most_recent_post_date = most_recent_post_date;
	}
	public String getImage_link() {
		return image_link;
	}
	public void setImage_link(String image_link) {
		this.image_link = image_link;
	}
}

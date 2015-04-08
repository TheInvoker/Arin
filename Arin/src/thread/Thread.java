package thread;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;
import arin.ArinContext;

public class Thread {

	private int id;
	private int user_id;
	private String title;
	private List<Bitmap> images = new ArrayList<Bitmap>();
	private List<Comment> comments = new ArrayList<Comment>();
	
	public Thread(int id, int user_id, String title) {
		this.id = id;
		this.user_id = user_id;
		this.title = title;
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
	
	public List<Comment> getComments() {
		return comments;
	}
	
	public Comment addComment(int id, String comment, String username, String email) {
		Comment newComment = new Comment(
				id,
				ArinContext.getUser().getId(),
				username,
				email,
				new Date(),
				comment,
				false
			);
		comments.add(newComment);
		return newComment;
	}
	
	public void addComment(Comment comment) {
		comments.add(comment);
	}
	
	public Comment getCommentById(int id) {
		for(Comment comment : comments) {
			if (comment.getId() == id) {
				return comment;
			}
		}
		return null;
	}
	
	public List<Bitmap> getImages() {
		return images;
	}
	
	public void addImage(Bitmap image) {
		images.add(image);
	}
	
	public Boolean hasMarkedAnswer() {
		for(Comment comment : comments) {
			if (comment.is_answer()) {
				return true;
			}
		}
		return false;
	}
}

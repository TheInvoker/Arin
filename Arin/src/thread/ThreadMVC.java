package thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import widget.Popup;
import common.MyDate;
import common.Views;
import activity.ThreadScreen;
import com.arin.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import arin.ArinContext;
import async.image.Async_dl_image;
import async.image.Async_up_thread_image;
import async.thread.Async_add_comment;
import async.thread.Async_get_thread;
import async.thread.Async_set_answer;

public class ThreadMVC {

	private ThreadScreen context;
	
	// thread info
	private int thread_id;
	private String thread_title;
	private Thread thread;
	
	// keep track of widgets
	private ImageView mainIV;
	private LinearLayout layout;
	private ImageButton prev;
	private ImageButton next;
	private EditText commentET;
	private ScrollView scroll;
	
	// keep track of the threads
	private List<LinearLayout> nodes = new ArrayList<LinearLayout>();
	// keep track of image information
	private int imageIndex = 0;
	// set to false while no image has loaded
	private Boolean showingImages = false;
	
	public final static String THREAD_ID = "thread_id";
	public final static String THREAD_TITLE = "thread_title";
	
	public ThreadMVC(ThreadScreen context, int thread_id, String thread_title) {
		this.context = context;
		this.thread_id = thread_id;
		this.thread_title = thread_title;
		
		this.mainIV = (ImageView) context.findViewById(R.id.imageView);
		this.layout = (LinearLayout) context.findViewById(R.id.commentcontainer);
		this.prev = (ImageButton) context.findViewById(R.id.prev);
		this.next = (ImageButton) context.findViewById(R.id.next);
		this.commentET = (EditText) context.findViewById(R.id.comment);
		this.scroll = (ScrollView) context.findViewById(R.id.threadscroll);
		
		scroll.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
	        @Override
	        public void onGlobalLayout() {
	        	scroll.fullScroll(View.FOCUS_DOWN);
	        }
	    });
		
		setUpControls();
	}
	
	public int getThreadID() {
		return thread_id;
	}
	
	public String getThreadTitle() {
		return thread_title;
	}
	
	public int getOPuserId() {
		return thread.getUser_id();
	}
	
	public void downloadData() {
		new Async_get_thread(context, thread_id);
	}
	
	public void displayData(String data) {
		clear();
		Thread thread = generateData(data);
		this.thread = thread;
		loadScreen();
	}
	
	private Thread generateData(String data) {
		Thread thread = null;

		try {
			JSONObject mainobj = new JSONObject(data);
			thread = new Thread(thread_id, mainobj.getInt("op_id"), thread_title);
			
			JSONArray array = mainobj.getJSONArray("comments");
			for(int i=0; i<array.length(); i+=1) {
				JSONObject obj = array.getJSONObject(i);

				Comment comment = new Comment(
					obj.getInt("id"), 
					obj.getInt("user_id"),
					obj.getString("username"),
					obj.getString("email"),
					MyDate.getDateFromSQLDate(obj.getString("date_sent")),
					obj.getString("comment"),
					obj.getInt("is_answer")==1
				);

				thread.addComment(comment);
			}
			
			JSONArray images = mainobj.getJSONArray("images");
			for(int i=0; i<images.length(); i+=1) {
				String link = images.getString(i);
				
				new Async_dl_image(context, null, link);
			}
		} catch (JSONException e) {
			Popup.ShowErrorMessage(context, R.string.unexpected_error, true);
		}
		
		return thread;
	}
	
	private void loadScreen() {
		for(Comment comment : thread.getComments()) {
			addComment(comment);
		}
	}

	private void addComment(final Comment comment) {
		TextView nameview = getTextView(String.format(Locale.getDefault(), "%s: %s", comment.getUsername(), comment.getComment()));
		TextView dateview = getDateView(MyDate.dateToString(comment.getDate_sent()));
		
		LinearLayout content = Views.getContainer(context, true, 10);
		content.setBackgroundResource(comment.is_answer() ? R.drawable.bg_answer : 0);
		nodes.add(content);
		
		content.addView(nameview);
		content.addView(dateview);
		
		if (comment.getUser_id() == ArinContext.getUser().getId()) {
			content.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View arg0) {
					handleSetAsAnswer(comment);
					return false;
				}
			});
		}
		
		layout.addView(content);
	}

	private TextView getTextView(String text) {
		TextView nametv = Views.getCommentText(context, text);
		nametv.setPadding(0, 0, 0, 5);
		return nametv;
	}
	
	private TextView getDateView(String text) {
		TextView datetv = Views.getDateText(context, text);
		return datetv;
	}
	
	
	
	
	private void handleSetAsAnswer(Comment comment) {
		new Async_set_answer(context, comment, getThreadID());
	}	
	
	public void handleSetAsAnswerCallback(Comment comment) {
		comment.setIs_answer(!comment.is_answer());
		List<Comment> comments = thread.getComments();
		
		for(int i=0; i<comments.size(); i+=1) {
			Comment c = comments.get(i);
			
			if (comment.is_answer()) {
				
				Boolean isMe = c.getId() == comment.getId();
				
				// update model
				c.setIs_answer(isMe);
				// update view
				nodes.get(i).setBackgroundResource(isMe ? R.drawable.bg_answer : 0);
			} else {
				// update model
				c.setIs_answer(false);
				// update view
				nodes.get(i).setBackgroundResource(0);
			}
		}
	}	
	
	public void addcomment() {
		String comment = commentET.getText().toString();
		if (!comment.trim().equals("")) {
			new Async_add_comment(context, getThreadID(), getThreadTitle(), comment);
		}
	}
	
	public void addcommentCallback(int comment_id) {
		String comment = commentET.getText().toString();
		
		// update model
		Comment newComment = thread.addComment(comment_id, comment, ArinContext.getUser().getName(), ArinContext.getUser().getEmail());
		
		// update view
		addComment(newComment);
		commentET.setText("");
		
		// close keyboard
		InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(commentET.getWindowToken(), 0);
	}
	
	
	
	
	
	
	
	
	private void setUpControls() {
		prev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int len = thread.getImages().size();
				if (len > 0) {
					if (imageIndex > 0) {
						imageIndex -= 1;
					} else {
						imageIndex = 0;
					}
					mainIV.setImageBitmap(thread.getImages().get(imageIndex));
				}
			}
		});
		next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int len = thread.getImages().size();
				if (len > 0) {
					if (imageIndex < len-1) {
						imageIndex += 1;
					} else {
						imageIndex = len-1;
					}
					mainIV.setImageBitmap(thread.getImages().get(imageIndex));
				}
			}
		});
	}
	
	
	
	public void uploadImage(Bitmap bitmap) {
		new Async_up_thread_image(context, getThreadID(), getThreadTitle(), bitmap);
	}
	
	public void uploadImageCallback(Bitmap bitmap, int id) {
		thread.addImage(bitmap);
		if (!showingImages) {
			mainIV.setImageBitmap(bitmap);
			showingImages = true;
		}
	}
	
	

	
	

	
	
	public void clear() {
		if (thread != null) {
	    	for(Bitmap bitmap : thread.getImages()) {
	    		bitmap.recycle();
	    	}
		}

    	nodes.clear();
    	thread = null;
    	imageIndex = 0;
    	showingImages = false;
    	mainIV.setImageDrawable(null);
    	commentET.setText("");
    	layout.removeAllViews();
	}
}

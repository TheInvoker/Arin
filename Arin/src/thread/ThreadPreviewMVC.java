package thread;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import widget.Popup;
import common.Common;
import common.MyDate;
import common.Transition;
import common.Views;
import activity.AllThreadsScreen;
import activity.ThreadScreen;
import com.arin.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import async.image.Async_dl_image;
import async.thread.Async_get_threads;

public class ThreadPreviewMVC {
	
	private AllThreadsScreen context;
	
	// keep track of the layout
	private LinearLayout layout;
	// keeps track of downloaded thread preview images, key is the thread id
	private List<Bitmap> images = new ArrayList<Bitmap>();
	// keeps track of the threads
	private List<ThreadPreview> allthreads = new ArrayList<ThreadPreview>();
	// keeps track of the page
	private int page = 0;
	// keeps track of the search text
	private String text = "";
	// keeps track of the state of 'is mine' or 'all'
	private Boolean mine = false;
	// keeps track of the search state
	private Boolean search = false;
	// page size
	private final int PAGE_LENGTH = 10;
	// size of the image
	private final int NODE_LENGTH = 150;
	
	
	public ThreadPreviewMVC(AllThreadsScreen context) {
		this.context = context;
		this.layout = (LinearLayout) context.findViewById(R.id.threadcontainer);
	}
	
	public void downloadData(Boolean mine, int pagenum, Boolean forSearch, Boolean fromNavButton) {
		new Async_get_threads(context, mine, pagenum, text, forSearch, fromNavButton, PAGE_LENGTH);
	}
	
	public void displayData(String data, boolean mine, int pagenum, String text, Boolean forSearch, Boolean fromNavButton) {
		List<ThreadPreview> threads = generateData(data);
		
		if ((fromNavButton && threads.size() > 0) || !fromNavButton || forSearch) {
			clear();
			
			this.mine = mine;
			this.page = pagenum;
			this.search = forSearch;
			this.allthreads.addAll(threads);
			
			loadScreen();
			setPageNum(pagenum);
	    	setTitle(mine, forSearch);
		}
	}
	
	
	private void setPageNum(int newpage) {
		TextView pagenumTV = (TextView) context.findViewById(R.id.page);
		pagenumTV.setText(Integer.toString(newpage+1, 10));
	}
   
	private void setTitle(Boolean mine, Boolean forSearch) {
		if (forSearch) {
			context.getActionBar().setTitle(R.string.title_activity_my_search);
		} else {
			context.getActionBar().setTitle(mine ? R.string.title_activity_my_threads : R.string.title_activity_all_threads);
		}
	}
	
	
	private List<ThreadPreview> generateData(String data) {
		List<ThreadPreview> allthreads = new ArrayList<ThreadPreview>();
		try {
			JSONArray array = new JSONArray(data);

			for(int i=0; i<array.length(); i+=1) {
				JSONObject obj = array.getJSONObject(i);
				
				ThreadPreview thread = new ThreadPreview(
					obj.getInt("id"), 
					obj.getInt("user_id"),
					obj.getString("title"),
					obj.getString("comment"),
					MyDate.getDateFromSQLDate(obj.getString("recent_date")),
					obj.getInt("has_answer")==1,
					obj.getString("image_link")
				);

				allthreads.add(thread);
			}
		} catch (JSONException e) {
			Popup.ShowErrorMessage(context, R.string.unexpected_error, true);
		}
		return allthreads;
	}
	
	private void loadScreen() {
		for(ThreadPreview thread : allthreads) {
			displayThread(thread);
		}
	}
	
	private void displayThread(final ThreadPreview thread) {
		Boolean isFirst = layout.getChildCount() == 0;
		
		LinearLayout content = Views.getContainer(context, false, 0);
		content.setBackgroundResource(R.drawable.thread_button_selector);
		
		ImageView imageview = getThreadImage(thread);
		LinearLayout maincontent = getContent(thread);
		
		content.addView(imageview);
		content.addView(maincontent);
		
		if (thread.getHas_answer()) {
			ImageView answerImage = getAnswerIcon();
			content.addView(answerImage);
		}
		
		content.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent myIntent = new Intent(context, ThreadScreen.class);
				myIntent.putExtra(ThreadMVC.THREAD_ID, thread.getId());
				myIntent.putExtra(ThreadMVC.THREAD_TITLE, thread.getTitle());
				myIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				context.startActivity(myIntent);
				
				Transition.TransitionForward(context);
			}
		});
		
		// add divisor
		if (!isFirst) {
			layout.addView(Views.getHorizontalLine(context));
		}
		
		layout.addView(content);
	}
	
	private ImageView getThreadImage(ThreadPreview thread) {
		ImageView imageview = new ImageView(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(NODE_LENGTH, LinearLayout.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		imageview.setLayoutParams(params);
		imageview.setMinimumHeight(NODE_LENGTH);
		imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
		
		if (!thread.getImage_link().equals("")) {
			new Async_dl_image(context, imageview, thread.getImage_link());
		}
		
		return imageview;
	}
	
	private ImageView getAnswerIcon() {
		ImageView imageview = new ImageView(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
		params.gravity = Gravity.CENTER;
		params.setMargins(0, 5, 0, 5);
		imageview.setLayoutParams(params);
		imageview.setPadding(5, 5, 5, 5);
		imageview.setImageResource(R.drawable.ic_action_good);
		imageview.setBackgroundColor(Color.parseColor("#00520f"));
		imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
		return imageview;
	}
	
	private LinearLayout getContent(ThreadPreview thread) {
		LinearLayout content = Views.getInnerContainer(context, true, 10);
		
		TextView title = Views.getPreviewText(context, thread.getTitle(), 1);
		content.addView(title);
		
		TextView recentMessage = Views.getCommentPreviewText(context, thread.getRecentPost(), 2);
		content.addView(recentMessage);

		TextView recent_dateText = Views.getDateText(context, MyDate.dateToString(thread.getMost_recent_post_date()));
		content.addView(recent_dateText);
		
		return content;
	}
	

	
	
	
	

	public void refresh() {
		downloadData(isMine(), getPage(), isSearch(), false);
	}
	
    public void loadPrev() {
    	downloadData(isMine(), Math.max(0, getPage()-1), isSearch(), true);
    }
    
    public void loadNext() {
    	downloadData(isMine(), getPage()+1, isSearch(), true);
    }
	
	
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = Common.fixStringForSearch(text);
	}
	
	public Boolean isSearch() {
		return search;
	}
	
	public Boolean isMine() {
		return mine;
	}

	public int getPage() {
		return page;
	}
	
	
	public void clear() {
    	for(Bitmap bitmap : images) {
    		bitmap.recycle();
    	}
    	
    	images.clear();
    	allthreads.clear();
    	layout.removeAllViews();
	}
	
	
	public void storeImage(Bitmap bitmap) {
		images.add(bitmap);
	}
}

package activity;

import image.ImageDownloader;
import thread.ThreadPreviewMVC;
import android.app.Activity;
import com.arin.R;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import arin.ArinContext;
import async.thread.Async_create_thread;

public class AllThreadsScreen extends Activity implements ImageDownloader {

	private Menu menu = null;
	private ThreadPreviewMVC MVC = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_threads_screen);
		
		
		// activates the action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        setUpScreen();
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.all_threads_screen, menu);
		
		this.menu = menu;
		
	 	if (ArinContext.isOfflineMode() || ArinContext.getUser().isBanned()) {
	 		menu.removeItem(R.id.action_add);
	 	}
	 	if (ArinContext.isOfflineMode()) {
	 		menu.removeItem(R.id.action_mine);
	 	}
		
	 	handleSearchFilter(menu);
	 	
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
	        case android.R.id.home:
	        	onBackPressed();
	            return true;
	        case R.id.action_all:
	        	showAllPosts();
	        	return true;
	        case R.id.action_mine:
	        	showMyPosts();
	        	return true;
	        case R.id.action_add:
	        	addPost();
	        	return true;
	        case R.id.action_refresh:
	        	refresh();
	        	return true;
		}
		return super.onOptionsItemSelected(item);
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        MVC.clear();
    }
	


	
    @Override
    public boolean onSearchRequested() {
    	MenuItem searchMenuItem = menu.findItem(R.id.action_search);
    	searchMenuItem.expandActionView();
        return false;  // don't go ahead and show the search box
    }
    
	private void handleSearchFilter(Menu menu) {
	    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	    searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextChange(String text) {
				return false;
			}

			@Override
			public boolean onQueryTextSubmit(String text) {
				MVC.setText(text);
				MVC.downloadData(MVC.isMine(), 0, true, false);
				return false;
			}
	    });
    }
 

	
    
	
	private void setUpScreen() {
    	MVC = new ThreadPreviewMVC(this);
    	MVC.downloadData(false, 0, false, false);
	}
	
	public void refresh() {
		MVC.refresh();
	}
    
	private void showAllPosts() {
		MVC.downloadData(false, 0, false, false);
	}
	
	private void showMyPosts() {
		MVC.downloadData(true, 0, false, false);
	}

    public void loadPrev(View view) {
    	MVC.loadPrev();
    }
    
    public void loadNext(View view) {
    	MVC.loadNext();
    }

    public void threadCallbackSuccess(String result, Boolean mine, int newpage, String text, Boolean forSearch, Boolean fromNavButton) {
    	MVC.displayData(result, mine, newpage, text, forSearch, fromNavButton);
    }

	   
   


	
	

	


	
	private void addPost() {
		new Async_create_thread(this);
	}
	
	public void addPostCallback() {
		showAllPosts();
	}
	
	
	@Override
	public void imageDownloadReturn(ImageView imageview, Bitmap bitmap) {
		if (bitmap != null) {
			MVC.storeImage(bitmap);
			imageview.setImageBitmap(bitmap);
		}
	}

	@Override
	public Activity getContext() {
		return this;
	}
}

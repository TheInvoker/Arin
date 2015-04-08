package activity;

import http.Network;
import image.ImageDownloader;
import image.MyImage;
import common.MyIntent;
import thread.Comment;
import thread.ThreadMVC;
import widget.Popup;
import android.app.Activity;
import com.arin.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import arin.ArinContext;

public class ThreadScreen extends Activity implements ImageDownloader {

	private Menu menu;
	private ThreadMVC MVC;
	private boolean isSetUp = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thread_screen);
		
		
		// activates the action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
      
        int thread_id = getIntent().getExtras().getInt(ThreadMVC.THREAD_ID);
        String thread_title = getIntent().getExtras().getString(ThreadMVC.THREAD_TITLE);
        setUpScreen(thread_id, thread_title);
        
        getActionBar().setTitle(thread_title);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.thread_screen, menu);
	 	this.menu = menu;
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
	        case R.id.action_upload_image:
	        	uploadImage();
	        	return true;
	        case R.id.action_accept:
	        	actionAccept();
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

    private void setUpScreen(int thread_id, String thread_title) {
		MVC = new ThreadMVC(this, thread_id, thread_title);
		MVC.downloadData();
    }
    
    private void refresh() {
		MVC.downloadData();
    }
    
	public void getThreadCallback(String data) {
		MVC.displayData(data);
		
		if (!isSetUp) {
			isSetUp = true;
		 	if (ArinContext.isOfflineMode() || ArinContext.getUser().isBanned()) {
		 		menu.removeItem(R.id.action_accept);
		 	}
		 	if (ArinContext.isOfflineMode() || ArinContext.getUser().isBanned() || MVC.getOPuserId() != ArinContext.getUser().getId()) {
		 		menu.removeItem(R.id.action_upload_image);
		 	}
		}
	}
	

	

	
	

	
	private void uploadImage() {
		if (Network.isNetworkAvailable(this)) {
			Intent pickIntent = new Intent();
			pickIntent.setType("image/*");
			pickIntent.setAction(Intent.ACTION_GET_CONTENT);
			Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			Intent chooserIntent = Intent.createChooser(pickIntent, getString(R.string.select_or_take));
			chooserIntent.putExtra
			(
			  Intent.EXTRA_INITIAL_INTENTS, 
			  new Intent[] { takePhotoIntent }
			);
			startActivityForResult(chooserIntent, MyIntent.SELECT_PICTURE);
		} else {
			Popup.ShowErrorMessage(this, R.string.no_internet, false);
		}
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
        	if (requestCode == MyIntent.SELECT_PICTURE) {
        		Bitmap bitmap = MyImage.GetBitmapFromPath(this, data.getData(), 240, 180);
                MVC.uploadImage(bitmap);
        	}
        }
    }
	
	
	
	private void actionAccept() {
		MVC.addcomment();
	}
	
	public void addcommentCallback(int comment_id) {
		MVC.addcommentCallback(comment_id);
	}

	public void handleSetAsAnswerCallback(Comment comment) {
		MVC.handleSetAsAnswerCallback(comment);
	}
	
	public void imageUploadReturn(Bitmap bitmap, int id) {
		MVC.uploadImageCallback(bitmap, id);
	}

	@Override
	public void imageDownloadReturn(ImageView imageview, Bitmap bitmap) {
		MVC.uploadImageCallback(bitmap, 0);
	}
	
	@Override
	public Activity getContext() {
		return this;
	}
}

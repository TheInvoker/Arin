package activity;

import http.Network;
import image.FishImage;
import java.io.File;
import java.util.List;
import java.util.Locale;
import user.Actor;
import widget.ActivitySwipeDetector;
import widget.Popup;
import com.ortiz.touch.TouchImageView;
import common.MyDate;
import fish.Fish;
import fish.FishGUI;
import fish.CategoryMVC;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import com.arin.R;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import arin.ArinContext;
import async.image.Async_delete_image;
import async.image.Async_update_image_state;
import async.user.Async_ban_user;

/*
 * This activity handles displaying images on the screen like a slide-show.
 * The user can swipe to change the image and biologists can manage settings.
 */

public class GalleryScreen extends Activity {

	private int len;                        // size of all images
	private int index;                      // index of the current image
	private Fish fish;                      // fish object
	private FishImage image;                // current image object
	private List<FishImage> imagestoshow;   // keep track of the images
	
	// views to change when image changes
	private TouchImageView imageview;       
	private TextView comment;
	private TextView date;
	private TextView page;
	private LinearLayout description;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery_screen);
		
		// activates the action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        // set up the screen
		setUpScreen();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gallery_screen, menu);
		
		// if not biologist, remove the delete button
		if (!ArinContext.getUser().canManageImage()) {
			menu.removeItem(R.id.action_edit);
			menu.removeItem(R.id.action_delete);
		}
		
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
	        case R.id.action_edit:
	        	handleEdit();
	        	return true;
	        case R.id.action_share:
	        	handleShareClick();
	        	return true;
	        case R.id.action_delete:   
	        	deleteClick();
	            return true;
		}

		return super.onOptionsItemSelected(item);
	}

	
	@SuppressLint("ClickableViewAccessibility")
	private void setUpScreen() {
		
		// set up global variables
		imageview = (TouchImageView)findViewById(R.id.imageView);
		comment =  (TextView) findViewById(R.id.comment);
		date =  (TextView) findViewById(R.id.date);
		page =  (TextView) findViewById(R.id.page);
		description =  (LinearLayout) findViewById(R.id.description);
		
		fish = CategoryMVC.selectedFish;
    	imagestoshow = fish.getImageList();
    	index = 0;
		len = imagestoshow.size();
		
		// change the name to the fish category name
		getActionBar().setTitle(fish.getName());
		
		if (len > 0) {
			// activate swipe controls
			ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(this);
			imageview.setOnTouchListener(activitySwipeDetector);
			
			// show the current image
			setImage();
		}
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        cleanup();
    }
    
    
    /*
     * clean up images when you exit
     */
	private void cleanup() {
		FishGUI.clearAllButMainImage(fish);
	}
    
    /*
     * sets the image
     */
	private void setImage() {
		image = imagestoshow.get(index);
		image.loadImage(imageview);
		comment.setText(image.getComment());
		date.setText(MyDate.dateToString(image.getDate_added()));
		page.setText(String.format(Locale.getDefault(), "%d/%d", index+1, len));
		setImageActive();
	}
	
	private void setImageActive() {
		description.setBackgroundColor(image.getApproved() ? 0 : Color.parseColor("#40ff0000"));
	}
	
	
	
	
	/*
	 *  handlers for switching images
	 */

	/*
	 * swiped left
	 */
    public void switchLeft() {
    	if (!imageview.isZoomed()) {
			if (index > 0) {
				index -= 1;
				setImage();
			}
    	}
    }
    
    /*
     *  swiped right
     */
    public void switchRight() {
    	if (!imageview.isZoomed()) {
			if (index < len-1) {
				index += 1;
				setImage();
			}
    	}
    }
    

	
	
	
	
	


	
	
	/*
	 *  click handlers
	 */
	
    @SuppressLint("InflateParams")
	private void handleEdit() {
		LayoutInflater inflater = getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_gallery, null);
		
		final AlertDialog d = new AlertDialog.Builder(this)
        .setView(dialoglayout)
        .setTitle(R.string.edit)
        .setNegativeButton(R.string.cancel, null)
        .setPositiveButton(R.string.ban, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Actor user = ArinContext.getUser();
				if (user.isBanned()) {
					Toast.makeText(GalleryScreen.this, GalleryScreen.this.getString(R.string.your_banned), Toast.LENGTH_SHORT).show();
				} else {
					new Async_ban_user(GalleryScreen.this, image);
				}
			}})
        .create();

	    CheckBox mainCB = (CheckBox)dialoglayout.findViewById(R.id.main);
	    CheckBox usedCB = (CheckBox)dialoglayout.findViewById(R.id.used);
	    mainCB.setChecked(image.getIsMain());
	    usedCB.setChecked(image.getApproved());
		
	    mainCB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Network.isNetworkAvailable(GalleryScreen.this)) {
					new Async_update_image_state(GalleryScreen.this, fish, image, !image.getIsMain(), image.getApproved(), false);
					d.dismiss();
				} else {
					Popup.ShowErrorMessage(GalleryScreen.this, R.string.no_internet, false);
				}
			}
		});
	    
	    usedCB.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Network.isNetworkAvailable(GalleryScreen.this)) {
					new Async_update_image_state(GalleryScreen.this, fish, image, false, !image.getApproved(), true);
					d.dismiss();
				} else {
					Popup.ShowErrorMessage(GalleryScreen.this, R.string.no_internet, false);
				}
			}
		});
		
		d.show();
    }
	
	private void handleShareClick() {
		File file = image.getFishFile();
		
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/png");
		share.putExtra(Intent.EXTRA_STREAM,  Uri.fromFile(file));
		startActivity(Intent.createChooser(share, getString(R.string.share)));
	}
	
	private void deleteClick() {
		if (Network.isNetworkAvailable(this)) {
			new Async_delete_image(this, fish, image);
		} else {
			Popup.ShowErrorMessage(this, R.string.no_internet, false);
		}
	}
	
	public void deleteCallback(FishImage image) {
		imagestoshow.remove(image);
		len = imagestoshow.size(); 
		if (len == 0) {
			finish();
		} else {
			while (index >= len) {
				index -= 1;
			}
			setImage();
		}
	}
	
	public void commentClick(View view) {
		Popup.ShowString(GalleryScreen.this, R.string.comment, image.getComment(), false);
	}
	

	
	
	
	
	
	
	
	/*
	 * successfully changed the 'main' status of the image
	 */
	public void mainClickedWorked() {
		if (image.getApproved()) {
			Boolean oldState = image.getIsMain();
			if (!oldState) {
				for(FishImage image : imagestoshow) {
					image.setIsMain(false);
				}
			}
			image.setIsMain(!oldState);
		}
		
		NavigationScreen.reloadPic = true;
	}

	/*
	 * successfully changed the 'used' status of the image
	 */
	public void usedClickWorked() {
		Boolean oldState = image.getApproved();
		image.setApproved(!oldState);
		if (oldState) {
			image.setIsMain(false);
		}
		
		setImageActive();
		NavigationScreen.reloadPic = true;
	}
}

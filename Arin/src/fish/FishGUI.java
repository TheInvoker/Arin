package fish;

import image.FishImage;
import android.app.Activity;
import com.arin.R;
import android.content.Context;
import android.view.Gravity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/*
 * handles creating the node GUI
 */

public class FishGUI {
	
	private static final int MIN_NODE_WIDTH = 250;
	
	public static LinearLayout createNodeWrapper(Activity context, Fish fish) {
		ImageView imageview = createImageView(context, fish);
		ImageButton info = createButton(context, R.drawable.ic_action_info);
		TextView nametv = createNameView(context, fish.getName());
		ImageButton settings = createButton(context, R.drawable.ic_action_settings);

		LinearLayout namewrapper = createNameWrapper(context);
		
		namewrapper.addView(info);
		namewrapper.addView(nametv);
		namewrapper.addView(settings);

		LinearLayout contentwrapper = createContentWrapper(context, fish);
		contentwrapper.addView(imageview);
		contentwrapper.addView(namewrapper);
		
		LinearLayout fullwrapper = createFullWrapper(context, fish);
		fullwrapper.addView(contentwrapper);

		return fullwrapper;
	}
	
	private static ImageView createImageView(Context context, Fish fish) {
		ImageView imageview = new ImageView(context);
		LinearLayout.LayoutParams params = fish.getImageParams();
		params.gravity = Gravity.CENTER;
		params.setMargins(20, 20, 20, 20);
		imageview.setLayoutParams(params);
		imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
		return imageview;
	}

	private static TextView createNameView(Context context, String name) {
		TextView nametv = new TextView(context);
		nametv.setText(name);
		nametv.setTextAppearance(context, R.style.node);
		nametv.setPadding(10, 0, 10, 0);
		nametv.setGravity(Gravity.CENTER);
		nametv.setSingleLine(true);
		LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		textparams.gravity = Gravity.CENTER;
		nametv.setLayoutParams(textparams);
		return nametv;
	}
	
	private static ImageButton createButton(Context context, int resId) {
		ImageButton imageButton = new ImageButton(context);
		imageButton.setPadding(0, 0, 0, 0);
		LinearLayout.LayoutParams imageparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		imageparams.gravity = Gravity.CENTER;
		imageButton.setLayoutParams(imageparams);
		imageButton.setImageResource(resId);
		imageButton.setBackgroundResource(R.drawable.mini_button_selector);
		return imageButton;
	}
	
	private static LinearLayout createNameWrapper(Context context) {
		LinearLayout wrapper = new LinearLayout(context);
		wrapper.setOrientation(LinearLayout.HORIZONTAL);
		wrapper.setPadding(0, 0, 0, 0);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		wrapper.setLayoutParams(params);
		return wrapper;
	}
	
	private static LinearLayout createContentWrapper(Context context, Fish fish) {
		LinearLayout wrapper = new LinearLayout(context);
		wrapper.setOrientation(LinearLayout.VERTICAL);
		wrapper.setPadding(3, 0, 3, 3);
		LinearLayout.LayoutParams params = fish.getParams();
		wrapper.setLayoutParams(params);
		wrapper.setMinimumWidth(MIN_NODE_WIDTH);
		return wrapper;
	}
	
	private static LinearLayout createFullWrapper(Context context, Fish fish) {
		LinearLayout wrapper = new LinearLayout(context);
		wrapper.setBackgroundResource(R.drawable.bg_fish);
		wrapper.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params = fish.getParams();
		params.setMargins(10, 10, 10, 10);
		wrapper.setLayoutParams(params);
		return wrapper;
	}
	

	
	
	
	
	public static LinearLayout getContentWrapper(LinearLayout nodeGUI) {
		LinearLayout content_wrapper = (LinearLayout) nodeGUI.getChildAt(0);
		return content_wrapper;
	}
	
	public static ImageView getImageView(LinearLayout nodeGUI) {
		LinearLayout content_wrapper = getContentWrapper(nodeGUI);
		ImageView imageview = (ImageView) content_wrapper.getChildAt(0);
		return imageview;
	}
	
	public static ImageButton getInfoView(LinearLayout nodeGUI) {
		LinearLayout content_wrapper = getContentWrapper(nodeGUI);
		LinearLayout name_wrapper = (LinearLayout) content_wrapper.getChildAt(1);
		ImageButton info = (ImageButton) name_wrapper.getChildAt(0);
		return info;
	}
	
	public static TextView getTextView(LinearLayout nodeGUI) {
		LinearLayout content_wrapper = getContentWrapper(nodeGUI);
		LinearLayout name_wrapper = (LinearLayout) content_wrapper.getChildAt(1);
		TextView textview = (TextView) name_wrapper.getChildAt(1);
		return textview;
	}
	
	public static ImageButton getSettingView(LinearLayout nodeGUI) {
		LinearLayout content_wrapper = getContentWrapper(nodeGUI);
		LinearLayout name_wrapper = (LinearLayout) content_wrapper.getChildAt(1);
		ImageButton setting = (ImageButton) name_wrapper.getChildAt(2);
		return setting;
	}
	
	

	public static void reloadName(Fish fish, LinearLayout nodeGUI) {
		TextView nametv = getTextView(nodeGUI);
		nametv.setText(fish.getName());
	}
	
	
	
	
	
	
	
	
	public static void clearMainImage(Fish fish, LinearLayout nodeGUI) {
		FishImage image = fish.getImageToShow();
		if (image != null) {
			image.freeBitmapMemory();
		}
		clearImage(nodeGUI);
	}
	public static void clearAllImages(Fish fish, LinearLayout nodeGUI) {
		for (FishImage image : fish.getImageList()) {
			image.freeBitmapMemory();
		}
		clearImage(nodeGUI);
	}
	public static void clearAllButMainImage(Fish fish) {
		for (FishImage image : fish.getImageList()) {
			if (!image.getIsMain()) {
				image.freeBitmapMemory();
			}
		}
	}
	public static void loadMainImage(Fish fish, LinearLayout nodeGUI) {
		FishImage image = fish.getImageToShow();
		if (image != null) {
			image.loadImage(getImageView(nodeGUI));
		}
	}
	private static void clearImage(LinearLayout nodeGUI) {
		if (nodeGUI != null) {
			ImageView imageview = getImageView(nodeGUI);
			imageview.setImageDrawable(null);
		}
	}
}

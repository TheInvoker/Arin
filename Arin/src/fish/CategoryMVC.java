package fish;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import widget.Popup;
import widget.TreeMaker;
import http.Network;
import image.CategoryImage;
import image.FishImage;
import activity.NavigationScreen;
import com.arin.R;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import arin.ArinContext;
import async.fish.Async_get_fish_stats;
import async.image.Async_up_image;
import async.image.Async_up_images;

public class CategoryMVC {

	private NavigationScreen context;
	private LinearLayout layout;
	private TextView textview;
	private SparseArray<LinearLayout> nodesMap = new SparseArray<LinearLayout>();
	
	// root
	public static Category root;
	// keeps track of the deepest selected category
	public static Category subRootCategory;
	// keeps track of last category that's selected when on its settings menu
	public static Category selectedCategory;
	// keeps track of last fish that's selected when on its settings menu
	public static Fish selectedFish;
	
	public CategoryMVC(NavigationScreen context) {
		this.context = context;
		this.layout = (LinearLayout) context.findViewById(R.id.carousel_container);
		this.textview = (TextView) context.findViewById(R.id.resultCount);
		
		root = null;
		subRootCategory = null;
		selectedCategory = null;
		selectedFish = null;
	}
	
	public void prepareData(String category_data, String species_data) {
		setUpCarousel(category_data, species_data);
	}
		
	public void clear() {
        if (root != null) {
        	destroyAllImages(root);
        }
	}

	public void reloadName(Fish fish) {
		LinearLayout nodeGUI = nodesMap.get(fish.getId());
		FishGUI.reloadName(fish, nodeGUI);
	}
	
	private void updateCount() {
		int count = subRootCategory == null ? 0 : Category.getAllSpecies(subRootCategory).size();
		textview.setText(Integer.toString(count, 10) + " " + context.getString(R.string.species_count));
	}

	public void uploadImage(Bitmap bitmap) {
		if (ArinContext.isOfflineMode()) {
			FishImage.getCommentAndSaveTemp(context, bitmap, CategoryImage.tablename, selectedCategory, new JSONObject());
		} else {
			new Async_up_image(context, bitmap, selectedCategory, new JSONObject());
		}
	}
	
	public void displayTree() {
		if (root != null) {
			initRoot(root);
		}
	}
	
	public void reloadSelectedPic() {
		Category cat = selectedCategory;
		LinearLayout nodeGUI = nodesMap.get(cat.getId());
		FishGUI.clearAllImages(cat, nodeGUI);
		FishGUI.loadMainImage(cat, nodeGUI);
	}
	

	
	private void setUpCarousel(String category_data, String species_data) {
		
		// show 0 matches initially
		updateCount();
		
		// generate the treeview
		TreeMaker treemaker = new TreeMaker(context);
		List<Category> categories = treemaker.getCategories(category_data, species_data);
		
		if (categories != null) {
			// store all nodes
			for(Category cat : categories) {
				addCategory(cat);
			}
			
			// get the root
			root = treemaker.getRoot(categories);
			
			if (root != null) {
				// start downloading images
				syncImages(root);
				return;
			}
		}
		
		Popup.ShowErrorMessage(context, R.string.corrupted, true);
	}
	
	public void addCategory(Category cat) {
		LinearLayout nodeGUI = FishGUI.createNodeWrapper(context, cat);
		FishGUI.getContentWrapper(nodeGUI).setBackgroundResource(R.drawable.fish_button_selector);
		nodesMap.put(cat.getId(), nodeGUI);
		configureClicks(cat);
	}

	
	/*
	 * deals with selection
	 */
	private void configureClicks(final Category cat) {
		LinearLayout nodeGUI = nodesMap.get(cat.getId());
		LinearLayout content_wrapper = FishGUI.getContentWrapper(nodeGUI);
		ImageButton infoButton = FishGUI.getInfoView(nodeGUI);
		ImageButton settingsButton = FishGUI.getSettingView(nodeGUI);
		
		content_wrapper.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				toggleSelect(cat);
				updateCount();
			}
		});
		if (ArinContext.isOfflineMode()) {
			infoButton.setVisibility(ImageButton.GONE);
		} else {
			infoButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (Network.isNetworkAvailable(context)) {
						new Async_get_fish_stats(context, cat);
					} else {
						Popup.ShowErrorMessage(context, R.string.no_internet, false);
					}
				}
			});
		}
		settingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				selectedFish = cat;
				selectedCategory = cat;
				cat.handleOptions(context);
			}
		});
	}
	public void unselectRoot(Category cat) {
		Category root = cat.getRoot();
		if (root.isSelected()) {
			unSelect(root);
		}
	}
	private void toggleSelect(Category cat) {	
		if (cat.isSelected()) unSelect(cat);
		else select(cat);
	}
	private void select(Category cat) {
		unSelectSiblings(cat);
		cat.setSelected(true);
		
		LinearLayout nodeGUI = nodesMap.get(cat.getId());
		LinearLayout current_row = (LinearLayout)nodeGUI.getParent();
		LinearLayout container = (LinearLayout)current_row.getParent();
		int row_index = container.indexOfChild(current_row);

		LinearLayout next_row;
		if (container.getChildCount() <= row_index + 1) {
			next_row = getRow();
			layout.addView(next_row);
		} else {
			next_row = (LinearLayout)container.getChildAt(row_index + 1);
		}
					
		for(Category category : cat.getCategories()) {
			addNodeToTable(category, next_row);
		}
		
		subRootCategory = cat;
		
		LinearLayout content_wrapper = FishGUI.getContentWrapper(nodeGUI);
		content_wrapper.setBackgroundResource(R.drawable.fish_button_sl_selector);
	}
	public void unSelect(Category cat) {
		cat.setSelected(false);
		
		for(Category category : cat.getCategories()) {
			LinearLayout nodeGUI = nodesMap.get(category.getId());
			LinearLayout current_row = (LinearLayout)nodeGUI.getParent();
			
			if (current_row != null) {
				current_row.removeView(nodeGUI);
					
				FishGUI.clearMainImage(category, nodeGUI);
				
				if (category.isSelected()) {
					unSelect(category);
				}
			}
		}
		
		subRootCategory = cat.getParent();
		
		LinearLayout nodeGUI = nodesMap.get(cat.getId());
		LinearLayout content_wrapper = FishGUI.getContentWrapper(nodeGUI);
		content_wrapper.setBackgroundResource(R.drawable.fish_button_selector);
	}
	private void unSelectSiblings(Category cat) {
		Category parent = cat.getParent();
		
		if (parent != null) {
			List<Category> fishes = parent.getCategories();
			for(Category category : fishes) {
				if (category.isSelected()) {
					unSelect(category);
				}
			}
		}
	}
	
	
	
	
	

	
	
	/*
	 * handles management of images
	 */
	public void destroyAllImages(Category cat) {
		LinearLayout nodeGUI = nodesMap.get(cat.getId());
		FishGUI.clearMainImage(cat, nodeGUI);
		for(Category child : cat.getCategories()) {
			destroyAllImages(child);
		}
	}
	private void syncImages(Category root) {
		if (Network.isNetworkAvailable(context)) {
			new Async_up_images(context, root);
		} else {
			displayTree();
		}
	}
	
	
	
	

	/*
	 * below functions manages adding new rows when categories are clicked
	 */
	private void initRoot(Category cat) {
		layout.removeAllViews();
		addAllRows(cat);

		LinearLayout first_row = (LinearLayout)layout.getChildAt(0);
		addNodeToTable(cat, first_row);
	}
	private void addAllRows(Category cat) {
		int height = getTreeDepth(cat);
		for (int i=0; i<height; i+=1) {
			layout.addView(getRow());
		}
	}
	private LinearLayout getRow() {
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER_HORIZONTAL;
		layout.setLayoutParams(params);
		return layout;
	}
	private void addNodeToTable(Category cat, LinearLayout next_row) {
		LinearLayout nodeGUI = nodesMap.get(cat.getId());
		FishGUI.loadMainImage(cat, nodeGUI);
		next_row.addView(nodeGUI);
	}
	private int getTreeDepth(Category category) {
		if (category == null) return -1;
		
		List<Category> categories = category.getCategories();
		
		if (categories.size() == 0) return 1;

		List<Integer> heights = new ArrayList<Integer>();
		
		for(Category cat : categories) {
			heights.add(1 + getTreeDepth(cat));
		}
		
		int max = 0;
		for (int i : heights) {
			max = Math.max(max, i);
		}
		return max;
	}
}

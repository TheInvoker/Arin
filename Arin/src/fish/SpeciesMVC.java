package fish;

import http.Network;
import image.FishImage;
import image.SpeciesImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.json.JSONObject;
import location.Place;
import com.google.android.gms.maps.model.LatLng;
import recognition.CompareResult;
import widget.Popup;
import common.Common;
import common.FieldValidation;
import common.SearchResult;
import activity.SpeciesScreen;
import android.app.Activity;
import com.arin.R;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import arin.ArinContext;
import async.fish.Async_get_fish_stats;
import async.image.Async_up_image;

public class SpeciesMVC {

	private Activity context;
	private LinearLayout layout;
	private SeekBar seekbar;
	
	private int page = 0;
	private String text = "";
	private Boolean search = false;
	private final int PAGE_LENGTH = 10;

	private final float MAX_DIST = 22037;    // distance in kilometers of half of the circumference of the world
	private float currentDist = MAX_DIST;
	private Double latitude = null;
	private Double longitude = null;
	
	private int type = SpeciesScreen.ALL_SPECIES;
	private List<SearchResult> content = new ArrayList<SearchResult>();
	private SparseArray<LinearLayout> nodesMap = new SparseArray<LinearLayout>();
	
	public SpeciesMVC(Activity context) {
		this.context = context;
		this.layout = (LinearLayout) context.findViewById(R.id.fishcontainer);
		this.seekbar = setUpSeekBar();
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public int getPage() {
		return page;
	}
	
	public void setPage(int page) {
		this.page = page;
	}
	
	public int getPageLen() {
		return PAGE_LENGTH;
	}

	public String getSearchText() {
		return text;
	}
	
	public void setSearchText(String text) {
		this.text = text;
	}

	public float getCurrentDist() {
		return currentDist;
	}
	
	public void setCurrentDist(float dist) {
		currentDist = dist;
	}
	
	public int getType() {
		return type;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	
	
	public void addRawData(List<Species> species) {
		for(Species specie : species) {
			addSearchResult(specie, false);
		}
	}
	
	public void addRecognitionData(List<CompareResult> compareResults) {
		for(CompareResult cr : compareResults) {
			addSearchResultHelper(cr, false);
		}
	}
	
	public void displayData() {
		double farthestDist = loadDistances();
		setSeekBarData(farthestDist);
		loadFirst(false);
	}
	
	public void insertSpecie(Species specie) {
		addSearchResult(specie, true);
		displayData();
	}

	public void removeSpecie(Species specie) {
		removeSearchResult(specie);
		displayData();
	}
	
	public void loadFirst(Boolean forSearch) {
		loadScreen(0, false, forSearch);
	}
	
	public void loadNext() {
		loadScreen(getPage()+1, true, search);
	}
	
	public void loadPrev() {
		if (getPage() > 0) {
			loadScreen(getPage()-1, true, search);
		}
	}
	
	public void clear() {
		softClear();
		content.clear();
		nodesMap.clear();
	}
	
	public void reloadName(Fish fish) {
		LinearLayout nodeGUI = nodesMap.get(fish.getId());
		FishGUI.reloadName(fish, nodeGUI);
	}

	public void uploadImage(Bitmap bitmap, JSONObject obj) {
		// store locally if offline mode
		if (ArinContext.isOfflineMode()) {
			FishImage.getCommentAndSaveTemp(context, bitmap, SpeciesImage.tablename, CategoryMVC.selectedFish, obj);
		} else {
			new Async_up_image(context, bitmap, CategoryMVC.selectedFish, obj);
		}
	}
	

	
	private void loadScreen(int page, Boolean pressedButton, Boolean forSearch) {
		// apply location and search filters
		List<SearchResult> content = getFilteredResults();
		
		// sort the results
		sortData(content);
		
		// show a specific page of the filtered content
		int start = page * getPageLen();
		int end = Math.min((page+1) * getPageLen(), content.size());
		
		// show if there are results
		if (!pressedButton || (pressedButton && start < content.size())) {
			softClear();
			
			search = forSearch;
			
			for(int i=start; i<end; i+=1) {
				Species specie = content.get(i).getSpecie();
				displaySpecie(specie);
			}
			
			setPage(page);
			setPageNum(page, content.size());
			setTitle(forSearch);
		}
	}
	
	

	
	private List<SearchResult> getFilteredResults() {
		List<SearchResult> filtered_content = new ArrayList<SearchResult>();
		
		for(SearchResult sr : content) {
			if (isDistanceOK(sr) && isSearchOK(sr)) {
				filtered_content.add(sr);
			}
		}
		
		return filtered_content;
	}
	
	private Boolean isDistanceOK(SearchResult sr) {
		return sr.getClosestDist() <= getCurrentDist();
	}

	private Boolean isSearchOK(SearchResult sr) {
		if (!getSearchText().equals("")) {
			String[] searchSegments = Common.getSearchSegments(getSearchText());
			String name = sr.getSpecie().getName().toLowerCase(Locale.getDefault());
			return Common.nameContains(searchSegments, name);
		}
		return true;
	}
	
	private void displaySpecie(Species specie) {
		LinearLayout nodeGUI = nodesMap.get(specie.getId());
		layout.addView(nodeGUI);
		FishGUI.loadMainImage(specie, nodeGUI);
	}
	
	private void softClear() {
    	for(SearchResult c : content) {
    		Species specie = c.getSpecie();
    		FishGUI.clearMainImage(specie, nodesMap.get(specie.getId()));
    	}
    	
		layout.removeAllViews();
	}
	
	
	
	
	
	private void setPageNum(int newpage, double total) {
		int num_pages = (int) Math.ceil(total / getPageLen());
		int maxpages = Math.max(1, num_pages);
		TextView pagenumTV = (TextView) context.findViewById(R.id.page);
		pagenumTV.setText(String.format(Locale.getDefault(), "%d/%d", newpage+1, maxpages));
	}
	
	private void setTitle(Boolean forSearch) {
		if (forSearch) {
			context.getActionBar().setTitle(R.string.title_activity_my_search);
		} else if (getType() == SpeciesScreen.SPECIES) {
			context.getActionBar().setTitle(CategoryMVC.selectedCategory.getName());
		} else {
			context.getActionBar().setTitle(R.string.title_activity_species);
		}
	}
	
	
	
	
	
	private void addSearchResult(Species specie, Boolean addToFront) {
		CompareResult cr = new CompareResult(specie, 0);
		addSearchResultHelper(cr, addToFront);
	}
	
	private void addSearchResultHelper(CompareResult cr, Boolean addToFront) {
		SearchResult sr = new SearchResult(cr, MAX_DIST);
		content.add(addToFront ? 0 : content.size(), sr);
		
		Species specie = cr.getSpecies();
		LinearLayout nodeGUI = FishGUI.createNodeWrapper(context, specie);
		FishGUI.getContentWrapper(nodeGUI).setBackgroundResource(R.drawable.fish_button_selector);
		nodesMap.put(specie.getId(), nodeGUI);
		
		configureClicks(specie);
	}
	
	private void removeSearchResult(Species specie) {
    	for(SearchResult c : content) {
    		Species s = c.getSpecie();
    		if (s.getId() == specie.getId()) {
    			content.remove(c);
    			break;
    		}
    	}
	}
	
	
	private void configureClicks(final Species specie) {
		LinearLayout nodeGUI = nodesMap.get(specie.getId());
		LinearLayout content_wrapper = FishGUI.getContentWrapper(nodeGUI);
		ImageButton infoButton = FishGUI.getInfoView(nodeGUI);
		ImageButton settingsButton = FishGUI.getSettingView(nodeGUI);
		
		content_wrapper.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String url = specie.getResourceLink();
				
				if (!url.equals("")) {
					url = FieldValidation.fixURL(url);
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					context.startActivity(browserIntent);
				} else {
					Popup.ShowErrorMessage(context, R.string.nolink, false);
				}	
			}
		});
		if (ArinContext.isOfflineMode()) {
			infoButton.setVisibility(ImageButton.GONE);
		} else {
			infoButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if (Network.isNetworkAvailable(context)) {
						new Async_get_fish_stats(context, specie);
					} else {
						Popup.ShowErrorMessage(context, R.string.no_internet, false);
					}
				}
			});
		}
		settingsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CategoryMVC.selectedFish = specie;
				specie.handleOptions(context);
			}
		});
	}
	
	
	
	public void moveFinish(Species specie) {
		if (getType() == SpeciesScreen.ALL_SPECIES) {
			if (CategoryMVC.subRootCategory.getSpeciesFromId(specie.getId()) == null) {
				removeSpecie(specie);
			}
		} else if (getType() == SpeciesScreen.SPECIES) {
			removeSpecie(specie);
		}
	}
	
	

	
	
	/*
	 * handles the location filtering
	 */
	private SeekBar setUpSeekBar() {
		SeekBar seekbar = (SeekBar) context.findViewById(R.id.seekBar);
		final TextView textview = (TextView) context.findViewById(R.id.progressText);
		final String km_away = context.getString(R.string.km_away);
		
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				setCurrentDist(arg1);
				textview.setText(km_away.replace("_", Integer.toString(arg1, 10)));
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				loadFirst(search);
			}
		});
		
		return seekbar;
	}
	
	/*
	 * generate the distances for each species ad stores results globally
	 * returns farthest distance
	 */
	private double loadDistances() {
		double totalFarthest = 0;
		LatLng here = (latitude!=null && longitude!=null) ?
				new LatLng(latitude, longitude) :  // current location
				new LatLng(43.653226, -79.383184); // TORONTO
		
		for(SearchResult sr : content) {
			Species specie = sr.getSpecie();

			List<Place> locations = specie.getLocations();
			float closestDist = Float.MAX_VALUE;
			for(Place place : locations) {
				float dist = Common.getDistance(here.latitude, here.longitude, place.getLatitude(), place.getLongitude());
				closestDist = Math.min(closestDist, dist);
			}
			
			float dist = closestDist == Float.MAX_VALUE ? MAX_DIST : closestDist;
			totalFarthest = Math.max(totalFarthest, dist);
			
			sr.setClosestDist(dist);
		}

		return totalFarthest + 100;
	}
	
	/*
	 * sorts the data
	 */
	private void sortData(List<SearchResult> content) {
		if (type == SpeciesScreen.RECOGNITION) { // image recognition
			Collections.sort(content, new Comparator<SearchResult>(){
				@Override
			    public int compare(SearchResult o1, SearchResult o2) {
			        if (o1.getCompareResult().getResult() > o2.getCompareResult().getResult()) return -1;
			        if (o1.getCompareResult().getResult() < o2.getCompareResult().getResult()) return 1;
			        if (o1.getClosestDist() > o2.getClosestDist()) return 1;
			        if (o1.getClosestDist() < o2.getClosestDist()) return -1;
			        return 0;
			    }
			});
		} else { // other
			Collections.sort(content, new Comparator<SearchResult>(){
				@Override
			    public int compare(SearchResult o1, SearchResult o2) {
			        if (o1.getClosestDist() > o2.getClosestDist()) return 1;
			        if (o1.getClosestDist() < o2.getClosestDist()) return -1;
			        return 0;
			    }
			});
		}
	}
	
	/*
	 * sets the seekbar length
	 */
	public void setSeekBarData(double farthestDist) {
		int farDist = (int)Math.ceil(farthestDist);
		seekbar.setMax(farDist);
		seekbar.setProgress(farDist);
	}
}

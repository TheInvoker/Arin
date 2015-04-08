package async.image;

import image.FishImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import widget.Popup;
import common.Common;
import fish.Category;
import fish.Fish;
import fish.Species;
import http.Network;
import activity.NavigationScreen;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import com.arin.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Pair;
import arin.ArinContext;

/*
 * This is a async task that downloads images.
 */

public class Async_dl_images extends AsyncTask<String, String, Boolean> {

	private ProgressDialog progressDialog;
	private NavigationScreen callerContext;
	private List<Pair<Fish, FishImage>> images;
	
	public Async_dl_images(NavigationScreen callerContext, Category root) {
		this.callerContext = callerContext;

		if (Network.isNetworkAvailable(callerContext) && !ArinContext.isOfflineMode()) {

			Pair<List<Pair<Fish, FishImage>>, Double> data = getMissingImages(root);
			images = data.first;

			if (images.size() > 0) {
				double totalSize = data.second;
			 	confirmDialog(totalSize);
			 	return;
			}
		}

		displayTree();
	}
	
	private Pair<List<Pair<Fish, FishImage>>, Double> getMissingImages(Category root) {
		List<Category> fishList = Category.flattenCategories(root);
		
		double totalFileSize = 0.0;
		List<Pair<Fish, FishImage>> imageList = new ArrayList<Pair<Fish, FishImage>>();
		
		for(Category fish : fishList) {
			totalFileSize += addToList(fish, imageList);
			
			List<Species> species = fish.getSpecies();
			for(Species specie : species) {
				totalFileSize += addToList(specie, imageList);
			}
		}
		
		return new Pair<List<Pair<Fish, FishImage>>, Double>(imageList, totalFileSize);
	}
	
	private double addToList(Fish fish, List<Pair<Fish, FishImage>> imageList) {
		double totalFileSize = 0.0;
		List<FishImage> images = fish.getImageList();
		for(FishImage image : images) {
			if (!image.imageExists()) {
				totalFileSize += image.getFileSize();
				imageList.add(new Pair<Fish, FishImage>(fish, image));
			}
		}
		return totalFileSize;
	}
	
	
	
	
	
	
	private void confirmDialog(double size) {
		String message = String.format(Locale.getDefault(), "%s (%.2f MB)", 
				callerContext.getString(R.string.abouttodownload).replace("_", Integer.toString(images.size(), 10)), 
				Math.max(0.01, Common.bytesToMegabytes(size)));
		
		AlertDialog.Builder builder = new AlertDialog.Builder(callerContext);
		builder.setTitle(R.string.downloadnotice)
		.setMessage(message)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	progressDialog = new ProgressDialog(callerContext, ProgressDialog.THEME_HOLO_DARK);
	    		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    		progressDialog.setMax(images.size());
	    		progressDialog.setMessage(callerContext.getString(R.string.downloading)); 
	        	progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
	                public void onCancel(DialogInterface dialog) {
	                    Async_dl_images.this.cancel(true);
	                }
	            });
	        	progressDialog.show();
	        	execute();
            }
        })
        .setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				displayTree();
			}
        })
		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	displayTree();
            }
        }).show();
	}

	@Override
	protected Boolean doInBackground(String... arg0) {
		for(int i=0; i<images.size(); i+=1) {
			
			Pair<Fish, FishImage> pair = images.get(i);
			Fish fish = pair.first;
			FishImage image = pair.second;
			
			try {
				synchronized (this) { 
					if (isCancelled()) {
						break;
					}
					
					String progressText = String.format(Locale.getDefault(), "%s %s", callerContext.getString(R.string.downloadedImage), fish.getName());
					String progressNumber = Integer.toString(i + 1, 10);
					publishProgress(progressText, progressNumber); 
					
					String httpimagelink = image.getHTTPLink();
					Bitmap bitmap = Network.DownloadImage((Context) callerContext, httpimagelink);

					if (bitmap!=null) {
						image.saveImageLocally(bitmap);
						bitmap.recycle();
					}
	            } 
			} catch (FileNotFoundException e) { 
				Popup.ShowErrorMessage(callerContext, R.string.error_saving, false);
				break;
			} catch (IOException e) {
				Popup.ShowErrorMessage(callerContext, R.string.error_saving, false);
				break;
			} catch (Exception e) {
				Popup.ShowErrorMessage(callerContext, R.string.error_saving, false);
				break;
			}
		}
	
		return false;
	}
	
    @Override  
    protected void onProgressUpdate(String... values) {  
        progressDialog.setMessage(values[0]);  
        progressDialog.setProgress(Integer.parseInt(values[1], 10));
    }  
	
	@Override
	protected void onPostExecute(Boolean downloaded) {
		displayTree();
		progressDialog.dismiss();
	}
	
	private void displayTree() {
		callerContext.displayTree();
	}
}
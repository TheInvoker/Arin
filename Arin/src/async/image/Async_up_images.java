package async.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;
import common.Common;
import http.HttpTask;
import http.Network;
import image.FishImage;
import image.TempImage;
import fish.Category;
import fish.Fish;
import activity.NavigationScreen;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import com.arin.R;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import arin.ArinContext;

public class Async_up_images extends AsyncTask<String, String, Boolean> implements HttpTask {
	
	private ProgressDialog progressDialog;
	private NavigationScreen callerContext;
	private Category root;
	private List<TempImage> paths = new ArrayList<TempImage>();
	private final String WEBFILE = "image/uploadimage.php";
	
	public Async_up_images(NavigationScreen callerContext, Category root) {
		this.callerContext = callerContext;
		this.root = root;
		
		if (Network.isNetworkAvailable(callerContext) && !ArinContext.isOfflineMode() && !ArinContext.getUser().isBanned()) {
			
			paths = FishImage.getImagesToUpload(root);
			
			if (paths.size() > 0) {
				double totalSize = 0.0;
			
				for(TempImage path : paths) {
					totalSize += path.getSize();
				}
			
				confirmDialog(totalSize);
				return;
			}
		}
			
		downloadImages();
	}
	
	private void confirmDialog(double size) {
		String message = String.format(Locale.getDefault(), "%s (%.2f MB)", 
				callerContext.getString(R.string.abouttoupload).replace("_", Integer.toString(paths.size(), 10)), 
				Math.max(0.01, Common.bytesToMegabytes(size)));
		
		AlertDialog.Builder builder = new AlertDialog.Builder(callerContext);
		builder.setTitle(R.string.uploadnotice)
		.setMessage(message)
		.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	progressDialog = new ProgressDialog(callerContext, ProgressDialog.THEME_HOLO_DARK);
	    		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    		progressDialog.setMax(paths.size());
	    		progressDialog.setMessage(callerContext.getString(R.string.uploading)); 
	        	progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
	                public void onCancel(DialogInterface dialog) {
	                    Async_up_images.this.cancel(true);
	                }
	            });
	        	progressDialog.show();
	        	execute();
            }
        })
        .setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface arg0) {
				downloadImages();
			}
        })
		.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	downloadImages();
            }
        }).show();
	}

	@Override
	protected Boolean doInBackground(String... params) {
		for(int i=0; i<paths.size(); i+=1) {
			
			TempImage tempImage = paths.get(i);
			
			synchronized (this) { 
				if (isCancelled()) break;
				
				Fish fish = tempImage.getFish(root);
				
				if (fish != null) {
					String progressText = callerContext.getString(R.string.uploadedImage);
					String progressNumber = Integer.toString(i + 1, 10);
					publishProgress(progressText, progressNumber); 
					
					Bitmap bitmap = BitmapFactory.decodeFile(tempImage.getFile().getAbsolutePath());
					
					if (bitmap != null) {
						try {
							String response = Network.UploadImage(callerContext, bitmap, WEBFILE, tempImage);
							
							if (response != null) {
								JSONObject object = new JSONObject(response);
								int code = object.getInt(CODE);
								
								if (code==200) {
									JSONObject result = object.getJSONObject(RESPONSE);
									int pic_id = result.getInt("pic_id");
									String elink = result.getString("elink");
									int locationId = result.getInt("locationId");
									
									worked(fish, pic_id, elink, bitmap, tempImage.getObj(), locationId);
									tempImage.dispose();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						} catch (ClientProtocolException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						bitmap.recycle();
					}
				}
			}
		}
		
		return true;
	}
	
	private void worked(Fish fish, int pic_id, String elink, Bitmap bitmap, JSONObject obj, int locationId) throws IOException {
		fish.handleUploadImageSuccess(pic_id, bitmap, obj, elink, locationId);
	}
	
    @Override  
    protected void onProgressUpdate(String... values) {  
        progressDialog.setMessage(values[0]);  
        progressDialog.setProgress(Integer.parseInt(values[1], 10));
    } 
	
	@Override
	protected void onPostExecute(Boolean downloaded) {
		downloadImages();
		progressDialog.dismiss();
	}
    
	private void downloadImages() {
		new Async_dl_images(callerContext, root);
	}
}

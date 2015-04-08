package async.recognition;

import fish.Species;
import image.FishImage;
import image.MyImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import common.MyDate;
import recognition.CompareResult;
import recognition.FishScan;
import recognition.Recognize;
import recognition.Result;
import widget.ColorGrid;
import activity.SpeciesScreen;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import com.arin.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Vibrator;

/*
 * This is a async task that does image recognition
 */

public class Async_recognition extends AsyncTask<String, Integer, List<CompareResult>> {

	private SpeciesScreen context;
	private ProgressDialog progressDialog;
	private FishScan initScan;
	private List<FishScan> scans = new ArrayList<FishScan>();
	private ColorGrid colorgrid;
	private JSONObject obj;
	private Date startDate = new Date();
	
	public static final int SCAN_COMPLETE_NOTIFICATION_ID = 0;
	
	public Async_recognition(SpeciesScreen context, Bitmap initBitmap, List<Species> speciesList, ColorGrid colorgrid, JSONObject obj) {
		this.context = context;
		this.initScan = new FishScan(initBitmap, null);
		this.colorgrid = colorgrid;
		this.obj = obj;
		
       	// get a list of the objects of the other pics
    	for(Species specie : speciesList) {
    		List<FishImage> images = specie.getImageList();
    		for(FishImage image : images) {
				File file = image.getFishFile();
				Bitmap bitmap = MyImage.getBitmapForRecognition(file);
				if (bitmap != null) {
					scans.add(new FishScan(bitmap, specie));
				}
    		}
    	}
		
		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(scans.size() + 1);
		progressDialog.setMessage(context.getString(R.string.analyzing)); 
    	progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
            public void onCancel(DialogInterface dialog) {
            	Async_recognition.this.cancel(true);
            }
        });
    	progressDialog.show();

    	execute();
    }
    
	private void add(CompareResult compareResult, List<CompareResult> compareResults) {
		for(int j=0; j<compareResults.size(); j+=1) {
			CompareResult cr = compareResults.get(j);
			if (cr.getSpeciesId() == compareResult.getSpeciesId()) {
				if (cr.getResult() > compareResult.getResult()) {
					compareResults.set(j, compareResult);
				}
				return;
			}
		}
		compareResults.add(compareResult);
	}

	@Override
	protected List<CompareResult> doInBackground(String... arg0) {
		try {
			List<CompareResult> compareResults = new ArrayList<CompareResult>();
			
			publishProgress(1); 
			Result initResult = Recognize.prepareForScan(initScan.getBitmap(), initScan.getSpecies());
			
			for(int i=0; i<scans.size(); i+=1) {
				if (isCancelled()) break;
				
				publishProgress(i + 2); 

				FishScan scan = scans.get(i);
				Result result = Recognize.prepareForScan(scan.getBitmap(), scan.getSpecies());
				
				CompareResult compareResult = initResult.compareTo(result);
				add(compareResult, compareResults);
				
				result.getBitmap().recycle();
			}
			
			initResult.getBitmap().recycle();
			
			return compareResults;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
    @Override  
    protected void onProgressUpdate(Integer... values) {  
    	colorgrid.changeCellColors();
        progressDialog.setProgress(values[0]);
    }  
	
    @Override
    protected void onCancelled() {
    	colorgrid.dismiss();
    	context.finish();
    }
    
	@Override
	protected void onPostExecute(List<CompareResult> compareResults) {
		progressDialog.dismiss();
		colorgrid.dismiss();
		context.recognitionReturn(compareResults, obj);
		
		if (context.paused) {
			showNotification();
			
			// Vibrate for 500 milliseconds
			Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
			if (v.hasVibrator()) v.vibrate(500);
		}
	}
	
	private void showNotification() {
		String time = context.getString(R.string.time_msg) + MyDate.getDateDiff(startDate);
		
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Intent notIntent = new Intent(context, SpeciesScreen.class);
		notIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent pIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, notIntent, 0);
		
		Notification noti = new Notification.Builder(context)
	    .setContentTitle(context.getString(R.string.scan_complete))
	    .setContentText(time)
	    .setSmallIcon(R.drawable.ic_stat_name)
	    .setContentIntent(pIntent)
	    .setAutoCancel(true)
	    .build();

        notificationManager.notify(SCAN_COMPLETE_NOTIFICATION_ID, noti); 
	}
}

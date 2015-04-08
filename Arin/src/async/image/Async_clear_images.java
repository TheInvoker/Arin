package async.image;

import java.io.File;
import widget.Popup;
import image.FishImage;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import com.arin.R;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;

public class Async_clear_images extends AsyncTask<String, Integer, String> {
	
	private Activity context;
	private File file;
	private int delCount = 1;
	private ProgressDialog progressDialog;
	
	public Async_clear_images(Activity context) {
		this.context = context;
		
		String path = Environment.getExternalStorageDirectory().toString();
    	String folder = FishImage.ROOT_FOLDER;
    	file = new File(path, folder);
		
		handleDelete();
	}
	
    private void handleDelete() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    builder.setTitle(R.string.confirmdelete);
	    builder.setMessage(R.string.deleteAll);
	    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {
	    		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
	    		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    		progressDialog.setMax(GetTotalDeleteCount(file));
	    		progressDialog.setMessage(context.getString(R.string.deleting));
	          	progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
	                public void onCancel(DialogInterface dialog) {
	                	Async_clear_images.this.cancel(true);
	                }
	            });
	        	progressDialog.show();
	    		
	    		execute();
	        }
	    }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        }
	    });
	    builder.show();
    }
	
	@Override
	protected String doInBackground(String... arg0) {
    	DeleteRecursive(file);
		return null;
	}
	
    @Override  
    protected void onProgressUpdate(Integer... values) {  
        progressDialog.setProgress(values[0]);
    }  
	
	@Override
	protected void onPostExecute(String response) {
		worked();
		progressDialog.dismiss();
	}
	
	
	
	
	private int GetTotalDeleteCount(File fileOrDirectory) {
    	int count = 0;

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
            	count += GetTotalDeleteCount(child);
            }
        } else {
        	count += 1;
        }

        return count;
    }
    
    private void DeleteRecursive(File fileOrDirectory) {
    	if (isCancelled()) return;
    	
        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                DeleteRecursive(child);
            }
        }
        
    	if (fileOrDirectory.isFile()) {
    		publishProgress(delCount); 
    		delCount += 1;
    	}
    	
        fileOrDirectory.delete();
    }
	
	private void worked() {
		Popup.ShowString(context, R.string.clear_title, R.string.clear_message, false);
	}
}

package image;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import widget.Popup;
import common.Ban;
import common.Common;
import fish.Category;
import fish.Fish;
import fish.Species;
import activity.SpeciesScreen;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import com.arin.R;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/*
 * This is a class for images for a category and species.
 */

public abstract class FishImage implements Ban {

	protected int id;
	protected int fish_id;
	protected Bitmap bitmap;
	protected Boolean isMain;
	protected int fileSize;
	protected String comment;
	protected Boolean approved;
	protected Date date_added;
	protected int user_id;
	protected String elink;
	protected int ban_days_left;
	
	public static final String ROOT_FOLDER = "arin";
	
	public int getId() {
		return id;
	}
	
	public int getFishId() {
		return fish_id;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Boolean getIsMain() {
		return isMain;
	}

	public void setIsMain(Boolean isMain) {
		this.isMain = isMain;
	}
	
	public int getFileSize() {
		return fileSize;
	}
	
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}
    
    public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Boolean getApproved() {
		return approved;
	}

	public void setApproved(Boolean approved) {
		this.approved = approved;
	}

	public Date getDate_added() {
		return date_added;
	}

	public void setDate_added(Date date_added) {
		this.date_added = date_added;
	}

	public String getHTTPLink() {
		return elink;
	}
	
	
	


	public int getBanUserId() {
		return user_id;
	}

	public int getBanDaysLeft() {
		return ban_days_left;
	}
	
	public void setBanDaysLeft(int days) {
		this.ban_days_left = days;
	}
	
	
	
	/*
	 * get the local link
	 */
    private String getLocalDir() {
    	return Environment.getExternalStorageDirectory().toString() + 
    			"/" + ROOT_FOLDER + "/" + getTableName() + "/" + Integer.toString(getFishId(), 10);
    }
	
	/*
	 * get the local link
	 */
    private String getLocalLink() {
    	return getLocalDir() + "/" + getId() + ".png";
    }

    /*
     * gets the file object of the image
     */
	public File getFishFile() {
		String localimagelink = getLocalLink();
		return new File(localimagelink);
	}
    
	/*
	 * checks if the file exists
	 */
	public Boolean imageExists() {
		return getFishFile().exists();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * loads the image into its GUI
	 */
	public void loadImage(ImageView imageview) {
		if (imageExists()) {
		
			Bitmap bitmap;
			
			if (getBitmap()==null) {
				bitmap = generateBitmap();
				setBitmap(bitmap);
			} else {
				bitmap = getBitmap();
			}
			
			imageview.setImageBitmap(bitmap);
		}
	}
	
	/*
	 * get the image from memory
	 */
	private Bitmap generateBitmap() {
		Bitmap bitmap = null;
		
		File file = getFishFile();
		if (file.exists()) {
			try {
				FileInputStream streamIn = new FileInputStream(file);
				bitmap = BitmapFactory.decodeStream(streamIn); 
				streamIn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return bitmap;
	}
	
	
	/*
	 * free the image
	 */
	public void freeBitmapMemory() {
		Bitmap bitmap = getBitmap();
		if (bitmap != null) {
			bitmap.recycle();
			setBitmap(null);
		}
	}
		
	/*
	 * save the image locally
	 */
	public void saveImageLocally(Bitmap bitmap) throws IOException {
		String imgpath = getLocalLink();
		File file = new File(imgpath);
		if (file.exists()) {
			file.delete();
		}
		
		imageSaverHelper(file, bitmap);
	}
	
	public static void markAsNoMedia() throws IOException {
		String path = Environment.getExternalStorageDirectory().toString();
    	String nomediafile = ROOT_FOLDER + "/.nomedia";
    	File file = new File(path, nomediafile);
		if (!file.exists()) {
			 BufferedWriter output = new BufferedWriter(new FileWriter(file));
	         output.close();
		}
	}
	
	
	
	
	
	
	
	public static List<TempImage> getImagesToUpload(Category root) {
		List<TempImage> imagestoupload = new ArrayList<TempImage>();
		List<Category> categories = Category.flattenCategories(root);
		
		for(Category category : categories) {
			List<FishImage> images = category.getImageList();
			for(FishImage image : images) {
				imagestoupload.addAll(getTempImages(image));
			}
			
			List<Species> species = category.getSpecies();
			for(Species specie : species) {
				images = specie.getImageList();
				for(FishImage image : images) {
					imagestoupload.addAll(getTempImages(image));
				}
			}
		}
		
		return imagestoupload;
	}
	
	private static List<TempImage> getTempImages(FishImage image) {
		List<TempImage> imagestoupload = new ArrayList<TempImage>();
		
		String dir = image.getLocalDir();
		File tempDir = new File(dir, "temp");
		if (tempDir.exists()) {
			File[] files = tempDir.listFiles();
			
			for (File file : files) {
				String fileAbsPath = file.getAbsolutePath();
				
    			if (fileAbsPath.endsWith(".png")) {
    				String commentAbsPath = fileAbsPath.substring(0, fileAbsPath.length()-3) + "txt";
    				File commentFile = new File(commentAbsPath);
    				
    				JSONObject obj = null;
    				if (commentFile.exists()) {
	    				try {	
	    					obj = new JSONObject(Common.readFileContents(commentFile));
	    				} catch (JSONException e) {
	    					e.printStackTrace();
	    				}
    				}
    				
    				imagestoupload.add(image.getTableName().equals(CategoryImage.tablename) ? 
    						new TempCategoryImage(file, commentFile, obj, image.getFishId()) :
    						new TempSpeciesImage(file, commentFile, obj, image.getFishId())
    				);
    			}
			}
		}
		
		return imagestoupload;
	}

	
	
	@SuppressLint("InflateParams")
	public static void getCommentAndSaveTemp(final Activity context, final Bitmap bitmap, final String tablename, final Fish fish, final JSONObject obj) {
        LayoutInflater inflater = context.getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_comment, null);
		final EditText commentET = (EditText)dialoglayout.findViewById(R.id.comment);
		
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	try {
            		String comment = commentET.getText().toString();
            		obj.put(SpeciesScreen.cmmtKey, comment);
            		
            		String fish_id = Integer.toString(fish.getId(), 10);
            		saveTempImage(tablename, fish_id, bitmap, obj.toString());
            		bitmap.recycle();
            		
            		Popup.ShowWarningMessage(context, R.string.offline_save_image, false);
            	} catch (IOException e) {
					Popup.ShowErrorMessage(context, R.string.error_saving, false);
            	} catch (JSONException e) {
					Popup.ShowErrorMessage(context, R.string.error_saving, false);
				}
            }
        };
        
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.add_comment).setCancelable(true);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).setPositiveButton(R.string.ok, listener);
        
        builder.setView(dialoglayout);
		builder.show();
	}
	
    private static void saveTempImage(String tablename, String fish_id, Bitmap bitmap, String str) throws IOException {
    	String path = Environment.getExternalStorageDirectory().toString();
    	String imgfolderpath = ROOT_FOLDER + "/" + tablename + "/" + fish_id + "/temp/";
    	
    	File file,cfile;
    	int count = 0;
    	
    	while (true) {
    		file = getTempPath(path, imgfolderpath, count, ".png");
    		cfile = getTempPath(path, imgfolderpath, count, ".txt");
    		
    		if (!file.exists()) break;
    		count += 1;
    	}

		imageSaverHelper(file, bitmap);
		saveCommentFile(cfile, str);
    }
    
    private static File getTempPath(String path, String imgfolderpath, int count, String ext) {
    	return new File(path, imgfolderpath + Integer.toString(count, 10) + ext);
    }
    
	private static void imageSaverHelper(File file, Bitmap bitmap) throws IOException {
		file.getParentFile().mkdirs();

		OutputStream fOut = new FileOutputStream(file);
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

		fOut.flush();
		fOut.close();
	}
	
	private static void saveCommentFile(File file, String str) throws IOException {
		if (file.exists()) {
			file.delete();
		}
		BufferedWriter output = new BufferedWriter(new FileWriter(file));
        output.write(str);
        output.close();
	}

	
	

	public abstract String getTableName();
}

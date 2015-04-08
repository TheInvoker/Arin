package common;

import http.Network;
import image.MyImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.zip.InflaterInputStream;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;

/*
 * This gives some common functions to all
 */

public final class Common {

	public static double bytesToMegabytes(double bytes) {
		return bytes/1048576.0;
	}
	
	// joins a list of strings into 1 string with a delimiter	
	public static String StringJoin(List<String> selected, String delimiter) {
		String str = "";
		
		int h = selected.size();
		for(int i=0; i<h; i+=1) {
			str += selected.get(i) + (i < h-1 ? delimiter : "");
		}
		
		return str;
	}
	
	/*
	 * Calculates the distance in km between two lat/long points
	 */
	public static float getDistance(double lat1, double lng1, double lat2, double lng2) {
	    Location loc1 = new Location("");
	    loc1.setLatitude(lat1);
	    loc1.setLongitude(lng1);

	    Location loc2 = new Location("");
	    loc2.setLatitude(lat2);
	    loc2.setLongitude(lng2);
	    
	    return loc1.distanceTo(loc2) / 1000;
	}
	
    public static Boolean nameContains(String[] searchSegments, String name) {
		for(int j=0; j<searchSegments.length; j+=1) {
			if (name.contains(searchSegments[j])) {
				return true;
			}
		}
		return false;
    }
    
    public static String readFileContents(File file) {
		String comment = "";
        try {
        	FileReader reader = new FileReader(file);
	        char[] chars = new char[(int) file.length()];
			reader.read(chars);
			comment = new String(chars);
	        reader.close();
		} catch (IOException e) {
		}
        return comment;
    }
    
    public static String fixStringForSearch(String text) {
		text = text.trim();
		while (text.contains("  ")) {
			text.replace("  ", " ");
		}
		return text;
    }
    
    public static String[] getSearchSegments(String text) {
    	text = fixStringForSearch(text);
		text = text.toLowerCase(Locale.getDefault());
		String[] searchSegments = text.split(" ");
		return searchSegments;
    }
    
    public static String unzipString(byte[] zbytes) {
        String charsetName = "ISO-8859-1";
        String unzipped = null;
        try {
            // Add extra byte to array when Inflater is set to true
            byte[] input = new byte[zbytes.length + 1];
            System.arraycopy(zbytes, 0, input, 0, zbytes.length);
            input[zbytes.length] = 0;
            ByteArrayInputStream bin = new ByteArrayInputStream(input);
            InflaterInputStream in = new InflaterInputStream(bin);
            ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
            int b;
            while ((b = in.read()) != -1) {
                bout.write(b); 
            }
            bout.close();
            unzipped = bout.toString(charsetName);
        } catch (IOException e) { 
            e.printStackTrace();
        }
        return unzipped;
    }
    
    public static String GetAddressFromAddress(Address address) {
    	String addressStr = "";
    	int len = address.getMaxAddressLineIndex();
    	for (int i=0; i<=len; i+=1) {
    		addressStr += address.getAddressLine(i) + (i==len ? "" : " ");
    	}
    	
		return addressStr;
    }
    
    public static Point GetLatLongFromImage(Context context, Uri uri) {
    	try {
    		String imagefile = MyImage.getPath(context, uri);
			ExifInterface exifInterface = new ExifInterface(imagefile);
			GeoDegree geodegree = new GeoDegree(exifInterface); 
			
			if (geodegree.isValid() && Network.isNetworkAvailable(context)) {
				Geocoder geocoder = new Geocoder(context, Locale.getDefault());
				List<Address> addresses = geocoder.getFromLocation(geodegree.getLatitude(), geodegree.getLongitude(), 1);
				StringBuilder sb = new StringBuilder();
				if (addresses.size() > 0) {
					Address address = addresses.get(0);
					sb.append(Common.GetAddressFromAddress(address));
				}
				
				return new Point(geodegree.getLatitude(), geodegree.getLongitude(), sb.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
    
    public static void openRate(Context context) {
		Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

		try {
			context.startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
		}
    }
}

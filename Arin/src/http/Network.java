package http;

import image.TempImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import user.Actor;
import common.Common;
import activity.SpeciesScreen;
import com.arin.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Pair;
import arin.ArinContext;

/*
 * Handles management of networking tasks.
 */

public final class Network {
	
	// time out until it gives up
	public static final int TIMEOUT_SECONDS = 15;
	
	// checks if internet is on
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    
    // makes a http request
	public static Pair<String,Integer> GetHTTPResponse(String url, List<NameValuePair> urlparameters) {
		try {
			HttpEntity reqEntity = new UrlEncodedFormEntity(urlparameters);
			return httpRequestHelper(url, reqEntity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return getErrorReturn();
	}
	
	// downloads image
	public static Bitmap DownloadImage(Context context, String imageURL) throws IOException {
    	System.gc();
    	URL url = new URL(imageURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();   
        conn.setDoInput(true);   
        conn.connect();     
        InputStream is = conn.getInputStream();
        Bitmap bmImg = BitmapFactory.decodeStream(is); 
        return bmImg; 
	}
	
	// uploads image
	public static String UploadImage(Context context, Bitmap bitmap, String page, TempImage tempImage) throws ClientProtocolException, IOException {
		Actor user = ArinContext.getUser();
		JSONObject obj = tempImage.getObj();
		
		Double lat = 0.0, lng = 0.0;
		String adr = "", cmt = "";
		
		if (obj.has(SpeciesScreen.latKey)) {
			try {
				lat = obj.getDouble(SpeciesScreen.latKey);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (obj.has(SpeciesScreen.longKey)) {
			try {
				lng = obj.getDouble(SpeciesScreen.longKey);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (obj.has(SpeciesScreen.addrKey)) {
			try {
				adr = obj.getString(SpeciesScreen.addrKey);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		if (obj.has(SpeciesScreen.cmmtKey)) {
			try {
				cmt = obj.getString(SpeciesScreen.cmmtKey);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		String url = String.format(Locale.getDefault(), 
				context.getString(R.string.arin_host) + "?page=%s&user_id=%d&fish_id=%d&comment=%s&lat=%f&long=%f&addr=%s&is_node=%d", 
				page, 
				user.getId(), 
				tempImage.getFishId(), 
				cmt.replaceAll(" ", "%20"), 
				lat,
				lng,
				adr.replaceAll(" ", "%20"), 
				tempImage.isCategory() ? 1 : 0);
		
		return UploadImageHelper(context, bitmap, url);
	}
	
	public static String UploadThreadImage(Context context, Bitmap bitmap, String page, int thread_id, String thread_title) throws ClientProtocolException, IOException {
		Actor user = ArinContext.getUser();
		String url = String.format(Locale.getDefault(), context.getString(R.string.arin_host) + "?page=%s&thread_id=%d&user_id=%d&thread_title=%s", page, thread_id, user.getId(), thread_title.replaceAll(" ", "%20"));
		return UploadImageHelper(context, bitmap, url);
	}
	
	private static String UploadImageHelper(Context context, Bitmap bitmap, String url) throws ClientProtocolException, IOException {
		if (bitmap != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, bos);
			byte[] data = bos.toByteArray();
			String fileName = "image";
			ByteArrayBody bab = new ByteArrayBody(data, fileName);
			
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			reqEntity.addPart("uploadedfile", bab);
			
			Pair<String, Integer> pair = httpRequestHelper(url, reqEntity);
			return pair.first;
		}
		return null;
	}
	
	private static Pair<String, Integer> httpRequestHelper(String url, HttpEntity reqEntity) {
		try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = TIMEOUT_SECONDS * 1000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = TIMEOUT_SECONDS * 1000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			
			HttpPost httppost = new HttpPost(url);
			httppost.setEntity(reqEntity);
			
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpResponse entity = client.execute(httppost);
			int responseCode = entity.getStatusLine().getStatusCode();	
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			entity.getEntity().writeTo(baos);
			byte[] content = baos.toByteArray();
			String responseVal = Common.unzipString(content);

			//Log.d("IMAGE SAVE", responseVal);
			
			return new Pair<String, Integer>(responseVal, responseCode);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return getErrorReturn();
	}
	
	private static Pair<String, Integer> getErrorReturn() {
		return new Pair<String, Integer>(null, 0);
	}
}

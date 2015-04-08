package async.thread;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import widget.Popup;
import http.HttpTask;
import http.Network;
import activity.AllThreadsScreen;
import android.app.ProgressDialog;
import com.arin.R;
import android.os.AsyncTask;
import android.util.Pair;
import arin.ArinContext;

public class Async_get_threads extends AsyncTask<String, Void, String> implements HttpTask {

	private AllThreadsScreen context;
	private Boolean mine;
	private int page;
	private String text;
	private Boolean forSearch;
	private Boolean fromNavButton;
	private int page_size;
	private ProgressDialog progressDialog;
	private final String WEBFILE = "thread/getthreads.php";
	
	public Async_get_threads(AllThreadsScreen context, Boolean mine, int page, String text, Boolean forSearch, Boolean fromNavButton, int page_size) {
		this.context = context;
		this.mine = mine;
		this.page = page;
		this.text = text;
		this.forSearch = forSearch;
		this.fromNavButton = fromNavButton;
		this.page_size = page_size;
		
		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
    	progressDialog.setMessage(context.getString(forSearch ? R.string.searching : R.string.loading));
    	progressDialog.setCancelable(false);
    	progressDialog.show();
    	
    	execute();
	}
	
	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("user_id", Integer.toString(ArinContext.getUser().getId(), 10)));
		urlValues.add(new BasicNameValuePair("mine", mine ? "1" : "0"));
		urlValues.add(new BasicNameValuePair("pagenum", Integer.toString(page, 10)));
		urlValues.add(new BasicNameValuePair("text", forSearch ? text : ""));
		urlValues.add(new BasicNameValuePair("page_size", Integer.toString(page_size, 10)));
		
		Pair<String, Integer> pair = Network.GetHTTPResponse(context.getString(R.string.arin_host), urlValues);
		
		return pair.first;
	}

	@Override
	protected void onPostExecute(String response) {
		if (response!=null) {
			try {
				JSONObject object = new JSONObject(response);
				int code = object.getInt(CODE);
				
				if (code==200) {
					String result = object.getString(RESPONSE);
					worked(result);
				} else {
					Popup.ShowErrorMessage(context, object.getString(RESPONSE), true);
				}
			} catch (JSONException e) {
				Popup.ShowErrorMessage(context, R.string.unexpected_error, true);
			}
		} else {
			Popup.ShowErrorMessage(context, R.string.server_error, true);
		}
		
		progressDialog.dismiss();
	}
	
	public void worked(String result) {
		context.threadCallbackSuccess(result, mine, page, text, forSearch, fromNavButton);
	}
}

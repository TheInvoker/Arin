package async.location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import location.Place;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import common.Common;

import widget.Popup;
import fish.Species;
import http.HttpTask;
import http.Network;
import activity.LocationScreen;
import activity.SpeciesScreen;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import com.arin.R;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import arin.ArinContext;

/*
 * This is a async task that edits locations
 */

public class Async_edit_location extends AsyncTask<String, Void, String> implements HttpTask {
	
	private LocationScreen context;
	private ProgressDialog progressDialog;
	
	private String address;
	private String comment;
	private double latitude;
	private double longitude;
	private Boolean isOk = false;
	
	private Species specie;
	private Place location;
	
	private final int MAX_ADDRESS_RESULTS = 10;
	private final String WEBFILE = "location/editlocation.php";
	
	public Async_edit_location(LocationScreen context, Species specie, Place location) {
		this.context = context;
		this.location = location;
		this.specie = specie;
		
		handleEditAdd();
	}
	
    @SuppressLint("InflateParams")
	private void handleEditAdd() {
		LayoutInflater inflater = context.getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_edit_location, null);
		
		final AlertDialog d = new AlertDialog.Builder(context)
        .setView(dialoglayout)
        .setTitle(location==null ? R.string.add : R.string.edit)
        .setPositiveButton(R.string.save, null)
        .setNegativeButton(R.string.cancel, null)
        .create();

		final EditText addressET = (EditText)dialoglayout.findViewById(R.id.address);
		final EditText commentET = (EditText)dialoglayout.findViewById(R.id.comment);
		final Button verifyButton = (Button)dialoglayout.findViewById(R.id.verify);
		
		if (location != null) {
			addressET.setText(location.getAddress());
			commentET.setText(location.getComment());
		}
		
		addressET.addTextChangedListener(new TextWatcher(){
			@Override
			public void afterTextChanged(Editable arg0) {}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				isOk = false;
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
		});
		
		verifyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String addressStr = addressET.getText().toString();
            	Geocoder coder = new Geocoder(context);
            	List<Address> addresses = GetAddressesFromStringAddress(context, coder, addressStr);
            	if (addresses != null) {
            		if (addresses.size() == 0) {
            			Toast.makeText(context, R.string.no_address, Toast.LENGTH_LONG).show();
            		} else {
	            		String[] items = new String[addresses.size()];

	        		    List<String> itemList = new ArrayList<String>();
	            		for(Address address : addresses) {
	            			String addressText = Common.GetAddressFromAddress(address);
	            			itemList.add(addressText);
	        		    }
	            		items = itemList.toArray(items);
	        		    
	        		    HandleOptions(addressET, items, addresses);
            		}
            	} else {
            		Popup.ShowErrorMessage(context, R.string.restart_error, false);
            	}
			}
		});
		
		d.setOnShowListener(new DialogInterface.OnShowListener() {
		    @Override
		    public void onShow(DialogInterface dialog) {
		        Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
		        b.setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View view) {
		            	if (isOk) {
			            	address = addressET.getText().toString();
			            	comment = commentET.getText().toString();
			 
			        		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
			            	progressDialog.setMessage(context.getString(R.string.saving));
			            	progressDialog.setCancelable(false);
			            	progressDialog.show();
			        		
			            	execute();
			            	d.dismiss();
		            	} else {
		            		Popup.ShowErrorMessage(context, R.string.need_verification, false);
		            	}
		            }
		        });
	        }
		});
		
		d.show();
    }
	
    private void HandleOptions(final EditText addressET, final String[] items, final List<Address> addresses) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(context);
	    builder.setTitle(R.string.select_location);

	    builder.setItems(items, new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int which) {
	    		addressET.setText(items[which]);
	    		
	    		Address addressObj = addresses.get(which);
	    		latitude = addressObj.getLatitude();
	    		longitude = addressObj.getLongitude();
	    		
	    		isOk = true;
	    	}
	    });
	    
	    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface arg0, int arg1) {
	        }
	    });

	    builder.create();
	    builder.show();
    }
    
    private List<Address> GetAddressesFromStringAddress(Context context, Geocoder coder, String strAddress) {
    	try {
    	    return coder.getFromLocationName(strAddress, MAX_ADDRESS_RESULTS);
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	} catch (IllegalArgumentException ex) {
    		ex.printStackTrace();
    	}
    	return null;
    }
    
	@Override
	protected String doInBackground(String... arg0) {
		List<NameValuePair> urlValues = new ArrayList<NameValuePair>();
		
		urlValues.add(new BasicNameValuePair(PAGE, WEBFILE));
		urlValues.add(new BasicNameValuePair("id", location==null ? "0" : Integer.toString(location.getId(), 10)));
		urlValues.add(new BasicNameValuePair("species_id", Integer.toString(specie.getId(), 10)));
		urlValues.add(new BasicNameValuePair("address", address));
		urlValues.add(new BasicNameValuePair("comment", comment));
		urlValues.add(new BasicNameValuePair("latitude", Double.toString(latitude)));
		urlValues.add(new BasicNameValuePair("longitude", Double.toString(longitude)));
		urlValues.add(new BasicNameValuePair("user_id", Integer.toString(ArinContext.getUser().getId(), 10)));
		
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
					JSONObject result = object.getJSONObject(RESPONSE);
					int location_id = result.getInt("location_id");
					worked(location_id);
				} else {
					Popup.ShowErrorMessage(context, object.getString(RESPONSE), false);
				}
			} catch (JSONException e) {
				Popup.ShowErrorMessage(context, R.string.unexpected_error, false);
			}
		} else {
			Popup.ShowErrorMessage(context, R.string.server_error, false);
		}
		progressDialog.dismiss();
	}
	
	private void worked(int location_id) {
		// created a new location
		if (location == null) {
			Place location = new Place(location_id, address, latitude, longitude, comment, true, ArinContext.getUser().getId(), ArinContext.getUser().getRemainingBanDays());
			specie.addLocation(location);
			context.addPlace(location);
		} 
		// edited an existing location
		else {
			location.setAddress(address);
			location.setComment(comment);
			location.setLatitude(latitude);
			location.setLongitude(longitude);

			context.reloadPlace(location);
		}
		
		SpeciesScreen.locationChanged = true;
	}
}

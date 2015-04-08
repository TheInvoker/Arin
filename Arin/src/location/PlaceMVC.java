package location;

import fish.Species;
import http.Network;
import user.Actor;
import widget.Popup;
import common.Views;
import activity.LocationScreen;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import com.arin.R;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import arin.ArinContext;
import async.location.Async_delete_location;
import async.location.Async_edit_location;
import async.location.Async_set_location_approved;
import async.user.Async_ban_user;

public class PlaceMVC {
	
	private LocationScreen context;
	private LinearLayout layout;
	private SparseArray<LinearLayout> nodesMap = new SparseArray<LinearLayout>();
	
	public PlaceMVC(LocationScreen context) {
		this.context = context;
		this.layout = (LinearLayout) context.findViewById(R.id.locationcontainer);
	}

	public void setUpScreen(Species selectedfish) {
		for(Place place : selectedfish.getLocations()) {
			addLocation(selectedfish, place);
		}
	}
	
	public void addLocation(final Species selectedfish, final Place location) {
		Boolean isFirst = layout.getChildCount() == 0;
		
		LinearLayout content = Views.getContainer(context, true, 10);
		nodesMap.put(location.getId(), content);
		setPlaceApproved(location);
		
		TextView addressTV = Views.getText(context, location.getAddress());
		TextView commentTV = Views.getCommentText(context, location.getComment());
		commentTV.setTextAppearance(context, R.style.comment);
		
		content.addView(addressTV);
		content.addView(commentTV);
		
		if (ArinContext.getUser().canManageLocation()) {
			content.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					locationClick(selectedfish, location);
				}
			});
		}
		
		// add divisor
		if (!isFirst) {
			layout.addView(Views.getHorizontalLine(context));
		}
		
		layout.addView(content);
	}
	
	public void reloadPlace(Place location) {
		LinearLayout node = nodesMap.get(location.getId());
		
		TextView addressTV = (TextView) node.getChildAt(0);
		TextView commentTV = (TextView) node.getChildAt(1);
		
		addressTV.setText(location.getAddress());
		commentTV.setText(location.getComment());
	}
	
	public void removePlace(Place location) {
		LinearLayout node = nodesMap.get(location.getId());
		layout.removeView(node);
	}
	
	public void setPlaceApproved(Place location) {
		LinearLayout node = nodesMap.get(location.getId());
		Boolean isApproved = location.isApproved();
		node.setBackgroundResource(isApproved ? R.drawable.location_button_selector : R.drawable.location_button_un_selector);
	}
	
	@SuppressLint("InflateParams")
	private void locationClick(final Species selectedfish, final Place place) {
		LayoutInflater inflater = context.getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_place, null);
		
		final AlertDialog d = new AlertDialog.Builder(context)
        .setView(dialoglayout)
        .setTitle(R.string.settings)
        .setNegativeButton(R.string.cancel, null)
        .setNeutralButton(R.string.delete, null)
        .setPositiveButton(R.string.edit, null)
        .create();

	    CheckBox checkBox = (CheckBox)dialoglayout.findViewById(R.id.checkBox);
	    checkBox.setChecked(place.isApproved());
		
	    checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (Network.isNetworkAvailable(context)) {
					new Async_set_location_approved(context, place);
					d.dismiss();
				} else {
					Popup.ShowErrorMessage(context, R.string.no_internet, false);
				}
			}
		});
	    
	    Button buttonBan = (Button)dialoglayout.findViewById(R.id.ban);
	    buttonBan.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Actor user = ArinContext.getUser();
				if (user.isBanned()) {
					Toast.makeText(context, context.getString(R.string.your_banned), Toast.LENGTH_SHORT).show();
				} else {
					new Async_ban_user(context, place);
				}
			}
	    });
	    
	    
		d.setOnShowListener(new DialogInterface.OnShowListener() {
		    @Override
		    public void onShow(DialogInterface dialog) {
		        Button b = d.getButton(AlertDialog.BUTTON_NEUTRAL);
		        b.setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View view) {
						if (Network.isNetworkAvailable(context)) {
							new Async_delete_location(context, selectedfish, place);
							d.dismiss();
						} else {
							Popup.ShowErrorMessage(context, R.string.no_internet, false);
						}
		            }
		        });
		        
		        b = d.getButton(AlertDialog.BUTTON_POSITIVE);
		        b.setOnClickListener(new View.OnClickListener() {
		            @Override
		            public void onClick(View view) {
						if (Network.isNetworkAvailable(context)) {
							new Async_edit_location(context, selectedfish, place);
							d.dismiss();
						} else {
							Popup.ShowErrorMessage(context, R.string.no_internet, false);
						}
		            }
		        });
	        }
		});
		
		d.show();
	}
}

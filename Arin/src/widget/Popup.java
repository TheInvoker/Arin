package widget;

import android.app.Activity;
import android.app.AlertDialog;
import com.arin.R;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.view.ContextThemeWrapper;

/*
 * This handles showing messages as dialogs
 */

public final class Popup {
	
	public static void ShowErrorMessage(Activity context, String message, Boolean endActivity) {
		ShowMessage(context, R.string.error_title, message, R.drawable.ic_action_error, endActivity);
	}
	
	public static void ShowErrorMessage(Activity context, int message, Boolean endActivity) {
		ShowMessage(context, R.string.error_title, context.getString(message), R.drawable.ic_action_error ,endActivity);
	}
	
	public static void ShowWarningMessage(Activity context, String message, Boolean endActivity) {
		ShowMessage(context, R.string.warning_title, message, R.drawable.ic_action_error, endActivity);
	}
	
	public static void ShowWarningMessage(Activity context, int message, Boolean endActivity) {
		ShowMessage(context, R.string.warning_title, context.getString(message), R.drawable.ic_action_error, endActivity);
	}
	
	public static void ShowString(Activity context, int title, String message, Boolean endActivity) {
		ShowMessage(context, title, message, -1, endActivity);
	}

	public static void ShowString(Activity context, int title, int message, Boolean endActivity) {
		ShowMessage(context, title, context.getString(message), -1, endActivity);
	}
	
	private static void ShowMessage(final Activity context, int title, String message, int iconID, Boolean endActivity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo_Dialog))
	    .setTitle(title)
	    .setMessage(message)
	    .setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        }
	    });
		
		if (iconID != -1) {
			builder.setIcon(R.drawable.ic_action_error);
		}

		AlertDialog alert = builder.create();
		
		if (endActivity) {
			alert.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					context.finish();
				}
			});
			alert.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					context.finish();
				}
			});
		}
		
		alert.show();
	}
}

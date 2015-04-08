package common;

import android.app.Activity;
import com.arin.R;

public class Transition {
	
	public static void TransitionForward(Activity activity) {
		activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}
	
	public static void TransitionBackward(Activity activity) {
		activity.overridePendingTransition(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
	}
}

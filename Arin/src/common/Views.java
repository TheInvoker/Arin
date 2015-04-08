package common;

import com.arin.R;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class Views {
	
	public static View getHorizontalLine(Context context) {
		View v = new View(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 1);
		params.setMargins(20, 0, 20, 0);
		v.setLayoutParams(params);
		v.setBackgroundColor(Color.parseColor("#88ffffff"));
		return v;
	}
	
	public static LinearLayout getContainer(Context context, Boolean vertical, int padding) {
		LinearLayout content = getContainerHelper(context, vertical, padding);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		content.setLayoutParams(params);
		return content;
	}
	
	public static LinearLayout getInnerContainer(Context context, Boolean vertical, int padding) {
		LinearLayout content = getContainerHelper(context, vertical, padding);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
		content.setLayoutParams(params);
		return content;
	}
	
	public static TextView getPreviewText(Context context, String text, int maxLines) {
		TextView tv = getText(context, text);
		return getPreviewTextHelper(tv, maxLines);
	}
	
	public static TextView getCommentPreviewText(Context context, String text, int maxLines) {
		TextView tv = getCommentText(context, text);
		return getPreviewTextHelper(tv, maxLines);
	}
	
	public static TextView getText(Context context, String text) {
		return getTextHelper(context, R.style.regular_white, text);
	}
	
	public static TextView getCommentText(Context context, String text) {
		return getTextHelper(context, R.style.comment, text);
	}
	
	public static TextView getDateText(Context context, String text) {
		return getTextHelper(context, R.style.date, text);
	}
	
	public static CheckBox getCheckBox(Context context, int text, Boolean state) {
		CheckBox approvedCB = new CheckBox(context);
		approvedCB.setText(text);
		LinearLayout.LayoutParams buttonParam = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
		buttonParam.setMargins(10, 0, 10, 0);
		approvedCB.setLayoutParams(buttonParam);
		approvedCB.setChecked(state);
		return approvedCB;
	}
	
	public static ImageButton getImageButton(Context context, int bgID) {
		ImageButton editButton = new ImageButton(context);
		LinearLayout.LayoutParams buttonParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		buttonParam.setMargins(10, 0, 10, 0);
		editButton.setLayoutParams(buttonParam);
		editButton.setBackgroundResource(bgID);
		return editButton;
	}
	
	public static void makeTextViewHyperlink(TextView tv) {
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		ssb.append(tv.getText());
		ssb.setSpan(new URLSpan("#"), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(ssb, TextView.BufferType.SPANNABLE);
	} 
		
	
	
	
	private static TextView getPreviewTextHelper(TextView tv, int maxLines) {
		tv.setSingleLine(false);
		tv.setEllipsize(TextUtils.TruncateAt.END);
		tv.setMaxLines(maxLines);
		return tv;
	}
	
	private static TextView getTextHelper(Context context, int style, String text) {
		TextView nametv = new TextView(context);
		nametv.setText(text);
		nametv.setTextAppearance(context, style);
		LinearLayout.LayoutParams textparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		textparams.setMargins(5, 5, 5, 5);
		nametv.setLayoutParams(textparams);
		return nametv;
	}
	
	private static LinearLayout getContainerHelper(Context context, Boolean vertical, int padding) {
		LinearLayout content = new LinearLayout(context);
		content.setOrientation(vertical ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
		content.setPadding(padding, padding, padding, padding);
		return content;
	}
}

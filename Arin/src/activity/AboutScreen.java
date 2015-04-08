package activity;

import android.app.Activity;
import com.arin.R;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_screen);
		
		
		// activates the action bar
		getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        

        TextView about = (TextView)findViewById(R.id.about);
        about.setText(Html.fromHtml(getCompleteAboutString()));
        about.setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about_screen, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch(id) {
	        case android.R.id.home:
	        	onBackPressed();
	            return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private String getCompleteAboutString() {
		PackageInfo pInfo;
		String version = "???";
		
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			version = pInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		 
        String remainingText = "<br/><br/>" + getString(R.string.websites) + "<br/><a href=\"" + getString(R.string.arin_link) + "\">" + getString(R.string.app_name) + "</a>";
        remainingText += "<br/><br/>" + getString(R.string.copyright);
        
        String credits = getString(R.string.about).replace("___", version);
        String fullabout = credits + remainingText;
        
        return fullabout;
	}
}

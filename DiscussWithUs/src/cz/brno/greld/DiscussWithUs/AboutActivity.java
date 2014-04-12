package cz.brno.greld.DiscussWithUs;

import android.app.Activity;

import android.os.Bundle;

/**
 *	Activity which display some information about this application.
 * @author Jan Kucera
 *
 */
public class AboutActivity extends Activity {
	private MyApplication app;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.about);
	        app = ((MyApplication)getApplicationContext());
	        
	    }
	 
		@Override
		protected void onResume() {
			super.onResume();
			app.setOnForeground(MyApplication.ABOUT);
		}
		
		@Override
		protected void onPause() {
			super.onPause();
			app.setOnForeground(MyApplication.NOTHING);
		}
		
		@Override
		protected void onStart() {
			super.onStart();
			app.plusNumberOfDisplayActivities();
		}
		
		@Override
		protected void onStop() {
			super.onStop();
			app.minusNumberOfDisplayActivities();
		}
}

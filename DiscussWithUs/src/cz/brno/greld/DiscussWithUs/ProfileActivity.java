package cz.brno.greld.DiscussWithUs;

import android.app.Activity;

import android.os.Bundle;
import android.widget.TextView;

/**
 *	Activity which display some information about logged user.
 * @author Jan Kucera
 *
 */
public class ProfileActivity extends Activity {

    private MyApplication app;
    
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.user_profile);
	        app = ((MyApplication)getApplicationContext());
	        
	        TextView t = (TextView) findViewById(R.id.nickname);
	        
	        t.setText(app.getUser().getNickname());
	        
	    }
	 
		@Override
		protected void onResume() {
			super.onResume();
			app.setOnForeground(MyApplication.PROFILE);
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
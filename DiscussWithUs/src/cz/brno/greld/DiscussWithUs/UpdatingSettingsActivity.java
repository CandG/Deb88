package cz.brno.greld.DiscussWithUs;


import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class UpdatingSettingsActivity extends Activity implements OnItemSelectedListener {

    private MyApplication app;
    private User user;
    private ArrayList<Integer> options = new ArrayList<Integer>();
    private int activeOption;
    private int inactiveOption;
    private long idOfActiveSpinner;
    private long idOfInactiveSpinner;
    
    
	private static final int OK_MSG = 1;
	private static final int KO_MSG = 2;
	private static final int ConnectivityExeption_MSG = 101;
	private static final int MistakeInJSONException_MSG = 102;
	private static final int SaveExeption_MSG = 103;
    
    private Handler mHandler = new Handler() {
        @Override
		public void handleMessage(Message msg) {
        	switch(msg.what)
        	{ 
	          	case OK_MSG:
					Toast.makeText(UpdatingSettingsActivity.this, "Nastavení bylo uloženo.", Toast.LENGTH_SHORT).show();
					finish();
		            break;	
		            
	          	case KO_MSG:
					Toast.makeText(UpdatingSettingsActivity.this, "Nastavení se nepodaøilo uložit.", Toast.LENGTH_SHORT).show();
					break;
	          	case ConnectivityExeption_MSG:
					Toast.makeText(UpdatingSettingsActivity.this, "Nastal problém s pøipojením k Internetu.", Toast.LENGTH_SHORT).show();
					break;
				case MistakeInJSONException_MSG:
					Toast.makeText(UpdatingSettingsActivity.this, "Nastal problém v parsování dat ze serveru.", Toast.LENGTH_SHORT).show();
					break;
				case SaveExeption_MSG:
					Toast.makeText(UpdatingSettingsActivity.this, "Nastal problém s uložením dat na server.", Toast.LENGTH_SHORT).show();
					break;
        	}
        }
	};


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.updating_settings);
        
        app = ((MyApplication)getApplicationContext());
        user  = app.getUser();
        

        options.add(new Integer(0));
        options.add(new Integer(5));
        options.add(new Integer(10));
        options.add(new Integer(20));
        options.add(new Integer(30));
        options.add(new Integer(45));
        options.add(new Integer(60));
        options.add(new Integer(2 * 60));
        options.add(new Integer(5 * 60));
        options.add(new Integer(10 * 60));
        options.add(new Integer(20 * 60));
        options.add(new Integer(30 * 60));
        options.add(new Integer(60 * 60));
        options.add(new Integer(2 * 60 * 60));
        options.add(new Integer(3 * 60 * 60));
        options.add(new Integer(5 * 60 * 60));
        options.add(new Integer(8 * 60 * 60));
        
        activeOption = user.getUserSettings().getUpdateFrequencyIfActive();
        inactiveOption = user.getUserSettings().getUpdateFrequencyIfInactive();
        
        Spinner activeSpinner = (Spinner) findViewById(R.id.activeSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.updating_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
    	idOfActiveSpinner = activeSpinner.getId();
    	activeSpinner.setAdapter(adapter);
    	activeSpinner.setSelection(options.indexOf(new Integer(activeOption)));
    	activeSpinner.setOnItemSelectedListener(this);
    	
    	Spinner inactiveSpinner = (Spinner) findViewById(R.id.inactiveSpinner);

        adapter = ArrayAdapter.createFromResource(this,
                R.array.updating_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
    	idOfInactiveSpinner = inactiveSpinner.getId();
    	inactiveSpinner.setAdapter(adapter);
    	inactiveSpinner.setSelection(options.indexOf(new Integer(inactiveOption)));
    	inactiveSpinner.setOnItemSelectedListener(this);

        Button create = (Button) findViewById(R.id.button1);
        create.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				Runnable loadHeader = new Runnable(){
                    public void run() {	
                    	try {
                    		int backUpUpdateFrequencyIfActive = user.getUserSettings().getUpdateFrequencyIfActive();
                    		int backUpdateFrequencyIfInactive = user.getUserSettings().getUpdateFrequencyIfInactive();
        					user.getUserSettings().setUpdateFrequencyIfActive(activeOption);
        					user.getUserSettings().setUpdateFrequencyIfInactive(inactiveOption);
        					boolean result = user.saveUserSettings();
        					if (result){
        						
        						if (user.getUserSettings().getUpdateFrequencyIfActive() > 0 && app.updatesResaverCounter == null)
	    						{
	    							Runnable checkingForUpdates = new Runnable(){
	    					            public void run() {	
	    					            	Looper.prepare();
	    					            	app.updatesResaverCounter = new UpdatesResaverCounter(10,10, UpdatingSettingsActivity.this);
	    					            	app.updatesResaverCounter.start();
	    									Looper.loop();
	    					            }
	    					        };
	    					        app.threadUpdating =  new Thread(null, checkingForUpdates, "checkingForUpdates");
	    					        app.threadUpdating.start();
	    						}
        						
        						mHandler.sendEmptyMessage(OK_MSG);
        					}
        					else {
        						user.getUserSettings().setUpdateFrequencyIfActive(backUpUpdateFrequencyIfActive);
            					user.getUserSettings().setUpdateFrequencyIfInactive(backUpdateFrequencyIfInactive);
        						mHandler.sendEmptyMessage(KO_MSG);
        					}
	                        
						} catch (ConnectivityExeption e) {
							mHandler.sendEmptyMessage(ConnectivityExeption_MSG);
							e.printStackTrace();
						} 

                    }

                    
                };
                (new Thread(null, loadHeader, "loadHeader")).start();
				
					
				
			}
		});
        
	}
	
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
    	if (parent.getId() == idOfActiveSpinner){
    		activeOption = options.get(pos).intValue();
    	} 
    	else if (parent.getId() == idOfInactiveSpinner) {
    		inactiveOption = options.get(pos).intValue();
    	}
    }


	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		app.setOnForeground(MyApplication.UPDATING_SETTINGS);
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

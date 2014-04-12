package cz.brno.greld.DiscussWithUs;


import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class NewTopicActivity extends Activity implements OnItemSelectedListener {

    private MyApplication app;
    private User user;
    private int lifeTime;
    private int range;
    private Category category;
    private long idOfLifeTimeSpinner;
    private long idOfRangeSpinner;
    private long idOfCategorySpinner;
    private ArrayList<Integer> lifeTimesOptions = new ArrayList<Integer>(); 
    private ArrayList<Integer> rangeOptions = new ArrayList<Integer>(); 
    

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
					Toast.makeText(NewTopicActivity.this, "Téma bylo vytvoøeno.", Toast.LENGTH_SHORT).show();
					finish();
		            break;	
		            
	          	case KO_MSG:
					Toast.makeText(NewTopicActivity.this, "Téma se nepodaøilo vytvoøit.", Toast.LENGTH_SHORT).show();
					break;
	          	case ConnectivityExeption_MSG:
					Toast.makeText(NewTopicActivity.this, "Nastal problém s pøipojením k Internetu.", Toast.LENGTH_SHORT).show();
					break;
				case MistakeInJSONException_MSG:
					Toast.makeText(NewTopicActivity.this, "Nastal problém v parsování dat ze serveru.", Toast.LENGTH_SHORT).show();
					break;
				case SaveExeption_MSG:
					Toast.makeText(NewTopicActivity.this, "Nastal problém s uložením dat na server.", Toast.LENGTH_SHORT).show();
					break;
        	}
        }
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.new_topic);
        
        app = ((MyApplication)getApplicationContext());
        user  = app.getUser();
        
        
        

        lifeTimesOptions.add(new Integer(5));
        lifeTimesOptions.add(new Integer(10));
        lifeTimesOptions.add(new Integer(20));
        lifeTimesOptions.add(new Integer(30));
        lifeTimesOptions.add(new Integer(45));
        lifeTimesOptions.add(new Integer(60));
        lifeTimesOptions.add(new Integer(2 * 60));
        lifeTimesOptions.add(new Integer(5 * 60));
        lifeTimesOptions.add(new Integer(12 * 60));
        lifeTimesOptions.add(new Integer(1 * 24 * 60));
        lifeTimesOptions.add(new Integer(2 * 24 * 60));
        lifeTimesOptions.add(new Integer(5 * 24 * 60));
        lifeTimesOptions.add(new Integer(7 * 24 * 60));
        lifeTimesOptions.add(new Integer(2 * 7 * 24 * 60));
        lifeTimesOptions.add(new Integer(30 * 24 * 60));
        lifeTimesOptions.add(new Integer(2 * 30 * 24 * 60));
        lifeTimesOptions.add(new Integer(5 * 30 * 24 * 60));
        lifeTimesOptions.add(new Integer(365 * 24 * 60));
        lifeTimesOptions.add(new Integer(2 * 365 * 24 * 60));
        lifeTimesOptions.add(new Integer(5 * 365 * 24 * 60));
        lifeTimesOptions.add(new Integer(100 * 365 * 24 * 60));

        lifeTime = lifeTimesOptions.get(0);
        

        rangeOptions.add(new Integer(100));
        rangeOptions.add(new Integer(200));
        rangeOptions.add(new Integer(500));
        rangeOptions.add(new Integer(1000));
        rangeOptions.add(new Integer(2 * 1000));
        rangeOptions.add(new Integer(5 * 1000));
        rangeOptions.add(new Integer(10 * 1000));
        rangeOptions.add(new Integer(50 * 1000));
        rangeOptions.add(new Integer(100 * 1000));
        
        range = rangeOptions.get(0);

        if (user.getCategories().size() > 0)
        	category = user.getCategories().get(0);
        else
        	category = null;
        
        Spinner lifeTimeSpinner = (Spinner) findViewById(R.id.lifeTime);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.lifetime_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
    	idOfLifeTimeSpinner = lifeTimeSpinner.getId();
    	lifeTimeSpinner.setAdapter(adapter);
    	lifeTimeSpinner.setOnItemSelectedListener(this);
        
        Spinner rangeSpinner = (Spinner) findViewById(R.id.range);

        adapter = ArrayAdapter.createFromResource(this,
                R.array.range_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    	idOfRangeSpinner = rangeSpinner.getId();
    	rangeSpinner.setAdapter(adapter);
    	rangeSpinner.setOnItemSelectedListener(this);
    	
    	Spinner categorySpinner = (Spinner) findViewById(R.id.category);

        adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, user.getCategoriesStringArray());
        		
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

    	idOfCategorySpinner = categorySpinner.getId();
    	categorySpinner.setAdapter(adapter);
    	categorySpinner.setOnItemSelectedListener(this);
        
        
        Button create = (Button) findViewById(R.id.button1);
        create.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
					Runnable loadHeader = new Runnable(){
	                    public void run() {	
	                    	try {
	        					EditText text = (EditText) findViewById(R.id.text);
	        					boolean result = user.createTopic(text.getText().toString(), lifeTime, range, category);
	        					if (result)
	        						mHandler.sendEmptyMessage(OK_MSG);
	        					else
	        						mHandler.sendEmptyMessage(KO_MSG);
		                        
							} catch (ConnectivityExeption e) {
								mHandler.sendEmptyMessage(ConnectivityExeption_MSG);
								e.printStackTrace();
							} catch (MistakeInJSONException e) {
								mHandler.sendEmptyMessage(MistakeInJSONException_MSG);
								e.printStackTrace();
							} catch (SaveExeption e) {
								mHandler.sendEmptyMessage(SaveExeption_MSG);
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
    	if (parent.getId() == idOfLifeTimeSpinner){
    		lifeTime = lifeTimesOptions.get(pos);
    	} 
    	else if (parent.getId() == idOfRangeSpinner) {
    		range = rangeOptions.get(pos);
    	}
    	else if (parent.getId() == idOfCategorySpinner) {
    		category = user.getCategories().get(pos);
    	}
    }


	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		app.setOnForeground(MyApplication.NEW_TOPIC);
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

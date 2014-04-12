package cz.brno.greld.DiscussWithUs;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewReactionActivity extends Activity {

    private MyApplication app;
    private User user;
    private int idOfParent;
    private boolean parentIsFirst;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.new_reaction);
        
        app = ((MyApplication)getApplicationContext());
        user  = app.getUser();
        
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			idOfParent = extras.getInt("cz.brno.greld.discuss.idOfParent");
			parentIsFirst = extras.getBoolean("cz.brno.greld.discuss.parentIsFirst");
		}

        if (idOfParent == 0){
            setContentView(R.layout.error);
			TextView text = (TextView) findViewById(R.id.reason);
			text.setText("Chyba pøi pøenosu dat.");
			return;
        }

        Button create = (Button) findViewById(R.id.button1);
        create.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					
					EditText text = (EditText) findViewById(R.id.text);
					
					boolean result = user.createReactionFor(idOfParent, parentIsFirst, text.getText().toString());
					
					if (result) {
						CharSequence textToDisplay = "Reakce byla vytvoøena.";
						int duration = Toast.LENGTH_SHORT;
	
						Toast toast = Toast.makeText(NewReactionActivity.this, textToDisplay, duration);
						toast.show();
						finish();
					} else {
						CharSequence textToDisplay = "Reakci se nepodaøilo vytvoøit.";
						int duration = Toast.LENGTH_SHORT;
	
						Toast toast = Toast.makeText(NewReactionActivity.this, textToDisplay, duration);
						toast.show();
					}
					
				} catch (ConnectivityExeption e) {
					System.err.println("ConnectivityExeption: " + e.getMessage());
					CharSequence text = "Chyba v pøipojení k Internetu.";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(NewReactionActivity.this, text, duration);
					toast.show();
				} catch (MistakeInJSONException e) {
					System.err.println("MistakeInJSONException: " + e.getMessage());
					CharSequence text = "Chyba v pøipojení k Internetu.";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(NewReactionActivity.this, text, duration);
					toast.show();
				} catch (SaveExeption e) {
					System.err.println("SaveExeption: " + e.getMessage());
					CharSequence text = "Chyba v pøipojení k Internetu.";
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(NewReactionActivity.this, text, duration);
					toast.show();
				}
				
			}
		});
        
	}
	


	public void onNothingSelected(AdapterView<?> parent) {
		
	}
}

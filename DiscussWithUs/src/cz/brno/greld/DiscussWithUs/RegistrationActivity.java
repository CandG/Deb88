package cz.brno.greld.DiscussWithUs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity which allows player to register to the game
 * @author Jan Kucera
 *
 */
public class RegistrationActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        
        Bundle extras = getIntent().getExtras();
		if (extras == null) {
			return;
		}
		String mistake = extras.getString("mistake");
		if (mistake != null) {
			TextView text = (TextView) findViewById(R.id.registrationMistake);
			text.setText(mistake);
		}
	}

	public void onClick(View view) {
		Intent data = new Intent();
		EditText text = (EditText) findViewById(R.id.login);
		data.putExtra("login", text.getText().toString());
		text = (EditText) findViewById(R.id.password);
		data.putExtra("password", text.getText().toString());
		
		text = (EditText) findViewById(R.id.email);
		data.putExtra("email", text.getText().toString());
		setResult(RESULT_OK, data);
		finish();
	}

}

package cz.brno.greld.DiscussWithUs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Activity to login user
 * @author Jan Kucera
 *
 */
public class LoginActivity extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        Bundle extras = getIntent().getExtras();
		if (extras == null) {
			return;
		}
		String mistake = extras.getString("mistake");
		if (mistake != null) {
			TextView text = (TextView) findViewById(R.id.loginMistake);
			text.setText(mistake);
		}
	}

	public void onClick(View view) {
		Intent data = new Intent();
		EditText text = (EditText) findViewById(R.id.login);
		data.putExtra("login", text.getText().toString());
		text = (EditText) findViewById(R.id.password);
		data.putExtra("password", text.getText().toString());
		
		CheckBox checkbox = (CheckBox) findViewById(R.id.remember);
		data.putExtra("remember", checkbox.isChecked());
		setResult(RESULT_OK, data);
		finish();
	}
}

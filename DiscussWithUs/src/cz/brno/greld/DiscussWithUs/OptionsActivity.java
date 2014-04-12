package cz.brno.greld.DiscussWithUs;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Added options for player
 * @author Jan Kucera
 *
 */
public class OptionsActivity extends ListActivity {
	
	private String[] options;
	
	
	private ArrayAdapter<String> adapter;

    private MyApplication app;
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

      app = ((MyApplication)getApplicationContext());
	  
	  options = new String[] {
			  "Nastavení aktualizací", "O aplikaci"
			};
	  
	  adapter = new ArrayAdapter<String>(this, R.layout.option_item, options);
	  setListAdapter(adapter);

	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);
		
	  lv.setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				Intent intent = new Intent(OptionsActivity.this, UpdatingSettingsActivity.class);
				startActivity(intent);
				break;
			case 1:
				Intent intent2 = new Intent(OptionsActivity.this, AboutActivity.class);
				startActivity(intent2);
				break;
			default:
				return;
			}
		}
	  });
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		app.setOnForeground(MyApplication.OPTIONS);
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

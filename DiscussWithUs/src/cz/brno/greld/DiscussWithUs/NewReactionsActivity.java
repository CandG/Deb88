package cz.brno.greld.DiscussWithUs;

import java.util.ArrayList;


import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NewReactionsActivity  extends ListActivity {
	 
	private static final int ITEM_VIEW_TYPE_OLD_POST = 0;
	private static final int ITEM_VIEW_TYPE_NEW_POST = 1;
	private static final int ITEM_VIEW_TYPE_COUNT = 2;

	private User user;
	private MyApplication app;

	private ArrayList<Post> posts;
	private NewReactionsAdapter mAdapter;
	
	
	private static final int REACTION_WAS_SENT_MSG = 1;
	private static final int AFTER_REFRESH_MSG = 2;
	public static final int REFRESH_MSG = 3;
	private static final int AFTER_HEADER_LOAD_MSG = 4;
	
	private static final int ConnectivityExeption_MSG = 101;
	private static final int MistakeInJSONException_MSG = 102;
	private static final int SaveExeption_MSG = 103;
	private static final int BadIdException_MSG = 104;
    
    
	private Handler mHandler = new Handler() {
        @Override
		public void handleMessage(Message msg) {
        	switch(msg.what)
        	  { 
          	case AFTER_REFRESH_MSG:
                ((TextView) findViewById(android.R.id.empty)).setText(getString(R.string.no_posts));
                
          		mAdapter.notifyDataSetChanged();
          		
          		Runnable loadUpdates = new Runnable(){
                    public void run() {	
                    	UpdatesResaverCounter.update(NewReactionsActivity.this);
                    }
                };
                (new Thread(null, loadUpdates, "updateAfterDisplayNews")).start();
            break;	
            
          	case REFRESH_MSG:
          		refresh();
            break;	
          	
          	case ConnectivityExeption_MSG:
				Toast.makeText(getApplicationContext(), "Nastal problém s pøipojením k Internetu.", Toast.LENGTH_SHORT).show();
				break;
			case MistakeInJSONException_MSG:
				Toast.makeText(getApplicationContext(), "Nastal problém v parsování dat ze serveru.", Toast.LENGTH_SHORT).show();
				break;
			case SaveExeption_MSG:
				Toast.makeText(getApplicationContext(), "Nastal problém s uložením dat na server.", Toast.LENGTH_SHORT).show();
				break;
			case BadIdException_MSG:
				Toast.makeText(getApplicationContext(), "Nebyli nalezeny potøebné informace.", Toast.LENGTH_SHORT).show();
				break;
        	 
        	}
        }
	};

 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_posts);
		
        app = ((MyApplication) getApplicationContext());
		user = app.getUser();
		
		refresh();

		
		posts = new ArrayList<Post>();
		mAdapter = new NewReactionsAdapter();
		setListAdapter(mAdapter);
	}
 
	private class NewReactionsAdapter extends BaseAdapter {
		
		
		public int getCount() {
			return posts.size();
		}
 
		
		public Object getItem(int position) {
			return posts.get(position);
		}
 
		public long getItemId(int position) {
			return position;
		}
 
		@Override
		public int getViewTypeCount() {
			return ITEM_VIEW_TYPE_COUNT;
		}
 
		@Override
		public int getItemViewType(int position) {
			return (posts.get(position).isNewOne()) ? ITEM_VIEW_TYPE_NEW_POST
					: ITEM_VIEW_TYPE_OLD_POST;
		}
 
		@Override
		public boolean isEnabled(int position) {
			// both can be clicked
			return true;
		}
 
		
		public View getView(int position, View convertView, ViewGroup parent) {
 
			final int type = getItemViewType(position);
 
			// First, let's create a new convertView if needed. You can also
			// create a ViewHolder to speed up changes if you want ;)
			if (convertView == null) {
				convertView = LayoutInflater.from(NewReactionsActivity.this)
						.inflate(
								type == ITEM_VIEW_TYPE_OLD_POST ? R.layout.old_post_list_item
										: R.layout.new_post_list_item, parent,
								false);
			}
			Post post = ((Post) getItem(position));
			TextView tt = (TextView) convertView.findViewById(R.id.text);
            tt.setText(post.getText());
            tt = (TextView) convertView.findViewById(R.id.info);
            tt.setText(post.getDateString()+ " " + post.getAuthor().getNickname());
 
			return convertView;
		}
 
	}
	
	public void refresh() {
		
		TextView tt = (TextView) findViewById(android.R.id.empty);
        tt.setText("naèítám...");
        
		Runnable runnable = new Runnable(){
            public void run() {
				try {
					posts = user.loadNewReactions();
		        	
		      		mHandler.sendEmptyMessage(AFTER_REFRESH_MSG);
		      		
				} catch (ConnectivityExeption e) {
					mHandler.sendEmptyMessage(ConnectivityExeption_MSG);
				} catch (MistakeInJSONException e) {
					mHandler.sendEmptyMessage(MistakeInJSONException_MSG);
					e.printStackTrace();
				} catch (BadIdException e) {
					mHandler.sendEmptyMessage(BadIdException_MSG);
					e.printStackTrace();
				}
            }
        };
        Thread thread =  new Thread(null, runnable, "refreshingList");
        thread.start();	
		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Post post = posts.get(position);
		showDetail(post);
	}
	
	
	public void showDetail(Post post){
		Intent intent = new Intent();
    	intent.putExtra("cz.brno.greld.discuss.idOfParent", post.getId());
    	intent.putExtra("cz.brno.greld.discuss.parentIsFirst", post.isFirst());
    	
        intent.setClass(getApplicationContext(), PostsPreviewActivity.class);
        startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		app.setOnForeground(MyApplication.NEW_REACTIONS);
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
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.topics_menu, menu);
	    return true;
	}
	

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.newTopic:
	        	newTopic();
	            return true;
	        case R.id.mapView:
	        	//showMapView();
	            return true;
	        case R.id.update:
	        	Runnable loadUpdates = new Runnable(){
                    public void run() {	
                    	UpdatesResaverCounter.update(NewReactionsActivity.this);
                    }
                };
                (new Thread(null, loadUpdates, "updateAfterClickInMenu")).start();
	            return true;
	        case R.id.logout:
	        	logout();
	            return true;
	        case R.id.options:
	        	showOptions();
	            return true;
	        case R.id.profil:
	        	showProfile();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void newTopic(){
		Intent intent = new Intent(this, NewTopicActivity.class);
		
		startActivity(intent);
    }
	
	public void showOptions() {

		Intent intent = new Intent(this, OptionsActivity.class);
		
		startActivity(intent);
		
	}
	
	public void showProfile() {

		Intent intent = new Intent(this, ProfileActivity.class);
		
		startActivity(intent);
		
	}
	
	
	public void logout(){
    	SharedPreferences pref = getSharedPreferences(MainActivity.PREFS_NAME,MODE_PRIVATE);
    	pref.edit().clear().commit();
    	
    	if (app.updatesResaverCounter != null)
			app.updatesResaverCounter.cancel();
		String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) NewReactionsActivity.this.getSystemService(ns);
        nMgr.cancel(1);
        nMgr.cancel(2);
        app.setNumOfNewReactions(0);
        app.setNumOfNewTopics(0);
        
        user.logout();
           	
    	Intent intent = new Intent();
    	
        intent.setClass(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        NewReactionsActivity.this.finish();
    }
	
}
package cz.brno.greld.DiscussWithUs;

import java.util.ArrayList;

import android.app.ListActivity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PostsPreviewActivity extends ListActivity {
	

    private MyApplication app;
    private User user;
    private int idOfParent = 0;
    private Post parent;
    private boolean parentIsFirst = false;
    private PostsAdapter m_adapter;
    private ArrayList<Post> showingPosts;
    

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
          	case REACTION_WAS_SENT_MSG:
          			
          			EditText text = (EditText) findViewById(R.id.textEdit);
          			text.setText("");
          			Button sentButton = (Button) findViewById(R.id.sent);
          			sentButton.setClickable(true);
				
					CharSequence textToDisplay = "Reakce byla vytvoøena.";
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(PostsPreviewActivity.this, textToDisplay, duration);
					toast.show();
					refresh();
	            break;	
	            
          	case AFTER_REFRESH_MSG:
                ((TextView) findViewById(android.R.id.empty)).setText(getString(R.string.no_posts));
                
          		m_adapter.clear();
          		for (Post post : showingPosts) {
              		m_adapter.add(post);
				} 
        		m_adapter.notifyDataSetChanged();
        		
        		Runnable loadUpdates = new Runnable(){
                    public void run() {	
                    	UpdatesResaverCounter.update(PostsPreviewActivity.this);
                    }
                };
                (new Thread(null, loadUpdates, "updateAfterDisplayNews")).start();
            break;	
            
          	case REFRESH_MSG:
          		refresh();
            break;	
          	case AFTER_HEADER_LOAD_MSG:
            	LinearLayout header = (LinearLayout) findViewById(R.id.header);
	            TextView tt = (TextView) header.findViewById(R.id.text);
	            tt.setText(parent.getText());
	            tt = (TextView) header.findViewById(R.id.info);
	            tt.setText(parent.getDateString()+" " + parent.getAuthor().getNickname());
	            break;	    
          	case ConnectivityExeption_MSG:
				Toast.makeText(PostsPreviewActivity.this, "Nastal problém s pøipojením k Internetu.", Toast.LENGTH_SHORT).show();
				break;
			case MistakeInJSONException_MSG:
				Toast.makeText(PostsPreviewActivity.this, "Nastal problém v parsování dat ze serveru.", Toast.LENGTH_SHORT).show();
				break;
			case SaveExeption_MSG:
				Toast.makeText(PostsPreviewActivity.this, "Nastal problém s uložením dat na server.", Toast.LENGTH_SHORT).show();
				break;
			case BadIdException_MSG:
				Toast.makeText(PostsPreviewActivity.this, "Nebyli nalezeny potøebné informace.", Toast.LENGTH_SHORT).show();
				break;
        	 
        	}
        }
	};
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_posts);
        app = ((MyApplication)getApplicationContext());
        user  = app.getUser();
        
        if (!user.isLogin()){
        	logout();
        }
        
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			idOfParent = extras.getInt("cz.brno.greld.discuss.idOfParent");
			parentIsFirst = extras.getBoolean("cz.brno.greld.discuss.parentIsFirst");
		}
        
        	if (idOfParent != 0){
        		
    			LinearLayout header = (LinearLayout) findViewById(R.id.header);
    			header.setVisibility(View.VISIBLE);
        		
        		Runnable loadHeader = new Runnable(){
                    public void run() {	
                    	try {
							parent = user.loadPost(idOfParent, parentIsFirst);
							mHandler.sendEmptyMessage(AFTER_HEADER_LOAD_MSG);
	                        
						} catch (ConnectivityExeption e) {
							mHandler.sendEmptyMessage(ConnectivityExeption_MSG);
							e.printStackTrace();
						} catch (MistakeInJSONException e) {
							mHandler.sendEmptyMessage(MistakeInJSONException_MSG);
							e.printStackTrace();
						} catch (BadIdException e) {
							mHandler.sendEmptyMessage(BadIdException_MSG);
							e.printStackTrace();
						}

                    }

                    
                };
                (new Thread(null, loadHeader, "loadHeader")).start();
        		
        		
    			

    			LinearLayout footer = (LinearLayout) findViewById(R.id.footer);
    			footer.setVisibility(View.VISIBLE);
    			
    			Button sent = (Button) footer.findViewById(R.id.sent);
    			sent.setOnClickListener(new OnClickListener() {
    				public void onClick(View v) {

						final EditText text = (EditText) findViewById(R.id.textEdit);
						
						if (text.getText().toString().length() != 0){
    						v.setClickable(false);
    						
    						InputMethodManager imm = (InputMethodManager)getSystemService(
    							      Context.INPUT_METHOD_SERVICE);
    							imm.hideSoftInputFromWindow(text.getWindowToken(), 0);
    						
    						Runnable runnable = new Runnable(){
    		                    public void run() {
    		                    	boolean saved;
    								try {
    									saved = user.createReactionFor(idOfParent, parentIsFirst, text.getText().toString());
    		    						
    			    					if (saved){
    			    						mHandler.sendEmptyMessage(REACTION_WAS_SENT_MSG);
    			    					} else
    			    					{
    			    						mHandler.sendEmptyMessage(SaveExeption_MSG);
    			    					}
    								} catch (ConnectivityExeption e) {
    		    						mHandler.sendEmptyMessage(ConnectivityExeption_MSG);
    								} catch (MistakeInJSONException e) {
    		    						mHandler.sendEmptyMessage(MistakeInJSONException_MSG);
										e.printStackTrace();
									} catch (SaveExeption e) {
    		    						mHandler.sendEmptyMessage(SaveExeption_MSG);
										e.printStackTrace();
									}
    		                    }
    		                };
    		                Thread thread =  new Thread(null, runnable, "creatingReaction");
    		                thread.start();
						}	
    				}
    			});
    			
    			getListView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
    			getListView().setStackFromBottom(true);

        	}
        showingPosts = new ArrayList<Post>();
		this.m_adapter = new PostsAdapter(this, R.layout.row_posts, showingPosts);
        setListAdapter(this.m_adapter);
        
        
        registerForContextMenu(this.getListView());
        
               
        
	}
	
	
	
	
	
	public void refresh(){
		final Handler handler = mHandler;
		refresh(handler);
	}

	
	public void refresh(final Handler handler) {
		
		TextView tt = (TextView) findViewById(android.R.id.empty);
        tt.setText("naèítám...");
        
		Runnable runnable = new Runnable(){
            public void run() {
				try {
					if (idOfParent != 0)
		    			showingPosts = user.loadReactionsFor(idOfParent, parentIsFirst);
		        	else	
		        		showingPosts = user.loadFirstPosts();
		        	
		      		m_adapter.setPosts(showingPosts);
		      		
		      		handler.sendEmptyMessage(AFTER_REFRESH_MSG);
		      		
				} catch (ConnectivityExeption e) {
					handler.sendEmptyMessage(ConnectivityExeption_MSG);
				} catch (MistakeInJSONException e) {
					handler.sendEmptyMessage(MistakeInJSONException_MSG);
					e.printStackTrace();
				}
            }
        };
        Thread thread =  new Thread(null, runnable, "refreshingList");
        thread.start();	
		
	}






	private class PostsAdapter extends ArrayAdapter<Post> {

        private ArrayList<Post> posts;

        public PostsAdapter(Context context, int textViewResourceId, ArrayList<Post> posts) {
                super(context, textViewResourceId, posts);
                this.posts = posts;
        }
        
        public void setPosts(ArrayList<Post> posts){
        	this.posts = posts;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	
        		System.err.println("getView position: " + position);
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.row_posts, null);
                }
                final Post post = posts.get(position);
                if (post != null) {
                        TextView tt = (TextView) v.findViewById(R.id.toptext);
                        TextView btl = (TextView) v.findViewById(R.id.bottomtextLeft);
                        TextView btr = (TextView) v.findViewById(R.id.bottomtextRight);
                        LinearLayout ll = (LinearLayout) v.findViewById(R.id.postListRow);
                        if (tt != null) {
                              tt.setText(post.getText());                            
                        }
                        if(btl != null){
                        	btl.setText(post.getDateString()+" " + post.getAuthor().getNickname());
                        }
                        if(btr != null){
                        	btr.setText("Reakcí: " + post.getNumOfReactions());
                        }
                        if(ll != null){
                        	if (post.isNewOne())
                        		ll.setBackgroundColor(Color.rgb(87, 120, 191));
                        	else
                        		ll.setBackgroundColor(Color.rgb(7, 11, 44));
                        }
                            
                }
                return v;
        }
        
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Post post = showingPosts.get(position);
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
                    	UpdatesResaverCounter.update(PostsPreviewActivity.this);
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
        NotificationManager nMgr = (NotificationManager) PostsPreviewActivity.this.getSystemService(ns);
        nMgr.cancel(1);
        nMgr.cancel(2);
        app.setNumOfNewReactions(0);
        app.setNumOfNewTopics(0);
        
        user.logout();
           	
    	Intent intent = new Intent();
    	
        intent.setClass(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        PostsPreviewActivity.this.finish();
    }
	
	public void newTopic(){
		Intent intent = new Intent(this, NewTopicActivity.class);
		
		startActivity(intent);
    }
	
	public void newReaction(Post post){
		int idOfParent;
		boolean parentIsFirst;
		if (post == null){
			idOfParent = this.idOfParent;
			parentIsFirst = this.parentIsFirst;
		} else
		{
			idOfParent = post.getId();
			parentIsFirst = post.isFirst();
		}
		if (idOfParent != 0) {
			Intent intent = new Intent(this, NewReactionActivity.class);
	    	intent.putExtra("cz.brno.greld.discuss.idOfParent", idOfParent);
	    	intent.putExtra("cz.brno.greld.discuss.parentIsFirst", parentIsFirst);
			
			startActivity(intent);
		}
    }
	
	public void deletePost(int position){
		Post post = showingPosts.get(position);
		int idOfParent;
		boolean parentIsFirst;
		if (post == null){
			idOfParent = this.idOfParent;
			parentIsFirst = this.parentIsFirst;
		} else
		{
			idOfParent = post.getId();
			parentIsFirst = post.isFirst();
		}
		try {
        	if (idOfParent != 0){
    			boolean result = user.deletePost(idOfParent, parentIsFirst);
    			if (result) {
    				showingPosts.remove(position);
    				m_adapter.setPosts(showingPosts);
    				m_adapter.notifyDataSetChanged();
    				
					CharSequence textToDisplay = "Pøíspìvek byl vymazán.";
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(PostsPreviewActivity.this, textToDisplay, duration);
					toast.show();
				} else {
					CharSequence textToDisplay = "Pøíspìvek se nepodaøilo vymazat.";
					int duration = Toast.LENGTH_SHORT;

					Toast toast = Toast.makeText(PostsPreviewActivity.this, textToDisplay, duration);
					toast.show();
				}
        	}
		} catch (ConnectivityExeption e) {
			System.err.println("ConnectivityExeption: " + e.getMessage());
			((TextView) findViewById(android.R.id.empty)).setText(R.string.noNetwork);
			return;
		}   
	}
	
	@Override
	protected void onResume() {
		super.onResume();
        app.postsPreviewHandler = mHandler;
		app.setOnForeground((idOfParent == 0?MyApplication.TOPICS:MyApplication.POST_REACTIONS));
		
        if (user.getUserSettings().getUpdateFrequencyIfActive() > 0 && app.updatesResaverCounter == null)
		{
			Runnable checkingForUpdates = new Runnable(){
	            public void run() {	
	            	Looper.prepare();
	            	app.updatesResaverCounter = new UpdatesResaverCounter(10,10, PostsPreviewActivity.this);
	            	app.updatesResaverCounter.start();
					Looper.loop();
	            }
	        };
	        app.threadUpdating =  new Thread(null, checkingForUpdates, "checkingForUpdates");
	        app.threadUpdating.start();
		}
		
		refresh();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		app.setOnForeground(MyApplication.NOTHING);
		
		if (user.getUserSettings().getUpdateFrequencyIfActive() > 0 && app.updatesResaverCounter == null)
		{
			Runnable checkingForUpdates = new Runnable(){
	            public void run() {	
	            	Looper.prepare();
	            	app.updatesResaverCounter = new UpdatesResaverCounter(10,10, PostsPreviewActivity.this);
	            	app.updatesResaverCounter.start();
					Looper.loop();
	            }
	        };
	        app.threadUpdating =  new Thread(null, checkingForUpdates, "checkingForUpdates");
	        app.threadUpdating.start();
		}
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
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	    super.onCreateContextMenu(menu, v, menuInfo);
	    MenuInflater inflater = getMenuInflater();

	    Post post = showingPosts.get((((AdapterContextMenuInfo)menuInfo).position));
	    if (post.getAuthor().getId() == user.getId() && post.getNumOfReactions() == 0)
		    inflater.inflate(R.menu.post_select_menu, menu);
	    else
	    	inflater.inflate(R.menu.post_select_menu_nodelete, menu);
	}
	

	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.detail:
	        	showDetail(showingPosts.get(info.position));
	            return true;
	        case R.id.reaction:
	        	newReaction(showingPosts.get(info.position));
	            return true;
	        case R.id.delete:
	            deletePost(info.position);
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
	
}

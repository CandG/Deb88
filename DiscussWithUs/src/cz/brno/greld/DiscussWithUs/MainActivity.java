package cz.brno.greld.DiscussWithUs;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.net.Uri;

/**
 * First decisions what to display for user. normally introduction
 * Called when the activity is first created.
 * @author Jan Kucera
 *
 */
public class MainActivity extends Activity {
	
	public static final String PREFS_NAME = "Login";
	public static final String PREF_USERNAME = "login";
	public static final String PREF_PASSWORD = "password";
	private static final int REQUEST_CODE_REGISTRATION = 1;
	private static final int REQUEST_CODE_LOGIN = 2;
	private static final int REQUEST_CODE_SETTINGS = 3;
	private Game game = new Game();
	private boolean isActive;
	private ProgressDialog dialog;
	private Thread threadLoading;
    private MyApplication app;
	
	public Handler loadingHandler;
	private static final int INTRODUCTION_MSG = 1;
	private static final int PLAYER_MSG = 3;
	private static final int REGISTRATION_MSG = 4;
	private static final int REGISTER_AGAIN_MSG = 5;
	private static final int LOGIN_AGAIN_MSG = 6;
	private static final int LOGIN_MSG = 7;
	private static final int NO_NETWORK_MSG = 101;
	private static final int NOT_ACTIVE_MSG = 102;
	private static final int NOT_UP_TO_DATE_MSG = 103;
	private static final int NAME_NOT_FOUND_MSG = 104;
	private static final int OUR_MISTAKE_MSG = 105;
	private static final int TRY_AGAIN = 201;
	
	
	public Handler mainActivityHandler = new Handler() {
        @Override
		public void handleMessage(Message msg) {
        	switch(msg.what)
      	  {
      	   case INTRODUCTION_MSG:
      		   showIntroduction();
      		   break;
      	   case PLAYER_MSG:
      		   showPlayer();
      		   break;
	   	   case NO_NETWORK_MSG:
	   		   	setContentView(R.layout.no_network);
	            Button try_again = (Button) findViewById(R.id.try_again);
	            try_again.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View v) {
	                    loadingHandler.sendEmptyMessage(TRY_AGAIN);
	                    dialog.show();
	                }
	            });
	            
	            Button settings = (Button) findViewById(R.id.settings);
	            settings.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View v) {
	                	Intent intent=new Intent(Settings.ACTION_WIRELESS_SETTINGS);
	                	startActivityForResult(intent, REQUEST_CODE_SETTINGS);
	                }
	            });
	   		 	break;
	   	   case NOT_ACTIVE_MSG:
				setContentView(R.layout.error);
				TextView reasonView2 = (TextView) findViewById(R.id.reason);
				reasonView2.setText(R.string.notActive);
				break;
	   	   case OUR_MISTAKE_MSG:
				setContentView(R.layout.error);
				TextView reasonView5 = (TextView) findViewById(R.id.reason);
				reasonView5.setText(R.string.our_mistake);
				break;
	   	   case NOT_UP_TO_DATE_MSG:
				setContentView(R.layout.not_uptodate);
				TextView reasonView3 = (TextView) findViewById(R.id.text);
				reasonView3.setText(R.string.notUpToDate);
				
				Button googlePlayButton = (Button) findViewById(R.id.button1);
				googlePlayButton.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View v) {
	                	Intent intent = new Intent(Intent.ACTION_VIEW);
	    				intent.setData(Uri.parse("market://details?id=cz.brno.greld.wizards"));
	    				startActivity(intent);
	                }
	            });
				
				Button againButton = (Button) findViewById(R.id.button2);
				againButton.setOnClickListener(new View.OnClickListener() {
	                public void onClick(View v) {
	                	loadingHandler.sendEmptyMessage(TRY_AGAIN);
	                    dialog.show();
	                }
	            });
				
				break;
	   	   case NAME_NOT_FOUND_MSG:
				setContentView(R.layout.error);
				TextView reasonView4 = (TextView) findViewById(R.id.reason);
				reasonView4.setText(R.string.nameNotFount);
				break;
	   	   case REGISTER_AGAIN_MSG:
	   		   	registration((String) msg.obj);
	   		   break;
	   	   case LOGIN_AGAIN_MSG:
	   		   	login((String) msg.obj);
	   		   break;
      	  }
        	dialog.dismiss();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_page);
        dialog = new ProgressDialog(this);
		dialog.setTitle("");
		dialog.setMessage("Inicializuji...");
		dialog.setCancelable(false);
		dialog.setOwnerActivity(this);
        dialog.show();
        
        app = ((MyApplication)getApplicationContext());
        
        Runnable loading = new Runnable(){
            public void run() {
            	Looper.prepare();
            	tryStart();
            	loadingHandler = new Handler() {
                    @Override
            		public void handleMessage(Message msg) {
                    	switch(msg.what)
                  	  {
                  	   case TRY_AGAIN:
                  		   tryStart();
                  		   break;
                  	   case REGISTRATION_MSG:
                 		    User user = null;;
	         				try {
	         					String[] data = ((String[])msg.obj);
	         					user = game.registration(data[0], data[1], data[2]);
	         				    app.setUser(user);	
	         					
	         		        	mainActivityHandler.sendEmptyMessage(PLAYER_MSG);
	         		        	
	         				} catch (ConnectivityExeption e){
	         					mainActivityHandler.sendEmptyMessage(NO_NETWORK_MSG);
	         				} catch (MistakeInJSONException e) {
	         					mainActivityHandler.sendEmptyMessage(NO_NETWORK_MSG);
	         				} catch (SaveExeption e) {
	         					mainActivityHandler.sendEmptyMessage(NO_NETWORK_MSG);
	         				} catch (IllegalArgumentException e) {
	         					Message msg2 = Message.obtain();
	         		       	 	msg2.obj = e.getMessage();
	         		       	 	msg2.what = REGISTER_AGAIN_MSG;
	         					mainActivityHandler.sendMessage(msg2);
	         				} catch (MissingDataException e) {
	         					mainActivityHandler.sendEmptyMessage(OUR_MISTAKE_MSG);
							}
                  		   break;
                  	   case LOGIN_MSG:
	                  		 User user2 = null;
	         				try {
	         					DataToLogin dataToLogin = (DataToLogin) msg.obj;
	         					user2 = game.login(dataToLogin.login, dataToLogin.password, dataToLogin.pref);
	         					if (user2 == null){
	             					Message msg2 = Message.obtain();
	             		       	 	msg2.obj = "Nesprávné pøihlašovací jméno èi heslo.";
	             		       	 	msg2.what = LOGIN_AGAIN_MSG;
	             					mainActivityHandler.sendMessage(msg2);
	             				}
	             				else if (isActive) {
	             					app.setUser(user2);
	             					mainActivityHandler.sendEmptyMessage(PLAYER_MSG);
	             				}
	             		        else 
             		            	mainActivityHandler.sendEmptyMessage(NOT_ACTIVE_MSG);
	             		        
	         				} catch (MistakeInJSONException e) {
	         					mainActivityHandler.sendEmptyMessage(NO_NETWORK_MSG);
	         				} catch (ConnectivityExeption e) {
	         					mainActivityHandler.sendEmptyMessage(NO_NETWORK_MSG);
	         				} catch (MissingDataException e) {
	         					mainActivityHandler.sendEmptyMessage(OUR_MISTAKE_MSG);
							}
                  		   break;
                  	  }
                    }
            	};
            	Looper.loop();
            }
        };
        threadLoading =  new Thread(null, loading, "loading");
        threadLoading.start();
        	
    }
    
    private void tryStart(){
    	if (checkConnectivity()){
    		int lastVersion, myVersion;
        	try {
        		int [] result = game.isActive();
        		isActive = result[0] == 1;
        		lastVersion = result[1];
    			PackageManager packageManager = getPackageManager();
    			PackageInfo packageInfo = packageManager.getPackageInfo("cz.brno.greld.DiscussWithUs", 0);
    			myVersion = packageInfo.versionCode;
			} catch (MistakeInJSONException e) {
				mainActivityHandler.sendEmptyMessage(NO_NETWORK_MSG);
				return;
			} catch (ConnectivityExeption e) {
				mainActivityHandler.sendEmptyMessage(NO_NETWORK_MSG);
				return;
			} catch (NameNotFoundException e) {
				mainActivityHandler.sendEmptyMessage(NAME_NOT_FOUND_MSG);
				return;
			}
			if (isActive){
				if (lastVersion == myVersion)
					checkAlreadyLogIn();
				else 
					mainActivityHandler.sendEmptyMessage(NOT_UP_TO_DATE_MSG);
			}
			else {
				mainActivityHandler.sendEmptyMessage(NOT_ACTIVE_MSG);
			}
        }
    }

    /**
     * if there is Internet connectivity available
     * @return true if there is connectivity, false if not
     */
    private boolean checkConnectivity(){
    	ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    	if (connectivityManager.getActiveNetworkInfo() == null){

			mainActivityHandler.sendEmptyMessage(NO_NETWORK_MSG);
            
            return false;
        }
        return true;
    }
    
    /**
     * If user saved his last login than lets play, introduction otherwise.
     */
    private void checkAlreadyLogIn(){
    	
    	SharedPreferences pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);   
        String username = pref.getString(PREF_USERNAME, null);
        String password = pref.getString(PREF_PASSWORD, null);

        if (username == null || password == null) {
        	mainActivityHandler.sendEmptyMessage(INTRODUCTION_MSG);
        }
        else {
        	User user = null;
        	try {
        		user = game.login(username, password, null);
			} catch (ConnectivityExeption e) {
				mainActivityHandler.sendEmptyMessage(NO_NETWORK_MSG);
				return;
			} catch (MistakeInJSONException e) {
				mainActivityHandler.sendEmptyMessage(NO_NETWORK_MSG);
				return;
			} catch (MissingDataException e) {
					mainActivityHandler.sendEmptyMessage(OUR_MISTAKE_MSG);
			}
        	if (user == null){
        		getSharedPreferences(PREFS_NAME,MODE_PRIVATE).edit().clear().commit();
            	mainActivityHandler.sendEmptyMessage(INTRODUCTION_MSG); 
        	}
        	else if (isActive){
					app.setUser(user);
					mainActivityHandler.sendEmptyMessage(PLAYER_MSG);
				}
    			else {
            		mainActivityHandler.sendEmptyMessage(NOT_ACTIVE_MSG); 
    			}
        }
    }
    

    /**
     * open MapActivity - start playing
     */
    private void showPlayer(){
    	Intent intent = new Intent();
    	
        intent.setClass(getApplicationContext(), PostsPreviewActivity.class);
        startActivity(intent);
        MainActivity.this.finish();
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (loadingHandler == null)
    		loadingHandler.getLooper().quit();
    	dialog.dismiss();
    }
    
    /**
     * Show introduction
     */
    private void showIntroduction(){
    	setContentView(R.layout.introduction);
    	if (!isActive){
    		TextView deactiveView = (TextView) findViewById(R.id.deactive);
    		deactiveView.setVisibility(TextView.VISIBLE);
    		deactiveView.setText(R.string.notActive);
    	}
    }
    
    /**
     * when registration button was clicked
     * @param view
     */
    public void registrationClick(View view){
    	if (isActive)
        	registration("");
    	else {
			setContentView(R.layout.error);
			TextView reasonView = (TextView) findViewById(R.id.reason);
			reasonView.setText(R.string.notActive);
    	}
    }
    
    /**
     * open RegistrationActivity
     * @param mistake - which mistake should be displayed to user
     */
    public void registration(String mistake){
    	Intent intent = new Intent(this, RegistrationActivity.class);
    	intent.putExtra("mistake", mistake);
		startActivityForResult(intent, REQUEST_CODE_REGISTRATION);
    }
    
    /**
     * when login button was clicked
     * @param view
     */
    public void loginClick(View view){
    	login("");
    }
    
    /**
     * open LoginActivity
     * @param mistake - which mistake should be displayed to user
     */
    public void login(String mistake){
    	Intent i = new Intent(this, LoginActivity.class);
    	i.putExtra("mistake", mistake);
		startActivityForResult(i, REQUEST_CODE_LOGIN);
    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
        
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_REGISTRATION) {
			if (data.hasExtra("login") && data.hasExtra("password") && data.hasExtra("email")) {
				dialog.setMessage("Pøihlašuji...");
		        dialog.show();
				String [] data2 = new String[3];
				data2[0] = data.getExtras().getString("login");
				data2[1] = data.getExtras().getString("password");
				data2[2] = data.getExtras().getString("email");
				Message msg = new Message();
				msg.obj = data2;
				msg.what = REGISTRATION_MSG;
				loadingHandler.sendMessage(msg);
			}
		}
		
        
        
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_LOGIN) {
			if (data.hasExtra("login") && data.hasExtra("password") && data.hasExtra("remember")) {
				dialog.setMessage("Pøihlašuji...");
		        dialog.show();
				SharedPreferences pref;
				if (!data.getExtras().getBoolean("remember"))
					pref = null;
				else
					pref = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);   
				
				DataToLogin dataToLogin = new DataToLogin();
				dataToLogin.login = data.getExtras().getString("login");
				dataToLogin.password = data.getExtras().getString("password");
				dataToLogin.pref = pref;
				Message msg = new Message();
				msg.obj = dataToLogin;
				msg.what = LOGIN_MSG;
				loadingHandler.sendMessage(msg);
			}
				
		}
		
		if (requestCode == REQUEST_CODE_SETTINGS) {
			loadingHandler.sendEmptyMessage(TRY_AGAIN);
			dialog.setMessage("Inicializuji...");
	        dialog.show();
		}
		
	}
    
    private class DataToLogin{
    	public String login;
    	public String password;
    	public SharedPreferences pref;
    }
}
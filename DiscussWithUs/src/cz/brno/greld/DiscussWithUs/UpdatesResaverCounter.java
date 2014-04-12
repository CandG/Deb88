package cz.brno.greld.DiscussWithUs;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Vibrator;

/**
	 * load num of new topics and reactions.. refresh activity or create notification
	 * @author Jan Kucera
	 *
	 */
	public class UpdatesResaverCounter extends CountDownTimer{

		private Activity activity;
		private MyApplication app;
		private User user;
	    private int freqency;
		
	    public UpdatesResaverCounter(long millisInFuture, long countDownInterval, Activity activity) {
	    super(millisInFuture, countDownInterval);
	    this.activity = activity;
	    this.app = ((MyApplication)(activity.getApplicationContext()));
	    this.user = app.getUser();
	    setFreqency();
	    }
	    
	    public void setFreqency(){
	    	System.err.println("app:" + app);
	    	System.err.println("OnForeground: " + app.getOnForeground() + " Sum: " + app.getNumberOfDisplayActivities());
	    	if (app.getNumberOfDisplayActivities() > 0)
	    	    freqency = user.getUserSettings().getUpdateFrequencyIfActive() * 1000;
	    	else
	    	    freqency = user.getUserSettings().getUpdateFrequencyIfInactive() * 1000;
	    }

	    @Override
	    public void onFinish() {
	    	
	    	update(activity);
			
	    	if (freqency>0)
	    		resetTimer(freqency, freqency);
	    	else {
	    		String ns = Context.NOTIFICATION_SERVICE;
		        NotificationManager nMgr = (NotificationManager) app.getSystemService(ns);
		        nMgr.cancel(1);
		        nMgr.cancel(2);
		        app.setNumOfNewReactions(0);
		        app.setNumOfNewTopics(0);
		        app.updatesResaverCounter = null;
		        app.threadUpdating = null;
	    	}
	    }

	    public void resetTimer(long howLong, long tick)
	    {
	    	if (freqency>0){	    	
	    		app.updatesResaverCounter = new UpdatesResaverCounter(howLong,tick, activity);
		    	app.updatesResaverCounter.start();
	    	}
	    }

		@Override
		public void onTick(long millisUntilFinished) {
			
		}
		
		
		
		public static void update(Activity activity){
			MyApplication app = ((MyApplication)activity.getApplicationContext());
			User user = app.getUser();
	    	try {
				int numOfNewTopics = user.loadNumOfNewTopics();
				System.err.println("numOfNewTopics: "+numOfNewTopics);
				int numOfNewReactions = user.loadNumOfNewReactions();
				System.err.println("numOfNewReactions: "+numOfNewReactions);
				

				String ns = Context.NOTIFICATION_SERVICE;
				NotificationManager mNotificationManager = (NotificationManager) activity.getSystemService(ns);
				if (numOfNewTopics == 0){
					mNotificationManager.cancel(1);
				}
				if (numOfNewReactions == 0){
					mNotificationManager.cancel(2);
				}
				
				if (numOfNewTopics != app.getNumOfNewTopics() && numOfNewTopics != 0){
					
					int icon = R.drawable.alert;
					CharSequence tickerText = "Nové téma";
					long when = System.currentTimeMillis();
	
					Notification notification = new Notification(icon, tickerText, when);
					
					
					CharSequence contentTitle = "Nové téma na dosah!";
					CharSequence contentText = (numOfNewTopics) + " nových témat na dosah.";
					Intent notificationIntent = new Intent(activity, PostsPreviewActivity.class);
					PendingIntent contentIntent = PendingIntent.getActivity(activity, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	
					notification.setLatestEventInfo(activity.getApplicationContext(), contentTitle, contentText, contentIntent);
					
					mNotificationManager.notify(1, notification);
					
					
				}
				
				if (numOfNewReactions  != app.getNumOfNewReactions() && numOfNewReactions != 0){
					
					int icon = R.drawable.alert;
					CharSequence tickerText = "Nová reakce";
					long when = System.currentTimeMillis();
	
					Notification notification = new Notification(icon, tickerText, when);
					
					
					CharSequence contentTitle = "Nová reakce na pøíspìvek!";
					CharSequence contentText = (numOfNewReactions) + " nových reakcí na váš pøíspìvek.";
					Intent notificationIntent = new Intent(activity, NewReactionsActivity.class);
					PendingIntent contentIntent = PendingIntent.getActivity(activity, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
	
					notification.setLatestEventInfo(activity.getApplicationContext(), contentTitle, contentText, contentIntent);
					
					mNotificationManager.notify(2, notification);
					
				}
				if (numOfNewTopics > app.getNumOfNewTopics() || numOfNewReactions > app.getNumOfNewReactions())
				{
					// Get instance of Vibrator from current Context
					Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
					// Vibrate for 300 milliseconds
					v.vibrate(500);
					
					if (numOfNewTopics > app.getNumOfNewTopics() && app.getOnForeground() == MyApplication.TOPICS){
						app.postsPreviewHandler.sendEmptyMessage(PostsPreviewActivity.REFRESH_MSG);
					} 
					else if (numOfNewReactions > app.getNumOfNewReactions() && app.getOnForeground() == MyApplication.POST_REACTIONS){
						app.postsPreviewHandler.sendEmptyMessage(PostsPreviewActivity.REFRESH_MSG);
					}
				}

				app.setNumOfNewTopics(numOfNewTopics);
				app.setNumOfNewReactions(numOfNewReactions);
				
				
			} catch (ConnectivityExeption e) {
				System.err.println("ConnectivityExeption: "+ e.getMessage());
			} catch (MistakeInJSONException e) {
				System.err.println("MistakeInJSONException: "+ e.getMessage());
			} 
		}

	 }
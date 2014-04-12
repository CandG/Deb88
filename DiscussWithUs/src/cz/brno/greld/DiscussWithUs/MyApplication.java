package cz.brno.greld.DiscussWithUs;

import android.app.Application;
import android.os.Handler;

/**
 * Global data to access from all activities
 * @author Jan Kucera
 *
 */
public class MyApplication extends Application 
{     
	public static final int NOTHING = 0;
	public static final int TOPICS = 1;
	public static final int POST_REACTIONS = 2;
	public static final int NEW_TOPIC = 3;
	public static final int ABOUT = 4;
	public static final int NEW_REACTIONS = 5;
	public static final int OPTIONS = 6;
	public static final int PROFILE = 7;
	public static final int UPDATING_SETTINGS = 8;
	
	
	private User user;
	private int numOfNewTopics;
	private int numOfNewReactions;
	public Thread threadUpdating;
	public UpdatesResaverCounter updatesResaverCounter;
	private int onForeground;
	public Handler postsPreviewHandler;
	private int numberOfDisplayActivities = 0;
	
	
	
	public int getNumberOfDisplayActivities() {
		return numberOfDisplayActivities;
	}
	
	public void plusNumberOfDisplayActivities() {
		numberOfDisplayActivities++;
		System.err.println("Sum: " + getNumberOfDisplayActivities());
	}
	
	public void minusNumberOfDisplayActivities() {
		numberOfDisplayActivities--;
		System.err.println("Sum: " + getNumberOfDisplayActivities());
	}

	public int getOnForeground() {
		return onForeground;
	}

	public void setOnForeground(int onForeground) {
		this.onForeground = onForeground;
		if (updatesResaverCounter != null)
			updatesResaverCounter.setFreqency();
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getNumOfNewTopics() {
		return numOfNewTopics;
	}

	public void setNumOfNewTopics(int numOfNewTopics) {
		this.numOfNewTopics = numOfNewTopics;
	}

	public int getNumOfNewReactions() {
		return numOfNewReactions;
	}

	public void setNumOfNewReactions(int numOfNewReactions) {
		this.numOfNewReactions = numOfNewReactions;
	}

	
	
}

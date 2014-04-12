package cz.brno.greld.DiscussWithUs;

import java.util.Calendar;
import java.util.Date;


public class Post {

	private int id;
	private String text;
	private Date date;
	private int lifeTime;
	private int range;
	private Category category;
	private Location location;
	private int idOfPredecessor;
	private boolean predecessorFirst;
	private User author;
	private int numOfReactions;
	private boolean newOne;
	
	
	public Post(int id, String text, Date date, int lifeTime, int range,
			Category category, Location location, int idOfPredecessor,
			boolean predecessorFirst, User author, int numOfReactions) {
		super();
		this.id = id;
		this.text = text;
		this.date = date;
		this.lifeTime = lifeTime;
		this.range = range;
		this.category = category;
		this.location = location;
		this.idOfPredecessor = idOfPredecessor;
		this.predecessorFirst = predecessorFirst;
		this.author = author;
		this.setNumOfReactions(numOfReactions);
		this.newOne = false;
	}
	
	
	public Post(int id, String text, Date date, int lifeTime, int range,
			Category category, Location location, int idOfPredecessor,
			boolean predecessorFirst, User author, int numOfReactions, boolean newOne) {
		super();
		this.id = id;
		this.text = text;
		this.date = date;
		this.lifeTime = lifeTime;
		this.range = range;
		this.category = category;
		this.location = location;
		this.idOfPredecessor = idOfPredecessor;
		this.predecessorFirst = predecessorFirst;
		this.author = author;
		this.setNumOfReactions(numOfReactions);
		this.newOne = newOne;
	}
	
	




	public boolean isNewOne() {
		return newOne;
	}


	public void setNewOne(boolean newOne) {
		this.newOne = newOne;
	}


	public int getId() {
		return id;
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
	}


	public int getLifeTime() {
		return lifeTime;
	}


	public void setLifeTime(int lifeTime) {
		this.lifeTime = lifeTime;
	}


	public int getRange() {
		return range;
	}


	public void setRange(int range) {
		this.range = range;
	}


	public Category getCategory() {
		return category;
	}


	public void setCategory(Category category) {
		this.category = category;
	}


	public Location getLocation() {
		return location;
	}


	public void setLocation(Location location) {
		this.location = location;
	}


	public int getIdOfPredecessor() {
		return idOfPredecessor;
	}


	public void setIdOfPredecessor(int idOfPredecessor) {
		this.idOfPredecessor = idOfPredecessor;
	}


	public boolean isPredecessorFirst() {
		return predecessorFirst;
	}


	public void setPredecessorFirst(boolean predecessorFirst) {
		this.predecessorFirst = predecessorFirst;
	}


	public User getAuthor() {
		return author;
	}


	public void setAuthor(User author) {
		this.author = author;
	}
	
	
	public boolean isFirst(){
		return (idOfPredecessor == 0);
	}
	
	
	public String getDateString(){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int minutes = calendar.get(Calendar.MINUTE);
		int seconds = calendar.get(Calendar.SECOND);

		return calendar.get(Calendar.HOUR_OF_DAY) + ":" + (minutes<10?"0":"") + minutes + ":" 
		 + (seconds<10?"0":"") +  seconds + "  " + 
		calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH)+1) + "." + 
		calendar.get(Calendar.YEAR);
		 
	}


	public int getNumOfReactions() {
		return numOfReactions;
	}


	public void setNumOfReactions(int numOfReactions) {
		this.numOfReactions = numOfReactions;
	}
	
	
}

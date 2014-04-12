package cz.brno.greld.DiscussWithUs;

import java.util.ArrayList;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Everybody who use this application - life person
 * @author Jan Kucera
 *
 */
public class User {

	private int id;
	private int role;
	private String nickname;
	private Double latitude;
	private Double longitude;
	private UserSettings userSettings;
	private ArrayList<Category> categories = new ArrayList<Category>();
	private boolean login;

	protected static final String VALUE_OF_CHECK = "poiulkjh456";
	
	
	public User(int id, int role, String nickname) throws ConnectivityExeption, MistakeInJSONException {
		this.id = id;
		this.role = role;
		this.nickname = nickname;
		this.login = true;
	}

	public int getRole(){
		return role;
	}
	
	public int getId(){
		return id;
	}
	
	public String getNickname(){
		return nickname;
	}

	
	public UserSettings getUserSettings() {
		return userSettings;
	}
	
	

	public ArrayList<Category> getCategories() {
		return categories;
	}
	
	public boolean isLogin(){
		return login;
	}

	/**
	 * Load FirstPosts from DB
	 * 
	 * @throws ConnectivityExeption 
	 * @throws MistakeInJSONException 
	 */
	public ArrayList<Post> loadFirstPosts() throws ConnectivityExeption, MistakeInJSONException {
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("idOfUser", String.valueOf(id)));

		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "loadPosts.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("loadPosts: jArray je null");

		JSONObject json_data;
		ArrayList<Post> posts = new ArrayList<Post>();
		
		try {
			if (jArray.getJSONObject(0).getBoolean("empty") == true)
				return posts;
		} catch (JSONException e1) {}
		
		try {
			int id = 0, authorId = 0, lifeTime = 0, range = 0, numOfReactions = 0, 
					categoryId = 0, predecessorId = 0, authorRole = 0; 
			long date = 0;
			boolean predecessorFirst = false, newOne = false;
			String text = null, authorNickname = null, categoryName = null, 
					categoryDescription = null;
			double latitude = 0, longitude = 0;
			
			for(int i=0;i<jArray.length();i++){
				 	json_data = jArray.getJSONObject(i);
					id = json_data.getInt("idOfPost");
					text = json_data.getString("text");
					date = json_data.getLong("date");
					authorId = json_data.getInt("authorId");
					authorRole = json_data.getInt("authorRole");
					authorNickname = json_data.getString("authorNickname");
					lifeTime = json_data.getInt("lifeTime");
					range = json_data.getInt("range");
					categoryId = json_data.getInt("categoryId");
					categoryName = json_data.getString("categoryName");
					categoryDescription = json_data.getString("categoryDescription");
					latitude = json_data.getDouble("latitude");
					longitude = json_data.getDouble("longitude");
					predecessorId = json_data.getInt("predecessorId");
					predecessorFirst = (1 == json_data.getInt("predecessorFirst"));
					numOfReactions = json_data.getInt("numOfReactions");
					newOne = (1 == json_data.getInt("newOne"));

				if (id != 0){
					posts.add(new Post(id, text, new Date(date*1000), lifeTime, range, 
							new Category(categoryId,categoryName,categoryDescription),
							new Location(latitude, longitude),
							predecessorId, predecessorFirst,
					        new User(authorId,authorRole,authorNickname), numOfReactions, newOne)
					);
				}
			}
			return posts;
		} catch (JSONException e) {
			throw new MistakeInJSONException("loadPosts: Chyba pri parsovani", e);
		}
	}
	
	/**
	 * Load Reactions For post with id = idOfPost from DB
	 * 
	 * @throws ConnectivityExeption 
	 * @throws MistakeInJSONException 
	 */
	public ArrayList<Post> loadReactionsFor(int idOfParent, boolean parentIsFirst) throws ConnectivityExeption, MistakeInJSONException {
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("idOfUser", String.valueOf(id)));
		nameValuePairs.add(new BasicNameValuePair("idOfParent", String.valueOf(idOfParent)));
		nameValuePairs.add(new BasicNameValuePair("parentIsFirst", String.valueOf((parentIsFirst?1:0))));

		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "loadReactionsFor.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("loadReactionsFor: jArray je null");

		JSONObject json_data;
		ArrayList<Post> posts = new ArrayList<Post>();
		
		try {
			if (jArray.getJSONObject(0).getBoolean("empty") == true)
				return posts;
		} catch (JSONException e1) {}
		
		try {
			int id = 0, authorId = 0,predecessorId = 0, authorRole = 0, numOfReactions = 0; 
			long date = 0;
			boolean predecessorFirst = false, newOne = false;
			String text = null, authorNickname = null;
			double latitude = 0, longitude = 0;
			
			for(int i=0;i<jArray.length();i++){
				 	json_data = jArray.getJSONObject(i);
					id = json_data.getInt("idOfPost");
					text = json_data.getString("text");
					date = json_data.getLong("date");
					authorId = json_data.getInt("authorId");
					authorRole = json_data.getInt("authorRole");
					authorNickname = json_data.getString("authorNickname");
					latitude = json_data.getDouble("latitude");
					longitude = json_data.getDouble("longitude");
					predecessorId = json_data.getInt("predecessorId");
					predecessorFirst = (1 == json_data.getInt("predecessorFirst"));
					numOfReactions = json_data.getInt("numOfReactions");
					newOne = (1 == json_data.getInt("newOne"));

				if (id != 0){
					posts.add(new Post(id, text, new Date(date*1000), 0, 0, 
							null,
							new Location(latitude, longitude),
							predecessorId, predecessorFirst,
					        new User(authorId,authorRole,authorNickname), numOfReactions, newOne)
					);
				}
			}
			return posts;
		} catch (JSONException e) {
			throw new MistakeInJSONException("loadReactionsFor: Chyba pri parsovani", e);
		}
	}
	
	
	
	/**
	 * Load categories from DB and save as attribute
	 * 
	 * @throws ConnectivityExeption 
	 * @throws MistakeInJSONException 
	 */
	public void loadCategories() throws ConnectivityExeption, MistakeInJSONException {
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("idOfUser", String.valueOf(id)));

		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "loadCategories.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("loadCategories: jArray je null");

		JSONObject json_data;
		categories.clear();
		
		try {
			if (jArray.getJSONObject(0).getBoolean("empty") == true)
				return;
		} catch (JSONException e1) {}
		
		try {
			int categoryId = 0; 
			String categoryName = null, categoryDescription = null;
			
			for(int i=0;i<jArray.length();i++){
				 	json_data = jArray.getJSONObject(i);
					categoryId = json_data.getInt("categoryId");
					categoryName = json_data.getString("categoryName");
					categoryDescription = json_data.getString("categoryDescription");

				if (categoryId != 0){
					categories.add(new Category(categoryId, categoryName, categoryDescription));
				}
			}
		} catch (JSONException e) {
			throw new MistakeInJSONException("loadCategories: Chyba pri parsovani", e);
		}
	}
	
	/**
	 * Load  post with id = idOfPost from DB
	 * 
	 * @throws ConnectivityExeption 
	 * @throws MistakeInJSONException 
	 * @throws BadIdException 
	 */
	public Post loadPost(int idOfPost, boolean postIsFirst) throws ConnectivityExeption, MistakeInJSONException, BadIdException {
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("idOfUser", String.valueOf(id)));
		nameValuePairs.add(new BasicNameValuePair("idOfPost", String.valueOf(idOfPost)));
		nameValuePairs.add(new BasicNameValuePair("postIsFirst", String.valueOf((postIsFirst?1:0))));

		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "loadPost.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("loadPost: jArray je null");

		JSONObject json_data;
			
			try {
				if (jArray.getJSONObject(0).optBoolean("empty") == true)
					throw new BadIdException("Nepodaøilo se získat potøebné informace z databáze.");
			} catch (JSONException e1) {
				throw new MistakeInJSONException("JSONException: jArray neobsahuje nultý objekt");
			}
			
		try {
			int  authorId = 0,predecessorId = 0, authorRole = 0, 
					numOfReactions = 0, lifeTime = 0, range = 0,categoryId = 0; 
			long date = 0;
			boolean predecessorFirst = false;
			String text = null, authorNickname = null, categoryName = null, 
					categoryDescription = null;
			double latitude = 0, longitude = 0;
			
			json_data = jArray.getJSONObject(0);
			text = json_data.getString("text");
			date = json_data.getLong("date");
			authorId = json_data.getInt("authorId");
			authorRole = json_data.getInt("authorRole");
			authorNickname = json_data.getString("authorNickname");
			lifeTime = json_data.getInt("lifeTime");
			range = json_data.getInt("range");
			categoryId = json_data.getInt("categoryId");
			categoryName = json_data.getString("categoryName");
			categoryDescription = json_data.getString("categoryDescription");
			latitude = json_data.getDouble("latitude");
			longitude = json_data.getDouble("longitude");
			predecessorId = json_data.getInt("predecessorId");
			predecessorFirst = (1 == json_data.getInt("predecessorFirst"));
			numOfReactions = json_data.getInt("numOfReactions");

			return new Post(idOfPost, text, new Date(date*1000), lifeTime, range, 
					new Category(categoryId,categoryName,categoryDescription),
					new Location(latitude, longitude),
					predecessorId, predecessorFirst,
			        new User(authorId,authorRole,authorNickname), numOfReactions);
		} catch (JSONException e) {
			throw new MistakeInJSONException("loadPost: Chyba pri parsovani", e);
		}
	}

	
	
	public boolean createTopic(String text, int lifeTime, int range, Category category) throws ConnectivityExeption, MistakeInJSONException, SaveExeption {
		int idOfCategory = 0;
		if (category != null)
		   idOfCategory = category.getId();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("idOfUser", String.valueOf(id)));
		nameValuePairs.add(new BasicNameValuePair("text", text));
		nameValuePairs.add(new BasicNameValuePair("lifeTime", String.valueOf(lifeTime)));
		nameValuePairs.add(new BasicNameValuePair("range", String.valueOf(range)));
		nameValuePairs.add(new BasicNameValuePair("category", String.valueOf(idOfCategory)));
		nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
		nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));
		
		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "createTopic.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("createTopic: jArray je null");

		
		try {
			return (jArray.getJSONObject(0).getBoolean("ok"));
		} catch (JSONException e1) {
			return false;
		}
		
	}
	
	
	public boolean createReactionFor(int idOfParent, boolean parentIsFirst, String text) throws ConnectivityExeption, MistakeInJSONException, SaveExeption {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("idOfUser", String.valueOf(id)));
		nameValuePairs.add(new BasicNameValuePair("idOfParent", String.valueOf(idOfParent)));
		nameValuePairs.add(new BasicNameValuePair("parentIsFirst", String.valueOf((parentIsFirst?1:0))));
		nameValuePairs.add(new BasicNameValuePair("text", text));
		nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
		nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));
		
		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "createReactionFor.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("createReactionFor: jArray je null");

		
		try {
			return (jArray.getJSONObject(0).getBoolean("ok"));
		} catch (JSONException e1) {
			return false;
		}
		
	}

	public boolean deletePost(int idOfPost, boolean postIsFirst) throws ConnectivityExeption {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("idOfUser", String.valueOf(id)));
		nameValuePairs.add(new BasicNameValuePair("idOfPost", String.valueOf(idOfPost)));
		nameValuePairs.add(new BasicNameValuePair("postIsFirst", String.valueOf((postIsFirst?1:0))));
		
		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "deletePost.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("deletePost: jArray je null");

		
		try {
			return (jArray.getJSONObject(0).getBoolean("ok"));
		} catch (JSONException e1) {
			return false;
		}
	}
	
	public boolean saveUserSettings() throws ConnectivityExeption {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("idOfUser", String.valueOf(id)));
		nameValuePairs.add(new BasicNameValuePair("updateFrequencyIfActive", String.valueOf((userSettings.getUpdateFrequencyIfActive()))));
		nameValuePairs.add(new BasicNameValuePair("updateFrequencyIfInactive", String.valueOf((userSettings.getUpdateFrequencyIfInactive()))));
		
		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "saveUserSettings.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("saveUserSettings: jArray je null");

		
		try {
			return (jArray.getJSONObject(0).getBoolean("ok"));
		} catch (JSONException e1) {
			return false;
		}
	}

	public int loadNumOfNewTopics() throws ConnectivityExeption, MistakeInJSONException {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("idOfUser", String.valueOf(id)));
		
		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "loadNumOfNewTopics.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("loadNumOfNewTopics: jArray je null");

		
		try {
			return (jArray.getJSONObject(0).getInt("numOfNewTopics"));
		} catch (JSONException e1) {
			throw new MistakeInJSONException("nenalezen požadovaný atribut");
		}
	}

	public int loadNumOfNewReactions() throws ConnectivityExeption, MistakeInJSONException {
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("idOfUser", String.valueOf(id)));
		
		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "loadNumOfNewReactions.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("loadNumOfNewReactions: jArray je null");

		
		try {
			return (jArray.getJSONObject(0).getInt("numOfNewReactions"));
		} catch (JSONException e1) {
			throw new MistakeInJSONException("nenalezen požadovaný atribut");
		}
	}
	
	
	/**
	 * Load user settings from DB from DB
	 * 
	 * @throws ConnectivityExeption 
	 * @throws MistakeInJSONException 
	 * @throws BadIdException 
	 * @throws MissingDataException 
	 */
	public void loadUserSettings() throws ConnectivityExeption, MistakeInJSONException, MissingDataException {
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("idOfUser", String.valueOf(id)));

		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "loadUserSettings.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("loadUserSettings: jArray je null");

		JSONObject json_data;
		
		try {
			if (jArray.getJSONObject(0).getBoolean("empty") == true)
				throw new MissingDataException("Nebylo nalezeno nastavení uživatele.");
		} catch (JSONException e1) {}
		
		try {
				json_data = jArray.getJSONObject(0);
				userSettings = new UserSettings(json_data.getInt("updateFrequencyIfActive"), 
						json_data.getInt("updateFrequencyIfInactive"));

		} catch (JSONException e) {
			throw new MistakeInJSONException("loadUserSettings: Chyba pri parsovani", e);
		}
	}
	
	

	
	/**
	 * Load new post with its predecessors from DB
	 * 
	 * @throws ConnectivityExeption 
	 * @throws MistakeInJSONException 
	 * @throws BadIdException 
	 */
	public ArrayList<Post> loadNewReactions() throws ConnectivityExeption, MistakeInJSONException, BadIdException {
		
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("idOfUser", String.valueOf(id)));

		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "loadNewReactions.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("loadNewReactions: jArray je null");

		JSONObject json_data;
		ArrayList<Post> posts = new ArrayList<Post>();
			
			try {
				if (jArray.getJSONObject(0).optBoolean("empty") == true)
					throw new BadIdException("Nepodaøilo se získat potøebné informace z databáze.");
			} catch (JSONException e1) {
				throw new MistakeInJSONException("JSONException: jArray neobsahuje nultý objekt");
			}
			
		try {
			int  idOfPost = 0, authorId = 0,predecessorId = 0, authorRole = 0, 
					numOfReactions = 0, lifeTime = 0, range = 0,categoryId = 0; 
			long date = 0;
			boolean predecessorFirst = false, newOne = false;
			String text = null, authorNickname = null, categoryName = null, 
					categoryDescription = null;
			double latitude = 0, longitude = 0;
			
			
			for(int i=0;i<jArray.length();i++){
			 	json_data = jArray.getJSONObject(i);
				idOfPost = json_data.getInt("idOfPost");
				text = json_data.getString("text");
				date = json_data.getLong("date");
				authorId = json_data.getInt("authorId");
				authorRole = json_data.getInt("authorRole");
				authorNickname = json_data.getString("authorNickname");
				lifeTime = json_data.getInt("lifeTime");
				range = json_data.getInt("range");
				categoryId = json_data.getInt("categoryId");
				categoryName = json_data.getString("categoryName");
				categoryDescription = json_data.getString("categoryDescription");
				latitude = json_data.getDouble("latitude");
				longitude = json_data.getDouble("longitude");
				predecessorId = json_data.getInt("predecessorId");
				predecessorFirst = (1 == json_data.getInt("predecessorFirst"));
				numOfReactions = json_data.getInt("numOfReactions");
				newOne = (1 == json_data.getInt("newOne"));
	
				posts.add(new Post(idOfPost, text, new Date(date*1000), lifeTime, range, 
						new Category(categoryId,categoryName,categoryDescription),
						new Location(latitude, longitude),
						predecessorId, predecessorFirst,
				        new User(authorId,authorRole,authorNickname), numOfReactions, newOne));
			}
			return posts;
		} catch (JSONException e) {
			throw new MistakeInJSONException("loadNewReactions: Chyba pri parsovani", e);
		}
	}

	public CharSequence [] getCategoriesStringArray() {
		CharSequence [] result = new CharSequence[categories.size()];
		int i = 0;
		for (Category category : categories) {
			result[i] = category.getName();
			i++;
		}
		return result;
	}

	public void logout() {
		login = false;
		
	}
}
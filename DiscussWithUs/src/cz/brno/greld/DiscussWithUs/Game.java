package cz.brno.greld.DiscussWithUs;


import java.util.ArrayList;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;

/**
 * The game to which you can login or register
 * @author Jan Kucera
 *
 */
public class Game {
	private User user;

	public static final String PREFS_NAME = "Login";
	

	private static final String VALUE_OF_CHECK = "poiulkjh456";
	

	public Game() {
		user = null;
	}

	/**
	 * register new user and login him
	 * @param login
	 * @param password
	 * @param email
	 * @return new User
	 * @throws ConnectivityExeption
	 * @throws MistakeInJSONException
	 * @throws SaveExeption
	 * @throws IllegalArgumentException - bad format of an entry or login is not unique
	 * @throws MissingDataException 
	 */
	public User registration(String login, String password, String email) throws ConnectivityExeption, 
	MistakeInJSONException, SaveExeption, IllegalArgumentException, MissingDataException {

		if (login == null || login.length() < 3)
			throw new IllegalArgumentException("Zvolte jiné pøihlašovací jméno.");

		if (password == null || password.length() < 4)
			throw new IllegalArgumentException("Zvolte jiné heslo.");


		if (email == null || !email.contains("@"))
			throw new IllegalArgumentException("Chybný e-mail.");

		// test jedinecnosti Loginu
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("login", login));

		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "isLoginUnique.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("Registrace: test jedinecnosti Loginu: jArray je null");

		JSONObject json_data;
		try {
			json_data = jArray.getJSONObject(0);

			if (json_data.getInt("isLoginUnique") > 0)
				throw new IllegalArgumentException("Login není jedineèný, zvolte jiný.");
		} catch (JSONException e) {
			throw new MistakeInJSONException("Registrace: test jedinecnosti Loginu: Chyba pri parsovani", e);
		}

		// ulozeni udaju do DB
		nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("login", login));
		nameValuePairs.add(new BasicNameValuePair("password", password));
		nameValuePairs.add(new BasicNameValuePair("email", email));

		jArray = DBWorker.dbQuery(nameValuePairs, "registration.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("Registrace: ulozeni udaju do DB: jArray je null");

		try {
			json_data = jArray.getJSONObject(0);
			if (!json_data.getBoolean("ok"))
				throw new SaveExeption("Nepodaøilo se uložit registraèní údaje.");
		} catch (JSONException e) {
			throw new MistakeInJSONException("Registrace: ulozeni udaju do DB: Chyba pri parsovani", e);
		}

		return login(login, password, null);
	}

	
	/**
	 * Login user.
	 * @param login
	 * @param password
	 * @param pref to remember login to future
	 * @return user who was login, null if bad login or password
	 * @throws MistakeInJSONException
	 * @throws ConnectivityExeption
	 * @throws MissingDataException 
	 */
	public User login(String login, String password, SharedPreferences pref) throws MistakeInJSONException, ConnectivityExeption, MissingDataException {
		if (login == null || login.length() <= 0)
			return null;

		if (password == null || password.length() <= 0)
			return null;

		// test prihlasovacich udaju a zisk id a emailu
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("login", login));
		nameValuePairs.add(new BasicNameValuePair("password", password));

		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "login.php", VALUE_OF_CHECK);
		if (jArray == null)
			return null;

		JSONObject json_data;
		json_data = jArray.optJSONObject(0);

		try {
			if (jArray.getJSONObject(0).getBoolean("empty") == true)
				return null;
		} catch (JSONException e1) {}

		// ulozeni udaju do SharedPreferences
		if (pref != null) {
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("login", login);
			editor.putString("password", password);
			editor.commit();
		}

		try {
			user = new User(json_data.getInt("ID_uzivatele"), json_data.getInt("role"), json_data.getString("prezdivka"));
			user.loadUserSettings();
			user.loadCategories();
		} catch (JSONException e) {
			throw new MistakeInJSONException("Pøihlášení: Chyba pri parsovani", e);
		} 
		
		return user;
	}

	
	/**
	 * if the game is active, not blocked by admin
	 * and last public version of app
	 * @return [1 if active, 0 if not, version number]
	 * @throws MistakeInJSONException
	 * @throws ConnectivityExeption
	 */
	public int [] isActive() throws MistakeInJSONException, ConnectivityExeption{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		int [] result = new int [2]; 
		JSONArray jArray = DBWorker.dbQuery(nameValuePairs, "isActiveGame.php", VALUE_OF_CHECK);
		if (jArray == null)
			throw new NullPointerException("isActive: jArray is null");

		try {
			JSONObject data = jArray.getJSONObject(0);
			result[0] = data.getInt("isActive");
			result[1] = data.getInt("version");
			return result;
		} catch (JSONException e1) {
			throw new MistakeInJSONException("isActive: ", e1);
		}
	}
	
	
	
	
	public User getUser(){
		return user;
	}
}
package cz.brno.greld.DiscussWithUs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * works with database
 * @author Jan Kucera
 *
 */
public class DBWorker {

	private static final String INDEX_OF_CHECK = "kontrola";
	public static final String URL = "http://d.casero.cz/android/";
	
	/**
	 * 
	 * @param nameValuePairs - specials values passes to PHP script
	 * @param script - name of PHP file
	 * @param password - secure password which is needed to access to PHP scripts
	 * @return data from database
	 * @throws ConnectivityExeption
	 */
	public static JSONArray dbQuery(ArrayList<NameValuePair> nameValuePairs,
			String script, String password) throws ConnectivityExeption {

		nameValuePairs.add(new BasicNameValuePair(INDEX_OF_CHECK, password));

		String result = "";
		InputStream is = null;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URL + script);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httppost);
			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (Exception e) {
			throw new ConnectivityExeption("Chyba pøi HttpPost, script: " + script, e);
		}
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.err.println(line);
				sb.append(line + "\n");
			}
			is.close();

			result = sb.toString();
		} catch (Exception e) {
			throw new ConnectivityExeption("Chyba pøi zpracování streamu, script: " + script, e);
		}
		
		if (result == null || result == "")
			return null;

		// parse json data
		JSONArray json_data = null;
		try {
			json_data = new JSONArray(result);

		} catch (JSONException e) {
			throw new ConnectivityExeption("Chyba pøi parsování JSON dat, script: " + script, e);
		}
		return json_data;
	}
}

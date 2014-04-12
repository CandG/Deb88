package cz.brno.greld.DiscussWithUs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/**
 * work with images
 * @author Jan Kucera
 *
 */

public class ImageDownloader {
	

	/**
	 * save image to SD card if any is available
	 * @param bm - image
	 * @param path - target place to save
	 * @param fname - name of saving image
	 * @return true if saved
	 */
	public static boolean saveImage(Bitmap bm, String path, String fname) {
    	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || path == null || fname == null || bm == null)
    		return false;
    	File file = new File(path);
    	     if (!file.exists()){
    	    	 if (!file.mkdirs()){
    	    		 System.err.println("Nelze vytvorit dir");
    	    		 return false;
    	    	 } 
    	     }
    	     
    	file = new File(path, fname);
    	OutputStream os = null;
		try {
			os = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException: " + e.getMessage());
			return false;
		}
        try {
            bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
		} catch (IOException e) {
			System.err.println("IOException: " + e.getMessage());
			return false;
		}
        System.err.println("saved.");
        return true;
    }

    
    /**
	 * Download and return a thumb specified by url
	 * @param url - from where should be the image be downloaded
	 * @return downloaded image
	 */
	public static Bitmap DownloadImage(String url) {

		// the downloaded thumb (none for now!)
		Bitmap thumb = null;

		// sub-sampling options
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 1;

		try {

			// open a connection to the URL
			// Note: pay attention to permissions in the Manifest file!
			URL u = new URL(url);
			URLConnection c = u.openConnection();
			c.connect();
			
			// read data
			BufferedInputStream stream = new BufferedInputStream(c.getInputStream());

			// decode the data, subsampling along the way
			thumb = BitmapFactory.decodeStream(stream, null, opts);

			// close the stream
			stream.close();

		} catch (MalformedURLException e) {
			Log.e("ImageDownloader", "malformed url: " + url);
		} catch (IOException e) {
			Log.e("ImageDownloader", "An error has occurred downloading the image: " + url);
		}

		// return the fetched thumb (or null, if error)
		return thumb;
	}

}

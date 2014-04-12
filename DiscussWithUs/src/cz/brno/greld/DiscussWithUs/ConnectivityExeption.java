package cz.brno.greld.DiscussWithUs;
/**
 * When error with network connection is appeared. Mainly during working with database.
 * @author Jan Kucera
 *
 */
public class ConnectivityExeption extends Exception {

	private static final long serialVersionUID = -3904512971258194635L;

	public ConnectivityExeption(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
}

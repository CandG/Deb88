package cz.brno.greld.DiscussWithUs;

/**
 * When the object with the id does not exists.
 * @author Jan Kucera
 *
 */
public class BadIdException extends Exception {
	private static final long serialVersionUID = -5148134029258376120L;

	public BadIdException(String detailMessage) {
		super(detailMessage);
	}

}

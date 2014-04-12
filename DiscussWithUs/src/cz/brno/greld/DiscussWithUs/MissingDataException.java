package cz.brno.greld.DiscussWithUs;

/**
 * If SQL query was unsuccessful
 * @author Jan Kucera
 *
 */
public class MissingDataException extends Exception {

	private static final long serialVersionUID = 20028879886014149L;

	public MissingDataException(String detailMessage) {
		super(detailMessage);
	}

	
}

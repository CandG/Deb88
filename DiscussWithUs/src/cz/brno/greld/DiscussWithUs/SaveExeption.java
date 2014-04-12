package cz.brno.greld.DiscussWithUs;

/**
 * If SQL query was unsuccessful
 * @author Jan Kucera
 *
 */
public class SaveExeption extends Exception {

	private static final long serialVersionUID = 20028879886014149L;

	public SaveExeption(String detailMessage) {
		super(detailMessage);
	}

	
}

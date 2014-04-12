package cz.brno.greld.DiscussWithUs;

/**
 * If PHP returns something else then it is expected by JAVA
 * @author Jan Kucera
 *
 */
public class MistakeInJSONException extends Exception {

	private static final long serialVersionUID = 3398552860114814874L;

	public MistakeInJSONException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}
	
	public MistakeInJSONException(String detailMessage) {
		super(detailMessage);
	}

	
}

/**
 * package tgdh
 */
package tgdh;

/**
 * This class implements the exception for TGDH.
 * 
 * @author Lijun Liao (<a href="mailto:lijun.liao@rub.de">lijun.liao@rub.de)
 * @version 1.0
 */
public class TgdhException extends Exception {

	/**
	 * Default constructor
	 */
	public TgdhException() {
		super();
	}

	/**
	 * Constructs a new exception with detail message.
	 * @param message the detail message
	 */
	public TgdhException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with detail cause.
	 * @param cause  the detail cause
	 */
	public TgdhException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new exception with detail message and cause.
	 * @param message   the detail message
	 * @param cause     the detail cause
	 */
	public TgdhException(String message, Throwable cause) {
		super(message, cause);
	}

}

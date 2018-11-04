package xxx.joker.apps.wmod.exception;

import xxx.joker.libs.core.exception.JkException;

/**
 * Created by f.barbano on 12/12/2017.
 */
public class WmodException extends JkException {

	public WmodException(String message, Object... params) {
		super(String.format(message, params));
	}

	public WmodException(Throwable cause, String message, Object... params) {
		super(String.format(message, params), cause);
	}

	public WmodException(Throwable cause) {
		super(cause);
	}
}

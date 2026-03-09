package se.sundsvall.users.utility;

import java.io.Serial;

public class EncryptionException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 1L;

	public EncryptionException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public EncryptionException(final String message) {
		super(message);
	}
}

package se.sundsvall.users.utility;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class EncryptionExceptionTest {
	@Test
	void testConstructor() {
		final var message = "someMessage";

		assertThat(new EncryptionException("someMessage")).hasMessage(message);
	}

	@Test
	void testConstructorWithCause() {
		final var message = "someMessage";
		final var cause = new RuntimeException();

		assertThat(new EncryptionException("someMessage", new RuntimeException())).hasMessage(message).hasCause(cause);
	}
}

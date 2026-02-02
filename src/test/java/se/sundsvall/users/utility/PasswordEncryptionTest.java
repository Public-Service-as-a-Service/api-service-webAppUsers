package se.sundsvall.users.utility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.users.configuration.CredentialsProperties;

@ExtendWith(MockitoExtension.class)
class PasswordEncryptionTest {
	@Mock
	private CredentialsProperties credentialsProperties;

	@InjectMocks
	private PasswordEncryption encryptionUtilityMock;

	@Test
	void encryptAndDecrypt() {
		final var input = "someInput";

		when(credentialsProperties.secretKey()).thenReturn("WbVG8XC%m&9Z!7a$xyKGWzB^#kUSoUUs");

		final var encodedResult = encryptionUtilityMock.encrypt(input);
		final var decodedResult = encryptionUtilityMock.decrypt(encodedResult);

		assertThat(decodedResult).isEqualTo(input);

	}
}

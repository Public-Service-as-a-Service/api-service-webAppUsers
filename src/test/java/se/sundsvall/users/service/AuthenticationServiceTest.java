package se.sundsvall.users.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.users.api.model.JwtResponse;
import se.sundsvall.users.api.model.LoginRequest;
import se.sundsvall.users.integration.db.UserRepository;
import se.sundsvall.users.integration.db.model.UserEntity;
import se.sundsvall.users.utility.JwtUtil;
import se.sundsvall.users.utility.PasswordEncryption;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
	@Mock
	private UserRepository userRepositoryMock;
	@Mock
	private PasswordEncryption passwordEncryptionMock;
	@Mock
	private JwtUtil jwtServiceMock;
	@InjectMocks
	private AuthenticationService authenticationService;

	@Test
	void login() {
		final var loginRequest = LoginRequest.create()
			.withPassword("password")
			.withEmail("test@email.se");
		final var token = "token";
		final var encryptedPassword = "encryptedPassword";
		final UserEntity userEntity = UserEntity.create()
			.withEmail("test@email.se")
			.withPassword("encryptedPassword");
		when(userRepositoryMock.findByEmail(loginRequest.getEmail()))
			.thenReturn(Optional.of(userEntity));
		when(passwordEncryptionMock.decrypt(encryptedPassword))
			.thenReturn("password");
		when(jwtServiceMock.generateToken(loginRequest.getEmail()))
			.thenReturn(token);

		JwtResponse response = authenticationService.login(loginRequest);
		assertThat(response).isNotNull();
		assertThat(response.getToken()).isEqualTo(token);
	}
}

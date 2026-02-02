package se.sundsvall.users.utility;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

import io.jsonwebtoken.*;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTest {

	@InjectMocks
	private JwtUtil jwtUtil;

	@Mock
	private JwtBuilder jwtBuilder;
	@Mock
	private JwtParser jwtParser;
	@Mock
	private JwtParserBuilder jwtParserBuilder;

	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(jwtUtil, "secret", "dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLWhzMjU2LWFsZ29yaXRobQ==");
		ReflectionTestUtils.setField(jwtUtil, "expiration", 3600L);
	}

	@Test
	void generateToken() {
		final var email = "test@email";

		var token = jwtUtil.generateToken(email);

		assertThat(token).isNotNull();
		assertThat(token).isNotEmpty();
		assertThat(jwtUtil.extractUsername(token)).isEqualTo(email);

	}

	@Test
	void extractUsername() {
		final var email = "test@email";
		final var token = jwtUtil.generateToken(email);

		final var extractedEmail = jwtUtil.extractUsername(token);
		assertThat(extractedEmail).isEqualTo(email);

	}

	@Test
	void extractExpiration() {
		final var email = "test@email";
		final var token = jwtUtil.generateToken(email);

		final var expiration = jwtUtil.extractExpiration(token);

		assertThat(expiration).isAfter(new Date());
	}

	@Test
	void validateToken_success() {
		final var email = "test@email.se";
		final var token = jwtUtil.generateToken(email);

		final var isValid = jwtUtil.validateToken(token, email);
		assertThat(isValid).isTrue();
	}

	@Test
	void validateToken_fail() {
		final var email = "test@email.se";
		final var differentEmail = "different email";
		final var token = jwtUtil.generateToken(email);
		final var isValid = jwtUtil.validateToken(token, differentEmail);
		assertThat(isValid).isFalse();
	}

	@Test
	void extractClaim() {
		final var email = "test@email.se";
		final var token = jwtUtil.generateToken(email);

		final var subject = jwtUtil.extractClaim(token, Claims::getSubject);
		assertThat(subject).isEqualTo(email);
	}
}

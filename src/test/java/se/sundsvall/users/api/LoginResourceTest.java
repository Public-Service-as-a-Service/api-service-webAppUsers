package se.sundsvall.users.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.users.Application;
import se.sundsvall.users.api.model.JwtResponse;
import se.sundsvall.users.api.model.LoginRequest;
import se.sundsvall.users.service.AuthenticationService;
import se.sundsvall.users.utility.JwtUtil;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")
class LoginResourceTest {

	@MockitoBean
	private AuthenticationService authenticationServiceMock;
	@MockitoBean
	private JwtUtil jwtUtilMock;

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void login() {
		final var jwtResponse = new JwtResponse("token");
		final var loginRequest = LoginRequest.create()
			.withEmail("Test@email.se")
			.withPassword("password");

		when(authenticationServiceMock.login(any(LoginRequest.class))).thenReturn(jwtResponse);
		final var response = webTestClient.post()
			.uri("/api/auth/login")
			.contentType(APPLICATION_JSON)
			.bodyValue(loginRequest)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().exists("Set-Cookie")
			.expectCookie().exists("token")
			.expectBody(String.class)
			.isEqualTo("Logged in!")
			.returnResult();

		final var setCookie = response.getResponseHeaders().getFirst("Set-Cookie");

		assertThat(setCookie).isNotNull();
		assertThat(setCookie).contains("token=");
		assertThat(setCookie).contains("HttpOnly");
		assertThat(setCookie).contains("Path=/");
		assertThat(setCookie).contains("SameSite=Strict");
		assertThat(setCookie).doesNotContain("SemeSite=Secure");
	}

	@Test
	void logoutTest() {
		final var response = webTestClient.post()
			.uri("/api/auth/logout")
			.exchange()
			.expectStatus().isOk()
			.expectHeader().exists("Set-Cookie")
			.expectCookie().exists("token")
			.expectBody(String.class)
			.isEqualTo("Logged out")
			.returnResult();

		final var setCookie = response.getResponseHeaders().getFirst("Set-Cookie");

		assertThat(setCookie).isNotNull();
		assertThat(setCookie).contains("token=");
		assertThat(setCookie).contains("Max-Age=0");
		assertThat(setCookie).contains("HttpOnly");
		assertThat(setCookie).contains("Path=/");
		assertThat(setCookie).contains("SameSite=Strict");
		assertThat(setCookie).doesNotContain("Secure");
	}
}

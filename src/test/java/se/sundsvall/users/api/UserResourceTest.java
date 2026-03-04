package se.sundsvall.users.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import se.sundsvall.users.Application;
import se.sundsvall.users.api.model.UpdateUserRequest;
import se.sundsvall.users.api.model.UserRequest;
import se.sundsvall.users.api.model.UserResponse;
import se.sundsvall.users.service.UserService;
import se.sundsvall.users.utility.JwtUtil;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("junit")

class UserResourceTest {

	@MockitoBean
	private UserService userServiceMock;

	@MockitoBean
	private JwtUtil jwtUtilMock;
	@Autowired
	private WebTestClient webTestClient;

	@BeforeEach
	public void setup() {
		webTestClient = webTestClient.mutate()
			.defaultHeader("Authorization", "Bearer test-token")
			.build();

		when(jwtUtilMock.validateToken(any(), any())).thenReturn(true);
		when(jwtUtilMock.extractUsername(any())).thenReturn("test@testmail.com");

	}

	@Test
	void createUser() {

		final var userRequest = UserRequest.create()
			.withEmail("test@testmail.com")
			.withMunicipalityId("2281")
			.withPhoneNumber("0701740649")
			.withPassword("password")
			.withStatus("ACTIVE");
		final var userResponse = UserResponse.create()
			.withEmail("test@testmail.com")
			.withMunicipalityId("2281")
			.withPhoneNumber("0701740649")
			.withStatus("ACTIVE");

		when(userServiceMock.createUser(any(UserRequest.class))).thenReturn(userResponse);
		var response = webTestClient.post()
			.uri("/api/users")
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(userRequest)
			.exchange()
			.expectStatus().isCreated()
			.expectBody(UserResponse.class)
			.returnResult()
			.getResponseBody();

		// Assert
		assertThat(response).isEqualTo(userResponse);
		verify(userServiceMock).createUser(userRequest);
		verifyNoMoreInteractions(userServiceMock);
	}

	@Test
	void getUserByEmail() {
		final var email = "kalle.kula@sundsvall.se";
		final var userResponse = UserResponse.create()
			.withEmail(email)
			.withMunicipalityId("2281")
			.withPhoneNumber("0701740629")
			.withStatus("ACTIVE");

		when(userServiceMock.getUserByEmail(email)).thenReturn(userResponse);

		final var response = webTestClient.get()
			.uri("/api/users/emails/{email}", email)
			.exchange()
			.expectStatus().isOk()
			.expectBody(UserResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(userResponse);
		verify(userServiceMock).getUserByEmail(email);
	}

	@Test
	void getUserByPartyId() {
		final var id = 1L;
		final var userResponse = UserResponse.create()
			.withId(id)
			.withMunicipalityId("2281")
			.withPhoneNumber("0701740629")
			.withStatus("ACTIVE");

		when(userServiceMock.getUserById(id)).thenReturn(userResponse);

		final var response = webTestClient.get()
			.uri("/api/users/ids/{id}", id)
			.exchange()
			.expectStatus().isOk()
			.expectBody(UserResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(userResponse);
		verify(userServiceMock).getUserById(id);
	}

	@Test
	void updatePassword() {
		final var email = "test@email.se";
		final var password = "password";

		doNothing().when(userServiceMock).updateUserPassword(email, password);
		final var response = webTestClient.patch()
			.uri("/api/users/emails/{email}/password", email)
			.bodyValue(password)
			.exchange()
			.expectStatus().isNoContent();

		verify(userServiceMock).updateUserPassword(email, password);
	}

	@Test
	void updateUserByEmail() {
		final var email = "test@test.com";
		final var userRequest = UpdateUserRequest.create()
			.withMunicipalityId("2281")
			.withPhoneNumber("0701740669")
			.withStatus("ACTIVE");
		final var userResponse = UserResponse.create()
			.withEmail(email)
			.withMunicipalityId("2281")
			.withPhoneNumber("0701740669")
			.withStatus("ACTIVE");

		when(userServiceMock.updateUserByEmail(any(UpdateUserRequest.class), eq(email))).thenReturn(userResponse);

		final var response = webTestClient.patch()
			.uri("api/users/emails/{email}", email)
			.bodyValue(userRequest)
			.exchange()
			.expectStatus().isCreated()
			.expectBody(UserResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(userResponse);
		verify(userServiceMock).updateUserByEmail(userRequest, email);

	}

	@Test
	void updateUserByPartyId() {
		final var id = 1L;
		final var userRequest = UpdateUserRequest.create()
			.withMunicipalityId("2281")
			.withPhoneNumber("0701740669")
			.withStatus("ACTIVE");
		final var userResponse = UserResponse.create()
			.withEmail("test@test.com")
			.withMunicipalityId("2281")
			.withPhoneNumber("0701740619")
			.withStatus("ACTIVE");

		when(userServiceMock.updateUserById(any(UpdateUserRequest.class), eq(id))).thenReturn(userResponse);

		final var response = webTestClient.patch()
			.uri("api/users/ids/{id}", id)
			.bodyValue(userRequest)
			.exchange()
			.expectStatus().isCreated()
			.expectBody(UserResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(userResponse);
		verify(userServiceMock).updateUserById(userRequest, id);

	}

	@Test
	void deleteUserByEmail() {
		final var email = "test@testmail.com";

		doNothing().when(userServiceMock).deleteUserByEmail(email);
		final var response = webTestClient.delete()
			.uri("api/users/emails/{email}", email)
			.exchange()
			.expectStatus().isNoContent()
			.expectBody(UserResponse.class)
			.returnResult()
			.getResponseBody();

		verify(userServiceMock).deleteUserByEmail(email);

	}

	@Test
	void deleteUserByPartyId() {
		final var id = 1L;

		doNothing().when(userServiceMock).deleteUserById(id);
		final var response = webTestClient.delete()
			.uri("api/users/ids/{id}", id)
			.exchange()
			.expectStatus().isNoContent()
			.expectBody(UserResponse.class)
			.returnResult()
			.getResponseBody();

		verify(userServiceMock).deleteUserById(id);

	}

}

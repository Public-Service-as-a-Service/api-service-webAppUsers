package se.sundsvall.users.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
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

	@Mock
	private JwtUtil jwtUtilMock;
	@Autowired
	private WebTestClient webTestClient;

	@BeforeEach
	public void setup() {
		ReflectionTestUtils.setField(jwtUtilMock, "secret", "dGVzdC1zZWNyZXQta2V5LXRoYXQtaXMtbG9uZy1lbm91Z2gtZm9yLWhzMjU2LWFsZ29yaXRobQ==");
		ReflectionTestUtils.setField(jwtUtilMock, "expiration", 3600L);
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
	void getUserByPersonalNumber() {
		final var personalNumber = "198001011234";
		final var municipalityId = "2281";
		final var userResponse = UserResponse.create()
			.withEmail(personalNumber)
			.withMunicipalityId("2281")
			.withPhoneNumber("0701740629")
			.withStatus("ACTIVE");

		when(userServiceMock.getUserByPersonalNumber(personalNumber, municipalityId)).thenReturn(userResponse);

		final var response = webTestClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("api/users/personalNumbers/{personalNumber}")
				.queryParam("municipalityId", municipalityId)
				.build(personalNumber))
			.exchange()
			.expectStatus().isOk()
			.expectBody(UserResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(userResponse);
		verify(userServiceMock).getUserByPersonalNumber(personalNumber, municipalityId);
	}

	@Test
	void getUserByPartyId() {
		final var partyId = UUID.randomUUID().toString();
		final var userResponse = UserResponse.create()
			.withPartyId(partyId)
			.withMunicipalityId("2281")
			.withPhoneNumber("0701740629")
			.withStatus("ACTIVE");

		when(userServiceMock.getUserByPartyId(partyId)).thenReturn(userResponse);

		final var response = webTestClient.get()
			.uri("/api/users/partyIds/{partyId}", partyId)
			.exchange()
			.expectStatus().isOk()
			.expectBody(UserResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(userResponse);
		verify(userServiceMock).getUserByPartyId(partyId);
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
		final var partyId = UUID.randomUUID().toString();
		final var userRequest = UpdateUserRequest.create()
			.withMunicipalityId("2281")
			.withPhoneNumber("0701740669")
			.withStatus("ACTIVE");
		final var userResponse = UserResponse.create()
			.withEmail("test@test.com")
			.withMunicipalityId("2281")
			.withPhoneNumber("0701740619")
			.withStatus("ACTIVE");

		when(userServiceMock.updateUserByPartyId(any(UpdateUserRequest.class), eq(partyId))).thenReturn(userResponse);

		final var response = webTestClient.patch()
			.uri("api/users/partyIds/{partyId}", partyId)
			.bodyValue(userRequest)
			.exchange()
			.expectStatus().isCreated()
			.expectBody(UserResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isEqualTo(userResponse);
		verify(userServiceMock).updateUserByPartyId(userRequest, partyId);

	}

	@Test
	void updateUserByPersonalNumber() {
		final var personalNumber = "198001011234";
		final var municipalityId = "2281";
		final var userRequest = UpdateUserRequest.create()
			.withMunicipalityId("2281")
			.withPhoneNumber("0701740669")
			.withStatus("ACTIVE");
		final var userResponse = UserResponse.create()
			.withPartyId(UUID.randomUUID().toString())
			.withEmail("test@test.com")
			.withMunicipalityId("2281")
			.withPhoneNumber("0701740669")
			.withStatus("ACTIVE");

		when(userServiceMock.updateUserByPersonalNumber(any(UpdateUserRequest.class), eq(personalNumber), eq(municipalityId)))
			.thenReturn(userResponse);

		final var response = webTestClient.patch()
			.uri(uriBuilder -> uriBuilder
				.path("api/users/personalNumbers/{personalNumber}")
				.queryParam("municipalityId", municipalityId)
				.build(personalNumber)).bodyValue(userRequest)
			.exchange()
			.expectStatus().isCreated()
			.expectBody(UserResponse.class)
			.returnResult()
			.getResponseBody();

		assertThat(response).isNotNull();
		assertThat(response).isEqualTo(userResponse);

		verify(userServiceMock).updateUserByPersonalNumber(userRequest, personalNumber, municipalityId);
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
	void deleteUserByPersonalNumber() {
		final var personalNumber = "198001011234";
		final var municipalityId = "2281";

		doNothing().when(userServiceMock).deleteUserByPN(personalNumber, municipalityId);
		final var response = webTestClient.delete()
			.uri(uriBuilder -> uriBuilder
				.path("api/users/personalNumbers/{personalNumber}")
				.queryParam("municipalityId", municipalityId)
				.build(personalNumber)).exchange()
			.expectStatus().isNoContent()
			.expectBody(UserResponse.class)
			.returnResult()
			.getResponseBody();

		verify(userServiceMock).deleteUserByPN(personalNumber, municipalityId);

	}

	@Test
	void deleteUserByPartyId() {
		final var partyId = UUID.randomUUID().toString();

		doNothing().when(userServiceMock).deleteUserByPartyId(partyId);
		final var response = webTestClient.delete()
			.uri("api/users/partyIds/{partyId}", partyId)
			.exchange()
			.expectStatus().isNoContent()
			.expectBody(UserResponse.class)
			.returnResult()
			.getResponseBody();

		verify(userServiceMock).deleteUserByPartyId(partyId);

	}

}

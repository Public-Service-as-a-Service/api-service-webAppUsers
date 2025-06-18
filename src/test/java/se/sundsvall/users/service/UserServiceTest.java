package se.sundsvall.users.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zalando.problem.Problem;
import se.sundsvall.users.api.model.UpdateUserRequest;
import se.sundsvall.users.api.model.UserRequest;
import se.sundsvall.users.api.model.UserResponse;
import se.sundsvall.users.integration.citizen.CitizenIntegration;
import se.sundsvall.users.integration.db.UserRepository;
import se.sundsvall.users.integration.db.model.Enum.Status;
import se.sundsvall.users.integration.db.model.UserEntity;
import se.sundsvall.users.service.Mapper.UserMapper;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepositoryMock;

	@Mock
	private CitizenIntegration citizenIntegrationMock;

	@Mock
	private UserMapper userMapperMock;

	@InjectMocks
	private UserService userService;

	@Test
	void getUserByEmail() {
		// Arrange
		final var email = "Test123@mail.com";
		final var userEntity = UserEntity.create().withPartyId(email);
		final var expectedUser = new UserResponse();

		when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(userEntity));
		when(userMapperMock.toUserResponse(userEntity)).thenReturn(expectedUser);

		// Act
		final var result = userService.getUserByEmail(email);

		// Assert
		assertThat(result).isSameAs(expectedUser);
		verify(userRepositoryMock).findByEmail(email);
		verify(userMapperMock).toUserResponse(userEntity);

	}

	@Test
	void getUserByPersonalNumber() {
		// Arrange
		final var personalNumber = "198001011234";
		final var municipalityId = "2281";
		final var partyId = UUID.randomUUID().toString();
		final var userEntity = UserEntity.create().withPartyId(partyId);
		final var expectedUserResponse = UserResponse.create().withPartyId(partyId);

		// Mocks
		when(citizenIntegrationMock.getCitizenPartyId(personalNumber, municipalityId)).thenReturn(partyId);
		when(userRepositoryMock.findByPartyId(partyId)).thenReturn(Optional.of(userEntity));
		when(userMapperMock.toUserResponse(userEntity)).thenReturn(expectedUserResponse);

		// Act
		final var result = userService.getUserByPersonalNumber(personalNumber, municipalityId);

		// Assert
		assertThat(result).isSameAs(expectedUserResponse);
		verify(citizenIntegrationMock).getCitizenPartyId(personalNumber, municipalityId);
		verify(userRepositoryMock).findByPartyId(partyId);
		verify(userMapperMock).toUserResponse(userEntity);
	}

	@Test
	void getUserByPartyId() {
		// Arrange
		final var partyId = UUID.randomUUID().toString();
		final var userEntity = UserEntity.create().withPartyId(partyId);
		final var expectedUser = new UserResponse();

		when(userRepositoryMock.findByPartyId(partyId)).thenReturn(Optional.of(userEntity));
		when(userMapperMock.toUserResponse(userEntity)).thenReturn(expectedUser);

		// Act
		final var result = userService.getUserByPartyId(partyId);

		// Assert
		assertThat(result).isSameAs(expectedUser);
		verify(userRepositoryMock).findByPartyId(partyId);
		verify(userMapperMock).toUserResponse(userEntity);

	}

	@Test
	void createUser() {
		// Arrange
		final var email = "Test@testmail.com";
		final var phoneNumber = "0701740669";
		final var municipalityId = "2281";
		final var status = "ACTIVE";

		// Build request and expected entities/responses
		var userRequest = UserRequest.create()
			.withEmail(email)
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus(status);

		var userEntity = UserEntity.create()
			.withEmail(email)
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus(Status.valueOf(status));

		var userResponse = UserResponse.create()
			.withEmail(email)
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus(status);

		when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.empty());
		when(userRepositoryMock.save(userEntity)).thenReturn(userEntity);
		when(userMapperMock.toUserEntity(eq(userRequest), anyString())).thenReturn(userEntity);
		when(userMapperMock.toUserResponse(userEntity)).thenReturn(userResponse);

		// Act
		var created = userService.createUser(userRequest);

		// Assert
		// Verify we saved exactly that entity instance
		verify(userRepositoryMock).save(userEntity);

		assertThat(created).isNotNull();
		assertThat(created).isEqualTo(userResponse);
	}

	@Test
	void updateUserEmail() {
		// Arrange
		final var email = "Test@testmail.se";
		final var phoneNumber = "0701740619";
		final var municipalityId = "2281";
		final var status = "ACTIVE";
		final var userRequestMock = UpdateUserRequest.create()
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus(status);
		final var userEntity = UserEntity.create().withEmail(email)
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus(Status.valueOf(status));
		final var userResponseMock = UserResponse.create().withEmail(email)
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus(status);
		// Mock
		when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(userEntity));

		when(userRepositoryMock.save(userEntity)).thenReturn(userEntity);
		when(userMapperMock.toUserResponse(userEntity)).thenReturn(userResponseMock);

		// Act
		final var updatedUser = userService.updateUserByEmail(userRequestMock, email);

		// Verify/Assert
		verify(userRepositoryMock).save(same(userEntity));
		assertThat(updatedUser).isNotNull();
		assertThat(updatedUser).isEqualTo(userResponseMock);
	}

	@Test
	void updateUserByPartyId() {
		// Arrange
		final var partyId = UUID.randomUUID().toString();
		final var phoneNumber = "0701740619";
		final var municipalityId = "2281";
		final var status = "ACTIVE";
		final var userRequestMock = UpdateUserRequest.create()
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus(status);
		final var userEntity = UserEntity.create().withPartyId(partyId)
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus(Status.valueOf(status));
		final var userResponseMock = UserResponse.create().withPartyId(partyId)
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus(status);
		// Mock
		when(userRepositoryMock.findByPartyId(partyId)).thenReturn(Optional.of(userEntity));

		when(userRepositoryMock.save(userEntity)).thenReturn(userEntity);
		when(userMapperMock.toUserResponse(userEntity)).thenReturn(userResponseMock);

		// Act
		final var updatedUser = userService.updateUserByPartyId(userRequestMock, partyId);

		// Verify/Assert
		verify(userRepositoryMock).save(same(userEntity));
		assertThat(updatedUser).isNotNull();
		assertThat(updatedUser).isEqualTo(userResponseMock);
	}

	@Test
	void updateUserByPersonalNumber() {
		// Arrange
		final var personalNumber = "198001011234";
		final var phoneNumber = "0701740619";
		final var municipalityId = "2281";
		final var partyId = UUID.randomUUID().toString();
		final var userRequestMock = UpdateUserRequest.create()
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus("ACTIVE");
		final var existingUserEntity = UserEntity.create().withPartyId(partyId)
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus(Status.SUSPENDED);
		final var updatedUserEntity = UserEntity.create().withPartyId(partyId).withPhoneNumber(phoneNumber);
		final var userResponseMock = UserResponse.create().withPartyId(partyId)
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus("ACTIVE");
		// Mock
		when(citizenIntegrationMock.getCitizenPartyId(personalNumber, municipalityId)).thenReturn(partyId);
		when(userRepositoryMock.findByPartyId(partyId)).thenReturn(Optional.of(existingUserEntity));
		when(userRepositoryMock.save(any(UserEntity.class))).thenReturn(updatedUserEntity);
		when(userMapperMock.toUserResponse(any(UserEntity.class))).thenReturn(userResponseMock);
		// Act
		final var result = userService.updateUserByPersonalNumber(userRequestMock, personalNumber, municipalityId);
		// Verify/Assert
		assertThat(result).isEqualTo(userResponseMock);
		verify(citizenIntegrationMock).getCitizenPartyId(personalNumber, municipalityId);
		verify(userRepositoryMock).findByPartyId(partyId);
		verify(userRepositoryMock).save(any(UserEntity.class));
		assertThat(updatedUserEntity).isNotNull();
	}

	@Test
	void deleteUserByEmail() {

		// Arrange
		final var email = "Test@testmail.se";

		// Act
		userService.deleteUserByEmail(email);

		// Verify/Assert
		verify(userRepositoryMock).deleteByEmail(email);

	}

	@Test
	void deleteUserByPersonalNumber() {

		// Arrange
		final var personalNumber = "198001011234";

		// Act
		userService.deleteUserByEmail(personalNumber);

		// Verify/Assert
		verify(userRepositoryMock).deleteByEmail(personalNumber);

	}

	@Test
	void deleteUserByPartyId() {

		// Arrange
		final var partyId = UUID.randomUUID().toString();

		// Act
		userService.deleteUserByPartyId(partyId);

		// Verify/Assert
		verify(userRepositoryMock).deleteByPartyId(partyId);

	}

	@Test
	void createUserAlreadyExists() {
		// Arrange
		final var email = "Test@testmail.se";
		final var userRequest = new UserRequest();
		userRequest.setEmail(email);

		// Mock
		when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.of(new UserEntity()));

		// Act & Assert
		final var exception = assertThrows(Throwable.class, () -> userService.createUser(userRequest));

		assertThat((exception))
			.isInstanceOf(Problem.class)
			.hasMessageContaining("user already exists");

		verify(userRepositoryMock).findByEmail(email);
		verify(userRepositoryMock, never()).save(any());
		verifyNoMoreInteractions(userRepositoryMock, userMapperMock);
	}

	@Test
	void getUserByEmailNotFound() {
		// Arrange
		final var email = "Test@testmail.com";

		when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.empty());

		// Act
		final var exception = assertThrows(Throwable.class, () -> userService.getUserByEmail(email));
		// Assert
		assertThat(exception)
			.isInstanceOf(Problem.class)
			.hasMessageContaining("user " + email + " was not found");

		verify(userRepositoryMock).findByEmail(email);
		verify(userRepositoryMock, never()).getReferenceById(any());
		verify(userMapperMock, never()).toUserResponse(any());
		verifyNoMoreInteractions(userRepositoryMock, userMapperMock);
	}

	@Test
	void updateUserNotFound() {
		// Arrange
		final var email = "Test@testmail.se";
		final var request = UpdateUserRequest.create();

		// Mock
		when(userRepositoryMock.findByEmail(email)).thenReturn(Optional.empty());
		final var problem = assertThrows(Throwable.class, () -> userService.updateUserByEmail(request, email));

		// Assert
		assertThat(problem).hasMessage("Not Found: user " + email + " was not found");
		assertThat(problem).isNotNull();
	}

}

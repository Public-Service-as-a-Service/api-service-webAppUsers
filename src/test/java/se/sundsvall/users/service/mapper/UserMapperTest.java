package se.sundsvall.users.service.Mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.users.api.model.UserRequest;
import se.sundsvall.users.integration.db.model.Enum.Status;
import se.sundsvall.users.integration.db.model.UserEntity;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

	@InjectMocks
	UserMapper userMapper;

	@Test
	void toUserResponse() {
		// Arrange
		final var email = "TestMail123@mail.se";
		final var phoneNumber = "99070121212";
		final var municipalityId = "2281";
		final var status = "ACTIVE";

		final var userEntity = UserEntity.create().withEmail(email)
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus(Status.valueOf(status));

		// Act
		final var result = userMapper.toUserResponse(userEntity);

		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getEmail()).isEqualTo(email);
		assertThat(result.getPhoneNumber()).isEqualTo(phoneNumber);
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getStatus()).isEqualTo(status);
	}

	@Test
	void toUserEntity() {
		// Arrange
		final var email = "TestMail123@mail.se";
		final var phoneNumber = "99070121212";
		final var municipalityId = "2281";
		final var status = "ACTIVE";
		final String partyId = "d7f2aff6-dab9-4cdf-b366-61cde975a26b";

		final var userRequest = UserRequest.create().withEmail(email)
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus(status);
		// Act
		final var result = userMapper.toUserEntity(userRequest, partyId);
		// Assert
		assertThat(result).isNotNull();
		assertThat(result.getPartyId()).isEqualTo(partyId);
		assertThat(result.getEmail()).isEqualTo(email);
		assertThat(result.getPhoneNumber()).isEqualTo(phoneNumber);
		assertThat(result.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(result.getStatus()).isEqualTo(Status.valueOf(status));
	}

//    @Test
//    void toUserEntityWhenNull() {
//    }
}

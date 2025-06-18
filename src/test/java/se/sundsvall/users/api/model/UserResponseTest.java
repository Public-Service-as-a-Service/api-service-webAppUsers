package se.sundsvall.users.api.model;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.AllOf.allOf;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import se.sundsvall.users.integration.db.model.UserEntity;

class UserResponseTest {

	@Test
	void testBean() {
		MatcherAssert.assertThat(UserEntity.class, allOf(
			hasValidBeanConstructor(),
			hasValidGettersAndSetters()));
	}

	@Test
	void testBuildMethod() {
		final var email = "email";
		final var phoneNumber = "phoneNumber";
		final var municipalityId = "municipalityId";
		final var status = "ACTIVE";

		final var userResponse = UserResponse.create()
			.withEmail(email)
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus(status);

		assertThat(userResponse.getEmail()).isEqualTo(email);
		assertThat(userResponse.getPhoneNumber()).isEqualTo(phoneNumber);
		assertThat(userResponse.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(userResponse.getStatus()).isEqualTo(status);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(UserResponse.create()).hasAllNullFieldsOrProperties();
	}
}

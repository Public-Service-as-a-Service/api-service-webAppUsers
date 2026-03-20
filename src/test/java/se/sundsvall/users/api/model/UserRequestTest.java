package se.sundsvall.users.api.model;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import se.sundsvall.users.integration.db.model.UserEntity;

import static com.google.code.beanmatchers.BeanMatchers.hasValidBeanConstructor;
import static com.google.code.beanmatchers.BeanMatchers.hasValidGettersAndSetters;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.AllOf.allOf;

class UserRequestTest {

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

		final var userRequest = UserRequest.create()
			.withEmail(email)
			.withPhoneNumber(phoneNumber)
			.withMunicipalityId(municipalityId)
			.withStatus(status);

		assertThat(userRequest.getEmail()).isEqualTo(email);
		assertThat(userRequest.getPhoneNumber()).isEqualTo(phoneNumber);
		assertThat(userRequest.getMunicipalityId()).isEqualTo(municipalityId);
		assertThat(userRequest.getStatus()).isEqualTo(status);
	}

	@Test
	void testNoDirtOnCreatedBean() {
		assertThat(UserResponse.create()).hasAllNullFieldsOrProperties();
	}
}

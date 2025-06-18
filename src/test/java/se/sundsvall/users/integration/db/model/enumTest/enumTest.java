package se.sundsvall.users.integration.db.model.enumTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.users.api.validation.ValidEnum;
import se.sundsvall.users.api.validation.impl.EnumValidator;

@ExtendWith(MockitoExtension.class)

class EnumValidatorTest {

	enum TestStatus {
		ACTIVE, INACTIVE
	}

	private final ConstraintValidatorContext context = mock(ConstraintValidatorContext.class);

	@Test
	void testValidEnumValue_caseSensitive() {
		ValidEnum annotation = mock(ValidEnum.class);
		doAnswer(invocation -> (Class<? extends Enum<?>>) TestStatus.class)
			.when(annotation).enumClass();

		when(annotation.ignoreCase()).thenReturn(false);

		EnumValidator validator = new EnumValidator();
		validator.initialize(annotation);

		assertThat(validator.isValid("ACTIVE", context)).isTrue();
		assertThat(validator.isValid("INACTIVE", context)).isTrue();
		assertThat(validator.isValid("active", context)).isFalse(); // case-sensitive
	}
}

package se.sundsvall.users.configuration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import se.sundsvall.users.integration.db.UserRepository;
import se.sundsvall.users.integration.db.model.Enum.Status;
import se.sundsvall.users.integration.db.model.UserEntity;
import se.sundsvall.users.utility.PasswordEncryption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

	@InjectMocks
	private DataInitializer dataInitializer;

	@Mock
	private PasswordEncryption passwordEncryption;

	@Mock
	private UserRepository userRepository;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(dataInitializer, "email", "test@example.se");
		ReflectionTestUtils.setField(dataInitializer, "password", "password");
	}

	@Test
	void createUserWhenDatabaseIsEmpty() {
		when(userRepository.count()).thenReturn(0L);
		when(passwordEncryption.encrypt("password")).thenReturn("encryptedPassword");

		dataInitializer.run();

		var captor = ArgumentCaptor.forClass(UserEntity.class);
		verify(userRepository).save(captor.capture());

		var savedUser = captor.getValue();
		assertThat(savedUser.getEmail()).isEqualTo("test@example.se");
		assertThat(savedUser.getMunicipalityId()).isEqualTo("2281");
		assertThat(savedUser.getPhoneNumber()).isEqualTo("0701234567");
		assertThat(savedUser.getStatus()).isEqualTo(Status.ACTIVE);
		assertThat(savedUser.getPassword()).isEqualTo("encryptedPassword");
	}

	@Test
	void doNotCreateUserWhenDatabaseIsNotEmpty() {
		when(userRepository.count()).thenReturn(1L);

		dataInitializer.run();

		verify(userRepository, never()).save(any());
	}

	@Test
	void encryptPasswordBeforeSaving() {
		when(userRepository.count()).thenReturn(0L);
		when(passwordEncryption.encrypt("password")).thenReturn("encryptedPassword");

		dataInitializer.run();

		verify(passwordEncryption).encrypt("password");
		verify(userRepository).save(argThat(user -> user.getPassword().equals("encryptedPassword")));
	}

}

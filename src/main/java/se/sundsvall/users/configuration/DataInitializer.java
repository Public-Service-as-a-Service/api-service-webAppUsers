package se.sundsvall.users.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import se.sundsvall.users.integration.db.UserRepository;
import se.sundsvall.users.integration.db.model.Enum.Role;
import se.sundsvall.users.integration.db.model.Enum.Status;
import se.sundsvall.users.integration.db.model.UserEntity;
import se.sundsvall.users.service.UserService;
import se.sundsvall.users.utility.PasswordEncryption;

@Component
@Profile("!(junit | it)")
public class DataInitializer implements CommandLineRunner {
	@Value("${user.credentials.email}")
	String email;
	@Value("${user.credentials.password}")
	String password;

	UserRepository userRepository;
	UserService userService;
	PasswordEncryption passwordEncryption;

	public DataInitializer(UserRepository userRepository, UserService userService, PasswordEncryption passwordEncryption) {
		this.userRepository = userRepository;
		this.userService = userService;
		this.passwordEncryption = passwordEncryption;
	}

	@Override
	public void run(String... args) {
		var existing = userRepository.findByEmail(email);
		if (existing.isEmpty()) {
			userRepository.save(UserEntity.create()
				.withEmail(email)
				.withMunicipalityId("2281")
				.withPhoneNumber("0701234567")
				.withStatus(Status.ACTIVE)
				.withRole(Role.ADMIN)
				.withPassword(passwordEncryption.encrypt(password)));
		} else if (existing.get().getRole() != Role.ADMIN) {
			existing.get().setRole(Role.ADMIN);
			userRepository.save(existing.get());
		}
	}
}

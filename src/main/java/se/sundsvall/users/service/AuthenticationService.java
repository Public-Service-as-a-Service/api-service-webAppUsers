package se.sundsvall.users.service;

import static java.lang.String.format;
import static org.zalando.problem.Status.NOT_FOUND;
import static org.zalando.problem.Status.UNAUTHORIZED;

import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.users.api.model.JwtResponse;
import se.sundsvall.users.api.model.LoginRequest;
import se.sundsvall.users.integration.db.UserRepository;
import se.sundsvall.users.integration.db.model.UserEntity;
import se.sundsvall.users.utility.JwtUtil;
import se.sundsvall.users.utility.PasswordEncryption;

@Service
public class AuthenticationService {

	private final String USER_NOT_FOUND = "user %s was not found";
	private final UserRepository userRepository;
	private final PasswordEncryption passwordEncryption;
	private final JwtUtil jwtService;

	public AuthenticationService(UserRepository userRepository, PasswordEncryption passwordEncryption, JwtUtil jwtService) {
		this.userRepository = userRepository;
		this.passwordEncryption = passwordEncryption;
		this.jwtService = jwtService;
	}

	public JwtResponse login(LoginRequest loginRequest) {
		UserEntity user = userRepository.findByEmail(loginRequest.getEmail())
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(USER_NOT_FOUND, loginRequest.getEmail()))); // kanske borde slänga en UNAUTHORIZED men kan vara bra att veta var det går fel just nu

		if (!loginRequest.getPassword().equals(passwordEncryption.decrypt(user.getPassword()))) {
			throw Problem.valueOf(UNAUTHORIZED);
		}

		String token = jwtService.generateToken(user.getEmail());
		return new JwtResponse(token);
	}

}

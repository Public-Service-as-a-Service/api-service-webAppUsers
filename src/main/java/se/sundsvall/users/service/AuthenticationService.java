package se.sundsvall.users.service;

import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.*;
import se.sundsvall.users.api.model.JwtResponse;
import se.sundsvall.users.api.model.LoginRequest;
import se.sundsvall.users.integration.db.UserRepository;
import se.sundsvall.users.integration.db.model.UserEntity;
import se.sundsvall.users.integration.db.model.enums.Status;
import se.sundsvall.users.utility.JwtUtil;
import se.sundsvall.users.utility.PasswordEncryption;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthenticationService {

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
			.orElseThrow(() -> Problem.valueOf(UNAUTHORIZED, "Invalid credentials"));

		if (!loginRequest.getPassword().equals(passwordEncryption.decrypt(user.getPassword()))) {
			throw Problem.valueOf(UNAUTHORIZED, "Invalid credentials");
		}

		if (Status.SUSPENDED == user.getStatus()) {
			throw Problem.valueOf(FORBIDDEN, "Account suspended");
		}
		if (Status.INACTIVE == user.getStatus()) {
			throw Problem.valueOf(FORBIDDEN, "Account inactive");
		}

		String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
		return new JwtResponse(token);
	}

}

package se.sundsvall.users.service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import se.sundsvall.dept44.problem.*;
import se.sundsvall.users.api.model.UpdateUserRequest;
import se.sundsvall.users.api.model.UserRequest;
import se.sundsvall.users.api.model.UserResponse;
import se.sundsvall.users.integration.db.UserRepository;
import se.sundsvall.users.integration.db.model.enums.Role;
import se.sundsvall.users.integration.db.model.enums.Status;
import se.sundsvall.users.service.Mapper.UserMapper;
import se.sundsvall.users.utility.PasswordEncryption;

import static java.lang.String.format;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;

	private final UserMapper userMapper;

	private final PasswordEncryption passwordEncryption;

	private final String USER_NOT_FOUND = "user %s was not found";

	public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncryption passwordEncryption) {
		this.userRepository = userRepository;
		this.userMapper = userMapper;
		this.passwordEncryption = passwordEncryption;
	}

	public UserResponse createUser(UserRequest userRequest) {

		if (userRepository.findByEmail(userRequest.getEmail()).isEmpty()) {
			String encryptedPassword = passwordEncryption.encrypt(userRequest.getPassword());
			final var userEntity = userRepository.save(userMapper.toUserEntity(userRequest, encryptedPassword));
			return userMapper.toUserResponse(userEntity);
		}
		String USER_ALREADY_EXISTING = "user already exists";
		throw Problem.valueOf(CONFLICT, format(USER_ALREADY_EXISTING));
	}

	// READ
	public UserResponse getUserByEmail(String email) {
		return userRepository.findByEmail(email).map(userMapper::toUserResponse)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(USER_NOT_FOUND, email)));
	}

	public UserResponse getUserById(Long id) {
		return userRepository.findById(id).map(userMapper::toUserResponse)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(USER_NOT_FOUND, id)));
	}

	// UPDATE
	public UserResponse updateUserByEmail(UpdateUserRequest userRequest, String email) {

		var userEntity = userRepository.findByEmail(email)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(USER_NOT_FOUND, email)));

		var updated = userEntity
			.withEmail(email)
			.withPhoneNumber(userRequest.getPhoneNumber())
			.withMunicipalityId(userRequest.getMunicipalityId())
			.withStatus(Status.valueOf(userRequest.getStatus().toUpperCase()));

		if (userRequest.getRole() != null) {
			updated = updated.withRole(Role.valueOf(userRequest.getRole().toUpperCase()));
		}

		userRepository.save(updated);
		return userMapper.toUserResponse(updated);
	}

	public void updateUserPassword(String email, String password) {
		var userEntity = userRepository.findByEmail(email)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(USER_NOT_FOUND, email)));
		final String encryptedPassword = passwordEncryption.encrypt(password);
		userEntity.setPassword(encryptedPassword);
		userRepository.save(userEntity);
	}

	public UserResponse updateUserById(UpdateUserRequest userRequest, Long id) {

		var userEntity = userRepository.findById(id)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(USER_NOT_FOUND, id)));

		userRepository.save(userEntity
			.withId(id)
			.withPhoneNumber(userRequest.getPhoneNumber())
			.withMunicipalityId(userRequest.getMunicipalityId())
			.withStatus(Status.valueOf(userRequest.getStatus().toUpperCase())));

		return userMapper.toUserResponse(userEntity);
	}

	// DELETE
	public void deleteUserByEmail(String email) {
		userRepository.deleteByEmail(email);
	}

	public void deleteUserById(Long id) {
		userRepository.deleteById(id);
	}

	public List<UserResponse> getAllUsers() {
		var users = userRepository.findAll();
		var userResponse = new ArrayList<UserResponse>();
		for (var user : users) {
			userResponse.add(userMapper.toUserResponse(user));
		}
		return userResponse;
	}
}

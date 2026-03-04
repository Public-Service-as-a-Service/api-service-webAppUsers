package se.sundsvall.users.service;

import static java.lang.String.format;
import static org.zalando.problem.Status.CONFLICT;
import static org.zalando.problem.Status.NOT_FOUND;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.users.api.model.UpdateUserRequest;
import se.sundsvall.users.api.model.UserRequest;
import se.sundsvall.users.api.model.UserResponse;
import se.sundsvall.users.integration.db.UserRepository;
import se.sundsvall.users.integration.db.model.Enum.Status;
import se.sundsvall.users.service.Mapper.UserMapper;
import se.sundsvall.users.utility.PasswordEncryption;

@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;

	private final UserMapper userMapper;

	private final PasswordEncryption passwordEncryption;

	private final String USER_NOT_FOUND = "user %s was not found";
	private final String USER_ALREADY_EXISTING = "user already exists";

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

		userRepository.save(userEntity
			.withEmail(email)
			.withPhoneNumber(userRequest.getPhoneNumber())
			.withMunicipalityId(userRequest.getMunicipalityId())
			.withStatus(Status.valueOf(userRequest.getStatus().toUpperCase())));

		return userMapper.toUserResponse(userEntity);
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
}

package se.sundsvall.users.service;

import static java.lang.String.format;
import static org.zalando.problem.Status.CONFLICT;
import static org.zalando.problem.Status.NOT_FOUND;

import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.zalando.problem.Problem;
import se.sundsvall.users.api.model.UpdateUserRequest;
import se.sundsvall.users.api.model.UserRequest;
import se.sundsvall.users.api.model.UserResponse;
import se.sundsvall.users.integration.citizen.CitizenIntegration;
import se.sundsvall.users.integration.db.UserRepository;
import se.sundsvall.users.integration.db.model.Enum.Status;
import se.sundsvall.users.service.Mapper.UserMapper;

@Service
@Transactional
public class UserService {

	private final UserRepository userRepository;

	private final CitizenIntegration citizenIntegration;

	private final UserMapper userMapper;

	private final String USER_NOT_FOUND = "user %s was not found";
	private final String USER_ALREADY_EXISTING = "user already exists";

	public UserService(UserRepository userRepository, CitizenIntegration citizenIntegration, UserMapper userMapper) {
		this.userRepository = userRepository;
		this.citizenIntegration = citizenIntegration;
		this.userMapper = userMapper;
	}

	public UserResponse createUser(UserRequest userRequest) {

		String partyId = null;
		String personalNumber = userRequest.getPersonalNumber();
		String municipalityId = userRequest.getMunicipalityId();

		if (personalNumber != null && !personalNumber.isBlank()) {
			partyId = citizenIntegration.getCitizenPartyId(personalNumber, municipalityId);
		}
		if (partyId == null) {
			partyId = UUID.randomUUID().toString();
		}

		if (userRepository.findByEmail(userRequest.getEmail()).isEmpty() && userRepository.findById(partyId).isEmpty()) {
			final var userEntity = userRepository.save(userMapper.toUserEntity(userRequest, partyId));
			return userMapper.toUserResponse(userEntity);
		}
		throw Problem.valueOf(CONFLICT, format(USER_ALREADY_EXISTING));
	}

	// READ
	public UserResponse getUserByEmail(String email) {
		return userRepository.findByEmail(email).map(userMapper::toUserResponse)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(USER_NOT_FOUND, email)));
	}

	public UserResponse getUserByPersonalNumber(String personalNumber, String municipalityId) {
		var partyId = citizenIntegration.getCitizenPartyId(personalNumber, municipalityId);
		return userRepository.findByPartyId(partyId).map(userMapper::toUserResponse)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(USER_NOT_FOUND, personalNumber)));
	}

	public UserResponse getUserByPartyId(String partyId) {
		return userRepository.findByPartyId(partyId).map(userMapper::toUserResponse)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(USER_NOT_FOUND, partyId)));
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

	public UserResponse updateUserByPersonalNumber(UpdateUserRequest updateUserRequest, String personalNumber, String municipalityId) {

		var userEntity = userRepository.findByPartyId(citizenIntegration.getCitizenPartyId(personalNumber, municipalityId))
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(USER_NOT_FOUND, personalNumber)));

		userRepository.save(userEntity
			.withPhoneNumber(updateUserRequest.getPhoneNumber())
			.withMunicipalityId(updateUserRequest.getMunicipalityId())
			.withStatus(Status.valueOf(updateUserRequest.getStatus().toUpperCase())));

		return userMapper.toUserResponse(userEntity);
	}

	public UserResponse updateUserByPartyId(UpdateUserRequest userRequest, String partyId) {

		var userEntity = userRepository.findByPartyId(partyId)
			.orElseThrow(() -> Problem.valueOf(NOT_FOUND, format(USER_NOT_FOUND, partyId)));

		userRepository.save(userEntity
			.withPartyId(partyId)
			.withPhoneNumber(userRequest.getPhoneNumber())
			.withMunicipalityId(userRequest.getMunicipalityId())
			.withStatus(Status.valueOf(userRequest.getStatus().toUpperCase())));

		return userMapper.toUserResponse(userEntity);
	}

	// DELETE
	public void deleteUserByEmail(String email) {
		userRepository.deleteByEmail(email);
	}

	public void deleteUserByPartyId(String partyId) {
		userRepository.deleteByPartyId(partyId);
	}

	public void deleteUserByPN(String personalNumber, String municipalityId) {
		userRepository.deleteByPartyId(citizenIntegration.getCitizenPartyId(personalNumber, municipalityId));
	}
}

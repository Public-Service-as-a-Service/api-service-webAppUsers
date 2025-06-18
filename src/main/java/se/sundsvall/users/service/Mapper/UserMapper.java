package se.sundsvall.users.service.Mapper;

import java.util.Optional;
import org.springframework.stereotype.Component;
import se.sundsvall.users.api.model.UserRequest;
import se.sundsvall.users.api.model.UserResponse;
import se.sundsvall.users.integration.db.model.Enum.Status;
import se.sundsvall.users.integration.db.model.UserEntity;

@Component
public class UserMapper {

	public UserResponse toUserResponse(final UserEntity user) {
		return Optional.ofNullable(user)
			.map(entity -> UserResponse.create()
				.withEmail(entity.getEmail())
				.withPartyId(entity.getPartyId())
				.withPhoneNumber(entity.getPhoneNumber())
				.withMunicipalityId(entity.getMunicipalityId())
				.withStatus(String.valueOf(entity.getStatus())))
			.orElse(null);
	}

	public UserEntity toUserEntity(UserRequest userRequest, String partyId) {
		return Optional.ofNullable(userRequest)
			.map(request -> UserEntity.create()
				.withPartyId(partyId)
				.withEmail(request.getEmail())
				.withPhoneNumber(request.getPhoneNumber())
				.withMunicipalityId(request.getMunicipalityId())
				.withStatus(Status.valueOf(request.getStatus().toUpperCase())))
			.orElse(null);
	}
}

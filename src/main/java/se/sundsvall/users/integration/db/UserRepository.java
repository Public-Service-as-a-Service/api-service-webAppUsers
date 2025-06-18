package se.sundsvall.users.integration.db;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import se.sundsvall.users.integration.db.model.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {

	Optional<UserEntity> findByEmail(String email);

	Optional<UserEntity> findByPartyId(String personalNumber);

	void deleteByEmail(String email);

	void deleteByPartyId(String partyId);
}

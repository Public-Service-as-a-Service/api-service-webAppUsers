package se.sundsvall.users.integration.db.model;

import jakarta.persistence.*;
import java.io.Serializable;
import se.sundsvall.users.integration.db.model.Enum.Status;

@Entity
@Table(name = "users",
	indexes = {
		@Index(name = "idx_email_address", columnList = "email_address", unique = true)
	})
public class UserEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(nullable = false, name = "party_id")
	private String partyId;

	@Column(nullable = false, name = "email_address", unique = true)
	private String email;

	@Column(nullable = false, name = "phone_number")
	private String phoneNumber;

	@Column(nullable = false, name = "municipality_id")
	private String municipalityId;

	@Column(nullable = false, name = "status")
	@Enumerated(EnumType.STRING)
	private Status status;

	public static UserEntity create() {
		return new UserEntity();
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public UserEntity withPartyId(final String partyId) {
		this.partyId = partyId;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserEntity withEmail(String email) {
		this.email = email;
		return this;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public UserEntity withPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public UserEntity withMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public UserEntity withStatus(Status status) {
		this.status = status;
		return this;
	}
}

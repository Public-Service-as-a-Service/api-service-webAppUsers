package se.sundsvall.users.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

public class UserResponse {

	@Schema(description = "partyId", example = "7225dc69-28d1-4064-a1a8-5c1de5da0e62")
	private String partyId;
	@Schema(description = "Epost-adress", example = "kalle.kula@sundsvall.se")
	private String email;
	@Schema(description = "Telefonnummer", example = "0701740669")
	private String phoneNumber;
	@Schema(description = "Kommun-id", example = "2281")
	private String municipalityId;
	@Schema(description = "Status", example = "ACTIVE")
	private String status;

	public static UserResponse create() {
		return new UserResponse();
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public UserResponse withMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public UserResponse withPartyId(String partyId) {
		this.partyId = partyId;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserResponse withEmail(String email) {
		this.email = email;
		return this;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public UserResponse withPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public UserResponse withStatus(String status) {
		this.status = status;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(municipalityId, email, phoneNumber, status);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserResponse that = (UserResponse) o;
		return Objects.equals(email, that.email) && Objects.equals(phoneNumber, that.phoneNumber)
			&& Objects.equals(status, that.status) && Objects.equals(municipalityId, that.municipalityId);

	}
}

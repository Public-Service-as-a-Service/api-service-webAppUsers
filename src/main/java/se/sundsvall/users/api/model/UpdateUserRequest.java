package se.sundsvall.users.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;
import se.sundsvall.dept44.common.validators.annotation.ValidMobileNumber;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.users.api.validation.ValidEnum;
import se.sundsvall.users.integration.db.model.Enum.Status;

public class UpdateUserRequest {

	@Schema(description = "Telefonnummer", example = "0701740669")
	@NotBlank(message = "cannot be blank")
	@ValidMobileNumber(message = "must be a valid mobile number")
	private String phoneNumber;

	@Schema(description = "Kommun", example = "2281")
	@NotBlank(message = "cannot be blank")
	@ValidMunicipalityId(message = "must be a valid Municipality-ID")
	private String municipalityId;

	@Schema(description = "Status", example = "ACTIVE")
	@ValidEnum(message = "must be ACTIVE, INACTIVE or SUSPENDED", enumClass = Status.class, ignoreCase = true)
	private String status;

	public static UpdateUserRequest create() {
		return new UpdateUserRequest();
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public UpdateUserRequest withPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public String getMunicipalityId() {
		return municipalityId;
	}

	public void setMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
	}

	public UpdateUserRequest withMunicipalityId(String municipalityId) {
		this.municipalityId = municipalityId;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public UpdateUserRequest withStatus(String status) {
		this.status = status;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(municipalityId, phoneNumber, status);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UpdateUserRequest that = (UpdateUserRequest) o;
		return Objects.equals(phoneNumber, that.phoneNumber)
			&& Objects.equals(status, that.status) && Objects.equals(municipalityId, that.municipalityId);
	}
}

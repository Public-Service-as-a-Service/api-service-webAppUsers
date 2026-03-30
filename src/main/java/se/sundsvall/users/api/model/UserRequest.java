package se.sundsvall.users.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Objects;
import se.sundsvall.dept44.common.validators.annotation.ValidMobileNumber;
import se.sundsvall.users.api.validation.ValidEnum;
import se.sundsvall.users.api.validation.ValidMunicipalityName;
import se.sundsvall.users.integration.db.model.enums.Role;
import se.sundsvall.users.integration.db.model.enums.Status;

public class UserRequest {

	@Schema(description = "Epost-adress", example = "kalle.kula@sundsvall.se")
	@Email(message = "must be a valid Email-adress", regexp = "^[A-Za-zÅÄÖåäö0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
	@NotBlank(message = "cannot be blank")
	private String email;

	@Schema(description = "Telefonnummer", example = "0701740669")
	@NotBlank(message = "cannot be blank")
	@ValidMobileNumber(message = "must be a valid mobile number")
	private String phoneNumber;

	@Schema(description = "Kommunnamn", example = "Sundsvall")
	@NotBlank(message = "cannot be blank")
	@ValidMunicipalityName(message = "must be a valid municipality name")
	private String municipalityName;

	@Schema(description = "Status", example = "ACTIVE")
	@ValidEnum(message = "must be ACTIVE, INACTIVE or SUSPENDED", enumClass = Status.class, ignoreCase = true)
	private String status;

	@Schema(description = "Lösenord", example = "mittLösenord")
	@NotBlank
	private String password;

	@Schema(description = "Roll", example = "USER")
	@ValidEnum(message = "must be USER or ADMIN", enumClass = Role.class, ignoreCase = true)
	private String role = "USER";

	public static UserRequest create() {
		return new UserRequest();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserRequest withEmail(String email) {
		this.email = email;
		return this;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public UserRequest withPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}

	public String getMunicipalityName() {
		return municipalityName;
	}

	public void setMunicipalityName(String municipalityName) {
		this.municipalityName = municipalityName;
	}

	public UserRequest withMunicipalityName(String municipalityName) {
		this.municipalityName = municipalityName;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public UserRequest withStatus(String status) {
		this.status = status;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserRequest withPassword(String password) {
		this.password = password;
		return this;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public UserRequest withRole(String role) {
		this.role = role;
		return this;
	}

	@Override
	public int hashCode() {
		return Objects.hash(municipalityName, email, phoneNumber, status);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserRequest that = (UserRequest) o;
		return Objects.equals(email, that.email) && Objects.equals(phoneNumber, that.phoneNumber)
			&& Objects.equals(status, that.status) && Objects.equals(municipalityName, that.municipalityName);
	}
}

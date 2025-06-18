package se.sundsvall.users.integration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import feign.FeignException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import se.sundsvall.users.integration.citizen.CitizenClient;
import se.sundsvall.users.integration.citizen.CitizenIntegration;

@ExtendWith(MockitoExtension.class)
class CitizenIntegrationTests {
	@Mock
	private CitizenClient mockCitizenClient;

	@InjectMocks
	private CitizenIntegration citizenIntegration;

	@Test
	void getPartyId() {
		// Arrange
		final var personalNumber = "198001011234";
		final var municipalityId = "2281";
		final var partyId = UUID.randomUUID().toString();

		when(mockCitizenClient.getCitizenPartyId(personalNumber, municipalityId)).thenReturn(partyId);

		// Act
		final var result = citizenIntegration.getCitizenPartyId(personalNumber, municipalityId);

		// Assert
		assertThat(result).isEqualTo(partyId);

		verify(mockCitizenClient).getCitizenPartyId(personalNumber, municipalityId);
		verifyNoMoreInteractions(mockCitizenClient);
	}

	@Test
	void getPartyIdNothingFound() {
		// Arrange
		final var personalNumber = "198001011234";
		final var municipalityId = "2281";

		doThrow(FeignException.InternalServerError.class)
			.when(mockCitizenClient).getCitizenPartyId(personalNumber, municipalityId);

		// Act
		final var result = citizenIntegration.getCitizenPartyId(personalNumber, municipalityId);

		// Assert
		assertThat(result).isNull();

		verify(mockCitizenClient).getCitizenPartyId(personalNumber, municipalityId);
		verifyNoMoreInteractions(mockCitizenClient);
	}
}

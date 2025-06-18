package se.sundsvall.users.integration.citizen.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.FeignBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import se.sundsvall.dept44.configuration.feign.FeignConfiguration;
import se.sundsvall.dept44.configuration.feign.FeignMultiCustomizer;
import se.sundsvall.dept44.configuration.feign.decoder.ProblemErrorDecoder;

@Import(FeignConfiguration.class)
@EnableConfigurationProperties(CitizenIntegrationProperties.class)
public class CitizenIntegrationConfiguration {

	public static final String CLIENT_ID = "citizen";

	@Bean
	FeignBuilderCustomizer feignBuilderCustomizer(final CitizenIntegrationProperties citizenIntegrationProperties) {
		return FeignMultiCustomizer.create()
			.withErrorDecoder(new ProblemErrorDecoder(CLIENT_ID))
			.withRequestTimeoutsInSeconds(citizenIntegrationProperties.connectTimeout(), citizenIntegrationProperties.readTimeout())
			.composeCustomizersToOne();
	}
}

package se.sundsvall.users.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import se.sundsvall.users.service.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

//	@Bean
//	@Order(Ordered.HIGHEST_PRECEDENCE)
//	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		return http
//			.securityMatcher("/api/**")
//			.csrf(AbstractHttpConfigurer::disable)
//			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//			.authorizeHttpRequests(auth -> auth
//				// createUser är med så att det går att skapa användare i dev, bör tas bort vid prod
//				.requestMatchers("/api/auth/**", "/api/users").permitAll()
//				.anyRequest().authenticated())
//				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//			.build();
//
//	}

	@Bean
	AuthenticationManager authenticationManager(
		AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}

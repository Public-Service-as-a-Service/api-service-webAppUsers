package se.sundsvall.users.configuration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import se.sundsvall.users.utility.JwtUtil;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

	@Mock
	private JwtUtil jwtUtil;
	@Mock
	private HttpServletRequest request;
	@Mock
	private HttpServletResponse response;
	@Mock
	private FilterChain filterChain;

	@InjectMocks
	private JwtAuthenticationFilter filter;

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void shouldAuthenticateWhenValidTokenInCookie() throws Exception {
		final var token = "valid.jwt.token";
		final var email = "test@email.se";
		final var cookie = new Cookie("token", token);

		when(request.getCookies()).thenReturn(new Cookie[] {
			cookie
		});
		when(request.getHeader("Authorization")).thenReturn(null);
		when(jwtUtil.extractUsername(token)).thenReturn(email);
		when(jwtUtil.validateToken(token, email)).thenReturn(true);

		filter.doFilterInternal(request, response, filterChain);

		final var authentication = SecurityContextHolder.getContext().getAuthentication();
		assertThat(authentication).isNotNull();
		assertThat(authentication.getPrincipal()).isEqualTo(email);
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void shouldNotAuthenticateWhenInvalidTokenInCookie() throws Exception {
		final var token = "invalid.jwt.token";
		final var email = "test@email.se";
		final var cookie = new Cookie("token", token);

		when(request.getCookies()).thenReturn(new Cookie[] {
			cookie
		});
		when(request.getHeader("Authorization")).thenReturn(null);
		when(jwtUtil.extractUsername(token)).thenReturn(email);
		when(jwtUtil.validateToken(token, email)).thenReturn(false);

		filter.doFilterInternal(request, response, filterChain);

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void shouldNotAuthenticateWhenNoCookies() throws Exception {
		when(request.getCookies()).thenReturn(null);
		when(request.getHeader("Authorization")).thenReturn(null);

		filter.doFilterInternal(request, response, filterChain);

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void shouldNotAuthenticateWhenCookieNameIsNotToken() throws Exception {
		final var cookie = new Cookie("other", "somevalue");

		when(request.getCookies()).thenReturn(new Cookie[] {
			cookie
		});
		when(request.getHeader("Authorization")).thenReturn(null);

		filter.doFilterInternal(request, response, filterChain);

		assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
		verify(filterChain).doFilter(request, response);
	}
}

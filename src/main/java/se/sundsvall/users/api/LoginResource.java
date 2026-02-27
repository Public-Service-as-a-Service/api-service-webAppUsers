package se.sundsvall.users.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zalando.problem.Problem;
import se.sundsvall.users.api.model.LoginRequest;
import se.sundsvall.users.service.AuthenticationService;

@RestController()
@RequestMapping("/api/auth")
@Tag(name = "Login", description = "Endpoint for user login")
@ApiResponse(
	responseCode = "200",
	description = "Successful Operation",
	useReturnTypeSchema = true)
public class LoginResource {
	private final AuthenticationService authenticationService;

	public LoginResource(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@ApiResponse(
		responseCode = "400",
		description = "Bad Request",
		content = @Content(schema = @Schema(implementation = Problem.class)))
	@ApiResponse(
		responseCode = "401",
		description = "Unauthorized",
		content = @Content(schema = @Schema(implementation = Problem.class)))
	@ApiResponse(
		responseCode = "500",
		description = "Internal Server Error",
		content = @Content(schema = @Schema(implementation = Problem.class)))
	@PostMapping("/login")
	@Operation(summary = "Login as a user")
	public ResponseEntity<String> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {
		String token = authenticationService.login(loginRequest).getToken();
		ResponseCookie cookie = ResponseCookie.from("token", token)
			.httpOnly(true)
			.secure(false)
			.path("/")
			.sameSite("Strict")
			.maxAge(3600)
			.build();
		response.addHeader("Set-Cookie", cookie.toString());
		return ResponseEntity.ok("Logged in!");
	}

	@PostMapping("/logout")
	@Operation(summary = "Logout")
	public ResponseEntity<String> logout(HttpServletResponse response) {
		ResponseCookie deleteCookie = ResponseCookie.from("token", "")
			.httpOnly(true)
			.secure(false)
			.path("/")
			.maxAge(0)
			.sameSite("Strict")
			.build();
		response.addHeader("Set-Cookie", deleteCookie.toString());
		return ResponseEntity.ok("Logged out");
	}
}

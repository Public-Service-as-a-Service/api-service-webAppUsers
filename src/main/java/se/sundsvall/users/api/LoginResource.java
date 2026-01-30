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
import se.sundsvall.dept44.util.jacoco.ExcludeFromJacocoGeneratedCoverageReport;
import se.sundsvall.users.api.model.JwtResponse;
import se.sundsvall.users.api.model.LoginRequest;
import se.sundsvall.users.service.AuthenticationService;

@RestController()
@RequestMapping("/api/auth")
@Tag(name = "Login", description = "Endpoint for user login")
@ApiResponse(
	responseCode = "200",
	description = "Successful Operation",
	useReturnTypeSchema = true)
@ApiResponse(
	responseCode = "400",
	description = "Bad Request",
	content = @Content(schema = @Schema(implementation = Problem.class)))
@ApiResponse(
	responseCode = "401",
	description = "Unauthorized",
	content = @Content(schema = @Schema(implementation = Problem.class)))
@ApiResponse(
	responseCode = "403",
	description = "Forbidden",
	content = @Content(schema = @Schema(implementation = Problem.class)))
@ApiResponse(
	responseCode = "404",
	description = "Not Found",
	content = @Content(schema = @Schema(implementation = Problem.class)))
@ApiResponse(
	responseCode = "500",
	description = "Internal Server Error",
	content = @Content(schema = @Schema(implementation = Problem.class)))
public class LoginResource {
	private final AuthenticationService authenticationService;

	public LoginResource(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	// Ligger här mest för att testa
	@PostMapping("/login/Admin")
	@Operation(summary = "Login for user to test auth")
	@ExcludeFromJacocoGeneratedCoverageReport
	public ResponseEntity<String> loginAdmin(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {
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

	@Operation(summary = "Login for user")
	@PostMapping("/login")
	public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
		JwtResponse token = authenticationService.login(loginRequest);
		return ResponseEntity.ok(token);
	}
}

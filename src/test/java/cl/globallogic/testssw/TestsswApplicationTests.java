package cl.globallogic.testssw;

import cl.globallogic.testssw.dto.UserDto;
import cl.globallogic.testssw.entity.Phone;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestsswApplicationTests {
	@Autowired
	TestRestTemplate restTemplate;
	@Value("${jwt.secret}")
	String jwtSecret;

	@Test
	@DirtiesContext
	void shouldCreateAndFindAnUser() {
		UserDto newUser = new UserDto(
				"Julio Gonzalez",
				"julio@testssw.cl",
				"a2asfGfdfdf4",
				Collections.singletonList(
						new Phone( 87650009L, 7, "25")
				)
		);

		//Test correct sign-up
		ResponseEntity<String> response =
				restTemplate.postForEntity("/sign-up", newUser, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		String name = documentContext.read("$.name");
		assertThat(name).isNotNull().isEqualTo(newUser.getName());

		String token = documentContext.read("$.token");
		assertThat(Jwts.parser()
				.setSigningKey(jwtSecret)
				.parseClaimsJws(token)
				.getBody().getIssuer()).isEqualTo(newUser.getEmail());

		//Test correct login
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("token", token);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

		ResponseEntity<String> loginResponse = restTemplate.postForEntity("/login", entity, String.class);
		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext loginContext = JsonPath.parse(loginResponse.getBody());

		String loginToken = loginContext.read("$.token");
		assertThat(token).isNotEqualTo(loginToken);
	}

	@Test
	@DirtiesContext
	void shouldReturnAnErrorForADuplicatedUser() {
		UserDto newUser = new UserDto(
				"Julio Gonzalez",
				"julio@testssw.cl",
				"a2asfGfdfdf4",
				Collections.singletonList(
						new Phone( 87650009L, 7, "25")
				)
		);

		//Test duplicated sign-up
		ResponseEntity<String> response =
				restTemplate.postForEntity("/sign-up", newUser, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		response = restTemplate.postForEntity("/sign-up", newUser, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

		DocumentContext context = JsonPath.parse(response.getBody());

		String timestamp = context.read("$.error[0].timestamp");
		Number codigo = context.read("$.error[0].codigo");
		String detail = context.read("$.error[0].detail");

		assertThat(timestamp).isNotNull().isNotEmpty();
		assertThat(codigo).isEqualTo(400);
		assertThat(detail).isNotNull().isNotEmpty();
	}

	@Test
	void shouldReturnAnErrorForInvalidEmail() {
		UserDto newUser = new UserDto(
				"Julio Gonzalez",
				"invalidemail",
				"a2asfGfdfdf4",
				Collections.singletonList(
						new Phone( 87650009L, 7, "25")
				)
		);

		//Test incorrect sign-up
		ResponseEntity<String> response =
				restTemplate.postForEntity("/sign-up", newUser, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

		DocumentContext context = JsonPath.parse(response.getBody());

		String timestamp = context.read("$.error[0].timestamp");
		Number codigo = context.read("$.error[0].codigo");
		String detail = context.read("$.error[0].detail");

		assertThat(timestamp).isNotNull().isNotEmpty();
		assertThat(codigo).isEqualTo(400);
		assertThat(detail).isNotNull().isNotEmpty();
	}

	@Test
	void shouldReturnAnErrorForInvalidPassword() {
		UserDto newUser = new UserDto(
				"Julio Gonzalez",
				"julio@testssw.cl",
				"invalidPassword",
				Collections.singletonList(
						new Phone( 87650009L, 7, "25")
				)
		);

		//Test incorrect sign-up
		ResponseEntity<String> response =
				restTemplate.postForEntity("/sign-up", newUser, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

		DocumentContext context = JsonPath.parse(response.getBody());

		String timestamp = context.read("$.error[0].timestamp");
		Number codigo = context.read("$.error[0].codigo");
		String detail = context.read("$.error[0].detail");

		assertThat(timestamp).isNotNull().isNotEmpty();
		assertThat(codigo).isEqualTo(400);
		assertThat(detail).isNotNull().isNotEmpty();
	}

	@Test
	void shouldReturnAnErrorForUserNotFound() {
		String token = "noToken";
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
		body.add("token", token);

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

		//test login with invalid token
		ResponseEntity<String> loginResponse = restTemplate.postForEntity("/login", entity, String.class);
		assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		DocumentContext context = JsonPath.parse(loginResponse.getBody());

		String timestamp = context.read("$.error[0].timestamp");
		Number codigo = context.read("$.error[0].codigo");
		String detail = context.read("$.error[0].detail");

		assertThat(timestamp).isNotNull().isNotEmpty();
		assertThat(codigo).isEqualTo(404);
		assertThat(detail).isNotNull().isNotEmpty();
	}
}

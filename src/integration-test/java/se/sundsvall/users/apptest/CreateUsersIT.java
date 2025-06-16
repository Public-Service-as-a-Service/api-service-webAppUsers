package se.sundsvall.users.apptest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.users.Application;
import se.sundsvall.users.integration.db.UserRepository;
import se.sundsvall.users.integration.db.model.Enum.Status;


import static org.assertj.core.api.Assertions.assertThat;


@WireMockAppTestSuite(files = "classpath:/CreateUsersIT/", classes = Application.class)
class CreateUsersIT extends AbstractAppTest {
    private static final String REQUEST = "request.json";

    @Autowired
    private UserRepository userRepository;

    @Test
    void test01_createUser() {

        assertThat(userRepository.findByEmail("test1@sundsvall.se")).isEmpty();

        setupCall()
                .withServicePath("/api/users")
                .withHttpMethod(HttpMethod.POST)
                .withRequest(REQUEST)
                .withExpectedResponseStatus(HttpStatus.CREATED)
                .sendRequestAndVerifyResponse();

        final var user = userRepository.findByEmail("test@sundsvall.se");
        assertThat(user).isPresent();
        assertThat(user.get().getStatus()).isNotNull();
        assertThat(user.get().getEmail()).isEqualTo("test@sundsvall.se");
        assertThat(user.get().getPhoneNumber()).isEqualTo("0701234567");
        assertThat(user.get().getMunicipalityId()).isEqualTo("2281");
        assertThat(user.get().getStatus()).isEqualTo(Status.INACTIVE);

    }

    @Test
    void test02_createUserWithPersonalNumber() {

        final String partyId = "5b67a8ce-8c06-41aa-96b8-31e81946e8ba";

        assertThat(userRepository.findByEmail("test2@sundsvall.se")).isEmpty();

        setupCall()
                .withServicePath("/api/users")
                .withHttpMethod(HttpMethod.POST)
                .withRequest(REQUEST)
                .withExpectedResponseStatus(HttpStatus.CREATED)
                .sendRequestAndVerifyResponse();

        final var user = userRepository.findByEmail("test2@sundsvall.se");
        assertThat(user).isPresent();
        assertThat(user.get().getPartyId()).isEqualTo(partyId);
        assertThat(user.get().getEmail()).isEqualTo("test2@sundsvall.se");
        assertThat(user.get().getPhoneNumber()).isEqualTo("0701234567");
        assertThat(user.get().getMunicipalityId()).isEqualTo("2281");
        assertThat(user.get().getStatus()).isEqualTo(Status.INACTIVE);

    }

    @Test
    void test03_createUserCitizenNotFound() {
        final String email = "test3@sundsvall.se";

        assertThat(userRepository.findByEmail(email)).isEmpty();
        setupCall()
        .withServicePath("/api/users")
                .withHttpMethod(HttpMethod.POST)
                .withRequest(REQUEST)
                .withExpectedResponseStatus(HttpStatus.CREATED)
                .sendRequestAndVerifyResponse();

        final var user = userRepository.findByEmail(email);
        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo("test3@sundsvall.se");
        assertThat(user.get().getPhoneNumber()).isEqualTo("0722143657");
        assertThat(user.get().getMunicipalityId()).isEqualTo("1440");
        assertThat(user.get().getStatus()).isEqualTo(Status.SUSPENDED);

    }

}

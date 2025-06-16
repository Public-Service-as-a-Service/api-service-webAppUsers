package se.sundsvall.users.apptest;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import se.sundsvall.dept44.test.AbstractAppTest;
import se.sundsvall.dept44.test.annotation.wiremock.WireMockAppTestSuite;
import se.sundsvall.users.Application;
import se.sundsvall.users.integration.db.UserRepository;
import se.sundsvall.users.integration.db.model.Enum.Status;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Update user apptests.
 *
 * @see /src/test/resources/db/script/UpdateUserAppTest.sql for data setup.
 */
@WireMockAppTestSuite(files = "classpath:/UpdateUserIT/", classes = Application.class)
@Sql(scripts = {
        "/db/script/truncate.sql",
        "/db/script/UpdateUserAppTest.sql"
})
class UpdateUserIT extends AbstractAppTest {
    private static final String REQUEST = "request.json";
    private static final String RESPONSE = "response.json";

    @Autowired
    private UserRepository userRepository;

    @Test
    void test01_UpdateUserWithEmail() {


        final String email = "testmail1@sundsvall.se";

        assertThat(userRepository.findByEmail(email)).isPresent();

        setupCall()
                .withServicePath("/api/users/emails/".concat(email))
                .withHttpMethod(HttpMethod.PATCH)
                .withRequest(REQUEST)
                .withExpectedResponseStatus(HttpStatus.CREATED)
                .withExpectedResponse(RESPONSE)
                .sendRequestAndVerifyResponse();

        final var user = userRepository.findByEmail(email);

        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo(email);
        assertThat(user.get().getPartyId()).isEqualTo("7225dc69-28d1-4064-a1a8-5c1de5da0e62");
        assertThat(user.get().getPhoneNumber()).isEqualTo("0701234567");
        assertThat(user.get().getMunicipalityId()).isEqualTo("2281");
        assertThat(user.get().getStatus()).isEqualTo(Status.INACTIVE);
    }

    @Test
    void test02_UpdateUserWithPartyId() {
        final String partyId = "7225dc69-28d1-4064-a1a8-5c1de5da0e63";
        assertThat(userRepository.findByPartyId(partyId)).isPresent();
        setupCall()
        .withServicePath("/api/users/partyIds/".concat(partyId))
                .withHttpMethod(HttpMethod.PATCH)
                .withRequest(REQUEST)
                .withExpectedResponseStatus(HttpStatus.CREATED)
                .withExpectedResponse(RESPONSE)
                .sendRequestAndVerifyResponse();

        final var userEntity = userRepository.findByPartyId(partyId);

        assertThat(userEntity).isPresent();
        assertThat(userEntity.get().getEmail()).isEqualTo("testmail2@sundsvall.se");
        assertThat(userEntity.get().getPartyId()).isEqualTo(partyId);
        assertThat(userEntity.get().getPhoneNumber()).isEqualTo("0701234567");
        assertThat(userEntity.get().getMunicipalityId()).isEqualTo("2281");
        assertThat(userEntity.get().getStatus()).isEqualTo(Status.INACTIVE);
    }

    @Test
    void test03_UpdateUserWithEmailNotFound() {

        setupCall()
        .withServicePath("/api/users/emails/".concat("testmail@sundsvall.se"))
                .withHttpMethod(HttpMethod.PATCH)
                .withRequest(REQUEST)
                .withExpectedResponseStatus(HttpStatus.NOT_FOUND)
                .sendRequestAndVerifyResponse();
    }

    @Test
    void test04_UpdateUserWithPersonalNumber() {
        final String partyId = "7225dc69-28d1-4064-a1a8-5c1de5da0e63";
        final String personalNumber = "198001011234";
        assertThat(userRepository.findByPartyId(partyId)).isPresent();
        setupCall()
                .withServicePath("/api/users/personalNumbers/" + personalNumber + "?municipalityId=2281")
                .withHttpMethod(HttpMethod.PATCH)
                .withRequest(REQUEST)
                .withExpectedResponseStatus(HttpStatus.CREATED)
                .withExpectedResponse(RESPONSE)
                .sendRequestAndVerifyResponse();

        final var userEntity = userRepository.findByPartyId(partyId);

        assertThat(userEntity).isPresent();
        assertThat(userEntity.get().getEmail()).isEqualTo("testmail2@sundsvall.se");
        assertThat(userEntity.get().getPartyId()).isEqualTo(partyId);
        assertThat(userEntity.get().getPhoneNumber()).isEqualTo("0706435467");
        assertThat(userEntity.get().getMunicipalityId()).isEqualTo("2281");
        assertThat(userEntity.get().getStatus()).isEqualTo(Status.SUSPENDED);

    }
}

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

@WireMockAppTestSuite(files = "classpath:/GetUserIT/", classes = Application.class)
@Sql(scripts = {
        "/db/script/truncate.sql",
        "/db/script/GetUserTest.sql"
})
class GetUserIT extends AbstractAppTest {
    private static final String RESPONSE = "response.json";
    private static final String Expected = "citizen-mapping.json";
    @Autowired
    UserRepository userRepository;

    @Test
    void test01_getUserByEmail() {

        final String email = "testmail1@sundsvall.se";


        setupCall()
                .withServicePath("/api/users/emails/".concat(email))
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.OK)
                .withExpectedResponse(RESPONSE)
                .sendRequestAndVerifyResponse();


    }

    @Test
    void test02_getUserByPartyId() {

        final String partyId = "7225dc69-28d1-4064-a1a8-5c1de5da0e62";


        setupCall()
                .withServicePath("/api/users/partyIds/".concat(partyId))
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.OK)
                .withExpectedResponse(RESPONSE)
                .sendRequestAndVerifyResponse();


    }
    @Test
    void test03_getUserByPersonNumber() {

        final String partyId = "7225dc69-28d1-4064-a1a8-5c1de5da0e62";



        setupCall()
                .withServicePath("/api/users/personalNumbers/198001011234?municipalityId=2281")
                .withHttpMethod(HttpMethod.GET)
                .withExpectedResponseStatus(HttpStatus.OK)
                .withExpectedResponse(RESPONSE)
                .sendRequestAndVerifyResponse();

    }
}
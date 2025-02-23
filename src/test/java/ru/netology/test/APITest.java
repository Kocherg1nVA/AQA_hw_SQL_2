package ru.netology.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.Specifications;
import ru.netology.data.Transfer;

import java.util.List;

import static io.restassured.RestAssured.given;

public class APITest {
    DataHelper.TokenData authToken;
    List<DataHelper.CardsInfo> userCards;

    @Test
    @DisplayName("Аутентификация пользователя с валидными данными")
    public void shouldSuccessLogin() {
        given()
                .log().all()
                .spec(Specifications.requestSpec())
                .body(DataHelper.LoginData.getLoginInfo())
                .when()
                .post(new DataHelper.PathData().getLoginPath())
                .then()
                .log().all()
                .spec(Specifications.responseSpecSuccessLogin());
    }

    @Test
    @DisplayName("Аутентификация пользователя с невалидными данными")
    public void shouldNotLoginWithInvalidInfo() {
        DataHelper.LoginErr loginErr;
        loginErr = given()
                .log().all()
                .spec(Specifications.requestSpec())
                .body(DataHelper.generateInvalidInfo())
                .when()
                .post(new DataHelper.PathData().getLoginPath())
                .then()
                .log().all()
                .spec(Specifications.responseSpecInvalidLogin())
                .extract().response().as(DataHelper.LoginErr.class);
        Assertions.assertEquals("AUTH_INVALID", loginErr.getCode());
    }

    @Test
    @DisplayName("Верификация пользователя с валидными данными")
    public void shouldSuccessVerify() {
        authToken = given()
                .spec(Specifications.requestSpec())
                .body(DataHelper.VerificationData.getVerifyInfo())
                .post(new DataHelper.PathData().getVerificationPath())
                .then()
                .log().all()
                .spec(Specifications.responseSpecSuccessVerify())
                .extract().response().as(DataHelper.TokenData.class);
        Assertions.assertNotNull(authToken.getToken());
    }

    @Test
    @DisplayName("Верификация пользователя с невалидными данными")
    public void shouldNotVerify() {
        DataHelper.VerifyErr verifyErr;
        verifyErr = given()
                .spec(Specifications.requestSpec())
                .body(DataHelper.InvalidVerificationData.getInvalidVerifyInfo())
                .post(new DataHelper.PathData().getVerificationPath())
                .then()
                .log().all()
                .spec(Specifications.responseSpecInvalidVerify())
                .extract().response().as(DataHelper.VerifyErr.class);
        Assertions.assertEquals("AUTH_INVALID", verifyErr.getCode());
    }

    @Test
    @DisplayName("Получить список карт пользователя и положить в класс")
    public void shouldGetUserCards() {
        shouldSuccessVerify();
        userCards = given()
                .spec(Specifications.requestSpec())
                .auth().oauth2(authToken.getToken())
                .get(new DataHelper.PathData().getCardsInfoPath())
                .then()
                .log().all()
                .spec(Specifications.responseSpecGetCards())
                .extract().response().jsonPath().getList(".", DataHelper.CardsInfo.class);
        Assertions.assertNotNull(userCards);
    }

    @Test
    @DisplayName("Запросить список карт пользователя без токена аутентификации")
    public void shouldNotGetUserCards() {
        given()
                .spec(Specifications.requestSpec())
                .get(new DataHelper.PathData().getCardsInfoPath())
                .then()
                .log().all()
                .spec(Specifications.responseSpecNotGetCards());
    }

    @Test
    public void shouldSuccessTransfer() {
        shouldSuccessVerify();
        given()
                .spec(Specifications.requestSpec())
                .auth().oauth2(authToken.getToken())
                .body(new Transfer("5559 0000 0000 0002", "5559 0000 0000 0001", 1_000))
                .post(new DataHelper.PathData().getTransferPath())
                .then()
                .log().all()
                .statusCode(200);
    }
}

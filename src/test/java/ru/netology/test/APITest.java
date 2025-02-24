package ru.netology.test;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.data.Specifications;

import java.util.List;

import static io.restassured.RestAssured.given;

public class APITest {
    DataHelper.TokenData authToken;
    List<DataHelper.CardsInfo> userCards;

//    @AfterAll
//    public static void reloadDB(){
//        SQLHelper.cleanDataBase();
//    }

    //happy path
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
                .statusCode(200)
                .header("Connection", "keep-alive")
                .header("Content-Length", "0");
    }

    @Test
    @DisplayName("Верификация пользователя с валидными данными")
    public void shouldSuccessVerify() {
        authToken = given()
                .spec(Specifications.requestSpec())
                .body(DataHelper.VerificationData.getVerifyInfo())
                .when()
                .post(new DataHelper.PathData().getVerificationPath())
                .then()
                .log().all()
                .statusCode(200)
                .header("Content-Length", "121")
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Connection", "keep-alive")
                .extract().response().as(DataHelper.TokenData.class);
        Assertions.assertNotNull(authToken.getToken());
    }

    @Test
    @DisplayName("Получить список карт пользователя и положить в класс")
    public void shouldGetUserCards() {
        authToken = given()
                .spec(Specifications.requestSpec())
                .body(DataHelper.VerificationData.getVerifyInfo())
                .when()
                .post(new DataHelper.PathData().getVerificationPath())
                .then()
                .extract().response().as(DataHelper.TokenData.class);
        userCards = given()
                .spec(Specifications.requestSpec())
                .auth().oauth2(authToken.getToken())
                .get(new DataHelper.PathData().getCardsInfoPath())
                .then()
                .log().all()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().response().jsonPath().getList(".", DataHelper.CardsInfo.class);
        Assertions.assertNotNull(userCards);
    }

    @Test
    @DisplayName("Перевод денег с 1 карты на 2 карту")
    public void shouldSuccessTransfer() {
        authToken = given()
                .spec(Specifications.requestSpec())
                .body(DataHelper.VerificationData.getVerifyInfo())
                .when()
                .post(new DataHelper.PathData().getVerificationPath())
                .then()
                .extract().response().as(DataHelper.TokenData.class);

        List<DataHelper.CardsInfo> cardsInfo = SQLHelper.getUserCards();
        int initialFirstCardBalance = cardsInfo.get(0).getBalance();
        int initialSecondCardBalance = cardsInfo.get(1).getBalance();
        int amount = DataHelper.generateAmount(initialSecondCardBalance);
        int expectedFirstCardBalance = initialFirstCardBalance - amount;
        int expectedSecondCardBalance = initialSecondCardBalance + amount;

        given()
                .spec(Specifications.requestSpec())
                .auth().oauth2(authToken.getToken())
                .body(DataHelper.TransferData.moneyTransfer(cardsInfo, 0, 1, amount))
                .post(new DataHelper.PathData().getTransferPath())
                .then()
                .log().all()
                .statusCode(200);
        List<DataHelper.CardsInfo> newCardsInfo = SQLHelper.getUserCards();
        Assertions.assertEquals(expectedFirstCardBalance, newCardsInfo.get(0).getBalance());
        Assertions.assertEquals(expectedSecondCardBalance, newCardsInfo.get(1).getBalance());
    }

    // sad path
    @Test
    @DisplayName("Аутентификация пользователя с невалидными данными")
    public void shouldNotLoginWithInvalidInfo() {
        DataHelper.LoginErr loginErr = given()
                .log().all()
                .spec(Specifications.requestSpec())
                .body(DataHelper.generateInvalidInfo())
                .when()
                .post(new DataHelper.PathData().getLoginPath())
                .then()
                .log().all()
                .statusCode(400)
                .header("Content-Length", "28")
                .header("Content-Type", "application/json; charset=UTF-8")
                .header("Connection", "keep-alive")
                .extract().response().as(DataHelper.LoginErr.class);
        Assertions.assertEquals("AUTH_INVALID", loginErr.getCode());
    }

    @Test
    @DisplayName("Верификация пользователя с невалидным кодом аутентификации")
    public void shouldNotVerify() {
        DataHelper.VerifyErr verifyErr;
        verifyErr = given()
                .spec(Specifications.requestSpec())
                .body(DataHelper.InvalidVerificationData.getInvalidVerifyInfo())
                .post(new DataHelper.PathData().getVerificationPath())
                .then()
                .log().all()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .extract().response().as(DataHelper.VerifyErr.class);
        Assertions.assertEquals("AUTH_INVALID", verifyErr.getCode());
    }

    @Test
    @DisplayName("Запросить список карт пользователя без токена аутентификации")
    public void shouldNotGetUserCards() {
        given()
                .spec(Specifications.requestSpec())
                .get(new DataHelper.PathData().getCardsInfoPath())
                .then()
                .log().all()
                .statusCode(401)
                .header("WWW-Authenticate", "Bearer realm=\"Ktor Server\"");
    }
//    @Test
//    @DisplayName("Перевод денег со 2 карты на 1 карту на сумму превышающую баланс карты списания")
//    public void shouldNotTransferWhenAmountMoreThanBalance() {
//        authToken = given()
//                .spec(Specifications.requestSpec())
//                .body(DataHelper.VerificationData.getVerifyInfo())
//                .when()
//                .post(new DataHelper.PathData().getVerificationPath())
//                .then()
//                .extract().response().as(DataHelper.TokenData.class);
//
//        List<DataHelper.CardsInfo> cardsInfo = SQLHelper.getUserCards();
//        int CardFromInitialBalance = cardsInfo.get(0).getBalance();
//        int amount = DataHelper.generateInvalidAmount(CardFromInitialBalance);
//
//        given()
//                .spec(Specifications.requestSpec())
//                .auth().oauth2(authToken.getToken())
//                .body(DataHelper.TransferData.moneyTransfer(cardsInfo, 1, 0, amount))
//                .post(new DataHelper.PathData().getTransferPath())
//                .then()
//                .log().all()
//                .statusCode(400);
//    }
}

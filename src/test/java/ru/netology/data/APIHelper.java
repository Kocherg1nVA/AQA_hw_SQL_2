package ru.netology.data;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;

import java.util.List;

import static io.restassured.RestAssured.given;

public class APIHelper {

    private static final String loginPath = "api/auth";
    private static final String verifyPath = "api/auth/verification";
    private static final String cardsInfoPath = "api/cards";
    private static final String transferPath = "api/transfer";

    public static void validLogin() {
        given()
                .log().all()
                .spec(Specifications.requestSpec())
                .body(DataHelper.LoginData.getLoginInfo())
                .when()
                .log().all()
                .post(loginPath)
                .then()
                .log().all()
                .statusCode(200)
                .header("Connection", "keep-alive")
                .header("Content-Length", "0");
    }

    public static String invalidLogin() {
        DataHelper.LoginErr loginErr =
                given()
                        .log().all()
                        .spec(Specifications.requestSpec())
                        .body(DataHelper.LoginData.generateInvalidInfo())
                        .when()
                        .log().all()
                        .post(loginPath)
                        .then()
                        .log().all()
                        .statusCode(400)
                        .header("Content-Length", "28")
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .header("Connection", "keep-alive")
                        .extract().response().as(DataHelper.LoginErr.class);
        return loginErr.getCode();
    }

    public static String getAccessToken() {
        validLogin();
        DataHelper.TokenData token =
                given()
                        .log().all()
                        .spec(Specifications.requestSpec())
                        .body(DataHelper.VerificationData.getVerifyInfo())
                        .when()
                        .log().all()
                        .post(verifyPath)
                        .then()
                        .log().all()
                        .statusCode(200)
                        .header("Content-Length", "121")
                        .header("Content-Type", "application/json; charset=UTF-8")
                        .header("Connection", "keep-alive")
                        .extract().body().as(DataHelper.TokenData.class);
        Assertions.assertNotNull(token);
        return token.getToken();
    }

    public static String invalidVerify() {
        DataHelper.VerifyErr verifyErr =
                given()
                        .log().all()
                        .spec(Specifications.requestSpec())
                        .body(DataHelper.VerificationData.getInvalidVerifyInfo())
                        .when()
                        .log().all()
                        .post(verifyPath)
                        .then()
                        .log().all()
                        .statusCode(400)
                        .contentType(ContentType.JSON)
                        .extract().response().as(DataHelper.VerifyErr.class);
        return verifyErr.getCode();
    }

    public static void getUserCardsInfo() {
        List<DataHelper.CardsInfo> userCards =
                given()
                        .log().all()
                        .spec(Specifications.requestSpec())
                        .auth().oauth2(getAccessToken())
                        .when()
                        .log().all()
                        .get(cardsInfoPath)
                        .then()
                        .log().all()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .extract().response().jsonPath().getList(".", DataHelper.CardsInfo.class);
        Assertions.assertNotNull(userCards);
    }

    public static void UnAuthorizationCardInfo() {
                given()
                        .log().all()
                        .spec(Specifications.requestSpec())
                        .when()
                        .log().all()
                        .get(cardsInfoPath)
                        .then()
                        .log().all()
                        .statusCode(401);
    }

    public static void moneyTransfer(List<DataHelper.CardsInfo> cards, int indexFrom, int indexTo, int amount) {
        given()
                .log().all()
                .spec(Specifications.requestSpec())
                .auth().oauth2(getAccessToken())
                .body(DataHelper.TransferData.moneyTransfer(cards, indexFrom, indexTo, amount))
                .when()
                .log().all()
                .post(transferPath)
                .then()
                .log().all()
                .statusCode(200);
    }
}

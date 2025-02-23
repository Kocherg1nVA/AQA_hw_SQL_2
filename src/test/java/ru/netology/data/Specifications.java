package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

public class Specifications {

    public static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost:9999/")
                .setContentType(ContentType.JSON)
                .build();
    }

    public static ResponseSpecification responseSpecSuccessLogin() {
        return new ResponseSpecBuilder()
                .expectStatusCode(200)
                .build();
    }

    public static ResponseSpecification responseSpecInvalidLogin() {
        return new ResponseSpecBuilder()
                .expectContentType("application/json; charset=UTF-8")
                .expectStatusCode(400)
                .build();
    }

    public static ResponseSpecification responseSpecSuccessVerify() {
        return new ResponseSpecBuilder()
                .expectContentType("application/json")
                .expectStatusCode(200)
                .build();
    }

    public static ResponseSpecification responseSpecInvalidVerify() {
        return new ResponseSpecBuilder()
                .expectContentType("application/json")
                .expectStatusCode(400)
                .build();
    }

    public static ResponseSpecification responseSpecGetCards() {
        return new ResponseSpecBuilder()
                .expectContentType("application/json; charset=UTF-8")
                .expectStatusCode(200)
                .build();
    }

    public static ResponseSpecification responseSpecNotGetCards() {
        return new ResponseSpecBuilder()
                .expectStatusCode(401)
                .build();
    }
}

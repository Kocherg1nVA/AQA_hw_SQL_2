package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class Specifications {

    public static RequestSpecification requestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri("http://localhost:9999/")
                .setContentType(ContentType.JSON)
                .build();
    }
}

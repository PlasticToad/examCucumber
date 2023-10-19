package api;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

public class Specifications {

    public static RequestSpecification reqSpec(String url) {
        return new RequestSpecBuilder()
                .setBaseUri(url)
                .setContentType(ContentType.JSON)
                .build();
    }
    public static ResponseSpecification respSpec() {
        return new ResponseSpecBuilder()
                .expectStatusCode(anyOf(is(200), is(201)))
                .build();
    }
    public static void specInstall(RequestSpecification reqSpec, ResponseSpecification respSpec) {
        RestAssured.requestSpecification = reqSpec;
        RestAssured.responseSpecification = respSpec;
    }
}

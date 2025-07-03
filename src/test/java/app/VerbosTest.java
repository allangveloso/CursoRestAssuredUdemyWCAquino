package app;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class VerbosTest {

    String baseUri = "http://restapi.wcaquino.me";

    @Test
    public void deveSalvarUsuario(){

    Response response = RestAssured.given()

            .when()
            .post(baseUri+"/users")

            .then()
            .statusCode(200) // Assert that the status code is OK
                .log().all()
            .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name", is("Jose"))
                .body("age", is(50))
            .extract()
            .response();
    ;

        //from - aqui usado para extrair o id
        int id = JsonPath.from(response.asString()).getInt("id");
        System.out.println();

    }

    @Test
    public void deveTentarSalvarUsuarioSemNome(){
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"age\": 50 }")
        .when()
                .post("https://restapi.wcaquino.me/users")
        .then()
                .log().all()
                .statusCode(400)
                .body("id", is(nullValue()))
                .body("error", is("Name é um atributo obrigatório"))
                ;
    }

    @Test
    public void deveTentarSalvarUsuarioSemNomeXML(){
        given()
                .log().all()
                .contentType(ContentType.XML)
                .body("<user><name>Jose</name><age>50</age></user>")
        .when()
                .post("https://restapi.wcaquino.me/usersXML")
        .then()
                .log().all()
                .statusCode(201)
                .body("user.@id", is(notNullValue()))
                .body("user.name", is("Jose"))
                .body("user.age", is("50"))
        ;
    }

    @Test
    public void deveAlterarUsuario(){
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\": \"Usuario Alterado\", \"age\": 80 }")
        .when()
                .put("https://restapi.wcaquino.me/users/1")
        .then()
                .log().all()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Usuario Alterado"))
                .body("age", is(80))
                .body("salary", is(1234.5678f))
        ;
    }

    @Test
    public void deveCustomizarURL(){
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\": \"Usuario Alterado\", \"age\": 80 }")
                .pathParam("entidade", "users")
                .pathParam("userId", 1)
        .when()
                .put("https://restapi.wcaquino.me/users/1")
        .then()
                .log().all()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Usuario Alterado"))
                .body("age", is(80))
                .body("salary", is(1234.5678f))
        ;
    }




}
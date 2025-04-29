//JUNIT version 4.12
//RESTASSURED version 5.5.0

package app;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserJsonTest {

    String baseUri = "http://restapi.wcaquino.me:80";

    @Test
    public void deveFicarNoMesmoNivel() {
        given()
        .when()
            .get(baseUri+"/users/1")
        .then()
            .statusCode(200)
            .body("id", is(1))
            .body("name", containsString("Silva"))
            .body("age", greaterThan(18));
    }

    @Test
    public void deveVerificarOutrasFormas(){


        Response response = RestAssured.request(Method.GET, baseUri + "/users/1");

        //path
        Assert.assertEquals(new Integer(1), response.path("id"));
        Assert.assertEquals(new Integer(1), response.path("%s","id"));

        //jsonpath
        JsonPath jpath = new JsonPath(response.asString());
        Assert.assertEquals(1, jpath.getInt("id"));

        //from
        int id = JsonPath.from(response.asString()).getInt("id");
        Assert.assertEquals(1,id);

    }

    @Test
    public void deveVerificarSegundoNivel() {
        given()
        .when()
                .get(baseUri+"/users/2")
        .then()
                .statusCode(200)
                .body("name", containsString("Joaquina"))
                .body("endereco.rua", is("Rua dos bobos"));
    }

    @Test
    public void deveVerificarListasEmSegundoNivel() {
        given()
        .when()
                .get(baseUri+"/users/3")
        .then()
                .statusCode(200)
                .body("name", containsString("Ana"))
                .body("filhos", hasSize(2))
                .body("filhos[0].name", is("Zezinho"))
                .body("filhos[1].name", is("Luizinho"))
                .body("filhos.name", hasItem("Zezinho"))
                //System.out.println(RestAssured.request(Method.GET, baseUri + "/users/3").asString());
                .body("filhos.name", hasItems("Zezinho","Luizinho"));
    }

    @Test
    public void deveRetornarErroUsuarioInexistente() {
        given()
        .when()
                .get(baseUri+"/users/4")
        .then()
                .statusCode(404)
                .body("error", is("Usuário inexistente"));
    }

}

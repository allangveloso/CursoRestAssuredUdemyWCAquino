//JUNIT version 4.12
//RESTASSURED version 5.5.0


package app;

import static io.restassured.RestAssured.*;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Test;

public class HelloWorldJUnit {

    String baseUri = "http://restapi.wcaquino.me:80/ola";

    @Test
    public void testHelloWorld() {
        Response response = RestAssured.request(Method.GET, baseUri);

        //O assertTrue deverá atender a uma condição ou uma lógica
        Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
        Assert.assertTrue(response.statusCode() == 200);
        Assert.assertTrue("Retorno esperado: 201, retorno obtido: "+response.statusCode(), response.statusCode()==201);

        /*O assertEquals atende a uma comparação e retorna uma mensagem mais amigável
        java.lang.AssertionError:
        Expected :201
        Actual   :200*/
        Assert.assertEquals(200, response.statusCode());

        //Uma outra forma de validar.
        ValidatableResponse validation = response.then();
        validation.statusCode(200);
    }

    @Test
    public void umaFormaAbreviadaDeValidarRequisicoes() {
        //Aqui pode-se resumir toda a cadeia anterior em poucas linhas, através do import static io.restassured.RestAssured.*;
        get(baseUri).then().statusCode(200);
    }

    @Test
    public void formaPadronizadaDeUtilizacaoRestAssured() {

        given()
            //Pré Condições
        .when()
            .get(baseUri)
        .then()
            .statusCode(200);
    }
}

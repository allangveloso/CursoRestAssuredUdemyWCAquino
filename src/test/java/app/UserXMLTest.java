package app;

import org.junit.Test;

import static  org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;



public class UserXMLTest {
    String baseUri = "http://restapi.wcaquino.me:80";

    @Test
    public void devoTrabalharComXML(){
        given()
        .when()
                //Agora faz o teste em cima do conteúdo em XML
            .get(baseUri+"/usersXML/3")
        .then()
            .statusCode(200)
                .body("user.name", is("Ana Julia"))
                .body("user.@id", is("3"))
                .body("user.filhos.name.size()", is(2))
                .body("user.filhos.name[0]", is("Zezinho"))
                .body("user.filhos.name[1]", is("Luizinho"))
                .body("user.filhos.name", hasItem("Luizinho"))
                .body("user.filhos.name", hasItems("Luizinho", "Zezinho"))
        ;
    }
}

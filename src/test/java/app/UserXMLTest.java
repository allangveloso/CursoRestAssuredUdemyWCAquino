//JUNIT version 4.12
//RESTASSURED version 5.5.0
//Pagina para realizar os testes: http://restapi.wcaquino.me/users/
//Documentacao do hamcrestest: https://hamcrest.org/JavaHamcrest/javadoc/

package app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

import io.restassured.response.ValidatableResponse;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

import static org.hamcrest.Matchers.*;




public class UserXMLTest {
    String baseUri = "http://restapi.wcaquino.me:80";

    @Test
    public void devoTrabalharComXMLNoRaiz(){
        //System.out.println(RestAssured.request(Method.GET, baseUri + "/usersxml").asString());
        given()
                .when()
                //Agora faz o teste em cima do conteúdo em XML
                .get(baseUri+"/usersxml")
                .then()
                .statusCode(200)
                .body("users.user.name", hasItems("João da Silva", "Maria Joaquina", "Ana Julia"))
                .body("$.user.name", hasItems("João da Silva", "Maria Joaquina", "Ana Julia"))
        ;
    }

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

            //Utilizando rootPath()
            .rootPath("user")
                .body("name", is("Ana Julia"))
                .body("@id", is("3"))

            //Incluindo filhos
            .rootPath("user.filhos")
                .body("name.size()", is(2))

            //Adicionando "filhos" ao rootPath para um teste pontual
            .appendRootPath("filhos")
                .body("name", hasItem("Luizinho"))

            //Retirando filhos de rootPath para um teste pontual
            .detachRootPath("filhos")
                .body("filhos.name[0]", is("Zezinho"))
        ;
    }

    @Test
    public void devoTrabalharComXmlAvancado(){
        given()
                .when()
                .get(baseUri + "/usersxml")
                .then()
                .statusCode(200)
                //Validar que a quantidade de usuários da listagem = 3
                .body("users.user.size()", is(3))
                //Validar que a quantidade de IDs da lista é 3
                .body("users.user.@id.size()", is(3))
                //Validar que a lista contém os Ids 1,2 e 3
                .body("users.user.@id", hasItems("1","2","3"))
                //Validar em todas as idades que a quantidade de idades <= 25 são 2
                .body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
                //Validar que a idade 25 pertence a "Maria Joaquina"
                .body("users.user.find{it.age == 25}", hasItem("Maria Joaquina"))
                //ou
                .body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))
                //Validar em todos os names que aqueles que contenham 'n', são "Maria Joaquina" e "Ana Julia"
                .body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
                //Validar em salary, que o salário 1234.5678 é diferente de null
                .body("users.user.salary.find{it != null}", is("1234.5678")) //o retorno é um string
                //tratar para retornar um double
                .body("users.user.salary.find{it != null}.toDouble()", is(1234.5678d)) //o retorno é um double
                //Validar que o retorno das idades é o dobro do valor da listagem para cada uma delas
                .body("users.user.age.collect{it.toInteger() * 2}", hasItems(40,50,60)) //o collect faz uma transformação em cima de todo o conjunto
                //Validar que os nomes que comecem com 'Maria' combinas em upperCase com MARIA JOAQUINA
                .body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))


        ;
    }
}
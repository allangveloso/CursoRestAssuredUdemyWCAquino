//JUNIT version 4.12
//RESTASSURED version 5.5.0
//Pagina para realizar os testes: http://restapi.wcaquino.me/users/
//Documentacao do hamcrestest: https://hamcrest.org/JavaHamcrest/javadoc/

package app;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

import org.junit.Test;
import static org.junit.Assert.*;

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
            .body("age", greaterThan(18 ));
    }

    @Test
    public void deveVerificarOutrasFormas(){

        Response response = RestAssured.request(Method.GET, baseUri + "/users/1");

        //path
        assertEquals(new Integer(1), response.path("id"));
        assertEquals(new Integer(1), response.path("%s","id"));

            //jsonpath
        JsonPath jpath = new JsonPath(response.asString());
        assertEquals(1, jpath.getInt("id"));

        //from
        int id = JsonPath.from(response.asString()).getInt("id");
        assertEquals(1,id);

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
        //Mostrar o retorno de users/3
//        System.out.println(RestAssured.request(Method.GET, baseUri + "/users/3").asString());
        given()
        .when()
                .get(baseUri+"/users/3")
        .then()
                .statusCode(200)
                .body("name", containsString("Ana"))
                .body("name", equalTo("Ana Júlia"))
                .body("filhos", hasSize(2))
                .body("filhos[0].name", is("Zezinho"))
                .body("filhos[1].name", is("Luizinho"))
                .body("filhos.name", hasItem("Zezinho"))
                .body("filhos.name", hasItems("Zezinho","Luizinho"))
        ;

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

    @Test
    public void deveVerificarListaRaiz() {
        given()
                .when()
                .get(baseUri + "/users")
                .then()
                .statusCode(200)
                //O $ representa a lista raiz
                .body("$", hasSize(3))
                //Ou opcionalmente pode deixar sem
                .body("", hasSize(3))
                .body("name", hasItems("Ana Júlia", "João da Silva", "Maria Joaquina"))
                .body("age[1]", is(25))
                //Assim irá dar erro, pois o retorno é um array
//                .body("filhos.name",hasItems("Zezinho", "Luizinho"))
                //Solução: indicar o retorno como uma lista de array
                .body("filhos.name", hasItems(Arrays.asList("Zezinho", "Luizinho")))
                .body("salary", hasItems(1234.5678f, 2500, null));
    }

    @Test
    public void deveFazerVerificacoesAvancadas() {
//        System.out.println(RestAssured.request(Method.GET, baseUri + "/users/").asString());
        given()
                .when()
                    .get(baseUri + "/users")
                .then()
                .body("$", hasSize(3))
                //validar que o tamanho da lista menor/igual a 25 é 2
                .body("age.findAll{it <= 25}.size()", is(2))
                .body("age.findAll{it > 20 && it <= 25}.size()", is(1))
                //validar o nome do usuário entre 20 e 25 anos
                //o retorno é um objeto de uma lista, por isso o hasItem
                .body("findAll{it.age > 20 && it.age <= 25}.name", hasItem("Maria Joaquina"))
                //buscar o primeiro elemento da lista, transformando-o em objeto
                .body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
                //buscar pelo último elemento
                .body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))
                //o find busca por um registro apenas, ao contrário do findAll
                .body("find{it.age <= 25}.name", is("Maria Joaquina"))
                //Verifica se os nomes informados contém a letra 'n'
                .body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
                //Verifica os nomes que tem mais que 10 caracatres
                .body("findAll{it.name.length() > 10}.name", hasItems("João da Silva","Maria Joaquina"))
                //Verifica se o nome está em maiúsculo
                .body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
                //Verifica nomes que comecem com Maria na lista restornada
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
                //Verifica nomes que comecem com Maria na lista restornada, mas que só traga 1 resultado
                .body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
                // Valida o máximo id
                .body("id.max()", is(3))
                //valida o dobro das idades retornadas
                .body("age.collect{it * 2}", hasItems(60,50,40))
                //Valida a idade máxima
                .body("age.max()", is(30))
                //Valida o menor salário
                .body("salary.min()", is(1234.5678f))
                //Valida a soma de salários, aproximadamente
                .body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001)))
                //Valida a soma de salários, por faixa de tolerância
                .body("salary.findAll{it != null}.sum()", allOf(greaterThan(3000d), lessThan(5000d)))


        ;
    }

    @Test
    public void testGetAgesBelow26() {
        Response response = RestAssured.given()
                .when()
                .get("http://restapi.wcaquino.me:80/users") // Replace with your actual API endpoint
                .then()
                .statusCode(200) // Assert that the status code is OK
                .extract()
                .response();

        // 1. Extract the list of ages
        List<Integer> ages = response.jsonPath().getList("age");

        // 2. Filter the ages using Java Streams
        List<Integer> agesBelow26 = ages.stream()
                .filter(age -> age < 26)
                .collect(Collectors.toList());

        // 3. Print the filtered ages
        System.out.println("Ages below 26:");
        agesBelow26.forEach(System.out::println);

        // 3. Assert the results
        assertEquals("Expected 2 ages below 26", 2, agesBelow26.size());
        assertTrue("Expected agesBelow26 to contain 25", agesBelow26.contains(25));
        assertTrue("Expected agesBelow26 to contain 20", agesBelow26.contains(20));
    }

}

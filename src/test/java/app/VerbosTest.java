package app;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class VerbosTest {

    String baseUri = "http://restapi.wcaquino.me";


    @Test
    public void deveSalvarUsuario(){

        given()
            .log().all()
            .contentType("application/json")
            .body("{ \"name\": \"Jose\", \"age\": 50}")
        .when()
                .post(baseUri+"/users")
        .then()
            .log().all()
            .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name", is("Jose"))
                .body("age", is(50))
            ;
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
        .when()
                //.put("https://restapi.wcaquino.me/users/1")
                .put("https://restapi.wcaquino.me/{entidade}/{userId}","users", "1")
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
    public void deveCustomizarURLParte2(){
        given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\": \"Usuario Alterado\", \"age\": 80 }")
                .pathParam("entidade","users")
                .pathParam("userId",1)  //incluídos os parametros aqui e retirados da url
                .when()
                //agora, foram retirados os parametros da url ,"users","1", já informados no given()
                .put("https://restapi.wcaquino.me/{entidade}/{userId}")
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
    public void deveSalvarUsuarioEObterIdCriado(){

        Response response = RestAssured.given()
                .contentType("application/json")
                .body("{ \"name\": \"Jose\", \"age\": 50}")
                .when()
                .post(baseUri+"/users")
                .then()
                .log().all()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name", is("Jose"))
                .body("age", is(50))
                .extract()
                .response();
        ;

        //from - aqui usado para extrair o  // necessário validar
        int id = JsonPath.from(response.asString()).getInt("id");
        int age = JsonPath.from(response.asString()).getInt("age");
        String name = JsonPath.from(response.asString()).getString("name");
        System.out.println("id: "+id);
        System.out.println("name: "+name);
        System.out.println("age: "+age);
    }

    //SERIALIZANDO VIA MAP
    @Test
    public void deveSalvarUsuarioUsandoMap(){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Usuario via map");
        params.put("age", 25);

        given()
            .log().all()
            .contentType("application/json")
            .body(params)
        .when()
            .post(baseUri+"/users")
        .then()
            .log().all()
            .statusCode(201)
            .body("id", is(notNullValue()))
            .body("name", is("Usuario via map"))
            .body("age", is(25))
        ;
    }

    //SERIALIZANDO VIA OBJETO
    @Test
    public void deveSalvarUsuarioUsandoObjeto(){
        User user = new User("Usuario via objeto", 35);

        given()
                .log().all()
                .contentType("application/json")
                .body(user)
        .when()
                .post(baseUri+"/users")
        .then()
                .log().all()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .body("name", is("Usuario via objeto"))
                .body("age", is(35))
        ;
    }
}
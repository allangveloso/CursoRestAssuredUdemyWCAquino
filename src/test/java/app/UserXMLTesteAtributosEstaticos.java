//JUNIT version 4.12
//RESTASSURED version 5.5.0
//Pagina para realizar os testes: http://restapi.wcaquino.me/users/
//Documentacao do hamcrestest: https://hamcrest.org/JavaHamcrest/javadoc/

package app;

import io.restassured.RestAssured;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.path.xml.XmlPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;


public class UserXMLTesteAtributosEstaticos {

    public static RequestSpecification reqSpec;
    public static ResponseSpecification resSpec;

    @BeforeClass
    public static void setup(){
        RestAssured.baseURI = "https://restapi.wcaquino.me";
       /* RestAssured.port = 443;
        RestAssured.basePath = "";*/

        //Incializando o request e response specification
        RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
        reqBuilder.log(LogDetail.ALL);
        reqSpec = reqBuilder.build();


        ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
        resBuilder.expectStatusCode(200);
        resSpec = resBuilder.build();

        //Define essas especificações globais para os logs e status code...
        //...no lugar de .spec(reqSpec) e .spec(resSpec)
        RestAssured.requestSpecification = reqSpec;
        RestAssured.responseSpecification = resSpec;
    }

    //Esta forma é utilizada para se focar apenas nos recursos, porém o escopo está apenas para este método
    /*@Test
    public void devoTrabalharComConstantesDeConexao(){
        RestAssured.baseURI = "http://restapi.wcaquino.me";
        RestAssured.port = 80;
        RestAssured.basePath = "/v2";

        given()
                .log().all()
                .when()
                .get( "/users")
                .then()
                .statusCode(200)
        ;
    }*/


//    A PARTIR DE AQUI, INSERINDO REQUEST/RESPONSE SPECIFICATION
    @Test
    public void devoTrabalharComXML(){

        given()
            .spec(reqSpec) //gera um log
        .when()
            .get("/usersXML/3")
        .then()
//                .statusCode(200)
                .spec(resSpec) //substitui o statuscode(200)
                .body("user.name", is("Ana Julia"))
                .body("user.@id", is("3"))
                .body("user.filhos.name.size()", is(2))
                .body("user.filhos.name[0]", is("Zezinho"))
                .body("user.filhos.name[1]", is("Luizinho"))
                .body("user.filhos.name", hasItem("Luizinho"))
                .body("user.filhos.name", hasItems("Luizinho", "Zezinho"))
        ;

    }

    @Test
    public void devoTrabalharComXMLNoRaiz(){
        given()
                //.spec(reqSpec) //substituido por RestAssured.requestSpecification = reqSpec; (linha 50)
                .when()
                //Agora faz o teste em cima do conteúdo em XML
                .get("/usersxml")
                .then()
//                .statusCode(200)
//                .spec(resSpec) //substituido por RestAssured.responseSpecification = resSpec; (linha 51)
                .body("users.user.name", hasItems("João da Silva", "Maria Joaquina", "Ana Julia"))
                .body("$.user.name", hasItems("João da Silva", "Maria Joaquina", "Ana Julia"))
        ;
    }
    @Test
    public void devoTrabalharComXmlAvancado(){
        given()
                .when()
                .get("/usersxml")
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
                //Validar que os nomes que comecem com 'Maria' combinam em upperCase com MARIA JOAQUINA
                .body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
        ;
    }

    @Test
    public void devoFazerPesquisasAvancadasComXmlEJava(){
        //Que será transformado para Java
        String path = given()
                .when()
                .get("/usersxml")
                .then()
                .statusCode(200)
                //Reduzir a expressão:
                //.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
                //para
                .extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}");
        System.out.println(path.toString())
        ;

        Assert.assertEquals("MariA Joaquina".toUpperCase(), path.toUpperCase());
    }

    @Test
    public void devoFazerPesquisasAvancadasComXmlEJavaComDoisRegistros(){
        //Que será transformado para Java
        XmlPath xmlPath = given()
                .when()
                .get("/usersxml")
                .then()
                .statusCode(200)
                //Reduzir a expressão:
                //.body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
                //para
                .extract().xmlPath();
        List<String> nomesComN = xmlPath.getList("users.user.findAll{it.name.toString().contains('n')}.name");
        System.out.println(nomesComN);

        Assert.assertEquals(2, nomesComN.size());
        Assert.assertEquals("MariA Joaquina".toUpperCase(), nomesComN.get(0).toUpperCase());
        Assert.assertTrue("ANA JULIA".equalsIgnoreCase(nomesComN.get(1).toString()));
    }
}

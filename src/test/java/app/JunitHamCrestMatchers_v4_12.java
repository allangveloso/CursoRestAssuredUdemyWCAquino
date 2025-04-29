//JUNIT version 4.12
//RESTASSURED version 5.5.0
package app;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;


public class JunitHamCrestMatchers_v4_12 {

    String baseUri = "http://restapi.wcaquino.me:80/ola";

    @Test
    public void testeHamcrestMatchers() {

        Assert.assertThat("Maria", Matchers.is("Maria"));
        Assert.assertThat("Maria", Matchers.is(not("Julia")));
        Assert.assertThat(128, Matchers.is(128));
        Assert.assertThat(128, Matchers.isA(Integer.class));
        Assert.assertThat(128d, Matchers.isA(Double.class));
        Assert.assertThat(128d, Matchers.greaterThan(120d));
        Assert.assertThat(128d, Matchers.lessThan(130d));

        List<Integer> impares = Arrays.asList(1, 3, 5, 7, 9);
        //Agora, depois de importar a lib Assert , Matchers
        assertThat(impares, hasSize(5));
        assertThat(impares, contains(1, 3, 5, 7, 9));
        assertThat(impares, containsInAnyOrder(5, 3, 7, 9, 1));
        assertThat(impares, hasItem(1));
        assertThat(impares, hasItems(1, 5));

        assertThat("Maria", is(not("Joao")));
        assertThat("Maria", not("Julia"));
        assertThat("Joaquina", anyOf(is("Maria"), is("Joaquina")));
        assertThat("Joaquina", allOf(startsWith("Joa"), endsWith("ina"), containsString("qui")));
    }

    @Test
    public void validandoBody() {

        given()
                //Pré Condições
        .when()
            .get(baseUri)
        .then()
            .statusCode(200)
            //.body(Matchers.is("Ola Mundo!"));
            .body(is("Ola Mundo!"))
            .body(containsString("Mundo"))
            .body(is(notNullValue()));

    }
}
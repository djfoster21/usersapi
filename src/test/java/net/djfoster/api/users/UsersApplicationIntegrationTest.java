package net.djfoster.api.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import net.djfoster.api.users.user.User;
import net.djfoster.api.users.user.UserController;
import net.djfoster.api.users.user.UserRepository;
import org.apache.tomcat.util.json.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = UsersApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UsersApplicationIntegrationTest {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    User user;
    String uri;

    @Before
    public void setUp() {
        Faker faker = new Faker(new Locale("en-US"), new Random(22));
        user = new User(
                faker.name().username(),
                faker.bothify("??????@mail.com"),
                faker.name().firstName(),
                faker.name().lastName()
        );
        uri = "http://localhost:" + port;
    }

    @Test
    public void testUserControllerMethods() throws JSONException {

        //CREATE
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                uri + "/users",
                HttpMethod.POST, entity, String.class
        );

        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assert.assertTrue(response.getHeaders().getContentType().includes(MimeTypeUtils.APPLICATION_JSON));

        JSONObject j = new JSONObject(response.getBody());
        Assert.assertEquals(j.get("username"), user.getUsername());

        //LIST
        entity = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(
                uri + "/users",
                HttpMethod.GET, entity, String.class
        );
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertTrue(response.getHeaders().getContentType().includes(MimeTypeUtils.APPLICATION_JSON));
        j = new JSONObject(response.getBody());
        Assert.assertEquals(
                j.getJSONObject("_embedded").getJSONArray("userList").getJSONObject(0).get("username"),
                user.getUsername()
        );

        //GET USER
        entity = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(
                uri + "/users/1",
                HttpMethod.GET, entity, String.class
        );
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assert.assertTrue(response.getHeaders().getContentType().includes(MimeTypeUtils.APPLICATION_JSON));
        j = new JSONObject(response.getBody());
        Assert.assertEquals(j.get("username"), user.getUsername());

        //EDIT USER
        user.setUsername("test");
        entity = new HttpEntity<>(user, headers);
        response = restTemplate.exchange(
                uri + "/users/1",
                HttpMethod.PUT, entity, String.class
        );

        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assert.assertTrue(response.getHeaders().getContentType().includes(MimeTypeUtils.APPLICATION_JSON));

        j = new JSONObject(response.getBody());
        Assert.assertEquals(j.get("username"), user.getUsername());

        //DELETE USER
        entity = new HttpEntity<>(null, headers);
        response = restTemplate.exchange(
                uri + "/users/1",
                HttpMethod.DELETE, entity, String.class
        );
        Assert.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}

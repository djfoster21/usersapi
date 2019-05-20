package net.djfoster.api.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import net.djfoster.api.users.user.User;
import net.djfoster.api.users.user.UserController;
import net.djfoster.api.users.user.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.Repository;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static com.sun.xml.internal.ws.policy.sourcemodel.wspolicy.XmlToken.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerUnitTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    UserRepository userRepository;

    @Before
    public void setUp() {
        Faker faker = new Faker(new Locale("en-US"), new Random(22));
        User user = new User(faker.name().username(), faker.bothify("??????@mail.com"));
        user.setId((long) 1);

        List<User> usersList = Arrays.asList(user);
        given(userRepository.findAll()).willReturn(usersList);

        given(userRepository.findById((long) 1)).willReturn(java.util.Optional.of(user));

        given(userRepository.save(any(User.class))).will((Answer<User>) invocation -> {
            User user2 = invocation.getArgument(0);
            user2.setId(new Random().nextLong());
            return user2;
        });
    }

    @Test
    public void givenAnUser_whenGetUsers_thenReturnJson() throws Exception {
        Faker faker = new Faker(new Locale("en-US"), new Random(22));
        User user = new User(faker.name().username(), faker.bothify("??????@mail.com"));
        //then
        mvc.perform(
                get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value(user.getUsername()));
    }

    @Test
    public void givenUser_whenGetUserById_thenReturnJson() throws Exception {
        //given
        int id = 1;
        int idFail = 2;
        //then
        mvc.perform(
                get("/users/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        mvc.perform(
                get("/users/" + idFail)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenJson_whenUserIsCreated_thenReturnJson() throws Exception {
        //given
        Faker faker = new Faker(new Locale("en-US"), new Random(22));
        User user = new User(faker.name().username(), faker.bothify("??????@mail.com"));
        ObjectMapper mapper = new ObjectMapper();
        //then
        mvc.perform(
                post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value(user.getUsername()));
    }

    @Test
    public void givenJson_whenUserIsUpdated_thenReturnJson() throws Exception {
        //given
        Faker faker = new Faker(new Locale("en-US"), new Random(1));
        User user = new User(faker.name().username(), faker.bothify("??????@mail.com"));
        ObjectMapper mapper = new ObjectMapper();

        mvc.perform(
                put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    public void givenId_whenUserIsDeleted_thenReturnOk() throws Exception {
        //given
        int id = 1;

        mvc.perform(
                delete("/users/" + id))
                .andExpect(status().isOk());
    }
}

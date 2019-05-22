package net.djfoster.api.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import net.djfoster.api.users.user.User;
import net.djfoster.api.users.user.UserController;
import net.djfoster.api.users.user.UserRepository;
import net.djfoster.api.users.user.UserResourceAssembler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static java.util.Optional.empty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerUnitTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    UserResourceAssembler assembler;
    @MockBean
    UserRepository repository;


    @Before
    public void setUp() {
        Faker faker = new Faker(new Locale("en-US"), new Random(22));
        User user = new User(
                faker.name().username(),
                faker.bothify("??????@mail.com"),
                faker.name().firstName(),
                faker.name().lastName()
        );
        user.setId((long) 1);

        List<User> usersList = Arrays.asList(user);
        Mockito.when(repository.findAll()).thenReturn(usersList);
        Mockito.when(assembler.toResource(any(User.class))).then((Answer<Resource<User>>) invocation -> {
            return new Resource<>(user,
                    linkTo(methodOn(UserController.class).one(user.getId())).withSelfRel(),
                    linkTo(methodOn(UserController.class).all()).withRel("users")
                    );
        });
        Mockito.when(repository.findById((long) 1)).thenReturn(java.util.Optional.of(user));
        Mockito.when(repository.findById((long) 2)).thenReturn(empty());
        Mockito.when(repository.save(any(User.class))).then((Answer<User>) invocation -> {
            User user2 = invocation.getArgument(0);
            user2.setId(new Random().nextLong());
            return user2;
        });
    }

    @Test
    public void givenAnUser_whenGetUsers_thenReturnJson() throws Exception {
        //given
        Faker faker = new Faker(new Locale("en-US"), new Random(22));
        User user = new User(faker.name().username(), faker.bothify("??????@mail.com"));


        //then
        mvc.perform(
                get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userList[0].username").value(user.getUsername())
                );
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
                .andExpect(status().isCreated())
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber());
        mvc.perform(
                put("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenId_whenUserIsDeleted_thenReturnNoContent() throws Exception {
        //given
        int id = 1;

        mvc.perform(
                delete("/users/" + id))
                .andExpect(status().isNoContent());
    }
}

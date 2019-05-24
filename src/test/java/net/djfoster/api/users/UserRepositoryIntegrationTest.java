package net.djfoster.api.users;

import net.djfoster.api.users.user.User;
import net.djfoster.api.users.user.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindAll_thenReturnList() {
        // given
        User user = new User("user_test","user@user.com");
        entityManager.persist(user);
        entityManager.flush();
        // when
        List<User> users = userRepository.findAll();

        // then
        assertThat(users)
                .isInstanceOf(List.class);
    }

}

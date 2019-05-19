package net.djfoster.api.users.database;

import lombok.extern.slf4j.Slf4j;
import net.djfoster.api.users.user.User;
import net.djfoster.api.users.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LoadDatabase {
    @Bean
    CommandLineRunner initDatabase(UserRepository repository)  {
        return args -> {
            log.info("Preloading "+repository.save(new User("user1","user@user.com")) );
        };
    }
}

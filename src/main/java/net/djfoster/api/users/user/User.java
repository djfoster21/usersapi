package net.djfoster.api.users.user;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.Null;
import java.util.Date;

@Data
@Entity
public class User {
    private @Id @GeneratedValue Long id;
    private String username;

    private @Email String email;

    private @Null String password;
    private @Null  Date created_at;
    private @Null  Date updated_at;

    public User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}

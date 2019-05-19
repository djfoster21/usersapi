package net.djfoster.api.users.user;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Data
@Entity
public class User {
    private @Id @GeneratedValue Long id;
    private String username;
    private String email;
    private String password;
    private Date created_at;
    private Date updated_at;

    public User() {}
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
